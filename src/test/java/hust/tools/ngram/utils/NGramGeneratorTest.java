package hust.tools.ngram.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ngram.utils.NGramGenerator;

/**
 *<ul>
 *<li>Description: 测试生成n元的方法 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月31日
 *</ul>
 */
public class NGramGeneratorTest {

	Gram[] grams;
	List<NGram> lGrams;
	
	@Before
	public void setup() {
		grams = new StringGram[]{
				   new StringGram("我"),
				   new StringGram("爱"),
				   new StringGram("自"),
				   new StringGram("然"),
				   new StringGram("语"),
				   new StringGram("言"),
				   new StringGram("处"),
				   new StringGram("理")};
		
		Gram[] grams1 = new StringGram[]{
				new StringGram("我"), new StringGram("爱"), new StringGram("自")};
		Gram[] grams2 = new StringGram[]{
				new StringGram("爱"), new StringGram("自"), new StringGram("然")};
		Gram[] grams3 = new StringGram[]{
				new StringGram("自"), new StringGram("然"), new StringGram("语")};
		Gram[] grams4 = new StringGram[]{
				new StringGram("然"), new StringGram("语"), new StringGram("言")};
		Gram[] grams5 = new StringGram[]{
				new StringGram("语"), new StringGram("言"), new StringGram("处")};
		Gram[] grams6 = new StringGram[]{
				new StringGram("言"), new StringGram("处"), new StringGram("理")};
		
		lGrams = new ArrayList<>();
		lGrams.add(new NGram(grams1));
		lGrams.add(new NGram(grams2));
		lGrams.add(new NGram(grams3));
		lGrams.add(new NGram(grams4));
		lGrams.add(new NGram(grams5));
		lGrams.add(new NGram(grams6));
	}
	
	@Test
	public void testGenerate() {
		List<NGram> list = NGramGenerator.generate(grams, 3);

		assertEquals(lGrams, list);
	}

}
