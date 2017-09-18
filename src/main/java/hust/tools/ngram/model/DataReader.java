package hust.tools.ngram.model;

import java.io.IOException;

import hust.tools.ngram.datastructure.NGramCountEntry;
import hust.tools.ngram.datastructure.NGramModelEntry;

/**
 *<ul>
 *<li>Description: 数据读取接口 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public interface DataReader {

	/**
	 * 返回读取的n元数量（int型）
	 * @return 读取的整型数据
	 * @throws IOException
	 */
	public int readCount() throws IOException;

	/**
	 * 返回读取的String类型数据
	 * @return 读取的String类型数据
	 * @throws IOException
	 */
	public String readUTF() throws IOException;
	
	/**
	 * 返回读取的n元及其概率  
	 * @return n元及其概率 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public NGramModelEntry readNGramModelEntry() throws IOException, ClassNotFoundException;
	
	/**
	 * 返回读取的n元及其数量  
	 * @return n元及其数量
	 * @throws IOException
	 */
	public NGramCountEntry readNGramCountEntry() throws IOException, ClassNotFoundException;
	
	/**
	 * <li>关闭数据读取  
	 * @throws IOException
	 */
	public void close() throws IOException;
}
