package linkedListNew;

public class dllMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		dllImp dImp = new dllImp();
		
		dImp.insertdLL(5);
		dImp.insertdLL(50);
		dImp.insertdLL(500);
		
		System.out.println("Doubly LL after Insertion =");
		dImp.displayDll();
		
		dImp.insertinsortedList(55);
		
		System.out.println("Doubly LL after Insertion in sorted list =");
		dImp.displayDll();
		dImp.dllreversal();
		System.out.println("Doubly LL after reversal in sorted list =");
		dImp.displayDll();
		
	}

}
