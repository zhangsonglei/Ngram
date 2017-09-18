package hust.tools.ngram.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import hust.tools.ngram.model.TextDataReader;

/**
 *<ul>
 *<li>Description: 从可读计数文本文件中读取n元计数 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class TextFileNGramCountReader extends NGramCountReader{

	public TextFileNGramCountReader(BufferedReader bReader) {
		super(new TextDataReader(bReader));
	}

	public TextFileNGramCountReader(File file) throws IOException {
		super(new TextDataReader(file));
	}
}
