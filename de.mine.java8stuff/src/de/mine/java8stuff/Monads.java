package de.mine.java8stuff;

import java.util.function.Function;

public class Monads {

	public static void main(String[] args) {
	
	}
	
//	abstract class MyMonad{
//		M<A> unit(A a);
//		
//		M<B> bind(M<A> ma, Function<A, M<B> f);
//	}
	
	interface M<B>{
		<A> M<B> map(Function<A,B> f);
		
		<A> M<B> flatMap(Function<A, M<B>> f);
	}
	
	
	
}
