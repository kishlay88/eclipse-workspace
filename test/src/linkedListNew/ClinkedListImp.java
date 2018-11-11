package linkedListNew;

public class ClinkedListImp {

	Node last;
	Node head;
	
	public void insert(int i) {
		
		Node node = new Node();
		node.data = i;
		
		if (head==null) {
			head = node;
			node.next=head;
			last=head;
		}
		else {
			last.next = node;
			last=last.next;
			node.next=head;
		}
	}
	
	public void dispClinkList() {
		
		Node node = head;
		while(node != last) {
			System.out.println(node.data);
			node=node.next;
		}
		
	}
}
