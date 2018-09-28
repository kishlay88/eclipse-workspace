package test;

class BB {
	
	static int a = 10;
	
}

public class B {

	public static void main(String[] args) throws ArithmeticException {
		// TODO Auto-generated method stub

			//System.out.print(BB.a);
			
			try {
				
				int t = (BB.a)/0;
			}
			
			
			catch (Exception e) {
				// TODO: handle exception
				//System.out.println("Devide by Zero");
				System.out.println(e.getMessage());
			}
			
			finally {
				System.out.println("Good Bye !!!");
			}
	}

}
