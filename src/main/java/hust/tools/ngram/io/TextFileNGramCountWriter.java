package hust.tools.ngram.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import hust.tools.ngram.model.NGramCounter;
import hust.tools.ngram.utils.NGramCountEntry;

/**
 *<ul>
 *<li>Description: 以可读文本的形式将n元的计数写入文件 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public class TextFileNGramCountWriter extends NGramCountWriter{

	private BufferedWriter bwriter;
	
	public TextFileNGramCountWriter(NGramCounter counter, File file) throws IOException {
		super(counter);
		bwriter = new BufferedWriter(new FileWriter(file));
	}
	
	public TextFileNGramCountWriter(NGramCounter counter, BufferedWriter bwriter) {
		super(counter);
		this.bwriter = bwriter;
	}
	
	@Override
	public void writeCountEntry(NGramCountEntry entry) throws IOException {
		bwriter.write(entry.toString());
		bwriter.newLine();
	}
	
	@Override
	public void writeNumber(int number) throws IOException {
		bwriter.write(Integer.toString(number));
		bwriter.newLine();
	}
	
	@Override
	public void close() throws IOException {
		bwriter.flush();
		bwriter.close();
	}
}
