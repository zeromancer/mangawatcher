package test.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SingleExecuter {

	public static class MyRunnable implements Runnable {
		private final long countUntil;

		MyRunnable(long countUntil) {
			this.countUntil = countUntil;
		}

		@Override
		public void run() {
			long sum = 0;
			for (long i = 1; i < countUntil; i++) {
				sum += i;
			}
			try {
				Thread.sleep((int)(Math.random()*100));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(sum);
		}
	}

	private static final int NTHREDS = 10;

	public static void main(String[] args) {
//		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		for (int i = 0; i < 500; i++) {
			Runnable worker = new MyRunnable(10000000L + i);
			executor.execute(worker);
		}
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		try {
			executor.awaitTermination(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished all threads");
	}

}
