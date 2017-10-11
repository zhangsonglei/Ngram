package hust.tools.ngram.model;

import java.io.IOException;
import hust.tools.ngram.utils.NGramCountEntry;

/**
 *<ul>
 *<li>Description: 写n元计数到文件的抽象类，实现写入方法 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public interface NGramCountWriter {
	
	/**
	 * 写入n元及其数量的记录
	 * @param entry n元及其数量
	 * @throws IOException
	 */
	void writeCountEntry(NGramCountEntry entry) throws IOException;
	
	/**
	 * 写入n元的总数量  
	 * @param number n元的总数量
	 * @throws IOException
	 */
	void writeNumber(int number) throws IOException;
	
	/**
	 * 关闭写入  
	 * @throws IOException
	 */
	void close() throws IOException;

	/**
	 * 保存计数 ，执行此方法后将自动关闭写入流
	 * @throws IOException
	 */
	void persist() throws IOException;
}
