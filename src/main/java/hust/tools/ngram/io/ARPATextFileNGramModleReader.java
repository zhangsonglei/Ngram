package hust.tools.ngram.io;

import java.io.File;
import java.io.IOException;

import hust.tools.ngram.model.DataReader;

/**
 *<ul>
 *<li>Description: 从arpa格式的文本文件中读取n元模型 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class ARPATextFileNGramModleReader extends ARPANGramModelReader {

	public ARPATextFileNGramModleReader(File file) throws IOException {
		super(file);
	}

	public ARPATextFileNGramModleReader(DataReader dataReader) {
		super(dataReader);
	}
}
