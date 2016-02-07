package de.mine.date;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapVsHashTable {

	public static void main(String[] args) {
		
		/*
		 * Comparison of Hashmap and Hashtable which do implement Map alltogether. 
		 */
		Map<String, Integer> map;
		
		/*
		 * not synchronized 
		 * -
		 * fast
		 * 
		 */
		HashMap<String, Integer> hashMap = new HashMap<>();
		
		/*
		 * synchronized
		 * locks
		 * slow
		 * 
		 */
		Hashtable<String, Integer> hashtable = new Hashtable<>();
		
		/*
		 * synchronized
		 * no locks
		 * slow
		 * 
		 * initialCapacity - 
		 * Joshua Bloch: The downside of using a power-of-two is that the resulting hash table is very sensitive to the quality of the hash function (hashCode). It is imperative that any change in the input must affect the low order bits of the hash value. (Ideally, it should affect all bits of the hash value with equal likelihood.) Because we have no assurance that this is true, we put in a secondary (or "defensive") hash function when we switched to the power-of-two hash table. This hash function is applied to the results of hashCode before masking off the low order bits. Its job is to scatter the information over all the bits, and in particular, into the low order bits. Of course it has to run very fast, or you lose the benefit of switching to the power-of-two-sized table. The original secondary hash function in 1.4 turned out to be insufficient. We knew that this was a theoretical possibility, but we thought that it didn't affect any practical data sets. We were wrong. The replacement secondary hash function (which I developed with the aid of a computer) has strong statistical properties that pretty much guarantee good bucket distribution.
		 */
		// initial capacity of the HashMap. Must be big enough to avoid collusion. Must be small enough to spare memory.
		int initialCapacity = 1000;
		// the HashMap will be increased, when this load factor is reached
		float loadFactor = 0.75f;
		// how many threads are used to WRITE the concurrent HashMap?
		int concurrencyLevel = 16;
		ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>(initialCapacity, loadFactor, concurrencyLevel);
	}

}
