# coding=utf-8

# Version:$Id: cfginit.py,v 1.1.2.13 2018/06/06 12:38:04 kshao Exp $

import os
import sys
import re
import json
import time
import glob
import yaml
import logging
import commands
import xml.etree.ElementTree as ET
import xml.dom.minidom as dom

sys.path.append(r"../")
import ip_util

logging.basicConfig(level=logging.DEBUG, format='%(asctime)s [%(levelname)s] %(message)s', datefmt="%Y-%m-%d %H:%M:%S",
                    filename="/var/log/cfginit.log", filemode="w")
console = logging.StreamHandler()
console.setLevel(logging.INFO)
console.setFormatter(logging.Formatter("%(asctime)s [%(levelname)s] %(message)s"))
logging.getLogger().addHandler(console)

_map = {"vCloud_bootproto_": "PROTO", "vCloud_gateway_": "GATEWAY", "vCloud_macaddr_": "MAC",
        "vCloud_netmask_": "PREFIX", "vCloud_ip_": "FIXED_IPADDR"}


doorbell_info = {}
vnfmAddr_info = {}
sssd_dict = {}
ldap_list = []
ssh_status = None
https_status = None
ldap_status = None
ldap_cert = ""
ldap_basedn = ""
ssh_public_key = None
ldap_basedn = ldap_cert = None
dhcp_intfs = {}
vnf_type = None

dns_info = {"server": set(), "search": set()}


def handleErr(messages=None):
    if messages:
        logging.error(messages)
    if os.path.exists(r"/etc/init.d/IMS"):
        os.system("chkconfig IMS off")
    exit(1)


def isInNet(ip, net):
    if not ip: return True
    ipaddr = ip_util.ip_address(unicode(ip))
    try:
        if ipaddr in net:
            return True
    except (Exception):
        return False


def get_namespace(file_name):
    status, out = commands.getstatusoutput("cat {0}".format(file_name))
    if status == 0:
        return re.findall('\s+namespace\s+"(.*)";', out)[0]
    return None



def vm_fill_vcloud_info(env, intf_map, map_dict):
    vcloud_info = {}
    root = env
    key_value_dict = dict([[e.items()[0][1],e.items()[1][1]] for s in root._children if "PropertySection" in s.tag 
                                                                 for e in s._children if "vCloud_" in e.items()[0][1]])
    mac_list = [ e.items()[0][1] for s in root._children if "EthernetAdapterSection" in s.tag for e in s._children]
    # initialize vcloud_info['ethX']
    for i, mac in enumerate(mac_list):
        ifname = "eth{i}".format(i=i)
        vcloud_info[ifname] = {"V4":[None,None,None,None], "V6":[None,None,None,None], "MTU":None, "MAC":mac}
        for key, value in key_value_dict.items():
            _prefix = None
            for m, n in map_dict.items():
                if "{m}{i}".format(m=m,i=intf_map[i]) ==  key:
                    if n == "PROTO": 
                        vcloud_info[ifname]["V4"][0] = value
                        vcloud_info[ifname]["V6"][0] = value
                    elif n == "GATEWAY":
                        # check ip type
                        _gw = ip_util.ip_address(unicode(value))
                        if isinstance(_gw, ip_util.IPv4Address):
                            vcloud_info[ifname]["V4"][3] = value
                        elif isinstance(_gw, ip_util.IPv6Address):
                            vcloud_info[ifname]["V6"][3] = value
                    elif n == "FIXED_IPADDR": 
                        _ip = value
                        if r"/" in value:
                            _ip, _prefix = value.split(r"/")
                        _ip_addr = ip_util.ip_address(unicode(_ip))
                        if isinstance(_ip_addr, ip_util.IPv4Address):
                            vcloud_info[ifname]["V4"][1] = _ip
                            if vcloud_info[ifname]["V4"][2] and _prefix:
                                vcloud_info[ifname]["V4"][2] = max(int(_prefix), int(vcloud_info[ifname]["V4"][2]))
                        elif isinstance(_ip_addr, ip_util.IPv6Address):
                            vcloud_info[ifname]["V6"][1] = _ip
                            if vcloud_info[ifname]["V6"][2] and _prefix:
                                vcloud_info[ifname]["V6"][2] = max(int(_prefix), int(vcloud_info[ifname]["V6"][2]))
                    elif n == "PREFIX":
                        _prefix = ip_util.prefix_from_netmask_string(value)
                        if _prefix:
                            if vcloud_info[ifname]["V4"][2]:
                                vcloud_info[ifname]["V4"][2] = max(int(_prefix), int(vcloud_info[ifname]["V4"][2]))
                            else:
                                vcloud_info[ifname]["V4"][2] = _prefix
                            
    return vcloud_info

def op_fill_vcloud_info(if_suffix_list):
    """
    fill vcloud_info basing on network_data.json.

    Returns:
        A dict:
        {
            'eth0': {"V4":[type, ip, prefix, gw], "V6":[type, ip, prefix, gw], "MTU":xx, "MAC":xx},
            'eth1': {"V4":[type, ip, prefix, gw], "V6":None, "MTU":xx, "MAC":xx},
            'eth2': {"V4":[type, ip, prefix, None], "V6":None, "MTU":xx, "MAC":xx},
            ...
        }
        type: ipv4, ipv6, ipv4_dhcp, ipv6_dhcp, dhcp, static
        ip: ipv4 or ipv6 ip address
        prefix: the ip prefix. for ipv4, the range is 0~32. for ipv6 the range is 0~128
        gw: the ipv4 or ipv6 gateway

    """
    vcloud_info = {}
    # parse Json
    network_data = None
    if os.path.exists(network_data_file):
        with open(network_data_file) as fd:
            network_data = json.load(fd)
    if network_data:
        taps = [ link.get("id") for link in network_data.get("links")]
        mtus = [ link.get("mtu") for link in network_data.get("links")]
        mac_list = [ link.get("ethernet_mac_address") for link in network_data.get("links")]
        interfaces = os.listdir("/sys/class/net/")

        # initialize vcloud_info['ethX']
        for i, mac in enumerate(mac_list):
            mtu = mtus[i]
            vcloud_info["eth{i}".format(i=i)] = {"V4":None, "V6":None, "MTU":mtu, "MAC":mac}

        # [ type, ip, prefix, gw ]
        if network_data.get("networks"):
            tmp_tap_info = [ {net.get("link"): [net.get("type"),net.get("ip_address",None), ip_util.prefix_from_netmask_string(net.get("netmask",None)), 
                                                                                    net.get("routes",None)]} for net in network_data.get("networks")]
            for i, tap in enumerate(taps):
                for info_dict in tmp_tap_info:
                    info_list = info_dict.get(tap, None)
                    if info_list:
                        if info_list and info_list[-1]:
                            info_list[-1] = info_list[-1][0].get("gateway",None)
                        if "ipv4" in info_list[0]:
                            vcloud_info["eth{i}".format(i=i)]["V4"] = info_list # a list
                        elif  "ipv6" in info_list[0]:
                            vcloud_info["eth{i}".format(i=i)]["V6"] = info_list # a list
    else:
        for i in if_suffix_list:
            with open('/sys/class/net/eth{i}/mtu'.format(i=i)) as fp:
                mtu = int(fp.read().replace('\n',''))
            with open('/sys/class/net/eth{i}/address'.format(i=i)) as fp:
                mac = fp.read().replace('\n','')
            vcloud_info["eth{i}".format(i=i)] = {"V4":None, "V6":None, "MTU":mtu, "MAC":mac}        
    return vcloud_info

def get_info_from_vcloud_info(vcloud_info, info):
    """
    give the iterface name to get some information from vcloud_info.

    Args:
        vcloud_info: the vcloud_info dict;
        info: a intem of traffic_info;
    Returns:
        return a tuple:
        (mtu, mac, proto, ip_info, ipv6_info)
    """
    ifname = info["IFNAME"].replace("fp", "")
    net_info = vcloud_info.get(ifname)
    ip_prefix_gw_v4_str = ip_prefix_gw_v6_str = None

    if not net_info:
        if info["TYPE"] == "bond": 
            return (info.get("MTU",None), None, "static", None, None)
        else:
            handleErr("invalid interface. interface: {intf}\n".format(intf=ifname))

    # get mtu and proto
    mac = net_info.get("MAC")
    mtu = net_info.get("MTU")
    if mtu:
        if info.get("MTU",None):
            mtu = min(int(mtu), int(info.get("MTU",None)))
    else:
        mtu = info.get("MTU",None)

    proto = proto_v4 = proto_v6 = "static"
    if net_info.get("V4") and len(net_info.get("V4")):
        proto_v4 = net_info.get("V4")[0] 
    if net_info.get("V6") and len(net_info.get("V6")):
        proto_v6 = net_info.get("V6")[0]
    if "dhcp" in proto_v4: proto = "ipv4_dhcp"
    if "dhcp" in proto_v6: proto = "ipv6_dhcp"
    # add ipaddr and prefix for static proto
    
    if "dhcp" not in proto:
        ipaddr_v4 = info.get("IPADDR", None)
        prefix_v4 = info.get("PREFIX", None)
        ipaddr_v6 = prefix_v6= None
        if info.get("IPV6ADDR") and '/' in info.get("IPV6ADDR"):
            ipaddr_v6, prefix_v6 = info.get("IPV6ADDR").split(r"/")
        else:
            ipaddr_v6 = info.get("IPV6ADDR")

        if net_info.get("V4") and len(net_info.get("V4")):
            if prefix_v4:
                prefix_v4 = max(int(prefix_v4), int(net_info.get("V4")[2]))
            else:
                prefix_v4 = net_info.get("V4")[2]
        if net_info.get("V6") and len(net_info.get("V6")):
            if prefix_v6:
                prefix_v6 = max(int(prefix_v6), int(net_info.get("V6")[2]))
            else:
                prefix_v6 = net_info.get("V6")[2]
        
        if (not ipaddr_v4) and net_info.get("V4") and len(net_info.get("V4")): ipaddr_v4 = net_info.get("V4")[1]
        if (not ipaddr_v6) and net_info.get("V6") and len(net_info.get("V6")): ipaddr_v6 = net_info.get("V6")[1]
        if ( not (ipaddr_v4 and prefix_v4)) and ( not (ipaddr_v6 and prefix_v6)):
            handleErr("cfgint can't get the ip information from network settings, so please specify the ipaddress and prefix, interface: {intf}\n".format(intf=ifname))
             

        gateway_v4 = info.get("GATEWAY", None)
        gateway_v6 = info.get("IPV6_DEFAULTGW", None)
        gateway_from_net_v4 = gateway_from_net_v6 = None
        if net_info.get("V4") and len(net_info.get("V4")): gateway_from_net_v4 = net_info.get("V4")[3]
        if net_info.get("V6") and len(net_info.get("V6")): gateway_from_net_v6 = net_info.get("V6")[3]

        for index, gw in enumerate([gateway_v4, gateway_v6]):
            if index == 0:
                _ip = ipaddr_v4
                _prefix = prefix_v4
                _gw_net = gateway_from_net_v4
            else:
                _ip = ipaddr_v6
                _prefix = prefix_v6
                _gw_net = gateway_from_net_v6

            # if the gateway defined in network_data, and user_data also specify gateway, should check:
            # 1. if the user_date gateway is in the same subnet as the network_data gateway. 
            # 2. if the ip/prefix is in the same subnet as the network_data gateway. 
            _ip_network = None
            if _gw_net: 
                _ip_network = ip_util.ip_interface(unicode("{ip}/{prefix}".format(ip=_gw_net, prefix=_prefix))).network
                if not isInNet(gw, _ip_network):
                    handleErr("the gateway: {gw} is not in the same subnet as the one defined in network_data.\n".format(gw=gw))
                if not gw:
                    if index == 0:
                        gw = gateway_v4 = _gw_net
                    else:
                        gw = gateway_v6 = _gw_net
            elif gw:
                _ip_network = ip_util.ip_interface(unicode("{ip}/{prefix}".format(ip=gw, prefix=_prefix))).network

            # check if the gateway_vX and ipaddr_vX are int the same subnet
            if _ip_network and not isInNet(_ip, _ip_network):
                handleErr("the ip: {ip}/{prefix} and the gateway: {gw} are not in the same subnet.\n".format(ip=_ip, prefix=_prefix, gw=gw))
        if prefix_v4: ip_prefix_gw_v4_str = "{ip}/{pr}/{gw}".format(ip=ipaddr_v4, pr=prefix_v4, gw=gateway_v4)
        if prefix_v6: ip_prefix_gw_v6_str = "{ip}/{pr}/{gw}".format(ip=ipaddr_v6, pr=prefix_v6, gw=gateway_v6)
    return (mtu, mac, proto, ip_prefix_gw_v4_str, ip_prefix_gw_v6_str)


def fill_intf_data(vcloud_info, traffic_info):
    """
    fill intf_data basing on vcloud_info and traffic_info.

    Args:
        vcloud_info: the vcloud_info dict
        traffic_info:  the traffic_info dict
    Returns:
        the dict:
          {
          'fpeth3': {
              'DEFAULTGW': 'no', 
              'MTU': 1200, 
              'IPS': set(['', '7777:8888:9999::3344/64/7777:8888:9999::1']), 
              'MAC': u'fa:16:3e:70:94:5f', 
              'TRAFFIC': {
                  'fpeth3-SWX': {'DEFAULTGW': 'no', 'IPS': set(['', '7777:8888:9999::3344/64/7777:8888:9999::1']), 'GATEWAY': '7777:8888:9999::1', 'RE': set([None])}
              }, 
              'PROPTO': u'ipv6', 
              'TYPE': 'Ethernet'
          }, 
        
    """
    intf_data = {}
    global dhcp_intfs
    
    for traffic, info in traffic_info.items():
        mtu, mac, proto, ip_info, ipv6_info = get_info_from_vcloud_info(vcloud_info, info)
        gateway = info.get("GATEWAY") or info.get("IPV6_DEFAULTGW")
        defaultgw = info.get("DEFAULTGW")
        if not defaultgw:
            defaultgw = "no"
        else:
            defaultgw = "yes"
            
        # initialize intf_data
        if info["IFNAME"] not in intf_data:
            intf_data[info['IFNAME']] = {"IPS": set(), "PROPTO":"", "TRAFFIC": {
                traffic: {"IPS": set(), "RE":set(), "DEFAULTGW": defaultgw, "GATEWAY": gateway}}}
        intf_data[info['IFNAME']]["DEFAULTGW"] = defaultgw
        intf_data[info['IFNAME']]["TYPE"] = info.get("TYPE")
        if intf_data[info['IFNAME']]["TYPE"] == "bond":
            intf_data[info['IFNAME']]["SLAVE"] = info.get("SLAVE", None)
            intf_data[info['IFNAME']]["OPTS"] = info.get("OPTS", None)
        
        intf_data[info['IFNAME']]["MTU"] = mtu

        if traffic not in intf_data[info['IFNAME']]["TRAFFIC"]:
            intf_data[info['IFNAME']]["TRAFFIC"][traffic] = {"IPS": set(), "DEFAULTGW": defaultgw}
        intf_data[info['IFNAME']]["TRAFFIC"][traffic]["IPS"].add(ip_info)
        intf_data[info['IFNAME']]["TRAFFIC"][traffic]["IPS"].add(ipv6_info)
        intf_data[info['IFNAME']]["MAC"] = mac
        intf_data[info['IFNAME']]["PROPTO"] = proto
        # FE-internal
        if "RE" in intf_data[info['IFNAME']]["TRAFFIC"][traffic]:
            intf_data[info['IFNAME']]["TRAFFIC"][traffic]["RE"].add(info.get("REMOTE_TRAFFIC_TYPE"))
        intf_data[info['IFNAME']]["IPS"] |= intf_data[info['IFNAME']]["TRAFFIC"][traffic]["IPS"]
        if "dhcp" in intf_data[info['IFNAME']]["PROPTO"]:
            dhcp_intfs.setdefault(info['IFNAME'], {"ipv4":None, "ipv6":None})
    return intf_data

def parse_ovfEnv():
    if DEBUG:
        ####### just for DEBUG ##################
        tree = ET.parse(out_xml)
        root = tree.getroot()
        ########## DEBUG end ####################
    else:
        try_again = False
        if os.path.exists(r"/var/lib/cfginit_ovfEnv"):
            with open(r"/var/lib/cfginit_ovfEnv") as fd:
                if not fd.read(): try_again = True
        else:
            try_again = True
            
        if try_again:
            status, vm_net = commands.getstatusoutput(r'vmtoolsd --cmd="info-get guestinfo.ovfEnv" >/var/lib/cfginit_ovfEnv')
            if status != 0:
                handleErr("fail to execute vmtoolsd.")
        tree = ET.parse(r"/var/lib/cfginit_ovfEnv")
        root = tree.getroot()
    return root

def vm_fill_traffic_info(env):
    """
    fill traffic_info and some global structs basing a user_data

    Returns:
        The traffic_info. It is a dict like this:
        {
            "Sig-HTTP": {"DNS1": null, "DNS2": null,"IPADDR": "192.168.1.253", "MTU": 1500, "PREFIX": 24, "IFNAME": "eth2", "TYPE": "Ethernet", "GATEWAY": null},
            "OAM": {"DEFAULTGW": true, "DNS1": null, "DNS2": null, "IPADDR": "10.2.3.113", "MTU": 1400, "PREFIX": 24, "IFNAME": "eth0", "TYPE": "Ethernet","GATEWAY": "10.2.3.1"},
            ...
        }
        Notice:
        TYPE: Ethernet/bond
    """
    traffic_info = {}
    global doorbell_info
    global dns_info
    global ssh_status
    global https_status
    global ldap_status
    global ldap_cert
    global ldap_basedn
    global ldap_port_2
    global ldap_ip_1
    global ldap_port_1
    global ldap_ip_2
    global ssh_public_key
    security_info = {}

    root = env
    key_value_dict = dict([[e.items()[0][1],e.items()[1][1]] for s in root._children if "PropertySection" in s.tag for e in s._children if "vnfmUserData." in e.items()[0][1]])
    network_list = traffic_dict = [ { m.split(".")[2]: {m.split(".")[3]: n}} for m,n in key_value_dict.items() if ".network." in m]
    traffic_info={}
    for item in network_list:
        for k, v in item.items():
            if k not in traffic_info:
                traffic_info.update(item)
            else:
                traffic_info[k].update(v)

    doorbell_info = dict([ [ m.split(".")[2], n] for m,n in key_value_dict.items() if "vnfmUserData.config." in m])
    security_info = dict([ [ m.split(".")[3], n] for m,n in key_value_dict.items() if "vnfmUserData.vdu.security." in m])
    dns_info["server"] = set([ n for m,n in key_value_dict.items() if "vCloud_dns1" in m])
    zone = [n for m,n in key_value_dict.items() if "vnfmUserData.vdu.TIMEZONE" in m]
    if zone:
        commands.getoutput(r"ln -sf /usr/share/zoneinfo/{zone} /etc/localtime".format(zone=zone))
    elif doorbell_info.pop("TIMEZONE",None):
        commands.getoutput(r"ln -sf /usr/share/zoneinfo/{zone} /etc/localtime".format(zone=value))
    name = [ n for m,n in key_value_dict.items() if "vnfmUserData.vm.vmname" in m]
    if "VNFM_IP_ADDRESSES" in doorbell_info: vnfmAddr_info["IpPortList"] = doorbell_info.pop("VNFM_IP_ADDRESSES")
    if "VNFM_FQDN" in doorbell_info: vnfmAddr_info["FQDN"] = doorbell_info.pop("VNFM_FQDN", None)
    if "VNFM_PORT" in doorbell_info: vnfmAddr_info["PORT"] = doorbell_info.pop("VNFM_PORT", None)
    if "VNFM_TTL" in doorbell_info: vnfmAddr_info["TTL"] = doorbell_info.pop("VNFM_TTL", None)
    if name and doorbell_info: doorbell_info["SYSTEM_ID"] = name[0]
    if len(doorbell_info) <= 1:
        doorbell_info = None
    
    # https
    https_status = security_info.get("HTTPS_ENABLED")
    # ssh
    # ssh_status = security_info.get("SSH_ENABLED")
    # if meta_data.get("keys"):
    #    for key in meta_data.get("keys"):
    #        if key.get("type") == "ssh":
    #            ssh_public_key = key.get("data")
    # ldap
    for index in range(1, 11):
        ip_key = "LDAP_SERVER_ADDR{0}".format(index)
        ldap_ip = security_info.get(ip_key)
        port_key = "LDAP_SERVER_PORT{0}".format(index)
        ldap_port = security_info.get(port_key)
        if ldap_port and ldap_ip:
            ldap_list.append("ldaps://{0}:{1}".format(ldap_ip, ldap_port))
    ldap_basedn = security_info.get("LDAP_BASEDN")
    ldap_cert = security_info.get("CA_CERT")
    return traffic_info

def op_fill_traffic_info():
    """
    fill traffic_info and some global structs basing a user_data

    Returns:
        The traffic_info. It is a dict like this:
        {
            "Sig-HTTP": {"DNS1": null, "DNS2": null,"IPADDR": "192.168.1.253", "MTU": 1500, "PREFIX": 24, "IFNAME": "eth2", "TYPE": "Ethernet", "GATEWAY": null},
            "OAM": {"DEFAULTGW": true, "DNS1": null, "DNS2": null, "IPADDR": "10.2.3.113", "MTU": 1400, "PREFIX": 24, "IFNAME": "eth0", "TYPE": "Ethernet","GATEWAY": "10.2.3.1"},
            ...
        }
        Notice:
        TYPE: Ethernet/bond
    """
    # parse YAMl
    traffic_info = {}
    global doorbell_info
    global dns_info
    global ssh_status
    global https_status
    global ldap_status
    global ldap_cert
    global ldap_basedn
    global ssh_public_key
    global vnf_type
    global sssd_dict
    global ldap_list

    with open(user_data_file) as fd:
        user_data = yaml.load(fd)
    traffic_info = user_data.get("network")
    with open(meta_data_file) as fd:
        meta_data = json.load(fd)

    # for doorbell, timezone, vnfmInfo
    if user_data.get("config"):
        config = user_data.get("config")
        zone = config.pop("TIMEZONE",None)
        if zone:
            commands.getoutput(r"ln -sf /usr/share/zoneinfo/{zone} /etc/localtime".format(zone=zone))
        doorbell_info = config
        if "VNFM_IP_ADDRESSES" in doorbell_info: vnfmAddr_info["IpPortList"] = doorbell_info.pop("VNFM_IP_ADDRESSES")
        if "VNFM_FQDN" in doorbell_info: vnfmAddr_info["FQDN"] = doorbell_info.pop("VNFM_FQDN", None)
        if "VNFM_PORT" in doorbell_info: vnfmAddr_info["PORT"] = doorbell_info.pop("VNFM_PORT", None)
        if "VNFM_TTL" in doorbell_info: vnfmAddr_info["TTL"] = doorbell_info.pop("VNFM_TTL", None)
        if doorbell_info:
            doorbell_info.setdefault("SYSTEM_ID", meta_data.get("uuid"))
        if len(doorbell_info) <= 1:
            doorbell_info = None

    # for vdu
    vdu = user_data.get("vdu")
    if vdu:
        try:
            vnf_type = vdu.pop("vnf-type", None).get("service-type")
        except:
            pass
        zone = vdu.pop("TIMEZONE",None)
        if zone:
            commands.getoutput(r"ln -sf /usr/share/zoneinfo/{zone} /etc/localtime".format(zone=zone))
    if vdu and vdu.get("security"):
        logging.info("%s", json.dumps(vdu.get("security"), indent=1))
        # https
        https_status = vdu.get("security").get("HTTPS_ENABLED")
        # ssh
        ssh_status = vdu.get("security").get("SSH_ENABLED")
        if meta_data.get("keys"):
            for key in meta_data.get("keys"):
                if key.get("type") == "ssh":
                    ssh_public_key = key.get("data")
        # ldap and sssd
        confd_user = vdu.get("security").get("CONFD_USERNAME")
        confd_pwd = vdu.get("security").get("CONFD_PASSWORD")
        if confd_user and confd_pwd:
            logging.info("add user for confd: %s", commands.getstatusoutput("useradd -d /home/{u} -m -s /sbin/nologin {u}".format(u=confd_user)))
            logging.info(commands.getstatusoutput("echo '{p}' | passwd --stdin {u}".format(p=confd_pwd, u=confd_user)))
            logging.info(commands.getstatusoutput("/bin/bash /opt/confd/confdSetInitialConfig.sh  [CONFD_USERNAME={u}]".format(u=confd_user)))
            #if os.path.exists('/opt/confd/confdInstallDir/etc/confd/ssh/authorized_keys'):
            #    logging.info(commands.getstatusoutput("mkdir -p /home/{u}/.ssh 2>/dev/null;cp -rf /opt/confd/confdInstallDir/etc/confd/ssh/authorized_keys /home/{u}/.ssh/".format(u=confd_user)))
            #else:
            #    logging.error("/opt/confd/confdInstallDir/etc/confd/ssh/authorized_keys does not exist.")

        for index in range(1, 11):
            ip_key = "LDAP_SERVER_ADDR{0}".format(index)
            ldap_ip = vdu.get("security").get(ip_key)
            port_key = "LDAP_SERVER_PORT{0}".format(index)
            ldap_port = vdu.get("security").get(port_key)
            if ldap_port and ldap_ip:
                ldap_list.append("ldaps://{0}:{1}".format(ldap_ip, ldap_port))
        ldap_basedn = vdu.get("security").get("LDAP_BASEDN")
        ldap_cert = vdu.get("security").get("CA_CERT")
        serv_list = []
        grp_list = []
        for index in range(1, 11):
            serv_key = 'LDAP_SERVER{0}'.format(index)
            sssd_serv = vdu.get("security").get(serv_key)
            if sssd_serv:
                serv_list.append(sssd_serv)
            grp_key = 'USER_GROUP{0}'.format(index)
            sssd_grp = vdu.get("security").get(grp_key)
            if sssd_grp:
                grp_list.append(sssd_grp)
                
        sssd_dict['LDAP_SERVERS'] = serv_list
        sssd_dict['USER_GROUPS'] = grp_list
        sssd_dict['LDAP_BASEDN'] = vdu.get("security").get("LDAP_BASEDN")
        sssd_dict['LDAP_USER_SEARCH_BASE'] = vdu.get("security").get("LDAP_USER_SEARCH_BASE")
        sssd_dict['LDAP_BIND_DN'] = vdu.get("security").get("LDAP_BIND_DN")
        sssd_dict['LDAP_GROUP_SEARCH_BASE'] = vdu.get("security").get("LDAP_GROUP_SEARCH_BASE")
        sssd_dict['LDAP_AUTHTOK'] = vdu.get("security").get("LDAP_AUTHTOK")
        sssd_dict['CA_CERT'] = vdu.get("security").get("CA_CERT")
       

    # for runcmd
    if user_data.get("runcmd"):
        logging.info("runcmd :\n%s", user_data.get("runcmd"))
        with open(tmp_cmd_file, "w") as fd:
            fd.write(user_data.get("runcmd"))

    # for dns_info
    for info in traffic_info.values():
        dns_info["server"].add(info.get("DNS"))
        dns_info["server"].add(info.get("DNS1"))
        dns_info["server"].add(info.get("DNS2"))

    return traffic_info


def generate_ifcfg(conf_dir, intf_data):
    global dhcp_intfs
    gateway_v4_intf = None
    gateway_v6_intf = None
    slaves = ""
    # remove old ifcfg files
    old_ifcfg_files = glob.glob(r"/etc/sysconfig/network-scripts/ifcfg-*")
    for ifcfg_file in old_ifcfg_files:
        if "lo" in ifcfg_file: continue
        os.remove(ifcfg_file)

    for intf, data in intf_data.items():
        index_ipv4 = 1
        index_ipv6 = 1
        ipv6_sec_ips = ""
        if intf in slaves:
            continue
        if "fp" not in intf:
            defaultgw = data["DEFAULTGW"]
            proto = None
            v6init = None
            autoconf = None
            if not data.get("PROPTO"):
                proto = "none"
            elif "ipv4_dhcp" in data.get("PROPTO"):
                proto = "dhcp"
                if defaultgw == "yes":
                    gateway_v4_intf = intf
                
            ifcfg = """# generate by mav-cfginit
TYPE={type}
NAME={dev}
DEVICE={dev}
BOOTPROTO={proto}
USERCTL=no
ONBOOT=yes
BONDING_OPTS="{opts}"
MTU={mtu}
""".format(type=data.get("TYPE"),
           dev=intf,
           proto=proto,
           opts=data.get("OPTS"),
           mtu=data.get("MTU")
           )
            if "dhcp" in data.get("PROPTO"):
                ifcfg += "PEERDNS=no\n"

            # for bond
            if "bond" in intf:
                if data.get("TYPE") != "bond":
                    handleErr("the TYPE ('{tp}') is incorrect for bond interface.".format(tp=data.get("TYPE")))
                slaves = ""
                if data.get("SLAVE"):
                    slaves = data.get("SLAVE").split(",")
                for slave in slaves:
                    slave_ifcfg = """# generate by mav-cfginit
DEVICE={slave}
BOOTPROTO=none
ONBOOT=yes
MASTER={bond}
SLAVE=yes
USERCTL=no
MTU={mtu}
""".format(slave=slave, bond=intf, mtu=data.get("MTU"))
                    slave_ifcfg = re.sub(".*None.*\n", "", slave_ifcfg)
                    logging.info("slave ifcfg ----> %s:\n %s", slave, slave_ifcfg)
                    if not DEBUG:
                        with open(r"{conf_dir}/ifcfg-{name}".format(conf_dir=conf_dir, name=slave), "w") as fd:
                            fd.write(slave_ifcfg)
            # add IP
            if "ipv6_dhcp" in data.get("PROPTO"):
                commands.getoutput(r"sed -i '/net.ipv6.conf.{intf}.accept_ra/d' /etc/sysctl.conf".format(intf=intf))
                commands.getoutput(r"echo 'net.ipv6.conf.{intf}.accept_ra=1' >> /etc/sysctl.conf".format(intf=intf))
                commands.getoutput("sysctl -p")
                ifcfg += "IPV6INIT=yes\nIPV6_AUTOCONF=no\nDHCPV6C=yes\n"
                if defaultgw == "yes":
                    gateway_v6_intf = intf
                if "ipv4_dhcp" not in data.get("PROPTO"):
                    ifcfg = re.sub(r"BOOTPROTO=.*\n", "", ifcfg)
            for ip_prefix_gw in data["IPS"]:
                if ip_prefix_gw:
                    ip, prefix, gw = ip_prefix_gw.split(r"/")
                    ipaddr = ip_util.ip_address(unicode(ip))
                    if isinstance(ipaddr, ip_util.IPv4Address):
                        ifcfg += "IPADDR{id}={ip}\nPREFIX{id}={pr}\n".format(id=index_ipv4, ip=ip, pr=prefix)
                        # set gw
                        if defaultgw == "yes" and gw:
                            if not gateway_v4_intf:
                                ifcfg += "GATEWAY={gw}\n".format(gw=gw)
                            if gateway_v4_intf and gateway_v4_intf != intf:
                                handleErr(
                                    "Failure adding gw to {name}, an IPv4 gateway has set on other interface".format(
                                        name=gateway_v4_intf))
                            gateway_v4_intf = intf
                        index_ipv4 += 1
                    elif isinstance(ipaddr, ip_util.IPv6Address):
                        if index_ipv6 == 1:
                            ifcfg += "IPV6INIT=yes\nIPV6_AUTOCONF=no\nIPV6ADDR={ip}/{pr}\n".format(ip=ip, pr=prefix)
                        else:
                            ipv6_sec_ips += "{ip}/{pr} ".format(ip=ip, pr=prefix)
                        if defaultgw == "yes" and gw:
                            if not gateway_v6_intf:
                                ifcfg += "IPV6_DEFAULTGW={gw}\n".format(gw=gw)
                            if gateway_v6_intf and gateway_v6_intf != intf:
                                handleErr(
                                    "Failure adding gw to {name}, an IPv6 gateway has set on other interface".format(
                                        name=gateway_v6_intf))
                            gateway_v6_intf = intf
                        index_ipv6 += 1

            if re.findall(r"IPADDR\d=", ifcfg).__len__() == 1:
                ifcfg = re.sub(r"IPADDR\d=", r"IPADDR=", ifcfg)
                ifcfg = re.sub(r"PREFIX\d=", r"PREFIX=", ifcfg)
            if ipv6_sec_ips:
                ifcfg += 'IPV6ADDR_SECONDARIES="{ips}"\n'.format(ips=ipv6_sec_ips.strip())
            ifcfg = re.sub(r".*None.*\n", r"", ifcfg)
            logging.info("interface ifcfg ----> %s:\n %s", intf, ifcfg)
            if not DEBUG:
                with open(r"/etc/sysconfig/network-scripts/ifcfg-{name}".format(name=intf), "w") as fd:
                    fd.write(ifcfg)
    with open(r"/etc/sysconfig/network", "r+") as fd:
        content = fd.read()
        content = re.sub(r"\nGATEWAYDEV=.*", "", content)
        content = re.sub(r"\nGATEWAY=.*", "", content)
        gw_dev = gateway_v4_intf or gateway_v6_intf
        if gw_dev:
            content += "GATEWAYDEV={dev}\n".format(dev=gw_dev)
            logging.info("update /etc/sysconfig/network: gateway dev = %s", gw_dev)
        if not DEBUG:
            fd.seek(0)
            fd.truncate()
            fd.write(content)
    if not DEBUG:
        logging.info("restart network service.")
        out = commands.getoutput("service network restart")
        logging.info("output: \n%s", out)

    # get dhcp interface ip
    for intf in dhcp_intfs.keys():
        out = commands.getoutput("ip a s {intf}".format(intf=intf))
        m = re.search(r"inet (\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3}/\d{1,2})\D+", out)
        if m:
            dhcp_intfs[intf]["ipv4"] = m.group(1)
        m = re.findall(r"inet6 (.*?)\s+", out)
        for ipv6 in m:
            if ipv6[0:4] != "fe80":
                dhcp_intfs[intf]["ipv6"] = ipv6
          
            
def set_ssh():
    if ssh_status is None:
        return
    logging.info("Will set SSH: %s", ssh_status)
    if ssh_status and ssh_public_key:
        logging.info("Will set SSH")
        try:
            if not os.path.exists(ssh_dir):
                logging.info("mkdir %s", ssh_dir)
                os.mkdir(ssh_dir)
                os.chmod(ssh_dir, 0700)
            os.chdir(ssh_dir)
            with open("{dir}/authorized_keys".format(dir=ssh_dir), "a+") as fd:
                fd.write(ssh_public_key)
                if DEBUG:
                    logging.info("ssh_public_key:\n{key}".format(key=ssh_public_key))
        except Exception:
            handleErr("ssh configuration failed")
        commands.getstatusoutput("chown -R admin:admin {dir}".format(dir=ssh_dir))
        logging.info("enable SSH")
        cmd = r"/bin/bash -c '/opt/confd/confdSetInitialConfig.sh  [SSH=enabled]'"
        logging.info("command:\n{cmd}".format(cmd=cmd))
        if not DEBUG:
            commands.getstatusoutput(cmd)
    else:
        logging.info("diable SSH")
        cmd = r"/bin/bash -c '/opt/confd/confdSetInitialConfig.sh  [SSH=disabled]'"
        logging.info("command:\n{cmd}".format(cmd=cmd))
        if not DEBUG:
            commands.getstatusoutput(cmd)
        


def set_https(namespace):
    if https_status is None: return
    logging.info("Will set HTTPS.%s", https_status)
    if https_status:
        logging.info("enable HTTPS.")
        cmd = r"/bin/bash -c '/opt/confd/confdSetInitialConfig.sh  [HTTPS=enabled]'"
        logging.info("command:\n{cmd}".format(cmd=cmd))
    else:
        logging.info("disable HTTPS.")
        cmd = r"/bin/bash -c '/opt/confd/confdSetInitialConfig.sh  [HTTPS=disabled]'"
        logging.info("command:\n{cmd}".format(cmd=cmd))
        
    if not DEBUG:
        commands.getstatusoutput(cmd)
        # "vnfmIntf.https=True" for notifyMgr
        system_static_xml = glob.glob("/opt/confd/confdInstallDir/var/confd/cdb/*_system_static_config_data.xml")
        if not system_static_xml: handleErr("system static config xml is nonexistent")
        with open(system_static_xml[0]) as fd:
            content = fd.read()
            content = re.sub(r"<config\s+.*>", r"<config>", content)
            content = re.sub(r"<sys\s+.*>", r"<sys>", content)
        root = ET.fromstring(content)
        node = root.find("*/*/vnfmIntf/https")
        if node is None: handleErr("table vnfmIntf is not defined in system static config xml")
        if https_status:
            value = "TRUE"
        else:
            value = "FALSE"
        node.text = value
        result = ET.tostring(root)
        result = re.sub(r'<config>', r'<config xmlns="http://tail-f.com/ns/config/1.0">', result)
        result = re.sub(r'<sys>', r'<sys xmlns="' + namespace + r'">', result)
        with open(system_static_xml[0], "w") as fd:
            fd.write(result)


def set_ldap():
    if ldap_basedn and ldap_list:
        logging.info("Will set LDAP.")
        if ldap_cert:
            try:
                TLS_CACERTDIR = ""
                TLS_CACERT = ""
                tls_cacertdir = ""
                tls_cacertfile = ""
                commands.getoutput(r"if ! grep -q '^[[:space:]]*bind_timelimit' {file}; then echo 'bind_timelimit 30' >> {file};fi".format(file=ldap_conf_nslcd))
                commands.getoutput(r"if ! grep -q '^[[:space:]]*timelimit' {file}; then echo 'timelimit 30' >> {file};fi".format(file=ldap_conf_nslcd))
                commands.getoutput(r"if ! grep -q '^[[:space:]]*idle_timelimit' {file}; then echo 'idle_timelimit 3600' >> {file};fi".format(file=ldap_conf_nslcd))
                commands.getoutput(r"sed -i 's/^[[:space:]]*\(bind_timelimit\) .*/\1 30/' {file}".format(file=ldap_conf_nslcd))
                commands.getoutput(r"sed -i 's/^[[:space:]]*\(timelimit\) .*/\1 30/' {file}".format(file=ldap_conf_nslcd))
                commands.getoutput(r"sed -i 's/^[[:space:]]*\(idle_timelimit\) .*/\1 3600/' {file}".format(file=ldap_conf_nslcd))
                with open(ldap_conf_ldap) as fd:
                    for line in fd:
                        if re.search("^\s*TLS_CACERTDIR",line):
                            TLS_CACERTDIR = line.split()[1]
                        elif re.search("^\s*TLS_CACERT", line):
                            TLS_CACERT = line.split()[1]
                with open(ldap_conf_nslcd) as fd:
                    for line in fd:
                        if re.search("^\s*tls_cacertdir", line):
                            tls_cacertdir = line.split()[1]
                        elif re.search("^\s*tls_cacertfile", line):
                            tls_cacertfile = line.split()[1]
                logging.info("\nTLS_CACERTDIR=%s, TLS_CACERT=%s\ntls_cacertdir=%s, tls_cacertfile=%s", TLS_CACERTDIR, TLS_CACERT, tls_cacertdir, tls_cacertfile)
                if (not TLS_CACERTDIR) or TLS_CACERTDIR != tls_cacertdir or TLS_CACERT != tls_cacertfile:
                    handleErr("ca cert directory/file in {conf1} and {conf2} are different or undefined".format(
                        conf1=ldap_conf_ldap, conf2=ldap_conf_nslcd))
                logging.info("write ca cert into ldap configuration files.")
                if not os.path.exists(TLS_CACERTDIR):
                    os.mkdir(TLS_CACERTDIR)
                if not DEBUG:
                    if TLS_CACERT:
                        file_name = TLS_CACERT
                    else:
                        file_name = "{dir}/CA_cert.pem".format(dir=TLS_CACERTDIR)
                    with open(file_name, "w") as fd:
                        logging.info(file_name)
                        fd.write(ldap_cert)
            except Exception:
                handleErr("set ldap failed")

        cmd = '''authconfig --disablesssd --disablesssdauth --enableldap --enableldapauth \
--ldapserver={0} --ldapbasedn="{1}" --enablemkhomedir --update'''.format(','.join(ldap_list), ldap_basedn)
        logging.info("command:\n{cmd}".format(cmd=cmd))
        if not DEBUG:
            status, out = commands.getstatusoutput(cmd)
            logging.info("status: %s\nout:\n%s", status, out)
            if status != 0:
                handleErr("authconfig failed") 
        cmd = r"/bin/bash -c '/opt/confd/confdSetInitialConfig.sh  [LDAP=enabled]'"
        logging.info("command:\n{cmd}".format(cmd=cmd))
        if not DEBUG:
            commands.getstatusoutput(cmd)
            commands.getstatusoutput("service nslcd restart")
            


def generate_network_xml(namespace, xml_file, intf_data):
    rule_index = 1
    ext_dynamic_route_id = 0
    tunnelMap_index = 1
    doc = dom.Document()
    config = doc.createElement("config")
    sys = doc.createElement("sys")

    config.appendChild(sys)
    platform = doc.createElement("platform")
    sys.appendChild(platform)
    # doorbell
    if doorbell_info:
        if "VNFM_IP_ADDRESSES" in doorbell_info: vnfmAddr_info["IpPortList"] = doorbell_info.pop("VNFM_IP_ADDRESSES")
        if "VNFM_FQDN" in doorbell_info: vnfmAddr_info["FQDN"] = doorbell_info.pop("VNFM_FQDN", None)
        if "VNFM_PORT" in doorbell_info: vnfmAddr_info["PORT"] = doorbell_info.pop("VNFM_PORT", None)
        if "VNFM_TTL" in doorbell_info: vnfmAddr_info["TTL"] = doorbell_info.pop("VNFM_TTL", None)
        # vnfmAddr
        if vnfmAddr_info:
            vnfmAddr = doc.createElement("vnfmAddr")
            vnfm_name = doc.createElement("name")
            vnfm_ipaddr = doc.createElement("IpPortList")
            vnfm_fqdn = doc.createElement("fqdn")
            vnfm_port = doc.createElement("port")
            vnfm_ttl = doc.createElement("ttl")
            
            vnfm_name.appendChild(doc.createTextNode("Name1"))
            vnfmAddr.appendChild(vnfm_name)
            ips = vnfmAddr_info.get("IpPortList","")
            vnfm_ips = ", ".join(str(x) for x in ips)
            vnfm_ipaddr.appendChild(doc.createTextNode(vnfm_ips))
            vnfmAddr.appendChild(vnfm_ipaddr)
            vnfm_fqdn.appendChild(doc.createTextNode(str(vnfmAddr_info.get("FQDN", ""))))
            vnfmAddr.appendChild(vnfm_fqdn)
            vnfm_port.appendChild(doc.createTextNode(str(vnfmAddr_info.get("PORT", ""))))
            vnfmAddr.appendChild(vnfm_port)
            vnfm_ttl.appendChild(doc.createTextNode(str(vnfmAddr_info.get("TTL", "60"))))
            vnfmAddr.appendChild(vnfm_ttl)

            platform.appendChild(vnfmAddr)
 
        doorbell = doc.createElement("doorbell")

        if doorbell_info.get("SYSTEM_ID"):
            db_systemId = doc.createElement("systemId")
            db_systemId.appendChild(doc.createTextNode(str(doorbell_info.get("SYSTEM_ID"))))
            doorbell.appendChild(db_systemId)

        if doorbell_info.get("VNFM_URI"):
            db_vnfmURI = doc.createElement("vnfmURI")
            db_vnfmURI.appendChild(doc.createTextNode(str(doorbell_info.get("VNFM_URI"))))
            doorbell.appendChild(db_vnfmURI)

        if doorbell_info.get("CLOUD_PROFILE_ID"):
            db_cloudProfId = doc.createElement("cloudProfId")
            db_cloudProfId.appendChild(doc.createTextNode(str(doorbell_info.get("CLOUD_PROFILE_ID"))))
            doorbell.appendChild(db_cloudProfId)

        if doorbell_info.get("VNFD_ID"):
            db_vnfdId = doc.createElement("vnfdId")
            db_vnfdId.appendChild(doc.createTextNode(str(doorbell_info.get("VNFD_ID"))))
            doorbell.appendChild(db_vnfdId)

        if doorbell_info.get("VNFD_VERSION"):
            db_vnfdVer = doc.createElement("vnfdVer")
            db_vnfdVer.appendChild(doc.createTextNode(str(doorbell_info.get("VNFD_VERSION"))))
            doorbell.appendChild(db_vnfdVer)

        if doorbell_info.get("VDU_NAME"):
            db_vduName = doc.createElement("vduName")
            db_vduName.appendChild(doc.createTextNode(str(doorbell_info.get("VDU_NAME"))))
            doorbell.appendChild(db_vduName)

        if doorbell_info.get("NSD_ID"):
            db_nsId = doc.createElement("nsId")
            db_nsId.appendChild(doc.createTextNode(str(doorbell_info.get("NSD_ID"))))
            doorbell.appendChild(db_nsId)

        if doorbell_info.get("NSD_VERSION"):
            db_nsdVer = doc.createElement("nsdVer")
            db_nsdVer.appendChild(doc.createTextNode(str(doorbell_info.get("NSD_VERSION"))))
            doorbell.appendChild(db_nsdVer)

        if doorbell_info.get("META_DATA"):
            db_metaData = doc.createElement("metaData")
            db_metaData.appendChild(doc.createTextNode(str(doorbell_info.get("META_DATA"))))
            doorbell.appendChild(db_metaData)

        if doorbell_info.get("VNFM_IP_ADDRESS1"):
            vnfm_ip1 = doorbell_info.get("VNFM_IP_ADDRESS1")
            db_vnfmIPAddress1 = doc.createElement("vnfmIPAddress1")
            db_vnfmIPAddress1.appendChild(doc.createTextNode(str(vnfm_ip1)))
            doorbell.appendChild(db_vnfmIPAddress1)

        if doorbell_info.get("VNFM_PORT1"):
            db_vnfmPort1 = doc.createElement("vnfmPort1")
            db_vnfmPort1.appendChild(doc.createTextNode(str(doorbell_info.get("VNFM_PORT1"))))
            doorbell.appendChild(db_vnfmPort1)

        if doorbell_info.get("VNFM_IP_ADDRESS2"):
            vnfm_ip2 = doorbell_info.get("VNFM_IP_ADDRESS2")
            db_vnfmIPAddress2 = doc.createElement("vnfmIPAddress2")
            db_vnfmIPAddress2.appendChild(doc.createTextNode(str(vnfm_ip2)))
            doorbell.appendChild(db_vnfmIPAddress2)

        if doorbell_info.get("VNFM_PORT2"):
            db_vnfmPort2 = doc.createElement("vnfmPort2")
            db_vnfmPort2.appendChild(doc.createTextNode(str(doorbell_info.get("VNFM_PORT2"))))
            doorbell.appendChild(db_vnfmPort2)

        if doorbell_info.get("CMS_IP_ADDRESS1"):
            cms_ip1 = doorbell_info.get("CMS_IP_ADDRESS1")
            db_cmsIPAddress1 = doc.createElement("cmsIPAddress1")
            db_cmsIPAddress1.appendChild(doc.createTextNode(str(cms_ip1)))
            doorbell.appendChild(db_cmsIPAddress1)

        if doorbell_info.get("CMS_PORT1"):
            db_cmsPort1 = doc.createElement("cmsPort1")
            db_cmsPort1.appendChild(doc.createTextNode(str(doorbell_info.get("CMS_PORT1"))))
            doorbell.appendChild(db_cmsPort1)

        if doorbell_info.get("CMS_IP_ADDRESS2"):
            cms_ip2 = doorbell_info.get("CMS_IP_ADDRESS2")
            db_cmsIPAddress2 = doc.createElement("cmsIPAddress2")
            db_cmsIPAddress2.appendChild(doc.createTextNode(str(cms_ip2)))
            doorbell.appendChild(db_cmsIPAddress2)

        if doorbell_info.get("CMS_PORT2"):
            db_cmsPort2 = doc.createElement("cmsPort2")
            db_cmsPort2.appendChild(doc.createTextNode(str(doorbell_info.get("CMS_PORT2"))))
            doorbell.appendChild(db_cmsPort2)

            # db_vnfmFIPAddress1 = doc.createElement("vnfmFIPAddress1")
            # db_vnfmFIPAddress1.appendChild(doc.createTextNode(doorbell_info.get()))
            # doorbell.appendChild(db_vnfmFIPAddress1)

            # db_vnfmFIPAddress2 = doc.createElement("vnfmFIPAddress2")
            # db_vnfmFIPAddress2.appendChild(doc.createTextNode(doorbell_info.get()))
            # doorbell.appendChild(db_vnfmFIPAddress2)

            # db_cmsFIPAddress1 = doc.createElement("cmsFIPAddress1")
            # db_cmsFIPAddress1.appendChild(doc.createTextNode(doorbell_info.get()))
            # doorbell.appendChild(db_cmsFIPAddress1)

            # db_cmsFIPAddress2 = doc.createElement("cmsFIPAddress2")
            # db_cmsFIPAddress2.appendChild(doc.createTextNode(doorbell_info.get()))
            # doorbell.appendChild(db_cmsFIPAddress2)

        platform.appendChild(doorbell)
    # IPs
    for intf, data in intf_data.items():
        ext_intf = doc.createElement("ext_ip_intf")
        platform.appendChild(ext_intf)
        inerface = doc.createElement("intf")
        inerface.appendChild(doc.createTextNode(intf))

        ext_intf.appendChild(inerface)
        for traffic, traffic_value in data["TRAFFIC"].items():
            defaultgw = traffic_value.get("DEFAULTGW")
            traffic_gw = traffic_value.get("GATEWAY")
            if dhcp_intfs.get(intf):
                for entry in dhcp_intfs.get(intf).values():
                    if entry:
                        traffic_value["IPS"].add("{ip_prefix}/".format(ip_prefix=entry))
            # tunnelMap
            if traffic_value.get("RE"):
                fe_res = traffic_value["RE"] - set([None,''])
                # disbale re_filter and enable ip forwarding
                commands.getoutput("echo 0 > /proc/sys/net/ipv4/conf/all/rp_filter")
                commands.getoutput("echo 0 > /proc/sys/net/ipv4/conf/default/rp_filter")
                commands.getoutput("echo 1 > /proc/sys/net/ipv4/ip_forward")
                commands.getoutput("echo 1 > /proc/sys/net/ipv6/conf/all/forwarding")
                for fe_re in fe_res:
                    mapName = doc.createElement("mapName")
                    local_traffic_type = doc.createElement("local_traffic_type")
                    remote_traffic_type = doc.createElement("remote_traffic_type")
                    mapName.appendChild(doc.createTextNode("Map{index}".format(index=tunnelMap_index)))
                    local_traffic_type.appendChild(doc.createTextNode(traffic))
                    remote_traffic_type.appendChild(doc.createTextNode(fe_re))
                    
                    tunnelMap = doc.createElement("tunnelMap")
                    tunnelMap.appendChild(mapName)
                    tunnelMap.appendChild(local_traffic_type)
                    tunnelMap.appendChild(remote_traffic_type)
                    tunnelMap_index += 1
                    platform.appendChild(tunnelMap)
            # add ext_ip
            traffic_name = "{name}".format(name=traffic)
            ip_sum = len(traffic_value["IPS"] - set([None,'']))
            for ip_prefix_gw in traffic_value["IPS"]:
                if ip_prefix_gw:
                    ip, prefix, gw = ip_prefix_gw.split(r"/")
                    name = doc.createElement("name")
                    ipaddr = ip_util.ip_address(unicode(ip))
                    if ip_sum == 2:
                        if isinstance(ipaddr, ip_util.IPv6Address):
                            traffic_name = "{name}-v6".format(name=traffic)
                        else:
                            traffic_name = "{name}".format(name=traffic)
                    name.appendChild(doc.createTextNode(traffic_name + "-static"))
                    ipaddr_node = doc.createElement("ip_addr")
                    ipaddr_node.appendChild(doc.createTextNode(ip))
                    prefix_node = doc.createElement("maskSize")
                    prefix_node.appendChild(doc.createTextNode(prefix))
                    fk_ext_intf = doc.createElement("fk_ext_ip_intf")
                    fk_ext_intf.appendChild(doc.createTextNode(intf))
                    ip_config_type = doc.createElement("ip_config_type")
                    ip_config_type.appendChild(doc.createTextNode("EXT_IP_PHYSICAL"))
                    fkprot_grp = doc.createElement("fkprot_grp")
                    fkprot_grp.appendChild(doc.createTextNode("ADM"))
                    fkcomponent = doc.createElement("fkcomponent")
                    fkcomponent.appendChild(doc.createTextNode("ADM-a"))

                    ext_ip = doc.createElement("ext_ip")
                    ext_ip.appendChild(name)
                    ext_ip.appendChild(ipaddr_node)
                    ext_ip.appendChild(prefix_node)
                    ext_ip.appendChild(fk_ext_intf)
                    ext_ip.appendChild(ip_config_type)
                    ext_ip.appendChild(fkprot_grp)
                    ext_ip.appendChild(fkcomponent)
                    platform.appendChild(ext_ip)


                    # ext_rule
                    if defaultgw != "yes" and traffic_gw and traffic_gw != "None":

                        rulename = "rule" + str(rule_index)
                        ruleName = doc.createElement("ruleName")
                        fkext_ip = doc.createElement("fkext_ip")
                        maskSize = doc.createElement("maskSize")
                        table_id = doc.createElement("table_id")
                        timeStamp = doc.createElement("timeStamp")

                        ruleName.appendChild(doc.createTextNode(rulename))
                        fkext_ip.appendChild(doc.createTextNode(traffic_name))
                        maskSize.appendChild(doc.createTextNode(prefix))
                        table_id.appendChild(doc.createTextNode(str(rule_index)))
                        timeStamp.appendChild(doc.createTextNode(str(time.time())))

                        ext_rule = doc.createElement("ext_rule")
                        ext_rule.appendChild(ruleName)
                        ext_rule.appendChild(fkext_ip)
                        ext_rule.appendChild(maskSize)
                        ext_rule.appendChild(table_id)
                        ext_rule.appendChild(timeStamp)

                        platform.appendChild(ext_rule)

                        # ext_dynamic_route
                        id = doc.createElement("id")
                        destination = doc.createElement("destination")
                        maskSize = doc.createElement("maskSize")
                        gateway = doc.createElement("gateway")
                        interface = doc.createElement("interface")
                        fkext_rule = doc.createElement("fkext_rule")

                        id.appendChild(doc.createTextNode(str(ext_dynamic_route_id)))
                        if isinstance(ipaddr, ip_util.IPv6Address):
                            destination.appendChild(doc.createTextNode(r"::0"))
                        else:
                            destination.appendChild(doc.createTextNode(r"0.0.0.0"))
                        maskSize.appendChild(doc.createTextNode("0"))
                        gateway.appendChild(doc.createTextNode(gw))
                        interface.appendChild(doc.createTextNode(intf))
                        fkext_rule.appendChild(doc.createTextNode(rulename))

                        ext_dynamic_route = doc.createElement("ext_dynamic_route")
                        ext_dynamic_route.appendChild(id)
                        ext_dynamic_route.appendChild(destination)
                        ext_dynamic_route.appendChild(maskSize)
                        ext_dynamic_route.appendChild(gateway)
                        ext_dynamic_route.appendChild(interface)
                        ext_dynamic_route.appendChild(fkext_rule)
                        ext_dynamic_route.appendChild(timeStamp)

                        platform.appendChild(ext_dynamic_route)

                        # ext_dynamic_route_ProtGrp_map
                        id = doc.createElement("id")
                        fkext_dynamic_route = doc.createElement("fkext_dynamic_route")
                        fkprot_grp = doc.createElement("fkprot_grp")
                        fkprot_grp.appendChild(doc.createTextNode("ADM"))

                        id.appendChild(doc.createTextNode(str(ext_dynamic_route_id)))
                        fkext_dynamic_route.appendChild(doc.createTextNode(str(ext_dynamic_route_id)))

                        ext_dynamic_route_ProtGrp_map = doc.createElement("ext_dynamic_route_ProtGrp_map")
                        ext_dynamic_route_ProtGrp_map.appendChild(id)
                        ext_dynamic_route_ProtGrp_map.appendChild(fkext_dynamic_route)
                        ext_dynamic_route_ProtGrp_map.appendChild(fkprot_grp)
                        ext_dynamic_route_ProtGrp_map.appendChild(timeStamp)

                        platform.appendChild(ext_dynamic_route_ProtGrp_map)

                        ext_dynamic_route_id += 1
                        rule_index += 1
    result = config.toprettyxml()
    result = re.sub(r'<config>', r'<config xmlns="http://tail-f.com/ns/config/1.0">', result)
    result = re.sub(r'<sys>', r'<sys xmlns="' + namespace + r'">', result)
    repl = lambda x: ">%s</" % x.group(1).strip() if len(x.group(1).strip()) != 0 else x.group(0)
    result = re.sub(r'>\n\s*([^<]+)</', repl, result)

    logging.info("XML :\n%s", result)
    if not DEBUG:
        with open(xml_file, "w") as fd:
            fd.write(result)

def sort_interfaces(env=None):
    """
        sort the interfaces basing on the MAC order defind in network-data or ovfEnv
        and for vmware vm, it will return a old-new interface map.
        generate the eth-pci-order file for fastpath
    Args:
        env: a ET.Element object fro ovfEnv
    
    Returns:
        a list for old-new interface map like:
            [3, 0, 1, 2]
        it is only need for vmware.

    """
    global DEBUG
    intf_map = None
    # for openStak
    mac_list = None
    if platform == "vmware":
        root = env
        mac_list = [ e.items()[0][1] for s in root._children if "EthernetAdapterSection" in s.tag for e in s._children]

        # create a dict for new-old interfac map
        old_mac_list = [ e.items()[1][1] for s in root._children if "PropertySection" in s.tag for e in s._children if "vCloud_macaddr" in e.items()[0][1]]
        intf_map = [ old_mac_list.index(x) for x in mac_list ]
        if os.path.exists(r"/etc/sysconfig/eth-pci-order"):
            return intf_map    
    else:
        if os.path.exists(network_data_file):
            with open(network_data_file) as fd:
                network_data = json.load(fd)
            if network_data:
                mac_list = [ link.get("ethernet_mac_address") for link in network_data.get("links")]
            if os.path.exists(r"/etc/sysconfig/eth-pci-order"):
                return intf_map    
    if mac_list:
        interface_list = os.listdir("/sys/class/net/")
        for i, mac in enumerate(mac_list):
            for j, intf in enumerate(interface_list):
                if "bond" in intf: continue
                with open("/sys/class/net/{intf}/address".format(intf=intf)) as fd:
                    new_name = "eth{i}".format(i=i)
                    if mac in fd.read() and intf != new_name:
                        tmp_name=None
                        if new_name in interface_list:
                            tmp_name = "{intf}_9".format(intf=intf)
                            cmd="ip link set dev {new} down; ip link set dev {new} name {old}; ip link set dev {old} up".format(old=tmp_name, new=new_name)
                            logging.info(cmd)
                            os.system(cmd)
                            interface_list[interface_list.index(new_name)] = "{intf}_9".format(intf=intf)
 
                        cmd = "ip link set dev {old} down; ip link set dev {old} name {new}; ip link set dev {new} up".format(old=intf, new=new_name)
                        logging.info(cmd)
                        os.system(cmd)
                        interface_list[interface_list.index(intf)] = new_name
    else:
        # if mac_list is None, re-name the interfacs base on the original order. the prefix of the new name should be "eth"
        intfs_dict = {}
        sysdir='/sys/class/net/'
        intfs = os.listdir(sysdir)
        for intf in intfs:
            if os.path.islink(sysdir + intf):
                link = os.readlink(sysdir + intf)
                if '0000:' in link:
                    info = link.split('/')
                    for item in info:
                        if re.search('\d+:\d+:\d+\.\d+',item):
                            intfs_dict[item] = info[-1]
                            break
        pci_sorted = sorted(intfs_dict)
        with open("/etc/sysconfig/eth-pci-order", "w") as fd:
            new_index = 0
            for pci in pci_sorted:
                old = intfs_dict.get(pci)
                new = 'eth'+str(new_index)
                cmd ="ip link set dev {old} down; ip link set dev {old} name {new}; ip link set dev {new} up".format(old=old, new=new)
                #logging.info(cmd)
                os.system(cmd)
                new_index += 1
                fd.write(pci + "\n")
            intf_map = range(len(pci_sorted))            
        return intf_map
                
   # generate eth-pci-order file
    logging.info("generate eth-pci-order file.")
    new_interface_list = os.listdir("/sys/class/net/")
    with open("/etc/sysconfig/eth-pci-order", "w") as fd:
        for i in range(len(mac_list)):
            out = commands.getoutput("ethtool -i eth{i}".format(i=i))
            pci_info = re.search(r"bus-info:\s+(.*)", out).group(1).replace("0000:","")
            fd.write(pci_info + "\n")

    logging.info("sort interfaces done.")
    return intf_map


def set_dns():
    global dns_info
    content = ""
    # with open(r"resolv.conf") as fd:
    if os.path.exists(r"/etc/resolv.conf.default"):
        fd = open(r"/etc/resolv.conf.default")
        logging.info("/etc/resolv.conf.default")
    else:
        fd = open(r"/etc/resolv.conf")
        logging.info("/etc/resolv.conf")
    for line in fd:
        logging.info("\t %s", line)
        tmp = line.split()
        if "nameserver" in line and len(tmp) == 2:
            dns_info["server"].add(tmp[1])
        elif "search" in line:
            dns_info["search"].add(line)
    fd.close()
    for entry in dns_info["search"]:
        content += entry
    for entry in dns_info["server"]:
        if entry:
            content += "nameserver {entry}\n".format(entry=entry)

    logging.info("DNS resolv.conf:\n%s", content)
    if os.path.exists(r"/etc/resolv.conf.default"):
        os.remove(r"/etc/resolv.conf.default")
    if not DEBUG:
        with open(r"/etc/resolv.conf", "w") as fd:
            fd.write(content)


def merge_xml():
    logging.info("start merge_xml")
    sys_xml_path = "/opt/confd/confdInstallDir/var/confd/cdb"
    xml_dir = "/tmp/merge_dir"
    try:
        import shutil
        systemxml = glob.glob("/usr/*/current/config/xml/*_system_static_config_data.xml")[0]
        if not vnf_type:
            logging.info("no vnf_type defined")
            shutil.copy(systemxml, sys_xml_path)
            return
        # The operation will be done in the temporary directory.
        _vnfxml = glob.glob("/usr/*/current/config/xml/vnfType_{0}_*.xml".format(vnf_type))
        _vnfcommonxml = glob.glob("/usr/*/current/config/xml/vnfType_common_*.xml")
        bn_sysxml = os.path.basename(systemxml)
        product = bn_sysxml.replace("_system_static_config_data.xml", "")
        if os.path.exists(xml_dir):
            shutil.rmtree(xml_dir)
        os.mkdir(xml_dir)
        shutil.copy(systemxml, xml_dir)
        if _vnfcommonxml:
            vnfcommonxml = _vnfcommonxml[0]
            shutil.copy(vnfcommonxml, xml_dir)
        if _vnfxml:
            vnfxml = _vnfxml[0]
            shutil.copy(vnfxml, xml_dir)
        # invoke merge script to generate new system xml
        cmd = '''python {0}/confd_xml_merge.py {1} {2} {3}/{4} {5} {6} "" "" "" "" True'''.format(script_dir, yang_files_dir, xml_dir, sys_xml_path, bn_sysxml, product, vnf_type.upper())
        logging.info("merge xml:\n\tvnf_type: %s\n\tcmd: %s", vnf_type, cmd)
        status, out = commands.getstatusoutput(cmd)
        if status != 0:
            handleErr("execute confd_xml_merge.py failed: {0}".format(out))
    except Exception as e:
        handleErr(str(e))
    finally:
        if os.path.exists(xml_dir):
            shutil.rmtree(xml_dir)

def set_sssd():
    LDAP_SERVERS = sssd_dict.get('LDAP_SERVERS')
    USER_GROUPS = sssd_dict.get('USER_GROUPS')
    LDAP_BASEDN = sssd_dict.get('LDAP_BASEDN')
    LDAP_USER_SEARCH_BASE = sssd_dict.get('LDAP_USER_SEARCH_BASE')
    LDAP_BIND_DN = sssd_dict.get('LDAP_BIND_DN')
    LDAP_GROUP_SEARCH_BASE = sssd_dict.get('LDAP_GROUP_SEARCH_BASE')
    LDAP_AUTHTOK = sssd_dict.get('LDAP_AUTHTOK')
    CA_CERT = sssd_dict.get('CA_CERT')

    if LDAP_SERVERS and LDAP_BASEDN and LDAP_USER_SEARCH_BASE and LDAP_BIND_DN and LDAP_GROUP_SEARCH_BASE and LDAP_AUTHTOK and USER_GROUPS:
        # do sth
        logging.info("Will set SSSD.")
        logging.info('LDAP_SERVERS: %s', LDAP_SERVERS)
        logging.info('USER_GROUPS: %s', USER_GROUPS)   
        logging.info('LDAP_BASEDN: %s', LDAP_BASEDN) 
        logging.info('LDAP_USER_SEARCH_BASE: %s', LDAP_USER_SEARCH_BASE)
        logging.info('LDAP_BIND_DN: %s', LDAP_BIND_DN)
        logging.info('LDAP_GROUP_SEARCH_BASE: %s', LDAP_GROUP_SEARCH_BASE)
        logging.info('LDAP_AUTHTOK: %s', LDAP_AUTHTOK)
        logging.info('CA_CERT: %s', CA_CERT)
        if CA_CERT:
            open('/etc/pki/ca-trust/source/anchors/CACert.pem', 'w').write(CA_CERT)
            commands.getstatusoutput('update-ca-trust extract')
        # update conf file
        conf_file = "/etc/sssd/sssd.conf"
        content = '''[domain/default]
id_provider = ldap
auth_provider = ldap
chpass_provider = ldap
cache_credentials = False
ldap_search_base = {0}
ldap_group_search_base = {1}
ldap_group_member = uniqueMember
ldap_user_search_base = {2}
access_provider = simple
simple_allow_groups = {3}
ldap_uri = {4}
ldap_id_use_start_tls = False
ldap_schema = rfc2307bis
ldap_tls_reqcert = allow
ldap_tls_cacertdir = /etc/ssl/certs
ldap_tls_cacert = /etc/ssl/certs/ca-bundle.crt
ldap_default_bind_dn = {5}
ldap_default_authtok_type = password
ldap_default_authtok = {6}

krb5_realm = #

[sssd]
domains = default
services = nss, pam
config_file_version = 2
reconnection_retries = 3
sbus_timeout = 30

[nss]
homedir_substring = /home
default_shell = /bin/bash

[pam]

[sudo]

[autofs]

[ssh]

[pac]

[ifp]
'''.format(LDAP_BASEDN, LDAP_GROUP_SEARCH_BASE, LDAP_USER_SEARCH_BASE, ','.join([ x.split(',')[0] for x in USER_GROUPS]), ','.join(LDAP_SERVERS), LDAP_BIND_DN, LDAP_AUTHTOK)
        open(conf_file, 'w').write(content)
        os.chmod(conf_file, 0600)
        commands.getstatusoutput('systemctl restart sssd')
        # add USER_GROUPS
        for g in USER_GROUPS:
            gname, gid, role = g.split(',')
            #logging.info(commands.getstatusoutput('groupadd -g{0} {1}'.format(gid, gname)))
            if 'admin' == role:
                cmd = "sed -i '/^[[:blank:]]*%{0} /d' /etc/sudoers".format(gname)
                commands.getstatusoutput(cmd)
                cmd = "echo '%{0} ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers".format(gname)
                commands.getstatusoutput(cmd)
                cmd = "sed -i '/^[[:blank:]]*snradmin /d' /etc/sudoers"
                commands.getstatusoutput(cmd)
                cmd = "echo 'snradmin ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers"
                commands.getstatusoutput(cmd)
        # update ssh
        commands.getstatusoutput("sed -i 's/^[#]*[[:blank:]]*PasswordAuthentication[[:blank:]]\+no/PasswordAuthentication yes/' /etc/ssh/sshd_config")
        commands.getstatusoutput("sed -i 's/^[#]*[[:blank:]]*PermitRootLogin[[:blank:]]\+yes/PermitRootLogin no/' /etc/ssh/sshd_config")
        commands.getstatusoutput("sed -i 's/^[#]*[[:blank:]]*UseDNS[[:blank:]]\+yes/UseDNS no/' /etc/ssh/sshd_config")
        commands.getstatusoutput("sed -i '/^[[:blank:]]*ciphers[[:blank:]]\+/d' /etc/ssh/sshd_config")
        commands.getstatusoutput("echo 'ciphers blowfish-cbc,aes256-cbc,aes256-ctr' >> /etc/ssh/sshd_config")
        commands.getstatusoutput("service sshd restart")
        cmd = "authconfig --enablesssd --enablesssdauth --enablelocauthorize --enableldap --enableldapauth --ldapserver={0} --disableldaptls --ldapbasedn='{1}' --enablerfc2307bis --enablemkhomedir --update".format(','.join(LDAP_SERVERS), LDAP_BASEDN)
        logging.info(cmd)
        logging.info(commands.getstatusoutput(cmd))
        # update confd.conf
        try:
            root = dom.parse('/opt/confd/confdInstallDir/etc/confd/confd.conf')
            aaa = root.getElementsByTagName('aaa')[0]
            lc_auth = aaa.getElementsByTagName('localAuthentication')[0]
            lc_auth_enabled = lc_auth.getElementsByTagName('enabled')[0]
            lc_auth_enabled.firstChild.data = 'false'
            with open('/opt/confd/confdInstallDir/etc/confd/confd.conf', 'w') as fd:
                root.writexml(fd, indent='',addindent='',newl='',encoding='UTF-8')
            #  All users and groups shall be removed from the aaa_init.xml file
            root = dom.parse('/opt/confd/confdInstallDir/var/confd/cdb/aaa_init.xml')
            auth = root.getElementsByTagName('authentication')[0]
            auth.childNodes = []
            with open('/opt/confd/confdInstallDir/var/confd/cdb/aaa_init.xml', 'w') as fd:
                root.writexml(fd, indent='',addindent='',newl='',encoding='UTF-8')
            
        except:
            pass
    else:
        return

            
# --- Main ----
if __name__ == '__main__':
    DEBUG = False
    namespace = None
    ssh_dir = r"/home/admin/.ssh/"
    ldap_conf_ldap = "/etc/openldap/ldap.conf"
    ldap_conf_nslcd = "/etc/nslcd.conf"
    platform = sys.argv[1]
    tmp_cmd_file = r"/tmp/tem_runcmd.sh"
    # print current version
    logging.info("current file: %s", __file__)
    script_dir = os.path.dirname(__file__)
    with open(sys.argv[0]) as fd:
        for line in fd:
            if "Version" in line:
                logging.info("%s\n\n", line)
                break

    if DEBUG:
        ##  ###### start: just for DEBUG ####
        if platform == "vmware":
            out_xml = sys.argv[2]
            dev_info_data = sys.argv[3]
        else:
            user_data_file = sys.argv[2]
            network_data_file = sys.argv[3]
            meta_data_file = sys.argv[4]
            dev_info_data = sys.argv[5]
        c_dev_info = None
        if dev_info_data:
            with open(dev_info_data) as fd:
                c_dev_info = json.load(fd)
        yang_files_dir = "yang/"
        net_xml_file = r"static_net.xml"
        conf_dir = "./"
        ssh_dir = "./"
        ldap_conf_ldap = "ldap.conf"
        ldap_conf_nslcd = "nslcd.conf"
    ############ DEBUG end ############
    else:
        yang_files_dir = sys.argv[2]
        if len(sys.argv) > 3:
          no_confd = sys.argv[3]
        else:
          no_confd = False
        #valid_no_confd_args="--noconfd"
        #if no_confd in valid_no_confd_args:
        if no_confd == "--noconfd":
          no_confd = True
        else:
          no_confd = False
        net_xml_file = r"/opt/confd/confdInstallDir/var/confd/cdb/static_net.xml"
        conf_dir = r"/etc/sysconfig/network-scripts"
        user_data_file = r"/mnt/config/openstack/latest/user_data"
        network_data_file = r"/mnt/config/openstack/latest/network_data.json"
        meta_data_file = r"/mnt/config/openstack/latest/meta_data.json"

    logging.info("Start cfginit")

    #user_data_file = r"/root/user_data"
    #network_data_file = r"/root/network_data.json"
    # get the vcloud_info

    logging.info("Will sort interfaces.")
    if platform == "vmware":
        env = parse_ovfEnv()
        intf_map = sort_interfaces(env)
        logging.info("new-old interface map: %s\n", intf_map)
        vcloud_info = vm_fill_vcloud_info(env, intf_map,  _map, )
        logging.info("vcloud_info: \n {data}".format(data=json.dumps(vcloud_info, indent=1)))
        traffic_info = vm_fill_traffic_info(env)
        logging.info("traffic_info: \n {data}".format(data=json.dumps(traffic_info, indent=1)))
    else:
        intf_map = sort_interfaces()
        vcloud_info = op_fill_vcloud_info(intf_map)
        logging.info("vcloud_info: \n {data}".format(data=json.dumps(vcloud_info, indent=1)))
        traffic_info = op_fill_traffic_info()
        logging.info("traffic_info: \n {data}".format(data=json.dumps(traffic_info, indent=1)))
    logging.info("doorbell_info: \n {data}".format(data=json.dumps(doorbell_info, indent=1)))
    logging.info("Will fill data to 'intf_data'")
    intf_data = fill_intf_data(vcloud_info, traffic_info)
    logging.info("intf_data : \n %s", intf_data)
    logging.info("Will generate ifcfg files")
    generate_ifcfg(conf_dir, intf_data)
    print("no_confd");
    if not no_confd:

      out = commands.getoutput("cat {dir}/*-platform.yang|grep belongs-to".format(dir=yang_files_dir)).split()
      if len(out) >= 2:
        file_name_prefix = out[1]
        namespace = get_namespace("{dir}/{file}*.yang".format(dir=yang_files_dir, file=file_name_prefix))
        logging.info("Master Yang file prefix: %s, namespace: %s", file_name_prefix, namespace)
      if not namespace:
        handleErr("Can't get the valid namespace from yangFiles")

      merge_xml()
      logging.info("Will generate XML file")
      generate_network_xml(namespace, net_xml_file, intf_data)
    logging.info("Will set DNS")
    set_dns()
    set_ssh()
    set_https(namespace)
    set_ldap()
    set_sssd()
    # run commands
    try:
        fp = open(tmp_cmd_file) 
        logging.info("runcmd: /tmp/tem_runcmd.sh") 
        os.system(r"/bin/bash " + tmp_cmd_file)
        os.unlink(tmp_cmd_file)
        fp.close()
    except:
        logging.info("no runcmd")
    logging.info("Done!")
    exit(0)
