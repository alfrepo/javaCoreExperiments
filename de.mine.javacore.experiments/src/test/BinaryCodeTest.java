import static org.junit.Assert.*;

import org.junit.Test;


public class BinaryCodeTest {

	@Test
	public void test1() {
		String[] result = new BinaryCode().decode("123210122");
		
		assertTrue(result[0].equals( "011100011" ));
		assertTrue(result[1].equals("NONE" ));
	}
	
	@Test
	public void test2() {
		String[] result = new BinaryCode().decode("22111");
		
		assertTrue(result[0].equals("NONE" ));
		assertTrue(result[1].equals("11001" ));
	}
	
	@Test
	public void test3() {
		String[] result = new BinaryCode().decode("123210120");
		
		assertTrue(result[0].equals("NONE" ));
		assertTrue(result[1].equals("NONE" ));
	}
	
	@Test
	public void test4() {
		String[] result = new BinaryCode().decode("3");
		
		assertTrue(result[0].equals("NONE" ));
		assertTrue(result[1].equals("NONE" ));
	}
	
	@Test
	public void test5() {
		String[] result = new BinaryCode().decode("12221112222221112221111111112221111");
		
		assertTrue(result[0].equals("01101001101101001101001001001101001" ));
		assertTrue(result[1].equals("10110010110110010110010010010110010" ));
	}

}
