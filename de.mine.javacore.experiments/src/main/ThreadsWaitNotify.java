
public class ThreadsWaitNotify {

	public static final Object LOCK  = "LOCK";
	
	static ThreadA a;
	static ThreadB b;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		a = new ThreadA();
		b = new ThreadB();
		
		a.start();
		b.start();
	}
	
	static class ThreadA extends Thread {
		@Override
		public void run() {
			for(int i=0; i<12; i++){
				System.out.println("ThreadA");
				sleepSec(5, this);
			}
		}
	}
	
	static class ThreadB extends Thread {
		@Override
		public void run() {
			for(int i=0; i<12; i++){
				System.out.println("ThreadB");
				synchronized (LOCK) {
					LOCK.notify();	
				}
				sleepSec(1, this);
			}
		}
	}
	
	
	public static void sleepSec(Integer sec, Thread t){
		try {
			synchronized (LOCK) {
				LOCK.wait(sec*1000);	
			}
		} catch (InterruptedException e) {
			System.out.println("Interrupted");
		}
	}

}
