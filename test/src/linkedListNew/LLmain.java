package linkedListNew;

public class LLmain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LinkedListImp ll = new LinkedListImp();
		
		ll.insert(10);
		ll.insert(10);
		ll.insert(10);
		ll.insert(40);
		ll.insert(40);
		ll.insert(50);
		ll.insert(60);
		ll.insert(70);
		
		System.out.println("Linked List after creation= ");
		ll.printLL();
		ll.removeDuplicates();
		System.out.println("Linked List  after Removing Duplicates = ");
		ll.printLL();
		ll.deleteN(10);
		System.out.println("Linked List  after deletion = ");
		ll.printLL();
		/*
		ll.reverseList();
		System.out.println("Reversed Linked List =");
		ll.printLL();
		ll.InsertInSortedList(12);
		ll.InsertInSortedList(0);
		System.out.println("Insertion in Sorted Linked List");
		ll.printLL();
*/	}

}
