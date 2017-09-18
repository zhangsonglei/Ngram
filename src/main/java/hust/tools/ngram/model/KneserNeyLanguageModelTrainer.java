package hust.tools.ngram.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import hust.tools.ngram.datastructure.ARPAEntry;
import hust.tools.ngram.datastructure.Gram;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.datastructure.PseudoWord;
import hust.tools.ngram.utils.GoodTuringCounts;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;

/**
 *<ul>
 *<li>Description: 使用Kneser-Ney平滑方法训练模型
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年8月20日
 *</ul>
 */
public class KneserNeyLanguageModelTrainer extends AbstractLanguageModelTrainer{
	
	/**
	 * n元出现频率的频率（出现r次的n元的个数）
	 */
	private GoodTuringCounts countOfCounts;
	
	/**
	 * n元的历史后缀
	 */
	private HashMap<NGram, Set<Gram>> nGramSuffix;
	
	public KneserNeyLanguageModelTrainer(GramStream gramStream, int  n) throws IOException {
		super(gramStream, n);
	}
	
	public KneserNeyLanguageModelTrainer(GramSentenceStream gramSentenceStream, int  n) throws IOException {
		super(gramSentenceStream, n);
	}
	
	public KneserNeyLanguageModelTrainer(NGramCounter nGramCounter, int n) {
		super(nGramCounter, n);
	}

	/**
	 * Kneser-Ney平滑中，模型参数含义：
	 * log_prob	  nGram    log_bo
	 * n元组概率的对数    n元组               n元组的回退权重的对数
	 */
	@Override
	public NGramLanguageModel trainModel() {
		nGramSuffix = new HashMap<>();
		statisticsNGramHistory(nGramCounter);
		countOfCounts = new GoodTuringCounts(nGramCounter.getNGramCountMap(), n);
		
		Iterator<NGram> iterator = nGramCounter.iterator();
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			int order = nGram.length();
			if(order > 2 && nGramCounter.getNGramCount(nGram) < 2)//高阶(n > 2)n元组计数小于2 的忽略
				continue;
			
			double prob = calcKNNGramProbability(nGram);
			double logBo = 0.0;
			if(prob <= 0) {
				System.out.println(nGram+":"+nGramCounter.getNGramCount(nGram)+":"+prob);
			}
			
			if(order < n) {
				double gamma = calcGamma(nGram);

				logBo = Math.log10(gamma);
			}
			
			ARPAEntry entry = new ARPAEntry(Math.log10(prob), logBo);
			
			nGramLogProbability.put(nGram, entry);
		}
		ARPAEntry entry = new ARPAEntry(Math.log10(1.0 / nGramCounter.getTotalNGramCountByN(1)), 0.0);
		nGramLogProbability.put(PseudoWord.oovNGram, entry);		
		
		nGramTypeCounts = new int[n];
		nGramTypes = new NGram[n][];
		for(int i = 0; i < n; i++) {
			nGramTypes[i] = statTypeAndCount(nGramLogProbability, i + 1);
			nGramTypeCounts[i] = nGramTypes[i].length;
		}
		
		return new NGramLanguageModel(nGramLogProbability, n, "kn", vocabulary);
	}

	/**
	 * 使用Kneser-Ney平滑算法计算给定ngram在词汇表中概率的对数
	 * @param ngram			待计算概率对数的n元
	 * @param nGramCount	n元的计数
	 * @return 				Kneser-Ney平滑概率
	 */
	private double calcKNNGramProbability(NGram nGram) {
		double prob = 0.0;		
		int order = nGram.length();

		if(order == 1) {
			prob = calcMLNGramProbability(nGram, nGramCounter);
		}else if(order > 1) {
			NGram n_Gram = nGram.removeLast();
			
			prob = Math.max(nGramCounter.getNGramCount(nGram) * 1.0 - getDiscount(nGram), 0) / getnGramSuffixTotalCount(n_Gram);
		}else
			throw new RuntimeException("n元长度小于1");
		
		return prob;
	}
	
	
	/**
	 * 计算给定n元组的回退权重
	 * @param nGram	给定n元组
	 * @return		给定n元组的回退权重
	 */
	private double calcGamma(NGram nGram) {
		int order = nGram.length();
		if(order > 1) {
			NGram n_Gram = nGram.removeLast();
			int total = getnGramSuffixTotalCount(n_Gram);
			int count = getHistorySuffixCount(n_Gram);
			return getDiscount(nGram) * count / total;
		}else {
			return getDiscount(nGram) * nGramCounter.getNGramCount(nGram)*nGramCounter.getNGramTypeCountByN(1) / nGramCounter.getTotalNGramCountByN(1);
		}		
	}
	
	/**
	 * 返回给定n元组的折扣率
	 * @param nGram	给定n元组
	 * @return		给定n元组的折扣率
	 */
	private double getDiscount(NGram nGram){
		int order = nGram.length();
		double n1 = countOfCounts.getNr(1, order);
		double n2 = countOfCounts.getNr(2, order);

		return n1 / (n1 + 2.0 * n2);
	}
	
	/**
	 * 统计n元的历史前后缀的类型
	 * @param nGram
	 */
	private void statisticsNGramHistory(NGramCounter nGramCounter) {
		Iterator<NGram> iterator = nGramCounter.iterator();
		
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			
			if(nGram.length() > 1){
				//统计历史后缀
				NGram n_Gram = nGram.removeLast();
				Gram suffix = nGram.getGram(nGram.length() - 1);
				if(nGramSuffix.containsKey(n_Gram)) {
					nGramSuffix.get(n_Gram).add(suffix);
				}else{
					Set<Gram> suffix_list = new HashSet<>();
					suffix_list.add(suffix);
					nGramSuffix.put(n_Gram, suffix_list);
				}
			}
		}
	}

	/**
	 * 返回给定n元历史后缀的数量
	 * @param nGram 给定n元
	 * @return 数量
	 */
	private int getHistorySuffixCount(NGram nGram) {
		if(nGramSuffix.containsKey(nGram))
			return nGramSuffix.get(nGram).size();
		
		return 0;
	}
	
	/**
	 * 返回n元与其所有历史后缀组成的n+1元的数量之和
	 * @return 总数量
	 */
	private int getnGramSuffixTotalCount(NGram nGram) {
		int total = 0;
		Set<Gram> grams = nGramSuffix.get(nGram);
		if(grams != null)
			for(Gram gram : grams)
				total += nGramCounter.getNGramCount(nGram.addLast(gram));
		
		return total;
	}
}
