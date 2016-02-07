package de.mine.java7stuff;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class ForkJoin {

	public static void main(String[] args) {
		
		ParallelSumComputer task = new ParallelSumComputer(0, 6000);
		Long summ = new ForkJoinPool().invoke(task);
		System.out.println("Summ: " + summ);
	}
	
	static class ParallelSumComputer extends RecursiveTask<Long>{

		private static final long serialVersionUID = 1145266272613225272L;
		long max = 1000;
		long startInclusive;
		long endInclusive;
		
		ParallelSumComputer(long startInclusive, long endInclusive){
			this.startInclusive = startInclusive;
			this.endInclusive = endInclusive;
		}
		
		@Override
		protected Long compute() {
			return computeForking(startInclusive, endInclusive);
		}
		
		private long computeForking(long start, long end){
			// QUIT: if dif < max - compute locally
			if(end - start < max){
				return computeSerially(start, end);
			}
			long startLeft = start;
			long endLeft = end - (end-start)/2;
			long startRight = endLeft + 1;
			long endRight = end;
			
			System.out.println(startLeft+","+endLeft);
			System.out.println(startRight+","+endRight);
			
			// compute left half on other thread
			ParallelSumComputer left = new ParallelSumComputer(startLeft, endLeft);
			left.fork(); // by calling "fork" the task is executed in the same pool
			
			// compute right half on this thread
			long rightResult = computeForking(startRight, endRight);
			
			// right job is done - wait for left
			Long leftResult = left.join();
			
			// summ up
			return leftResult + rightResult;
		}
		
		private long computeSerially(long startInclusive, long endInclusive){
			long result = LongStream.rangeClosed(startInclusive, endInclusive)
					.reduce(Long::sum).getAsLong();
			System.out.println(startInclusive+","+endInclusive + " summ: "+result);
			return result;
		}
		
	}

}
