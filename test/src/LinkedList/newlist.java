package LinkedList;


public class newlist {
	
	Node head;
	static int index = 1 ;
	
	public class Node {
		
		int data;
		Node next;
		
		public Node(int d) {
			// TODO Auto-generated constructor stub
			data = d;
			next = null;
			System.out.println("\nNode "+ index + " created");
			index++;
		}
	
	}
	
	public void push(int a) {
		
		Node nodeN = new Node(a);
		
		nodeN.next= head;
		head = nodeN;
		
	}
	
	public void disp_list() {
		
		Node tnode = head; 
	    while (tnode != null) 
	    { 
	        System.out.print(tnode.data+" "); 
	        tnode = tnode.next; 
	    }
	}
	
	public void append(int a) {
		
		Node nodeN = new Node(a);
		
		Node tnode = head; 
		
		nodeN.next=null;
		
	    while (tnode.next != null) 
	    { 
	       tnode = tnode.next; 
	    }
	    	
	    tnode.next=nodeN;
	}
	
	public void append_after(int IndeX , int d) {
		
		Node node = new Node(d);
		
		Node trgt = head;
		for(int i=1 ; i<IndeX; i++)
		{
			trgt=trgt.next;
		}
		
		node.next=trgt.next;
		trgt.next=node;
		
	}
	
	public void Del_node_indexwise(int IndeX) {
		
		Node trgt = head;
		
			try {
				for(int i=1 ; i<IndeX-1; i++)
				{
					trgt=trgt.next;
				}
				
				trgt.next=trgt.next.next;
				
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println("Index " + IndeX + " doesnt exist in the linkedlist");
			}
		
	}
	
	public void Del_node_keywise(int srch) {
		
		Node trgt = head;
		
		try {
			while(trgt.next != null) {
					
					if(trgt.next.data==srch) {
						trgt.next=trgt.next.next;
						return;
					}
					else
						trgt=trgt.next;
			}
						
		}
		catch (Exception e) {
			System.err.println("\nKey " + srch + " doesnt exist in the linkedlist");
		}
	}
	
	public void Lcount() {
		
		Node last=head;
		int length=0;
		
		while(last != null)
		{
			length++;
			last=last.next;
		}
		System.out.println("\nLength of Linked list is := " +  length);
	}
	
	public void DLinkedList() {
		
		head=null;
		System.out.println("\nLinked List deleted");
	}
}






