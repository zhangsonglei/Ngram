package hust.tools.ngram.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import hust.tools.ngram.datastructure.Gram;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.datastructure.NGramCountEntry;
import hust.tools.ngram.datastructure.NGramModelEntry;
import hust.tools.ngram.utils.StringGram;

/**
 *<ul>
 *<li>Description: 读取文本文件 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月26日
 *</ul>
 */
public class TextDataReader implements DataReader{

	private BufferedReader bReader;

	public TextDataReader(File file) throws IOException {
		bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file))));
	}

	public TextDataReader(InputStream in) {
		bReader = new BufferedReader(new InputStreamReader(in));
	}

	public TextDataReader(BufferedReader bReader) {
		this.bReader = bReader;
	}
	
	@Override
	public int readCount() throws IOException {
		return Integer.parseInt(bReader.readLine());
	}

	@Override
	public String readUTF() throws IOException {
		return bReader.readLine();
	}

	@Override
	public NGramModelEntry readNGramModelEntry() throws IOException {
		String string = bReader.readLine();
		String[] strings = string.trim().split("\t");
		
		double log_prob = Double.parseDouble(strings[0]);
		
		String[] string_grams = strings[1].split("\\s+");
		Gram[] grams = new Gram[string_grams.length];
		for(int i = 0; i < grams.length; i++) {
			grams[i] = new StringGram(string_grams[i]);
		}
		NGram nGram = new NGram(grams); 
		
		if(strings.length > 2) {
			double log_bo = Double.parseDouble(strings[2]);
			
			return new NGramModelEntry(log_prob, nGram, log_bo);
		}else{
			return new NGramModelEntry(log_prob, nGram);
		}
	}

	@Override
	public NGramCountEntry readNGramCountEntry() throws IOException {
		String string = bReader.readLine();
		if(string != null) {
			String[] strings = string.trim().split("\t");
			
			String[] string_grams = strings[0].split("\\s+");
			Gram[] grams = new Gram[string_grams.length];
			for(int i = 0; i < grams.length; i++) {
				grams[i] = new StringGram(string_grams[i]);
			}
			NGram nGram = new NGram(grams); 
			
			int count = Integer.parseInt(strings[1]);
			
			return new NGramCountEntry(nGram, count);
		}else
			return null;	
	}

	@Override
	public void close() throws IOException {
		bReader.close();
	}
}
