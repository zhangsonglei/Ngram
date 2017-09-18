package hust.tools.ngram.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import hust.tools.ngram.model.TextDataReader;

/**
 *<ul>
 *<li>Description: 从普通文本文件中读取模型 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class TextFileNGramModelReader extends NGramModelReader {

	public TextFileNGramModelReader(BufferedReader bReader) {
		super(new TextDataReader(bReader));
	}

	public TextFileNGramModelReader(File file) throws IOException {
		super(new TextDataReader(file));
	}
}
