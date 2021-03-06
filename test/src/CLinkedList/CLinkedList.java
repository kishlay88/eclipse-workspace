package CLinkedList;

public class CLinkedList {

	Node head;
	
	public static int Count=1;
	
	class Node {
		
		public int data;
		Node next;
		
		public void Node() {
			this.data=0;
			this.next=null;
		}
		
		public Node(int a) {
			// TODO Auto-generated constructor stub
			this.data = a;
			this.next=null;
			System.out.println("Node " + Count + " created in the Linked List");
			Count++;
		} 
	}
	
	public void append(int d) {
		
		Node node = new Node(d);
		
		Node last = head ;
		
		if (head == null)
		{
			head=node;
			head.next = head;
			return;
		}
		
		while(last.next != head)
			last=last.next;
		
		last.next=node;
		last.next.next = head;
				
	}
	
	public void prntCLinkedList() {
		
		Node last = head ;
				
		while(last.next != head)
		{
			System.out.println("Linked List Elements are :  " +  last.data );
			last=last.next;
		}
		
		System.out.println("Linked List Elements are :  " +  last.data );
		
	}
	
	public int detectLoop() {
		
		Node sTart = head ;Node eND = head.next;
		
		int count = 1;
		
		while (sTart.next != head ) {
			while ( eND != sTart) {
				count++;
				eND=eND.next;
			}
			if (count>0) {
				return count;
			}
			else
				sTart=sTart.next;
			
		}
		
		return count;	
	}
	
	
	
	
	
	
	
	
	
	
	
}
