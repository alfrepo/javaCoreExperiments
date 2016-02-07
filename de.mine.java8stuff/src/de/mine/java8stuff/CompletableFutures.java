package de.mine.java8stuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompletableFutures {

	public static void main(String[] args) {
//		useFuturesOnExecutorSerices();
//		useCompletableFuturesOnOwnThread();
		
		
		final long startTime = System.currentTimeMillis();
		
		Stream<String> countries = Arrays.asList("USA", "England", "Turkey").stream();
		Stream<CompletableFuture<List<CompletableFuture<Picture>>>> futures = 
		countries
				.parallel()

				// ASYNC DELAYED: getPictures for countries
				// List<Country> -> List<future<List<Picture>>>
				.map( (String country) -> CompletableFuture.supplyAsync( ()->getStolenPicturesDelayed(country) ))
				.map( (CompletableFuture<List<Picture>> fu) ->  fu.thenApply(

						// List<Picture> -> List<Futures<Picture>> 
						(List<Picture> l) -> {
							List<CompletableFuture<Picture>> listFuturesFIlledPrice = l
									.stream()
									
									// Picture -> Futures<Picture>
									// echo result of future when ready
									.map((Picture picturePriceless) -> {
										CompletableFuture<Picture> picFutureWithPrice;
										
										// ASYNC DELAYED: get price
										picFutureWithPrice = CompletableFutures.getFutureFillingPriceInPictureDelayed(picturePriceless);
										picFutureWithPrice = picFutureWithPrice.thenApply(CompletableFutures::echoPictureWhenFutureReady); 
										return picFutureWithPrice;
										}
									)
									.collect(Collectors.toList());
							return listFuturesFIlledPrice;
						}
					)
				);
		
		// COLLECT FUTURES TO LIST AND WAIT FOR COMPLETION
		
		// list with futures of lists of futures of pictures
		List<CompletableFuture<List<CompletableFuture<Picture>>>> futuresList = futures.collect(Collectors.toList());
		
		// break down to list of pictures. Wait for each future
		List<Picture> picturesFilled = futuresList.stream()
		.map( CompletableFutures::getFutureResultThrowException)
		.flatMap(
				(List<CompletableFuture<Picture>> l) -> l.stream()
		)
		.map( CompletableFutures::getFutureResultThrowException)
		.collect(Collectors.toList());
		
		long endTime = System.currentTimeMillis();
		
		// NOW ALL OF THE PICTURES ARE READY. PRINT THAT WE ARE DONE!
		System.out.println("Ready: "+Arrays.toString(picturesFilled.toArray()));
		System.out.println(String.format("All future ready after : %s sec", ((endTime-startTime)/1000) ));
		
//		CompletableFuture<Integer> c = null;
//		CompletableFuture<String> c2 = null;
//		
//		CompletableFuture cc = c.thenApply((Integer i) -> "Done");
//		/* thenApply((FutureType) -> T) -> CompletableFuture<T>
//		 * Executed, when previous Future is done.
//		 * Modifies the value "INSIDE" the completableFuture container to T		 */
//
//		/* thenRun(() -> Void) -> CompletableFuture<Void>
//		 * Executes some code (no params are passed), when previous Future is done		 */
//		
//		/* thenAccept(FutureType -> Void) -> CompletableFuture<Void>
//		 * Executed, when the previous Future is done
//		 * VOID will be inside the Future after this call. To preserve some Type T in FUture - use thenApply()
//		 */
//		
//		CompletableFuture ccc = c.thenCompose((Integer inte) -> CompletableFuture.runAsync(() -> System.out.println("Done")));
//		/* thenCompose(FutureType) -> CompletableFuture) -> CompletableFuture
//		 * Executes a SECOND CompletableFuture, when the first is done    */
//		
//		// thenCombine
//		CompletableFuture<Double> aaa = c.thenCombine(c2, (Integer ii, String ss) -> 2.2);
//		/* thenCombine(CompletableFuture, Function(FutureType1, FutureType2) -> T) -> CompletableFuture<T>
//		 * Executes FIRST and SECOND ComputableFutures. Produces the results in a Function which itselfe produces a ComputableFunction 
//		 */
		
		
		/* allOf
		 * CompletableFuture.allOf(array).join() 
		 * Allows to wait for ALL futures to be complete. 
		 */
		
		// TODO - denke dir AnwedungsBeispiele für alle obehn gelistete Mehtoden aus
	}
	
	/**
	 * Delayed
	 * Retrieves the ranking on an own thread
	 * @param picture
	 * @return
	 */
	private static Picture echoPictureWhenFutureReady(Picture picture){
		System.out.println("Picture of: "+picture.author+ " costs "+picture.price); 
		return picture;
	}

	/**
	 * Delayed
	 * Retrieves the ranking on an own thread
	 * @param picture
	 * @return
	 */
	private static CompletableFuture<Picture> getFutureFillingPriceInPictureDelayed(Picture picture){
		return CompletableFuture.supplyAsync(
			() -> {
				picture.price = rateStolenPictures(picture); 
				return picture;
			}
		);
	}
	
	private static <T> T getFutureResultThrowException(CompletableFuture<T> f){
		try {
			return f.get();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static void useCompletableFuturesOnOwnThread() {
		List<Future<Double>> futures = new ArrayList<>();
		for(int i = 0; i< 100; i++){
			
			/* CompletableFuture is a medium, to return the computation results.
			 * 
			 * Occurred Exceptions will be rethrown, wrapped by an "ExecutionException"
			 * original Exception may be retrieved via exception.getCause()
			 */
			
//			// MANUALLY create completableFuture
//			CompletableFuture<Double> f = new CompletableFuture<>();
//			// now I control my thread and exception handling
//			new Thread(() -> {
//				try{
//					// computations here
//					double d = calculatePrice();
//					// provide access to results
//					f.complete(d);
//				}catch(Exception e){
//					// provide access to exceptions
//					f.completeExceptionally(e);
//				}
//			}).start();
			
			// AUTOMATICALLY create CompletableFuture with errorhandling
			CompletableFuture<Double> f = CompletableFuture.supplyAsync(() -> calculatePrice());
			
			futures.add(f);
		}
		
		double sum = sumFutures(futures);
		System.out.println("Result: "+sum);
	}


	
	
	// FUTURE

	/* passing a callable to Executor creates a future.
	 * Future is complete, when the callable is done!
	 * + You do not have to care about setting future as "done".
	 * - You lose all exceptions and the future might never return   
	 */
	private static void useFuturesOnExecutorSerices() {
		List<Future<Double>> futures = new ArrayList<>();
		ExecutorService e = Executors.newFixedThreadPool(100);
		for(int i = 0; i< 100; i++){
			Future<Double> f = e.submit(getCallable());
			futures.add(f);
		}
		
		double sum = sumFutures(futures);
		System.out.println("Result: "+sum);
	}


	private static double sumFutures(List<Future<Double>> futures) {
		double sum = futures.stream()
		.map((Future<Double> f) -> {
			try {
				double calculatedPrice = f.get();
				System.out.println(String.format("Everything was fine. Price calculated: "+calculatedPrice));
				return calculatedPrice;
				
			} catch (Exception e1) {
				System.out.println(String.format("I know - an Exception occured earlier on another thread: %s.", e1.getCause()));
				return (double) 0;
			}
		})
		.reduce(Double::sum)
		.orElseGet(
				() -> {
					System.out.println("Empty calculation."); 
					return (double)0;
				}
		);
		return sum;
	}
	
	
	public static final Callable<Double> getCallable(){
		return new Callable<Double>() {
			@Override
			public Double call() throws Exception {
				System.out.println("Working on thread "+Thread.currentThread().getName());
				return calculatePrice();
			}
		};
	}
	
	
	public static final double calculatePrice(){
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		double random = Math.random();
		if(random>0.5){
			throw new MyDemoException();	
		}
		
		return Math.random();
	}
	
	public static class MyDemoException extends RuntimeException {	}

	/**
	 * Delayed
	 * @param picture
	 * @return
	 */
	static int rateStolenPictures(Picture picture){
		delay();
		if(picture.author.equals("Серов")){
			return 1;
		}
		return (int)(Math.random() * 1000000);
	}
	
	/**
	 * Delayed
	 * @return
	 */
	static List<Picture> getStolenPicturesDelayed(String country){
		delay();
		int amount = (int)(Math.random()*10);
		System.out.println("In "+country+ " there are "+amount+" pictures stolen");
		return Stream.generate(() -> new Picture()).limit(amount).map(pic -> {pic.country = country; return pic;}).collect(Collectors.toList()); 
	}
	
	static void delay(){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	static class Picture {
		public String country;
		public int price;
		public final String[] painters = {"Picasso", "Кандинский", "Da Vichi", "Малевич", "Серов"};  
		final String author = painters[ ((int)(Math.random() * (10 ^ painters.length))) % painters.length];
		
		@Override
		public String toString() {
			return String.format("Country:%s,Author:%s,price%s", country, author, price);
		}
	}
}
