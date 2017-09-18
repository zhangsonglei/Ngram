package hust.tools.ngram.io;

import java.io.ObjectInputStream;

import hust.tools.ngram.model.ObjectDataReader;

/**
 *<ul>
 *<li>Description: 从序列化的文件中反序列化读取n元计数
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class ObjectFileNGramCountReader extends NGramCountReader{
	
	public ObjectFileNGramCountReader(ObjectInputStream ois) {
		super(new ObjectDataReader(ois));
	}
}
