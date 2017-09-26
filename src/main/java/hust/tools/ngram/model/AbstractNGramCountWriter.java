package hust.tools.ngram.model;

import java.io.IOException;

import hust.tools.ngram.utils.NGramCountEntry;

/**
 *<ul>
 *<li>Description: 写n元数量的抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public abstract class AbstractNGramCountWriter {

	public AbstractNGramCountWriter() {
		super();
	}
	
	/**
	 * 写入n元及其数量的记录
	 * @param entry n元及其数量
	 * @throws IOException
	 */
	public abstract void writeCountEntry(NGramCountEntry entry) throws IOException;
	
	/**
	 * 写入n元的总数量  
	 * @param number n元的总数量
	 * @throws IOException
	 */
	public abstract void writeNumber(int number) throws IOException;
	
	/**
	 * 关闭写入  
	 * @throws IOException
	 */
	public abstract void close() throws IOException;

	/**
	 * 保存计数 ，执行此方法后将自动关闭写入流
	 * @throws IOException
	 */
	public abstract void persist() throws IOException;
}