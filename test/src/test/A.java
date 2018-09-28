package test;

class AA {
	
	private int k =10;
	
	public AA ()
	{
		this.k =20;
	}
	
	public void Disp(String a , String b) {
		
		System.out.println(a+b+"KK");
	}
	
	public String toString() {
		
		return(k+"toStingOverride");
	}
}
public class A {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
			AA obj = new AA();
			
			System.out.println(obj);
		}
	

}
