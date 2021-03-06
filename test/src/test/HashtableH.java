package test;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.security.auth.kerberos.KerberosKey;

import java.util.Set;

public class HashtableH {

	public static void main(String[] args)  {
		
		String string = "Kishlay how are you";
		String string1 = "Kishlay how are you";
		
		String string2 = new String("Kishlay").intern();
		
		System.out.println(string.hashCode());
		System.out.println(string1.hashCode());
		System.out.println(string2.hashCode());
		String [] strings = string.split(" ");

		Hashtable< Integer, String> hashtable = new Hashtable<Integer,String>();

		hashtable.put(1, "KK1");
		hashtable.put(2, "KK2");
		
		List list = new ArrayList();
		
		list = Arrays.asList(strings);
		
		System.out.println(list);
		Map<Integer, String > map = new HashMap<Integer,String>(hashtable);
		
		hashtable.putAll(map);
		
		// Method 1
		
		System.out.println("Method 1");
		Iterator<Map.Entry<Integer,String>> iterator = map.entrySet().iterator();
		
		while (iterator.hasNext())
			System.out.println(iterator.next());
			
		// Method 2
		
		System.out.println("Method 2");
		
		for(Map.Entry<Integer, String> entry : map.entrySet()  )
			System.out.println(entry.getKey() + "="  + entry.getValue());
		
		// Method 3
		
		System.out.println("Method 3");
		
		System.out.println(map);
		
		// Method 4
		
		System.out.println("Method 4");
		
		map.forEach((k,v) -> System.out.println(k + "=" + v));
			
		}
	}
	
		

	