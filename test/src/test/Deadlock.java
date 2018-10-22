package test;

import LinkedList.newlist;

class AAA {

	public static final AAA aa = new AAA(); 

	public synchronized void m1(BBB kB) throws InterruptedException {

		Thread.sleep(2000);

		kB.last();
	}

	public synchronized void last() {
		System.out.println("I am in A");
	}

	public static AAA getobj() {
		if (aa==null) {
			//aa=new AAA();
			return aa;
		}
		else
			return aa;
	}
}

class BBB {

	public static BBB bb = new BBB();

	public synchronized void m2(AAA kA) throws InterruptedException {

		Thread.sleep(2000);
		kA.last();
	}

	public synchronized void last() {

		System.out.println("I am in B");
	}

	public static BBB getobj() {
		if(bb==null) {
			//bb=new BBB();
			return bb;
		}
		else
			return bb;
	}

}

class MyThread1 extends Thread {

		int i = 0;

		AAA aa = AAA.getobj();
		BBB bb = BBB.getobj();

		public void run() {

			try {
				bb.m2(aa);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

public class Deadlock {

	public static void main(String[] args) throws InterruptedException {
		
		AAA a = AAA.getobj();
		BBB b = BBB.getobj();
		
		MyThread1 myThread = new MyThread1();
		
		myThread.start();
		a.m1(b);
				
	}

	
}
