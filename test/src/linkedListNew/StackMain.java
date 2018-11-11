package linkedListNew;

public class StackMain {

	public static void main(String... args) {

		StackImp sImp = new StackImp();
		sImp.push(10);
		sImp.push(20);
		sImp.push(30);
		
		System.out.println("Stack Created = ");
		sImp.displStack();
		
		System.out.println("Stack after popping element = " + sImp.pop() +":");
		sImp.displStack();
		
		
		sImp.push(50);
		System.out.println("Stack after pushing =");
		sImp.displStack();
		System.out.println("Stack after popping element1 = " + sImp.pop() +":");
		sImp.displStack();
		
		System.out.println("Stack after popping element2 = " + sImp.pop() +":");
		sImp.displStack();
		System.out.println("Stack after popping element3 = " + sImp.pop() +":");
		sImp.displStack();
		System.out.println("Stack after popping element4 = " + sImp.pop() +":");
		sImp.displStack();
		System.out.println("Stack after popping  = " + sImp.pop() +":");
		sImp.displStack();
		
		
		

	}
}
