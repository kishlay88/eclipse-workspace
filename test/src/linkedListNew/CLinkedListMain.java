package linkedListNew;

public class CLinkedListMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CircularLLusingSingleLL cImp = new CircularLLusingSingleLL();
		
		cImp.insert(1);
		cImp.insert(2);
		cImp.insert(3);
		
		cImp.displayCircularLL();
		cImp.detectLoop();
		
		
	}

}
