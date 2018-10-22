package test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

class acquireLock {

}

class bowerThread implements Runnable {

	// bowerThread bThread = new bowerThread();
	Thread t = null;
	String tname = null;

	ReentrantLock re;

	public bowerThread(ReentrantLock rent) {
		re = rent;
	}

	public Thread getThread() {

		t = new Thread(this);
		return t;

	}

	public String threadname() {
		return t.getName();
	}

	public void run() {

		try {
			
				while (true) {
					if (! re.tryLock(1000, TimeUnit.MILLISECONDS))
						System.out.println(Thread.currentThread().getName() + " : Failed to Acquire Lock...Trying Again !!!");
					else
					{
							System.out.println(Thread.currentThread().getName() + " : Acquired Lock");
			
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			
							re.unlock();
							break;
					}
				}

			}catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

public class Bower {

	public static void main(String[] args) throws InterruptedException {

		for (;;) {

			ReentrantLock ret = new ReentrantLock();
			bowerThread bThread1 = new bowerThread(ret);
			bowerThread bThread2 = new bowerThread(ret);
			bowerThread bThread3 = new bowerThread(ret);
			
			bThread1.getThread().start();
			System.out.println("Starting : " + bThread1.threadname());
			bThread2.getThread().start();
			System.out.println("Starting : " + bThread2.threadname());	
			bThread3.getThread().start();
			System.out.println("Starting : " + bThread3.threadname());
			
			bThread1.t.join();
			bThread2.t.join();
			bThread3.t.join();
			Thread.sleep(2000);

		}
	}

}
