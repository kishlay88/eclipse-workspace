package test;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;

class MyThread extends Thread {

	static int k = 0;
	
	
	static class Inner extends Thread {
		
		static int ii = 0;
		public void run() {
			
			synchronized (this.getClass()) {
				
				
				System.out.println("Sub-Thread of  thread " + ii++ );
			}
		}
	}
	
	public void run() {

		synchronized (this.getClass()) {

			System.out.println("Child Thread entry " + k++);
			Inner inner = new Inner();
			
			inner.start();
		}

	}
}	

public class TestThread {

	public static void main(String... args) throws InterruptedException {

		// MyThread [] myThread = new MyThread [10];

		int counter = 0;

		ArrayList<MyThread> myThread = new ArrayList<MyThread>();

		while (counter < 10) {
			myThread.add(new MyThread());
			counter++;
		}

		Iterator<MyThread> iterator = myThread.iterator();

		while (iterator.hasNext()) {
			MyThread myThread2 = (MyThread) iterator.next();
			myThread2.start();

		}

	}
}