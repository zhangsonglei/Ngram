package hust.tools.ngram.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import hust.tools.ngram.datastructure.ARPAEntry;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.datastructure.PseudoWord;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;

/**
 *<ul>
 *<li>Description: 使用加法平滑方法训练模型，默认是加一平滑， 0 < delta <= 1.0
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年8月20日
 *</ul>
 */
public class LaplaceLanguageModelTrainer extends AbstractLanguageModelTrainer {
	
	private final double DEFAULT_DELTA = 1.0;
	
	private double delta;	

	public LaplaceLanguageModelTrainer(GramStream gramStream, int  order) throws IOException {
		super(gramStream, order);
		delta = DEFAULT_DELTA;
	}
	
	public LaplaceLanguageModelTrainer(GramSentenceStream gramSentenceStream, int order) throws IOException {
		super(gramSentenceStream, order);
		delta = DEFAULT_DELTA;
	}

	public LaplaceLanguageModelTrainer(NGramCounter nGramCounter, int order) {
		super(nGramCounter, order);
		delta = DEFAULT_DELTA;
	}
	
	public LaplaceLanguageModelTrainer(GramStream gramStream, int  order, double delta) throws IOException {
		super(gramStream, order);
		this.delta = (delta > DEFAULT_DELTA || delta <= 0) ? DEFAULT_DELTA : delta;
	}
	
	public LaplaceLanguageModelTrainer(GramSentenceStream gramSentenceStream, int order, double delta) throws IOException {
		super(gramSentenceStream, order);
		this.delta = (delta > DEFAULT_DELTA || delta <= 0) ? DEFAULT_DELTA : delta;
	}
	
	public LaplaceLanguageModelTrainer(NGramCounter nGramCounter, int order, double delta) {
		super(nGramCounter, order);
		this.delta = (delta > DEFAULT_DELTA || delta <= 0) ? DEFAULT_DELTA : delta;
	}

	/**
	 * 加法平滑中，模型参数含义：
	 * log_prob	  nGram    log_bo
	 * n元组概率的对数    n元组    n元组在训练语料中的计数
	 */
	@Override
	public NGramLanguageModel trainModel() {		
		Iterator<NGram> iterator = nGramCounter.iterator();
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			if(nGram.length() > 2 && nGramCounter.getNGramCount(nGram) < 2)//高阶(n > 2)n元组计数小于2 的忽略
				continue;
			
			double prob = calcLaplaceNGramProbability(nGram);
			ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
			nGramLogProbability.put(nGram, entry);
		}
		
		//增加一个未登录词<unk>，计数为0
		ARPAEntry OOVEntry = new ARPAEntry(Math.log10(calcLaplaceNGramProbability(PseudoWord.oovNGram)), 0.0);
		nGramLogProbability.put(PseudoWord.oovNGram, OOVEntry);
		
		if(vocabulary.isSentence()) {
			//给开始标签一个较小的概率
			ARPAEntry StartEntry = new ARPAEntry(-99, 0.0);
			nGramLogProbability.put(PseudoWord.sentStart, StartEntry);
		}

		//统计历史前缀
		statisticsNGramHistorySuffix();
		
		//计算回退权重
		for(Entry<NGram, ARPAEntry> entry : nGramLogProbability.entrySet()) {
			NGram nGram = entry.getKey();
			double bow = calcBOW(nGram);
			entry.getValue().setLog_bo(Math.log10(bow));
		}
		
		nGramTypeCounts = new int[n];
		nGramTypes = new NGram[n][];
		for(int i = 0; i < n; i++) {
			nGramTypes[i] = statTypeAndCount(nGramLogProbability, i + 1);
			nGramTypeCounts[i] = nGramTypes[i].length;
		}
		
		return new NGramLanguageModel(nGramLogProbability, n, "laplace", vocabulary);
	}
	
	/**
	 * 使用add平滑算法计算给定ngram的概率
	 * @param nGram			待计算概率的n元
	 * @param nGramCounter	n元的计数器
	 * @param size			字典的大小
	 * @param delta			n元计数增加的大小
	 * @return				add平滑概率
	 */
	private double calcLaplaceNGramProbability(NGram nGram) {
		double prob = 0.0;
		int nCount = nGramCounter.getNGramCount(nGram);
		int V = vocabulary.size();
		int M = nGramCounter.getTotalNGramCountByN(1);
		if(vocabulary.isSentence())
			M -= nGramCounter.getNGramCount(PseudoWord.sentStart);
		
		if(0 == nGram.length() || nGram == null)
			return prob;
		else if(nGram.length() == 1) { 
			//prob = (c(n) + 1)/(M + N)
			prob = (nCount + delta)/(M + V * delta);
		}else if(nGram.length() > 1){
			int n_Count = nGramCounter.getNGramCount(nGram.removeLast());
			//prob = (c(n) + 1)/(c(n-1) + N)
			prob = (nCount + delta) / (n_Count + V * delta);
		}

		return prob;
	}
}
