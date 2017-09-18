package hust.tools.ngram.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.datastructure.NGramCountEntry;
import hust.tools.ngram.model.AbstractNGramCountWriter;
import hust.tools.ngram.model.NGramCounter;

/**
 *<ul>
 *<li>Description: 写n元计数到文件的抽象类，实现写入方法 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public abstract class NGramCountWriter extends AbstractNGramCountWriter{
	
	private NGram[] nGrams;
	
	private int[] counts;
	
	public NGramCountWriter(NGramCounter counter) {
		nGrams = new NGram[counter.size()];
		counts = new int[counter.size()];
		int index = 0;
		Iterator<NGram> iterator = counter.iterator();
		while(iterator.hasNext()) {
			nGrams[index++] = iterator.next();
		}
		
		//不再需要，节省内存
		iterator = null;

		Arrays.sort(nGrams);
		
		for(int i = 0; i < nGrams.length; i++) {
			counts[i] = counter.getNGramCount(nGrams[i]);
		}
	}

	@Override
	public void persist() throws IOException {
		
		writeNumber(nGrams.length);
		
		for(int i = 0; i < nGrams.length; i++)
			writeCountEntry(new NGramCountEntry(nGrams[i], counts[i]));
		
		close();
	}
}
