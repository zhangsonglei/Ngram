package hust.tools.ngram.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import hust.tools.ngram.datastructure.NGramCountEntry;
import hust.tools.ngram.model.NGramCounter;

/**
 *<ul>
 *<li>Description: 以序列化的形式将n元计数写入文件 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class ObjectFileNGramCountWriter extends NGramCountWriter {

	ObjectOutputStream oos;
	
	public ObjectFileNGramCountWriter(NGramCounter counter, ObjectOutputStream oos) {
		super(counter);
		this.oos = oos;
	}

	public ObjectFileNGramCountWriter(NGramCounter counter, File file) throws FileNotFoundException, IOException {
		super(counter);
		
		oos = new ObjectOutputStream(new FileOutputStream(file));
	}
	
	@Override
	public void writeCountEntry(NGramCountEntry entry) throws IOException {
		oos.writeObject(entry);
	}


	@Override
	public void writeNumber(int number) throws IOException {
		oos.writeInt(number);
	}
	
	@Override
	public void close() throws IOException {
		oos.close();
	}

}
