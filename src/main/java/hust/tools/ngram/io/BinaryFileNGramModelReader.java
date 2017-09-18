package hust.tools.ngram.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import hust.tools.ngram.model.BinaryDataReader;

/**
 *<ul>
 *<li>Description: 从二进制文件中读取模型 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class BinaryFileNGramModelReader extends NGramModelReader {

	public BinaryFileNGramModelReader(File file) throws IOException {
		super(new BinaryDataReader(file));
	}
	
	public BinaryFileNGramModelReader(DataInputStream dis) {
		super(new BinaryDataReader(dis));
	}
}
