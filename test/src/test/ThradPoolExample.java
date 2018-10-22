package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class Execute1 {

	static int i = 0;

	public void print1(String na) throws InterruptedException {

		for (int i = 0; i <= 5; i++) {
			System.out.println("[" + na + "]" + "Value of i =" + i);
			Thread.sleep(200);
		}

	}
}

class MyThreadEx implements Runnable {

	String nn = null;
	Execute1 execute1 = new Execute1();

	public MyThreadEx(String n) {
		// TODO Auto-generated constructor stub
		nn = n;
	}

	public void run() {

		ReentrantLock lock = new ReentrantLock();

		ThreadLocal tLocal = new ThreadLocal();
		tLocal.set(Thread.currentThread().getName());
		System.out.println(tLocal.get());

			try {
				execute1.print1(nn);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}


public class ThradPoolExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MyThreadEx[] mExs = { new MyThreadEx("Kishlay"), new MyThreadEx("Sweta"), new MyThreadEx("Puttu"),
				new MyThreadEx("Swati") };

		//ExecutorService e = Executors.newFixedThreadPool(10);
		ExecutorService e = Executors.newSingleThreadExecutor();
		
		for (MyThreadEx m : mExs)
			e.submit(m);

		e.shutdown();
	}

}
