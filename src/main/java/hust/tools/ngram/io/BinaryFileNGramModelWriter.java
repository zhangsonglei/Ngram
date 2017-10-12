package hust.tools.ngram.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hust.tools.ngram.model.AbstractNGramModelWriter;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.utils.NGramModelEntry;

/**
 *<ul>
 *<li>Description: 以二进制的形式将模型写入文件中 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public class BinaryFileNGramModelWriter extends AbstractNGramModelWriter{

	private DataOutputStream dos;
	
	public BinaryFileNGramModelWriter (NGramLanguageModel model, File file) throws IOException {
		super(model);
		dos = new DataOutputStream(new FileOutputStream(file));
	}
	
	public BinaryFileNGramModelWriter (NGramLanguageModel model, DataOutputStream dos) throws IOException {
		super(model);
		this.dos = dos;
	}

	@Override
	public void writeNGramModelEntry(NGramModelEntry entry) throws IOException {
		dos.writeUTF(entry.toString());
	}

	@Override
	public void writeUTF(String string) throws IOException {
		dos.writeUTF(string);
	}

	@Override
	public void writeCount(int count) throws IOException {
		dos.writeInt(count);
	}

	@Override
	public void close() throws IOException {
		dos.flush();
		dos.close();
	}
}
