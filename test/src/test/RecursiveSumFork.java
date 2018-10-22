package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


class pool {
	
	
}

class ThreadGen  implements Runnable{
	
	static int tcount=0;
	
	public static ArrayList<ThreadManaged> Eventhread = new ArrayList<ThreadManaged>();
	public static ArrayList<ThreadManaged> Oddthread = new ArrayList<ThreadManaged>();
	
	class ThreadManaged extends RecursiveAction{
		
		@Override
		protected void compute() {
			// TODO Auto-generated method stub
			
			//System.out.println(Eventhread);
			//System.out.println(Oddthread);
			
			Iterator<ThreadManaged> iterator1 = Eventhread.iterator();
			Iterator<ThreadManaged> iterator2 = Oddthread.iterator();
			
			while(iterator1.hasNext()) {
				//iterator1.next().fork();
				System.out.println(iterator1.next());
			}
		}
	}

	private ThreadManaged threadmanage = new ThreadManaged();
	
	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		tcount++;
		System.out.println(tcount);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Distribute();
		
		threadmanage.invoke();
		
	}
	
	public void Distribute() {
		
		if (tcount%2 ==0)
			Eventhread.add(threadmanage);
		else
			Oddthread.add(threadmanage);
		
	}
	
	
	
	
}

public class RecursiveSumFork {

	public static void main(String[] args) throws InterruptedException {
		
		//while(true) {
			
			final int thrdcnt = 5;
			
			int i = thrdcnt ;
			
			ExecutorService eService = Executors.newCachedThreadPool();
			ForkJoinPool forkJoinPool = new ForkJoinPool();
			ThreadGen [] threadG = new ThreadGen[thrdcnt];
			
			for(int k = 0 ; k < thrdcnt ; k++) {
				threadG[k]=new ThreadGen();
				eService.submit(threadG[k]);
				Thread.sleep(1000);
			}
			
			eService.shutdown();
			Thread.sleep(10000);
		//}
	}
}
