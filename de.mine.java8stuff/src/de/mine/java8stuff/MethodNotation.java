package de.mine.java8stuff;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MethodNotation {

	public static void main(String[] args) {
		
		// use Optionals 
		String s = "";
//		String s = null;
		Optional.ofNullable(s).ifPresent(MethodNotation::echo);
		
		
		// use map and flatMap to apply the methods and put them into the conext of the monad "Optionals"
		Person p = new Person();
		p.age = Optional.ofNullable(32);
		p.name = "Alex";
		
		Ranking r = new Ranking();
		r.author = p;
		r.value = 5;
		
		Movie m = new Movie();
		m.name = "Max Max";
		m.ranking = Optional.of(r);
		m.budget = Optional.ofNullable(null);
		
		
		// echo Person->RankingAuthors->Age if present. Or do nothing
		m.ranking.map(ranking -> ranking.author).flatMap(person -> person.age);
		
		
		
		
		// --- NEW JAVA8 METHA CLASSES ---
		
		// Function
		Function<Ranking, Integer> f = ranking -> ranking.value;
		
		// Consumer
		Consumer<MethodNotation> c = MethodNotation :: echo; 
		
		// Predicate - boolean returning function
		Predicate<Ranking> isLarger2 = ranking -> ranking.value > 2;
		
//		MethodNotation::echo;
	}
	
	public static void echo(Object value){
		System.out.println(String.format("Value is '%s'",value));
	}

	
	// MODELS
	
	static class Movie{
		String name;
		Optional<Ranking> ranking;
		Optional<Integer> budget; 
	}
	
	static class Ranking{
		int value;
		Person author;
	}
	
	static class Person{
		String name;
		Optional<Integer> age;
	}
}
