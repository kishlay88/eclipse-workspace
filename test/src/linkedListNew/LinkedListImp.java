package linkedListNew;

import java.util.Currency;

import org.omg.CORBA.IdentifierHelper;

public class LinkedListImp {

	Node head;

	public void insert( int i) {
		
		Node node = new Node();
		node.next=null;
		node.data=i;
		
		if(head==null)
			head=node;
		else
		{
			Node traverse = head;
			while(traverse.next != null)
				traverse=traverse.next;
			traverse.next=node;
			
		}	
	}
	
	public void printLL() {
		
		Node node = head;
		
		while(node!=null) {
			System.out.println(node.data);
			node=node.next;
		}
	}
	
	public void deleteN(int i) {
		
		Node node = head;
		Node temp = null;;
		
		if (head.data == i)
		{
			head= head.next;
			return;
		}
		
		while (node != null && node.data!=i) {
			
			temp = node;
			node=node.next;
			
		}
		
		if (node==null)
			return;
		
		temp.next = node.next;
		
		
		
		/*while(node!=null) {
			
			if ((node==head) &&(node.data==i)) {
				head=head.next;
				node=head;
			}
			
			if ((node.next!=null) && (node.next.data == i)) {
				node.next= node.next.next;
			}
			
			node=node.next;
		}*/
	
	}
	
	public void removeDuplicates() {
		
		Node node = head;
		while(node!=null && node.next!=null)
		{
			if (node.data == node.next.data) {
				node.next=node.next.next;
			}
			else
				node=node.next;
		}
		
	}
	
	public void reverseList() {
		
		Node current = head;
		Node previous = null;
		Node Next = null;
		
		while(current!=null) {
			
			Next=current.next;
			current.next=previous;
			previous=current;
			current=Next;
			
			head=previous;
			
		}
		
	}
	
	public void InsertInSortedList(int i) {
		
		
		Node node = new Node();
		Node previous = head;
		node.data = i;
		
		Node current = head;
		
		if (head.data > i)
		{
			node.next = head;
			head = node;
			return;
		}
		
		while(current!=null && current.data <= i)
		{
			
			previous = current;
			current=current.next;
			
		}
		
		node.next = current;
		previous.next = node;
		
	}
}






