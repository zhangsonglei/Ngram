package hust.tools.ngram.io;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import hust.tools.ngram.model.AbstractNGramModelWriter;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.utils.ARPAEntry;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.NGramModelEntry;

/**
 *<ul>
 *<li>Description: 将N元模型写入磁盘的抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月15日
 *</ul>
 */
public abstract class ARPANGramModelWriter extends AbstractNGramModelWriter{
	
	private int n;
	
	private HashMap<NGram, ARPAEntry> nGramLogProbability;
	
	private NGram[][] nGramTypes;	
	
	public ARPANGramModelWriter(NGramLanguageModel languageModel) {
		n = languageModel.getOrder();
		nGramLogProbability = languageModel.getnGramLogProbability();
		
		statNGramTypeAndCount();
	}
	
	/**
	 * 统计n元类型和n元类型数
	 */
	private void statNGramTypeAndCount() {
		nGramTypes = new NGram[n][];
		for(int i = 0; i < n; i++) 
			nGramTypes[i] = statTypeAndCount(nGramLogProbability, i + 1);
	}
	
	@Override
	public void persist() throws IOException {
		writeUTF("\n\\data\\");
		
		for(int i = 0; i < nGramTypes.length; i++) {
			writeUTF("ngram "+(i+1) + "=" + nGramTypes[i].length);
		}

		for(int i = 0; i < nGramTypes.length; i++) {
			writeUTF("\n\\" + (i + 1) + "-grams:");
			
			Arrays.sort(nGramTypes[i]);	
			for(int j = 0; j < nGramTypes[i].length; j++) {
				NGramModelEntry modelEntry;
				NGram nGram = nGramTypes[i][j];
				ARPAEntry entry = nGramLogProbability.get(nGram);
				
				double log_prob = entry.getLog_prob();
				double log_bo = entry.getLog_bo();
				
				
				if(0.0 == log_bo)
					modelEntry = new NGramModelEntry(log_prob, nGram);
				else
					modelEntry = new NGramModelEntry(log_prob, nGram, log_bo);
					writeNGramModelEntry(modelEntry);
			}
		}
		
		writeUTF("\n\\end\\");
		close();
	}
}
