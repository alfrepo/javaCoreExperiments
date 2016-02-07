

public class primitiveByReference {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Integer num = 12;
		
		String str = "123210122";
		String[] arr = str.split("");
		
		System.out.println(arr);
		
		
		System.out.println("Before increment: "+num); //12
		increment(num);
		System.out.println("After increment: "+num); //12
	}
	
	private static void increment(Integer integer){
		//Integer are immutable - so increment will not change  the passed integer outside this scopes
		integer ++;
	}

}
