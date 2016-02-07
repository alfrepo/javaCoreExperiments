package de.mine.concurrency;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Encapsulates a single thread task executor with a fixed execution timeout and
 * an unbounded queue. Should be used to execute to synchronize Callables through a single thread
 * 
 * @author alf
 *
 */
public final class SingleThreadQueueTimeoutExecutor {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingleThreadQueueTimeoutExecutor.class);

	private static final long SINGLE_TASK_TIMEOUT_SEC = 2; // timeout for each single Task
	private static final long IDLE_THREAD_KEEP_ALIVE_SEC = 60l;
	

	private static final String MESSAGE_EXEC_FAILED = "Failed to execute the task on the "+SingleThreadQueueTimeoutExecutor.class.getSimpleName();

	public static final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>();

	// ThreadPool executor with unbounded queue is never rejeted!
	private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 1, IDLE_THREAD_KEEP_ALIVE_SEC, TimeUnit.SECONDS, blockingQueue);

	// ThreadPool which will monitor the Tasks on the execution Thread and enforce Task-timeouts
	private static final ScheduledExecutorService executorTimeoutMonitor = Executors.newScheduledThreadPool(1);
	
	
	/**
	 * Async execution of a callable on the Single thread
	 * @param callable
	 * @return
	 */
	public static final <T> Future<T> executeAndReturn(final Callable<T> callable) {
		RunnableWhichCancelsFuture callbackOnTimeout = new RunnableWhichCancelsFuture(callable);
		Callable<T> callableWithTimeout = wrapWithTimeoutByMonitorThread(callable, callbackOnTimeout);
		Future<T> future = executor.submit(callableWithTimeout);
		callbackOnTimeout.future = future;
		return future;
	}
	

	/**
	 * Like {@link #executeAndWaitUnsafe(Callable)} but catches all exceptions and logs them
	 * @param callable
	 * @return
	 */
	public static final <T> T executeAndWaitSafely(Callable<T> callable) {
		try {
			return executeAndReturn(callable).get();
		} catch (InterruptedException | ExecutionException e) {
			LOG.error(MESSAGE_EXEC_FAILED, e);
			return null;
		}
	}
	
	/**
	 * Executes a command on the single thread. Does not catch exceptions
	 * @param callable - what to add to the JPA queue for execution
	 * @return the result of the callable, or null if an exception occurs
	 */
	public static final <T> T executeAndWaitUnsafe(Callable<T> callable) throws InterruptedException, ExecutionException {
		return executeAndReturn(callable).get();
	}
	
	/** Call, on application exit	 */
	public static void dispose(){
		executor.shutdownNow();
		executorTimeoutMonitor.shutdownNow();
	}
	

	// TOOLS

	private static final <T> Callable<T> wrapWithTimeoutByMonitorThread(final Callable<T> callable, final Runnable onTimeout) {
		return new Callable<T>() {
			@Override
			public T call() throws Exception {
				
				executorTimeoutMonitor.schedule(new Runnable() {
					@Override
					public void run() {
						onTimeout.run();
					}
				}, SINGLE_TASK_TIMEOUT_SEC, TimeUnit.SECONDS);
				
				LOG.debug("Executing the JPA command {}. Current command queue: "+blockingQueue);
				return callable.call();
			}
		};
	}
	
	
	/** Runnable, which cancels a future, if available */
	static class RunnableWhichCancelsFuture implements Runnable{
		Future<?> future;
		Callable<?> callable;
		
		RunnableWhichCancelsFuture(Callable<?> callable){
			this.callable = callable;
		}
		
		@Override
		public void run() {
			if(future!=null && !future.isDone() && !future.isCancelled()){
				LOG.error("Task Timeout after {} sec. Stopping the task {}", SINGLE_TASK_TIMEOUT_SEC, callable);
				future.cancel(true);
			}
		}
	}

}
