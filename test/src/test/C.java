package test;

import java.lang.Cloneable;
class SingleTon{
	
	private static SingleTon Singleton = null;
	
	public static SingleTon getSingleton() {
		
		if (Singleton == null)
			Singleton = new SingleTon();
		return Singleton;
	}	

	private SingleTon ()
	{
		System.out.println("SingleTon object Created");
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}

public class C {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			
		SingleTon aSingleTon = SingleTon.getSingleton();
		SingleTon aSingleTon1 = SingleTon.getSingleton();
		
		SingleTon aSingleTon2 = (SingleTon) aSingleTon1.clone();
		
		
		if (aSingleTon == aSingleTon1)
			System.out.println("Same");
		else
			System.out.println("different");
		
		}
		
		catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
		}
	}

}
