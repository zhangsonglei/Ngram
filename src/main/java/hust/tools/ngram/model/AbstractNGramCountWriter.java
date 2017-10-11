package hust.tools.ngram.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.NGramCountEntry;

/**
 *<ul>
 *<li>Description: 写n元数量的抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public abstract class AbstractNGramCountWriter implements NGramCountWriter {

	public AbstractNGramCountWriter() {
		super();
	}
	
	private NGram[] nGrams;
	
	private int[] counts;
	
	public AbstractNGramCountWriter(NGramCounter counter) {
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