package hust.tools.ngram.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import hust.tools.ngram.utils.ARPAEntry;
import hust.tools.ngram.utils.GoodTuringCounts;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.PseudoWord;

/**
 *<ul>
 *<li>Description: 使用Katz回退平滑方法训练模型
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年8月20日
 *</ul>
 */
public class KatzLanguageModelTrainer extends AbstractLanguageModelTrainer{
	
	/**
	 * n元出现频率的频率（出现r次的n元的个数）
	 */
	private GoodTuringCounts countOfCounts;
	
	public KatzLanguageModelTrainer(GramStream gramStream, int  n) throws IOException {
		super(gramStream, n);
	}
	
	public KatzLanguageModelTrainer(GramSentenceStream gramSentenceStream, int  n) throws IOException {
		super(gramSentenceStream, n);
	}
	
	public KatzLanguageModelTrainer(NGramCounter nGramCounter, int n) {
		super(nGramCounter, n);
	}

	/**
	 * Katz平滑中，模型参数含义：
	 * log_prob	  nGram    log_bo
	 * n元组概率的对数    n元组               n元组的回退权重的对数
	 */
	@Override
	public NGramLanguageModel trainModel() {
		countOfCounts = new GoodTuringCounts(nGramCounter.getNGramCountMap(), n);

		Iterator<NGram> iterator = nGramCounter.iterator();
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			if(nGram.length() > 2 && nGramCounter.getNGramCount(nGram) < 2)//高阶(n > 2)n元组计数小于2 的忽略
				continue;
			
			double prob = calcKatzNGramProbability(nGram);
			ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
			nGramLogProbability.put(nGram, entry);
		}
		
		//增加一个未登录词<unk>，计数为1
		double prob = 1.0 * countOfCounts.getDiscountCoeff(1, 1) / nGramCounter.getTotalNGramCountByN(1);
		ARPAEntry OOVEntry = new ARPAEntry(Math.log10(prob), 0.0);
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
			if(bow > 0.0)
				entry.getValue().setLog_bo(Math.log10(bow));
		}
		
		return new NGramLanguageModel(nGramLogProbability, n, "katz", vocabulary);
	}
	
	/**
	 * 使用Good Turing平滑算法计算给定ngram在词汇表中的概率
	 * @param nGram					待计算概率的n元
	 * @param nGramCount			n元的计数
	 * @return						Good Turing平滑概率			
	 */
	private double calcKatzNGramProbability(NGram nGram) {
		int order = nGram.length();
		if(order > 0) {
			int count = nGramCounter.getNGramCount(nGram);
			double prob = 0.0;
			double gtCount = 0.0;
			
			gtCount = countOfCounts.getDiscountCoeff(count, order) * count;
			if(order > 1) {
				prob =  gtCount / nGramCounter.getNGramCount(nGram.removeLast());
			}else
				prob = gtCount / nGramCounter.getTotalNGramCountByN(1);

			return prob;
		}else
			throw new RuntimeException("n元组不合法："+nGram);
	}
}
