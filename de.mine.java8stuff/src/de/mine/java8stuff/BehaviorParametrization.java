package de.mine.java8stuff;

import java.io.File;

/**
 * If a method M's parameter X is a functional interface, of the signature: f(String) -> String
 *  
 *  Then you can pass to M a Parameter X - a static function from any Class f(String) -> String
 *  Classname::staticMethodWhichMapsStringToString
 *  
 *  Or you could pass String's own non static method "toUpperCase", which maps String -> String:
 *  String::toUpperCase
 *  because Strings methods operate take String.this as a parameter and this special method "toUpperCase" return a String
 *  so its a  f(String) -> String  too
 *   
 * @author skip
 *
 */
public class BehaviorParametrization {

	public static void main(String[] args) {
		// passing functional interface isHidden 
		// TODO why does a non static reference work?
		File[] files = new File("C:").listFiles(File::isHidden);
		
		// passing a function: f(String) -> String
		// static of MyClass
		printStringModifiedByFunction(MyClass::myStaticMethodTakeStringAppendClassNameReturnsString);
		// local of MyClass
		printStringModifiedByFunction(new MyClass("Lola", "Runnin")::myLocalMethodTakeStringAppendClassNameReturnsString);

		// printToConsole1 needs a function: f(String) -> String
		// passing a Parameter Type's local method: String#method -> String
		printStringModifiedByFunction(String::toUpperCase);
		
		// printToConsole1 needs a function: f(MyClass) -> String and an MyClass instance 
		// passing an anonymous function, which would retrieve MyClass's name f(MyClass)-> nameString.  And pass an instance
		// use different functions to convert MyClass to String differently
		MyClass girly = new MyClass("Josephine", "Gently");
		printMyClassAfterConvertingItToString( (MyClass m)-> m.name, girly);
		printMyClassAfterConvertingItToString( (MyClass m)-> m.surname, girly);
	}
	
	// HELPER which uses passed functional interface
	public static void printStringModifiedByFunction(FunctionalInterfaceMapsStringToString functionalInterface){
		// use the functional interface
		String string = functionalInterface.function("Take that, console");
		System.out.println(string);
	}
	
	// HELPER which uses passed functional interface
	public static void printMyClassAfterConvertingItToString(FunctionalInterfaceMapsMyCassToString functionalInterface, MyClass myClass){
		// use the functional interface
		String string = functionalInterface.function(myClass);
		// add some stuff, which is always done after conversion
		string = "Girly was converted by functional interface to: "+string;
		System.out.println(string);
	}
	

	// experimental class
	static class MyClass{
		String name;
		String surname;
		
		public MyClass(String name, String surname) {
			this.name = name;
			this.surname = surname;
		}
		
		static String myStaticMethodTakeStringAppendClassNameReturnsString(String input){
			return input + " from MyClass";
		}
		
		String myLocalMethodTakeStringAppendClassNameReturnsString(String input){
			return input + " from MyClass";
		}
		
	}
	
	// define functional interfaces
	static interface FunctionalInterfaceMapsStringToString{
		String function(String input);
	}
	
	static interface FunctionalInterfaceMapsMyCassToString{
		String function(MyClass input);
	}
}
