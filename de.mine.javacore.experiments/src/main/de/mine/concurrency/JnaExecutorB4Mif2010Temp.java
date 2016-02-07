package de.mine.concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class JnaExecutorB4Mif2010Temp {
	
	public static void main(String[] args) {
		
		for(int i=0; i<4; i++){
			startTimeoutTaskThread();
		}
		
	}
	
	private static final void startLongTaskAsync(){
		Future<String> f = SingleThreadQueueTimeoutExecutor.executeAndReturn(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(1000);
				System.out.println("OK "+SingleThreadQueueTimeoutExecutor.blockingQueue);
				return "OK";
			}
		});
	}
	
	private static final void startLongTaskSync(){
		SingleThreadQueueTimeoutExecutor.executeAndWaitSafely(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(1000);
				System.out.println("OK "+SingleThreadQueueTimeoutExecutor.blockingQueue);
				return "OK";
			}
		});
	}
	
	private static final void startLongTaskThread(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				startLongTaskSync();
				System.out.println("Thread passed the call");
			}
		}).start();
	}
	
	
	private static final void startTimeoutTask(){
		SingleThreadQueueTimeoutExecutor.executeAndWaitSafely(new Callable<String>() {
			@Override
			public String call() throws Exception {
				Thread.sleep(4000);
				System.out.println("OK "+SingleThreadQueueTimeoutExecutor.blockingQueue);
				return "OK";
			}
		});
	}
	
	private static final void startTimeoutTaskThread(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				startTimeoutTask();
			}
		}).start();
	}
}
