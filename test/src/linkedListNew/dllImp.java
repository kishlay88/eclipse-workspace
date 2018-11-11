package linkedListNew;

public class dllImp {

	dllNode head;
	
	public void insertdLL(int i) {
		
		dllNode node = new dllNode();
		node.data = i;
		
		dllNode temp = head;
		
		if (head == null)
		{
			head=node;
			node.next=null;
			node.previous=null;
		}
		else
		{
			while(temp.next!=null)
			{
				temp=temp.next;
				
			}
			
			node.previous=temp;
			temp.next=node;
			node.next=null;
			
		}
		
	}
	
	public void displayDll() {
		
		dllNode node = null;
		
		node = head;
		
		while(node!=null) {
			
			System.out.println(node.data);
			node=node.next;
		}
	}
	
	public void insertinsortedList(int i) {
		
		dllNode node= new dllNode();
		node.data = i;
		dllNode prev = null;
		dllNode curr = head;
		
		while(curr!=null && curr.data<i) {
			prev = curr;
			curr=curr.next;
		}
		
		node.previous = prev;
		node.next=curr;
		curr.previous=node;
		prev.next=node;
		
	}
	
	public void dllreversal() {
		
		dllNode prev = null;
		dllNode next = null;
		dllNode curr = head;
		
		
		while (curr !=null) {
			
			next=curr.next;
			curr.previous=curr.next;
			curr.next = prev;
			prev=curr;
			curr=next;
			head=prev;
			
		}
				
	}
	
}
