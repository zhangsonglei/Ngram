package hust.tools.ngram.model;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.NGramCountEntry;
import hust.tools.ngram.utils.NGramModelEntry;
import hust.tools.ngram.utils.StringGram;

/**
 *<ul>
 *<li>Description: 读取二进制文件 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月26日
 *</ul>
 */
public class BinaryDataReader implements DataReader{
	
	private DataInputStream dis;

	public BinaryDataReader(File file) throws IOException {
	
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
	}


	public BinaryDataReader(InputStream in) {
		dis = new DataInputStream(in);
	}

	public BinaryDataReader(DataInputStream dis) {
		this.dis = dis;
	}

	@Override
	public int readCount() throws IOException {
		return dis.readInt();
	}

	@Override
	public String readUTF() throws IOException {
		return dis.readUTF();
	}

	@Override
	public NGramModelEntry readNGramModelEntry() throws IOException {
		String string = dis.readUTF();
		String[] strings = string.trim().split("\t");
		
		double log_prob = Double.parseDouble(strings[0]);
		
		String[] string_grams = null;
		if(strings.length > 1)
			string_grams = strings[1].split("\\s+");
		else
			string_grams = new String[] {""};
		
		Gram[] grams = new Gram[string_grams.length];
		for(int i = 0; i < grams.length; i++) 
			grams[i] = new StringGram(string_grams[i]);
		
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
		String string = dis.readUTF();
		if(string != null){
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
		dis.close();		
	}

}
