package de.mine.java8stuff.behaviorParametrization;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Write a prettyPrintApple method that takes a List of Apples and that can be
 * parameterized with multiple ways to generate a String output from an apple (a
 * bit like multiple customized toString methods). For example, you could tell
 * your pretty-PrintApple method to print only the weight of each apple. In
 * addition, you could tell your prettyPrintApple method to print each apple
 * individually and mention whether it’s heavy or light.
 * 
 * @author skip
 *
 */
public class Tutorial1 {

	public static void main(String[] args) {
		Apple apple = new Apple();
		apple.color = "Red";
		apple.weight = 22;
		
		prettyPrintApple(apple, (Apple a) -> {return "Color: "+a.color;} );
		prettyPrintApple(apple, (Apple a) -> {return "Weight: "+a.weight;} );
	}

	public static void prettyPrintApple(Apple apple, Function<Apple, String> appleToString){
		System.out.println(appleToString.apply(apple));
	}
	
	public static class Apple{
		int weight;
		String color;
	}
	
}
