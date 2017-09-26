package hust.tools.ngram.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.StringGram;

/**
 *<ul>
 *<li>Description: NGram类的单元测试 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月31日
 *</ul>
 */
public class NGramTest {

	Gram[] grams1;
	Gram[] grams2;
	Gram[] grams3;
	Gram[] grams4;
	
	NGram nGram1;
	NGram nGram2;
	NGram nGram3;
	NGram nGram4;

	@Before
	public void setup() {
		grams1 = new StringGram[]{
				new StringGram("我"), new StringGram("爱"), new StringGram("自")};
		grams2 = new StringGram[]{
				new StringGram("我"), new StringGram("爱"), new StringGram("然")};
		grams3 = new StringGram[]{
				new StringGram("我"), new StringGram("爱"), new StringGram("自")};
		grams4 = new StringGram[]{
				new StringGram("我"), new StringGram("爱")};
		
		nGram1 = new NGram(grams1);
		nGram2 = new NGram(grams2);
		nGram3 = new NGram(grams3);
		nGram4 = new NGram(grams4);
	}
	
	/**  
	 * 测试获取n元的长度
	 */
	@Test
	public void testLength() {
		assertEquals(3, nGram1.length());
		assertEquals(2, nGram4.length());
	}

	/**  
	 * 测试从n元中根据索引获取元
	 */
	@Test
	public void testGetGram() {
		for(int i = 0; i < nGram1.length(); i++)
			assertEquals(grams1[i], nGram1.getGram(i));
	}
	
	/**  
	 * 测试判断两个n元是否相等
	 */
	@Test
	public void testEqualsNGram() {
		assertTrue(nGram1.equals(nGram3));
		assertFalse(nGram1.equals(nGram4));
	}

	/**  
	 * 测试比较两个n元大小的方法
	 */
	@Test
	public void testCompareTo() {
		assertEquals(1, nGram1.compareTo(nGram4));
		assertEquals(0, nGram1.compareTo(nGram3));
		assertEquals(-1, nGram2.compareTo(nGram1));
	}
	
	/**  
	 * 测试从头部删除一个元
	 */
	@Test
	public void testRemoveFirst(){
		Gram[] grams = new StringGram[]{new StringGram("爱"), 
				new StringGram("自")};
		NGram nGram = new NGram(grams);

		assertEquals(nGram, nGram1.removeFirst());
	}
	
	/**  
	 * 测试从尾部删除一个元
	 */
	@Test
	public void testRemoveLast(){
		Gram[] grams = new StringGram[]{new StringGram("我"),
				new StringGram("爱")};
		NGram nGram = new NGram(grams);

		assertEquals(nGram, nGram1.removeLast());
	}
	
	/**  
	 * 测试在头部添加一个元
	 */
	@Test
	public void testAddFirst(){
		Gram gram= new StringGram("然");
		Gram[] grams = new StringGram[]{(StringGram) gram,
										new StringGram("我"),
										new StringGram("爱"), 
										new StringGram("自")};
		NGram nGram = new NGram(grams);
		
		assertEquals(nGram, nGram1.addFirst(gram));
	}
	
	/**  
	 * 测试在尾部添加一个元
	 */
	@Test
	public void testAddLast(){
		Gram gram= new StringGram("然");
		Gram[] grams = new StringGram[]{new StringGram("我"),
										new StringGram("爱"), 
										new StringGram("自"), 
										(StringGram) gram};
		NGram nGram = new NGram(grams);
		
		assertEquals(nGram, nGram1.addLast(gram));
	}
	
	/**
	 * 测试判断n元是否是以给定m（m<n）元开始的 
	 */
	@Test
	public void testStartWith(){
		Gram[] grams = new StringGram[]{new StringGram("她"),
										new StringGram("爱")};
		NGram nGram = new NGram(grams);
		
		assertTrue(nGram1.startWith(nGram4));
		assertTrue(nGram1.startWith(nGram1));
		assertFalse(nGram.startWith(nGram1));
		assertFalse(nGram1.startWith(nGram));
	}
	
	/**
	 * 测试判断n元是否是以给定m（m<n）元结束的 
	 */
	@Test
	public void testEndWith(){
		Gram[] grams = new StringGram[]{new StringGram("爱"), 
										new StringGram("自")};
		NGram nGram = new NGram(grams);

		
		assertTrue(nGram1.endWith(nGram));
		assertFalse(nGram.endWith(nGram1));
		assertTrue(nGram1.endWith(nGram1));
		assertFalse(nGram1.endWith(nGram2));
	}
	
	/**
	 * 测试修改n元某位
	 */
	@Test
	public void testSetGram() {
		Gram gram = new StringGram("然");
		nGram1.setGram(gram, 2);
		assertEquals(nGram2, nGram1);
	}
	
	@Test
	public void testGetGrams() {
		Gram[] grams = new StringGram[]{new StringGram("我"), new StringGram("爱"), new StringGram("然")};
		assertEquals(grams.length, nGram2.getGrams().length);
		for(int i = 0; i < grams.length; i++)
			assertEquals(grams[i], nGram2.getGrams()[i]);
	}
	
	@Test
	public void testToString() {
		String string = "我 爱 自";
		assertEquals(string, nGram1.toString());
	}
}
