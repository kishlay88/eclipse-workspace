package LinkedList;

public class LinkedList {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		newlist newl = new newlist();
	
		newl.push(1);
		newl.push(2);
		
		newl.disp_list();	
		
		newl.append(3);
		
		newl.disp_list();
		
		newl.push(4);
		
		newl.disp_list();
		
		newl.append(5);
		
		newl.disp_list();
		
		newl.append_after(2, 22);
		
		newl.disp_list();
		
		System.out.println("\nDel_node_indexwise");
		
		newl.Del_node_indexwise(1);
		
		newl.disp_list();
		
		System.out.println("\nDel_node_keywise");
		
		newl.Del_node_keywise(22);
		
		System.out.println();
		newl.disp_list();
		
		newl.Lcount();
		newl.DLinkedList();
	}

}

