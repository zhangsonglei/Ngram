package hust.tools.ngram.io;

import java.io.ObjectInputStream;

import hust.tools.ngram.model.ObjectDataReader;

/**
 *<ul>
 *<li>Description: 从序列化文件中反序列化模型 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class ObjectFileNGramModelReader extends NGramModelReader {

	public ObjectFileNGramModelReader(ObjectInputStream ois) {
		super(new ObjectDataReader(ois));
	}
}
