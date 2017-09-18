package hust.tools.ngram.model;

import java.io.IOException;
import java.io.ObjectInputStream;

import hust.tools.ngram.datastructure.NGramCountEntry;
import hust.tools.ngram.datastructure.NGramModelEntry;

/**
 *<ul>
 *<li>Description: 读取对象文件，反序列化 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月26日
 *</ul>
 */
public class ObjectDataReader implements DataReader {

	private ObjectInputStream ois;

	public ObjectDataReader(ObjectInputStream ois) {
		this.ois = ois;
	}
	
	@Override
	public int readCount() throws IOException {
		return ois.readInt();
	}

	@Override
	public String readUTF() throws IOException {
		return ois.readUTF();
	}

	@Override
	public NGramModelEntry readNGramModelEntry() throws IOException, ClassNotFoundException {
		return (NGramModelEntry) ois.readObject();
	}

	@Override
	public NGramCountEntry readNGramCountEntry() throws IOException, ClassNotFoundException {
		return (NGramCountEntry) ois.readObject();
	}

	@Override
	public void close() throws IOException {
		ois.close();
	}

}
