package hust.tools.ngram.utils;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ngram.pos.WordTagGram;

public class WordTagGramTest {
	WordTagGram wt;
	WordTagGram wt1;
	WordTagGram wt2;
	WordTagGram wt3;
	WordTagGram wt4;
	WordTagGram wt5;
	
	@Before
	public void setup() {
		wt = new WordTagGram("123","123");
		wt1 = new WordTagGram("123","123");
		wt2 = new WordTagGram("123","12");
		wt3 = new WordTagGram("123","1234");
		wt4 = new WordTagGram("12","123");
		wt5 = new WordTagGram("1234","123");
	}
	
	@Test
	public void testGetWord() {
		String string = "123";
		assertEquals(string, wt1.getWord());
		assertEquals(string, wt1.getTag());
	}
	
	@Test
	public void testEquals() {
		assertTrue(wt1.equals(wt));
		assertFalse(wt1.equals(wt2));
	}
	
	@Test
	public void testCompareTo() {
		assertEquals(0, wt1.compareTo(wt));
		assertEquals(1, wt1.compareTo(wt2));
		assertEquals(-1, wt1.compareTo(wt3));
		assertEquals(1, wt1.compareTo(wt4));
		assertEquals(-1, wt1.compareTo(wt5));
	}
	
	@Test
	public void testToString() {
		assertEquals("123/123", wt1.toString());
	}
	
	@Test
	public void testSort() {
		WordTagGram[] wordTagGrams = new WordTagGram[]{wt, wt1, wt2, wt3, wt4, wt5};
		show(wordTagGrams);
		Arrays.sort(wordTagGrams);
		show(wordTagGrams);
	}
	
	private void show(WordTagGram[] args){
		for(int i = 0; i < args.length; i++)
			System.out.println(args[i]);
	}
}
