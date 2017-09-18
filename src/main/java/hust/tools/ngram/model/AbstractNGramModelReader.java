package hust.tools.ngram.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import hust.tools.ngram.datastructure.NGramModelEntry;

/**
 *<ul>
 *<li>Description: 读取n元模型的抽象类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public abstract class AbstractNGramModelReader {

	private DataReader dataReader;

	public AbstractNGramModelReader(File file) throws IOException {
		String filename = file.getName();
		InputStream input = new FileInputStream(file);
		
		// 读取不同格式的文件
		if (filename.endsWith(".bin")) 	//二进制文件
			this.dataReader = new BinaryDataReader(input);
		else 	//文本文件
	    	this.dataReader = new TextDataReader(input);
		
	}
	
	public AbstractNGramModelReader(DataReader dataReader) {
		super();
		this.dataReader = dataReader;
	}

	/**
	 * 返回读取的n元模型  
	 * @return 读取的n元模型
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public NGramLanguageModel getModel() throws IOException, ClassNotFoundException {		
		return constructModel();
	}

	/**
	 * 重构n元模型  
	 * @return 读取n元模型
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public abstract NGramLanguageModel constructModel() throws IOException, ClassNotFoundException;
	
	public NGramModelEntry readNGramModelEntry() throws ClassNotFoundException, IOException {
		return dataReader.readNGramModelEntry();
	}
	
	public int readCount() throws IOException {
		return dataReader.readCount();
	}

	public String readUTF() throws IOException {
		return dataReader.readUTF();
	}
	
	public void close() throws IOException {
		dataReader.close();
	}
}
