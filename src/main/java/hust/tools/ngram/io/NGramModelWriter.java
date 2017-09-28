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
 *<li>Description: 写n元模型的抽象类，实现写入方法 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public abstract class NGramModelWriter extends AbstractNGramModelWriter{
	
	private int n;
	
	private String smooth;
	
	private HashMap<NGram, ARPAEntry> nGramLogProbability;
	
	private NGram[][] nGramTypes;
		
	public NGramModelWriter(NGramLanguageModel languageModel) {
		n = languageModel.getOrder();
		smooth = languageModel.getSmooth();
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
		
		/**
		 * 写入平滑类型
		 */
		writeUTF(smooth);					
		
		/**
		 * 写入n元的最大长度
		 */
		writeCount(nGramTypes.length);					
		
		/**
		 * 写入不同长度n元类型的数量
		 */
		for(int i = 0; i < nGramTypes.length; i++) { 	
			writeCount(nGramTypes[i].length);
		}
		
		/**
		 * 根据n元长度从小到大，写入所有n元及其概率
		 */
		for(int i = 0; i < nGramTypes.length; i++) {
			Arrays.sort(nGramTypes[i]);	//对n元进行排序
			
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
		
		close();
	}
}
