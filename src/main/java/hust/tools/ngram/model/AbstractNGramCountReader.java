package hust.tools.ngram.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import hust.tools.ngram.utils.NGramCountEntry;

/**
 *<ul>
 *<li>Description: 读取n元计数文件的抽象类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public abstract class AbstractNGramCountReader {

	private DataReader dataReader;
	
	public AbstractNGramCountReader(File file) throws IOException {
		String filename = file.getName();
		InputStream input = new FileInputStream(file);
		
		// 读取不同格式的文件
		if (filename.endsWith(".bin")) 	//二进制文件
			this.dataReader = new BinaryDataReader(input);
		else 	//文本文件
	    	this.dataReader = new TextDataReader(input);
	}
	
	public AbstractNGramCountReader(DataReader dataReader) {
		super();
		this.dataReader = dataReader;
	}
	
	/**
	 * 返回读取的n元模型  
	 * @return 读取的n元模型
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public NGramCounter getModel() throws IOException, ClassNotFoundException {		
		return constructNGramCount();
	}

	/**
	 * 重构n元计数
	 * @return 读取计数文件
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public abstract NGramCounter constructNGramCount() throws IOException, ClassNotFoundException;
	
	public NGramCountEntry readNGramCountEntry() throws ClassNotFoundException, IOException {
		return dataReader.readNGramCountEntry();
	}
	
	public int readCount() throws IOException {
		return dataReader.readCount();
	}
	
	/**
	 * 关闭文件读取  
	 * @throws IOException
	 */
	public void close() throws IOException {
		dataReader.close();
	}
}
