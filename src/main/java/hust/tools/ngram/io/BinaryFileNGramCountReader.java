package hust.tools.ngram.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import hust.tools.ngram.model.BinaryDataReader;

/**
 *<ul>
 *<li>Description: 从二进制文件中读取n元计数 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public class BinaryFileNGramCountReader extends NGramCountReader {

	public BinaryFileNGramCountReader(File file) throws IOException {
		super(new BinaryDataReader(file));
	}
	
	public BinaryFileNGramCountReader(DataInputStream dis) {
		super(new BinaryDataReader(dis));
	}

}
