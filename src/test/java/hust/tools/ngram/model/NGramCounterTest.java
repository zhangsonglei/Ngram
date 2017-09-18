package hust.tools.ngram.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ngram.datastructure.Gram;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.utils.StringGram;

/**
 *<ul>
 *<li>Description: 测试n元计数 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月31日
 *</ul>
 */
public class NGramCounterTest {
	Gram[] grams = new Gram[]{new StringGram("1"), new StringGram("2"),new StringGram("3"), new StringGram("4"),
							  new StringGram("3"), new StringGram("2"),new StringGram("3"), new StringGram("5")};
	
	Gram[] grams1 = new Gram[]{new StringGram("2"), new StringGram("3")};
	NGram ngram = new NGram(grams1);
	NGramCounter nGramCounter;
	
	/**
	 * 初始化n元模型
	 * @throws IOException
	 */
	@Before
	public void setup(){
		nGramCounter = new NGramCounter(grams, 3);
	}
	
	/**
	 * 测试n元总数和不同长度n元数量
	 * @throws IOException
	 */
	@Test
	public void testGetTotalNGramCountByN() throws IOException {
		assertEquals(21, nGramCounter.getTotalNGramCount());
		assertEquals(6, nGramCounter.getTotalNGramCountByN(3));	//trigram总数量
		assertEquals(7, nGramCounter.getTotalNGramCountByN(2));	//bigram总数量
		assertEquals(8, nGramCounter.getTotalNGramCountByN(1));	//unigram总数量
	}
	
	/**
	 * 测试获取n元数量方法
	 */
	@Test
	public void testGetNGramCount() {
		assertEquals(2, nGramCounter.getNGramCount(ngram));
	}
	
	/**
	 * 测试获取模型不同n元的数量的方法
	 */
	@Test
	public void testSize() {
		assertEquals(17, nGramCounter.size());
	}

	/**
	 * 测试n元是否存在的方法
	 */
	@Test
	public void testContains() {
		Gram[] grams1 = new Gram[]{new StringGram("2"), new StringGram("2")};
		NGram ngram1 = new NGram(grams1);
		
		assertTrue(nGramCounter.contains(ngram));		//判断n元是否存在
		assertFalse(nGramCounter.contains(ngram1));	//判断n元是否存在
	}
	
	/**
	 * 测试获取给定长度n元的类型数量
	 */
	@Test
	public void testGetNGramTypeCountByN(){
		assertEquals(6, nGramCounter.getNGramTypeCountByN(3));
		assertEquals(6, nGramCounter.getNGramTypeCountByN(2));
		assertEquals(5, nGramCounter.getNGramTypeCountByN(1));
	}
	
	/**
	 * 测试得到所有给定长度的n元
	 */
	@Test
	public void testGetNGramLabels() {
		NGram[] list = nGramCounter.getNGramTypeByN(2);
		Gram[] grams1 = new Gram[]{new StringGram("1"), new StringGram("2")};
		Gram[] grams2 = new Gram[]{new StringGram("2"), new StringGram("3")};
		Gram[] grams3 = new Gram[]{new StringGram("3"), new StringGram("4")};
		Gram[] grams4 = new Gram[]{new StringGram("4"), new StringGram("3")};
		Gram[] grams5 = new Gram[]{new StringGram("3"), new StringGram("2")};
		Gram[] grams6 = new Gram[]{new StringGram("3"), new StringGram("5")};
		
		NGram nGram1 = new NGram(grams1);
  		NGram nGram2 = new NGram(grams2);
  		NGram nGram3 = new NGram(grams3);
  		NGram nGram4 = new NGram(grams4);
  		NGram nGram5 = new NGram(grams5);
  		NGram nGram6 = new NGram(grams6);
  		Map<NGram, Integer> map = new HashMap<>();
  		map.put(nGram1, 1);
  		map.put(nGram2, 2);
 		map.put(nGram3, 1);
 		map.put(nGram4, 1);
 		map.put(nGram5, 1);
 		map.put(nGram6, 1);
		
 		for(NGram nGram : list)
 			assertTrue(map.containsKey(nGram));
 		
 		assertTrue(list.length == map.size());
	}
}
