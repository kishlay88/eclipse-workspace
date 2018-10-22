package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class ABCD {

	ReentrantLock r;

	public ABCD(ReentrantLock rr) {
		r = rr;
	}

	public void m() throws InterruptedException {

		r.lock();
			int i;
			for (i = 1; i <= 10; i++) {
				System.out.println("[Child] Working...");
			}
	
			System.out.println("[Child] Got Notified from Main...");
	
			Thread.sleep(2000);
	
			for (i = 0; i < 10; i++) {
				System.out.println("[Child] Work Resume...");
			}
		r.unlock();
	}

}

class MyThread2 extends Thread {

	ABCD abcd;
	ReentrantLock RRR;

	public MyThread2(ABCD aaa, ReentrantLock rrr) {
		// TODO Auto-generated constructor stub

		abcd = aaa;
		RRR = rrr;

	}

	public void run() {

		try {
			currentThread();
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		RRR.lock();
			try {
				abcd.m();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		RRR.unlock();
	}
}

public class WaitNotifyNotifyAll {

	public static void main(String[] args) throws InterruptedException {

		ReentrantLock rLock = new ReentrantLock();

		ABCD aa = new ABCD(rLock);

		MyThread2 myThread = new MyThread2(aa, rLock);

		myThread.start();

		for (int ii = 0; ii < 50; ii++) {
			System.out.println("[Main] Working...");
		}

		rLock.lock();
			Thread.sleep(1000);
		rLock.unlock();
		
		myThread.join();
		
		MyThread2 myThread1 = new MyThread2(aa, rLock);
		myThread1.start();

		for (int ii = 0; ii < 50; ii++) {
			System.out.println("[Main] Working...");
		}

		rLock.lock();
			Thread.sleep(1000);
		rLock.unlock();
		
		// }

	}

}
