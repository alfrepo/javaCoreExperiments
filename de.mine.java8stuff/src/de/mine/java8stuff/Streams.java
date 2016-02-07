package de.mine.java8stuff;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summarizingInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Streams {

	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public static void main(String[] args) {
		List<String> words = Arrays.asList("Fight", "For", "Your", "Right");
		List<Integer> numbers1 = Arrays.asList(1,2,3,4,5);
		List<Integer> numbers2 = Arrays.asList(7,8);

		// filling the stream with content using "iterate"
		Stream.iterate(0l, i -> i + 1)
				.limit(100);
		
		// array to stream
		Object[] wordArray = words.toArray(); 
		Arrays.stream(wordArray);
		
		// Composing functions
		UnaryOperator<String> stringPrepend = (String s) -> "The word is: "+s;
		UnaryOperator<String> stringREPLACE = (String s) -> s.replaceAll("o", "beee");
		String sCompose1 = stringPrepend.compose(stringREPLACE).apply("Wohoho!"); //The word is: Wbeeehbeeehbeee;
		String sAndThen1 = stringPrepend.andThen(stringREPLACE).apply("Wohoho!"); //The wbeeerd is: Wbeeehbeeehbeee;
		String sAndThen2 = stringREPLACE.andThen(stringPrepend).apply("Wohoho!"); //The word is: Wbeeehbeeehbeee;
		
		// refer to constructors - useful in Factory pattern
		Map<String, Supplier<Collection>> collectionConstructors = new HashMap<>();
		collectionConstructors.put("listConstructor", ArrayList::new);
		collectionConstructors.put("setConstructor", HashSet::new);
		Collection list = collectionConstructors.get("listConstructor").get();
		Collection set = collectionConstructors.get("setConstructor").get();
		
		// peek allows to apply an action to element, before the next step is applied
		getExampleStream()
		.peek((s) -> System.out.println("Before map: " + String.valueOf(s)))
		.map((Dish d) -> d.name.substring(0, 1))
		.peek((s) -> System.out.println("After map: " + String.valueOf(s)))
		.reduce((String result, String temp) -> result+"_"+temp)
		.ifPresent((String s) -> System.out.println("Result: "+s));
		
		//FLATMAP
		
		// stream -> to stream of streams with letters -> flattern to single letter stream 
		String[] lettersResult = words.stream().map(
				(String word) -> word.split(""))
				.flatMap(Arrays::stream)
				.toArray(String[]::new);
		
		// map, foreach
		numbers1.stream().map((Integer a) -> {return (Integer)(a*a);}).forEach(a -> System.out.println(a));
		// flatmap, filter, foreach
		numbers1.stream().flatMap(i -> numbers2.stream().map(j->new int[]{i,j})).filter(a -> {return ((a[0]+a[1])%2==0)?true:false;}).forEach(Streams::print);
		
		// FILTER
		Optional<Dish> optional200 = getExampleStream().filter(a->a.calories>200).findFirst();
		Optional<Dish> optional2000 = getExampleStream().filter(a->a.calories>2000).findFirst();
		
		optional200.ifPresent((d)->{System.out.println("Found first dish with >200 calories: "+d.name);} );
		optional2000.ifPresent((d)->{System.out.println("Found first dish with >2000 calories: "+d.name);} );
		
		// COLLECT
		long counting = getExampleStream().collect(counting());
		Comparator<Dish> comparator = Comparator.comparingInt((Dish a)->a.calories);
		Optional<Dish> maxCaloriesDish = getExampleStream().collect(maxBy(comparator));
		Optional<Dish> minCaloriesDish = getExampleStream().collect(minBy(comparator));
		IntSummaryStatistics summarize = getExampleStream().collect(summarizingInt(a->a.calories));
		
		maxCaloriesDish.ifPresent((a)->System.out.println("Dish with MAX calories: " + a.toString()));
		minCaloriesDish.ifPresent((a)->System.out.println("Dish with MINIMUM calories: " + a.toString()));
		System.out.println(summarize);
		
		String joinedNames = getExampleStream().map(Dish::toString).collect(joining(" , "));
		System.out.println(joinedNames);
		
		// COLLECT reducing
		Optional<Long> multiplied  = getExampleStream().map(a->(long)a.calories).collect(reducing((a,b)->a*b));
		// use Collectors.collectingAndThen to switch from Optional to value
		Long multipliedNotAsOptional  = getExampleStream()
				.map(a->(long)a.calories)
				.collect(Collectors.collectingAndThen(reducing((a,b)->a+b), Optional::get));
		
		Long multiplied2  = getExampleStream().collect(reducing(0l, a->(long)a.calories, Math::addExact));
		
		// COLLECT grouping via map-reduce
		
		// group Dishes by calories. 
		// Achtung: this one modifies Lists - so it is not thread safe! (ConcurrencyModException)
		Optional<HashMap> groupByCalories1 = getExampleStream().map( 
				(a)->{ 
					HashMap m = new HashMap<Integer, List<Dish>>(); 
					ArrayList<Dish> l = new ArrayList<Dish>(); 
					l.add(a);  
					m.put(a.calories, l); 
					return m;})
				.reduce(Streams::mergeDishGroups);

		BiConsumer<Integer,List<Dish>> dishCaloriesMapConsumer = (Integer calories, List<Dish> l)->{System.out.print(calories+" "); System.out.println(l);};
		applyBiConsumerToDishes(groupByCalories1.get(), dishCaloriesMapConsumer );
		
		// COLLECT groupingBy
		Map<Integer, List<Dish>> groupByCalories2 = getExampleStream().collect(groupingBy(a->a.calories));
		applyBiConsumerToDishes(groupByCalories2, dishCaloriesMapConsumer );
		// COLLECT groupingBy - multilevel reduction
		Object groupHierarchy = getExampleStream().collect(groupingBy(Dish::getOrder, groupingBy(Dish::getCalories, groupingBy( Dish::getName ))));
		// COLLECT groupingBy - finding max with "downstream collectior"
		Object groupMaxCalory = getExampleStream().collect(groupingBy(Dish::getOrder, maxBy(Comparator.comparingInt(Dish::getCalories))));

		// COLLECT partitionBy
		Object partitionByCaloriesMore200 = getExampleStream().collect(partitioningBy((Dish a)->{return a.getCalories() > 200;}));
		Object groupByCaloriesMore200 = getExampleStream().collect(groupingBy((Dish a) -> a.calories>200));

		// Efficiency
		
		long start;
		long max = 90000000;
		long summ;
		
		start = System.currentTimeMillis();
		summ = LongStream.iterate(0l, i -> i + 1)
		.limit(max)
		.reduce(0l, Long::sum);
		System.out.println("Sequential LongStream Ready filling after: "+ (System.currentTimeMillis()-start) +"ms");
		
		start = System.currentTimeMillis();
		summ = Stream.iterate(0l, i -> i + 1)
		.limit(max)
		.reduce(0l, Long::sum);
		System.out.println("Sequential Stream Ready filling after: "+ (System.currentTimeMillis()-start) +"ms");
		
		start = System.currentTimeMillis();
		summ = LongStream.rangeClosed(0l, max)
		.parallel()
		.reduce(0l, Math::addExact);
		System.out.println("Parallel Ready filling after: "+ (System.currentTimeMillis()-start) +"ms");
		
		
		// parallel streams of Dishes, reduced to String, which is a concatenated list of names
		String res = getExampleStream().parallel().reduce("", 
				new BiFunction<String, Dish, String>() { 
			@Override
			public String apply(String t, Dish u) {
				return t+u.name;
			} //accumulator - can convert the Stream content to something else (String)

		}, 
			(a,b) -> {
				return a+b;
			} // combiner - used in the case of using parallel streams
		);
		
		
		String dante = " they  had their faces twisted toward their haunches"
				+" and found it necessary to walk backward ";
		Stream<Character> streamDante = IntStream.range(0, dante.length()).mapToObj(dante::charAt);
		WordCounter counter = streamDante.parallel().reduce(new WordCounter(0, false), WordCounter::accumulate, WordCounter::combine);
		
		System.out.println("Counted "+counter.counter+" words");
		
		
		
		
		
		System.out.println("ready");
		
	
		/* COLLECTOR
		 * 
		 * Collector<T,A,R>
		 * 
		 *  T - Type of input item in stream
		 *  A - intermediate Type of Objects, during calculation
		 *  R - Result Type
		 *  
		 *  e.g. Collector which collects Stream of Strings to List 
		 *  T - String
		 *  A - List<String>
		 *  R - List<String>
		 *  
		 *  Supplier<A> supplier() - provides initial value of intermediate Type
		 *  BiConsumer<A,T> accumulator() -  input -> the A value calculated so far. T is the next value in stream
		 *  Function<A,R> finisher() - function is executed after iteration over all elements is done
		 *  BinaryOperator<A> combiner() - function which combined two intermediate Objects. This one makes multithreading possible
		 *  Set<Characteristics> characteristics - some optimization hints. UNORDERED, CONCURRENT, IDENTITY_FINISH
		 *  
		 *  
		 *  supplier - ArrayList::new
		 *  accumulator - (List<String> a, String b) -> {a.add(b)}
		 *  finisher - Functions::identity
		 *  combiner - (List<String> a, List<String> b) -> {a.addAll(b)}
		 *  characteristics - return Collections.unmodifyableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT ))
		 */		

	}
	
	static class WordCounter{
		private final int counter;
		private final boolean lastSpace;
		public WordCounter(int counter, boolean lastSpace) {
			this.counter = counter;
			this.lastSpace = lastSpace;
		}
		public WordCounter accumulate(Character c) {
			if(Character.isWhitespace(c)){
				return lastSpace ? 
							this :
							new WordCounter(counter, true);
			}else{
				return lastSpace ? 
							new WordCounter(counter+1, false) :
							this;
			}
		} 
		
		public WordCounter combine(WordCounter w){
			return new WordCounter(counter+w.counter, lastSpace);
		}
		
		public int getCounter() {
			return counter;
		}
	}
	
	static <A,B> void applyBiConsumerToDishes(Map<A, B> map, BiConsumer<A,B>  consumer ){
		System.out.println("Map: ");
		map.keySet().stream().forEach((a) -> {consumer.accept(a, map.get(a)); });
	}
	
	static HashMap<Integer, List<Dish>> mergeDishGroups(HashMap<Integer, List<Dish>> a, HashMap<Integer, List<Dish>> b){
		for(Integer i: b.keySet()){
			if(a.get(i) == null){
				a.put(i, new ArrayList<>());
			}
			a.get(i).addAll(b.get(i));
		}
		return a;
	}
	
	// TUTORIAL GLUECODE
	
	static void print(Object o){
		if(o instanceof int[]){
			o = Arrays.toString((int[])o);
		}
		System.out.println(o);
	}
	
	static Stream<Dish> getExampleStream(){
		return Arrays.asList(new Dish[]{
				new Dish(100, "Carrot soup", DishOrder.Main),
				new Dish(500, "Parrot soup", DishOrder.Main),
				new Dish(200, "Toast", DishOrder.Starter),
				new Dish(400, "Bread", DishOrder.Starter),
				new Dish(1000, "Chocolate", DishOrder.Dessert),
				new Dish(500, "Fish", DishOrder.Main),
				new Dish(1000, "Ice cream", DishOrder.Dessert),
				new Dish(1000, "Ice cream", DishOrder.Dessert),
				new Dish(1000, "Ice cream", DishOrder.Dessert),
		}).stream();
	}
	
	enum DishOrder {Apperetif, Starter, Main, Dessert}
	
	static class Dish {
		int calories = 0;
		String name = "Unnamed";
		DishOrder order = null;
		public Dish(int calories, String name, DishOrder dishOrder) {
			this.name = name;
			this.calories = calories;
			this.order = dishOrder;
		}
		
		public int getCalories() {
			return calories;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return name+":"+calories;
		}
		
		public DishOrder getOrder() {
			return order;
		}
		
	}

}
