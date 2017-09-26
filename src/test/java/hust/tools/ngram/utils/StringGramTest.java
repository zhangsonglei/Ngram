package hust.tools.ngram.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ngram.utils.StringGram;

public class StringGramTest {
	StringGram str1;
	StringGram str2;
	StringGram str3;
	StringGram str4;
	
	@Before
	public void setup() {
		str1 = new StringGram("zsl");
		str2 = new StringGram("zsl");
		str3 = new StringGram("sss");
		str4 = new StringGram("zzz");
	}
	
	@Test
	public void testGetWord() {
		String string = "zsl";
		assertEquals(string, str1.getGram());
	}
	
	@Test
	public void testEquals() {
		assertTrue(str1.equals(str2));
		assertFalse(str1.equals(str3));
	}
	
	@Test
	public void testCompareTo() {
		assertEquals(0, str1.compareTo(str2));
		assertEquals(-1, str1.compareTo(str4));
		assertEquals(1, str1.compareTo(str3));
	}
	
	@Test
	public void testToString() {
		assertEquals("zsl", str1.toString());
	}
}
