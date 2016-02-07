package de.mine.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Generics {

	public static void main(String[] args) { }

	// OK - can hold any Function
	private Collection<? extends Function> producerExtends1(){
		return new ArrayList<UnaryOperator>();
	}
	
	/* WRONG! - ArrayList<UnaryOperator> is not a legal replacement for Collection<Function>.
	 * It is legal to try putting NonUnaryOperator into Collection<Function>.
	 * It is NOT legal to put NonUnaryOperator  into Collection<UnaryOperator>.
	 * So you can not return Collection<UnaryOperator> here.
	 */
//	private Collection<Function> producerExtends2(){
//		return new ArrayList<UnaryOperator>();
//	}
	
	
	private void producerExtends3(Collection<? extends Function> collection){
		/* Indeed it is not possible to put something into the collection, 
		 * since you do not know what is ?,
		 * in the instance of Collection.
		 * May have been anything. UnaryOperator, NonUnaryOperator.
		 * 
		 * WRONG!
		 */
//		collection.add(new ArrayList<UnaryOperator>())
	}
	
	/* OK - can put any Function in here, 
	 * because you know - collection contains ONLY some superclasses of Function.
	 * So you can put any Function in here. 
	 */
	private void consumerSuper1(Collection<? super Function> collection){
		UnaryOperator u = null;
		collection.add(u);
	}
	
	private void consumerSuper2(Collection<? super Function> collection){
		/* WRONG!
		 * You do not know which superclasses of Function the collection contains.
		 */
//		Function func = collection.iterator().next();
	}
}
