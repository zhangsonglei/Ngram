package hust.tools.ngram.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import hust.tools.ngram.utils.ARPAEntry;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.NGramModelEntry;

/**
 *<ul>
 *<li>Description: 写n元模型的抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public abstract class AbstractNGramModelWriter implements NGramModelWriter{
	
	private int n;
	
	private String smooth;
	
	private HashMap<NGram, ARPAEntry> nGramLogProbability;
	
	private NGram[][] nGramTypes;
	
	public AbstractNGramModelWriter() {
		super();
	}
		
	public AbstractNGramModelWriter(NGramLanguageModel languageModel) {
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

	/**
	 * 统计给定n元长度的所有n元类型
	 * @param map	n元与其概率的映射
	 * @param n		n元长度
	 * @return		给定n元长度下的所有n元类型
	 */
	protected NGram[] statTypeAndCount(HashMap<NGram, ARPAEntry> map, int n) {
		Set<NGram> nGrams = map.keySet();
		List<NGram> list = new LinkedList<>();
		
		for(NGram nGram : nGrams)
			if(nGram.length() == n)
				list.add(nGram);
		
		return list.toArray(new NGram[list.size()]);
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
