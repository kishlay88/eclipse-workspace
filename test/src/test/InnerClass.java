package test;

class Animals {
	
	class B {
		
		class C{
			
			public void m() {
				System.out.println("[Class C] Inner");
			}
		}
	}
}


public class InnerClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
		Animals animals = new Animals();
		Animals.B b = animals.new B();
		Animals.B.C c = b.new C();
		c.m(); //Way 1
		
		
		new Animals().new B().new C().m(); //Way 2 
	}

}
