package test;

class DD {
	
	private int a = 10;
	
	public int getA() {
		return a;
	}
	
	private int b =20 ;
	public int getB() {
		return b;
	}
	
}

class DDchild extends DD {
	
	private int a = 100;
	private int b = 200;
	private int c = 300;
	
	public int getA() {
		return a;
	}
	
	public int getB() {
		return b;
	}
	
	public int getC() {
		return c;
	}
	
	}


public class D {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
			DD dd = new DD();
			
			DD ddc = new DDchild();
			
			System.out.println(ddc.getA() + ddc.getB());
			
			
			//DDchild ddc = new DDchild();
			//System.out.println(ddc.getB());
			//StringBuffer ss = new StringBuffer(ddc.a);
			
			//System.out.println(ss.toString());
	}

}
