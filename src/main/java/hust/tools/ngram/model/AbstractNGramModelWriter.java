package hust.tools.ngram.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import hust.tools.ngram.datastructure.ARPAEntry;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.datastructure.NGramModelEntry;

/**
 *<ul>
 *<li>Description: 写n元模型的抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public abstract class AbstractNGramModelWriter {
	
	public AbstractNGramModelWriter() {
		super();
	}
	
	/**
	 * 统计给定n元长度的所有n元类型
	 * @param map	n元与其概率的映射
	 * @param n		n元长度
	 * @return		给定n元长度下的所有n元类型
	 */
	public NGram[] statTypeAndCount(HashMap<NGram, ARPAEntry> map, int n) {
		Set<NGram> nGrams = map.keySet();
		List<NGram> list = new LinkedList<>();
		
		for(NGram nGram : nGrams)
			if(nGram.length() == n)
				list.add(nGram);
		
		return list.toArray(new NGram[list.size()]);
	}
	
	/**
	 * 写入n元及其概率 
	 * @param entry 待写入的条目
	 * @throws IOException
	 */
	public abstract void writeNGramModelEntry(NGramModelEntry entry) throws IOException;
	
	/**
	 * 写入字符串
	 * @param string 待写入的字符串
	 * @throws IOException
	 */
	public abstract void writeUTF(String string) throws IOException;
	
	/**
	 * 写入n元数量 
	 * @param i n元的数量
	 * @throws IOException
	 */
	public abstract void writeCount(int count) throws IOException;
	
	/**
	 * 关闭写入流  
	 * @throws IOException
	 */
	public abstract void close() throws IOException;

	/**
	 * 保存模型 ，执行此方法后将自动关闭写入流
	 * @throws IOException
	 */
	public abstract void persist() throws IOException;
}
