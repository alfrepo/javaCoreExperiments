package de.mine.java8stuff;

import java.util.function.IntPredicate;

public class Lambdas {
	
	public static void main(String[] args) {
		
		Human human = new Human(12, "Falka");
		
		// allow to speak the name for all older than 9
		speakTheName(a -> a > 9 , human);

		// allow to speak the name for all older than 16
		speakTheName(a -> a > 16 , human);
		
		// allow to speak the name for all older than 16
//		IntPredicate r0 = int a -> a > 16; // wrong - need parenthesis
		IntPredicate r1 = (int a) -> a > 16; // ok
		IntPredicate r2 = (int a) -> {return a > 16;}; // ok
//		Object o = (int a) -> a > 16; // wrong - Object is not a functional interface
		speakTheName(r1 , human);
		
		// allow to speak the name if human thinks - its old enough
		speakTheName(human::checkIfOldEnough , human);
	}
	
	static void speakTheName(IntPredicate allow, Human human){
		if(allow.test(human.age)){
			System.out.println(human.name);
		}else{
			System.out.println("Can't speak the name");
		}
	}

	static class Human{
		int age = 0;
		String name = "Sin Nombre";
		
		public Human(int age, String name) {
			this.age = age;
			this.name = name;
		}
		
		boolean checkIfOldEnough(int age){
			return age > 5;
		}
	}
}
