package hust.tools.ngram.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import hust.tools.ngram.datastructure.NGramModelEntry;
import hust.tools.ngram.model.NGramLanguageModel;

/**
 *<ul>
 *<li>Description: 将n元模型以arpa格式写入文本文件中 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class ARPATextFileNGramModleWriter extends ARPANGramModelWriter{

	private BufferedWriter bWriter;
	
	public ARPATextFileNGramModleWriter (NGramLanguageModel model, File file) throws IOException {
		super(model);
		bWriter = new BufferedWriter(new FileWriter(file));
	}

	public ARPATextFileNGramModleWriter (NGramLanguageModel model, String filePath) throws IOException {
		super(model);
		bWriter = new BufferedWriter(new FileWriter(filePath));
	}
	
	public ARPATextFileNGramModleWriter (NGramLanguageModel model, BufferedWriter bWriter) throws IOException {
		super(model);
		this.bWriter = bWriter;
	}

	@Override
	public void writeNGramModelEntry(NGramModelEntry entry) throws IOException {
		bWriter.write(entry.toString());
		bWriter.newLine();		
	}

	@Override
	public void writeUTF(String string) throws IOException {
		bWriter.write(string);
		bWriter.newLine();
	}
	
	@Override
	public void writeCount(int count) throws IOException {
		bWriter.write(Integer.toString(count));
		bWriter.newLine();
	}
	
	@Override
	public void close () throws IOException {
		bWriter.flush();
		bWriter.close();
	}
}
