package hust.tools.ngram.model;

import java.io.IOException;

import hust.tools.ngram.utils.NGramModelEntry;

/**
 *<ul>
 *<li>Description:  写n元模型到文件的接口  
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年10月12日
 *</ul>
 */
public interface NGramModelWriter {
	
	/**
	 * 写入n元及其概率 
	 * @param entry 待写入的条目
	 * @throws IOException
	 */
	void writeNGramModelEntry(NGramModelEntry entry) throws IOException;
	
	/**
	 * 写入字符串
	 * @param string 待写入的字符串
	 * @throws IOException
	 */
	void writeUTF(String string) throws IOException;
	
	/**
	 * 写入n元数量 
	 * @param i n元的数量
	 * @throws IOException
	 */
	void writeCount(int count) throws IOException;
	
	/**
	 * 关闭写入流  
	 * @throws IOException
	 */
	void close() throws IOException;

	/**
	 * 保存模型 ，执行此方法后将自动关闭写入流
	 * @throws IOException
	 */
	void persist() throws IOException;
}
