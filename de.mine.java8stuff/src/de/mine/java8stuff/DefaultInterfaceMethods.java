package de.mine.java8stuff;

public class DefaultInterfaceMethods {

	/**
	 * Resolution of default methods, when multiple implementations are available in hiearchy:
	 * - if explicit implementation available -> explicit implementation win over "default" implementation
	 * - if default implementations available -> most specific class wins. A <- B <- C <- D if someething implements all the interfaces - the default method from D will win
	 * - when diamond problem occurs - class wont compile  
	 */
	public class Pazak implements HasDefaultMethodKu, HasDefaultMethodKuToo{
		
		// have to override method #ku() to explicetly say which interface's method to execute 
		@Override
		public void ku() {
			HasDefaultMethodKu.super.ku();
		}
	}
	
	public interface HasDefaultMethodKu{
		default void ku(){
			System.out.println("Say Ku!");
		}
	}
	
	public interface HasDefaultMethodKuToo{
		default void ku(){
			System.out.println("Say Ku Too!");
		}
	}
	
}
