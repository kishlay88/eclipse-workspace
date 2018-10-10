package test;

class Single extends Thread{
	
	private static final Single single = new Single();
	
	private Single() {
		
	}
	
	public static Single getInstance() {
		
		return single;
	}

}

public class SingleTonS {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		
		
		Thread t1 = new Thread("t1") { 
									public void run() {
										System.out.println(currentThread().getName());
									}
		};
		
		
		t1.start();
		
		t1.sleep(30);
		
		t1.join();
		
		
		
		
	}
}
	
