package test;

interface kk{
	
	public  void ABCD();
}


public class AbstractClassEx implements kk {

	int a  = 10;
	static final int b =20;
	
	public static class d{
		
		AbstractClassEx aEx = new AbstractClassEx();
		
		
	}
	
	public static void main(String[] args) {

		AbstractClassEx aEx = new AbstractClassEx();
		System.out.println(aEx.a + " " + aEx.b);
		
	
	}

	@Override
	public void ABCD() {
		// TODO Auto-generated method stub
		
	}
	
}
