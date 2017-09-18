package hust.tools.ngram.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
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
	private GoodTuringCounts goodTuringCounts;
	
	/**
	 * n元的历史后缀
	 */
	private HashMap<NGram, Set<Gram>> nGramSuffix;
	
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
		nGramSuffix = new HashMap<>();
		goodTuringCounts = new GoodTuringCounts(nGramCounter.getNGramCountMap(), n);
		statisticsNGramHistorySuffix();
		Iterator<NGram> iterator = nGramCounter.iterator();
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			if(nGram.length() > 2 && nGramCounter.getNGramCount(nGram) < 2)//高阶(n > 2)n元组计数小于2 的忽略
				continue;
			
			double prob = calcGoodTuringNGramProbability(nGram, nGramCounter, goodTuringCounts);
			ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
			
			nGramLogProbability.put(nGram, entry);
		}
		
		for(Entry<NGram, ARPAEntry> entry : nGramLogProbability.entrySet()) {
			NGram nGram = entry.getKey();
			if(nGram.length() > 1) {
				NGram n_Gram = nGram.removeLast();
				double alpha = calcAlpha(n_Gram);
				
				if(alpha > 0.0)
					nGramLogProbability.get(n_Gram).setLog_bo(Math.log10(alpha));
			}			
		}
		
		double prob = 1.0 / nGramCounter.getTotalNGramCountByN(1);
		ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
		nGramLogProbability.put(PseudoWord.oovNGram, entry);
		
		nGramTypeCounts = new int[n];
		nGramTypes = new NGram[n][];
		for(int i = 0; i < n; i++) {
			nGramTypes[i] = statTypeAndCount(nGramLogProbability, i + 1);
			nGramTypeCounts[i] = nGramTypes[i].length;
		}
		
		return new NGramLanguageModel(nGramLogProbability, n, "katz", vocabulary);
	}
	
//	/**
//	 * 使用Good Turing平滑算法计算给定ngram在词汇表中的概率
//	 * @param nGram					待计算概率的n元
//	 * @param nGramCount			n元的计数
//	 * @return						Good Turing平滑概率			
//	 */
//	private double calcGoodTuringNGramProbability(NGram nGram, NGramCounter nGramCounter, GoodTuringCounts goodTuringCounts) {
//		int n = nGram.length();
//		if(n > 0) {
//			int r = nGramCounter.getNGramCount(nGram);
//			double prob = 0.0;
//			double gtCount = 0.0;
//			
//			gtCount = goodTuringCounts.getDiscountCoeff(r, n) * r;
//			if(n > 1) {
//				prob =  gtCount / nGramCounter.getNGramCount(nGram.removeLast());
//			}else
//				prob = gtCount / nGramCounter.getTotalNGramCountByN(1);
//
//			return prob;
//		}else
//			throw new RuntimeException("n元组不合法："+nGram);
//	}
	
	/**
	 * 计算Katz估计中的参数alpha
	 * @param countOfNGramTimesSeen
	 * @param nGram
	 * @return 参数alpha
	 */
	private  double calcAlpha(NGram nGram){	
		double numerator = 0.0;
		double denominator = 0.0;
		
		Set<Gram> suffixs = getNGramHistorySuffix(nGram);
		if(suffixs != null) {
			for(Gram gram : suffixs) {
				NGram ngram = nGram.addLast(gram);
				if(nGramLogProbability.containsKey(ngram))
					numerator += Math.pow(10, nGramLogProbability.get(nGram.addLast(gram)).getLog_prob());
				
				ngram = nGram.addLast(gram).removeFirst();
				if(nGramLogProbability.containsKey(ngram))
					denominator += Math.pow(10, nGramLogProbability.get(nGram.addLast(gram).removeFirst()).getLog_prob());
			}
			
			return (1 - numerator) / (1 - denominator);
		}else
			return 0.0;
	}
	
	/**
	 * 返回给定n元的历史后缀链表 
	 * @param nGram 待求历史后缀的n元
	 * @return 给定n元的历史后缀链表
	 */
	private Set<Gram> getNGramHistorySuffix(NGram nGram) {
		if(nGramSuffix.containsKey(nGram))
			return nGramSuffix.get(nGram);
		return null;
	}
	
	/**
	 * 统计n元的历史后缀的类型
	 */
	private void statisticsNGramHistorySuffix() {
		Iterator<NGram> iterator = nGramCounter.iterator();
		
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			NGram n_Gram = nGram.removeLast();
			if(nGram.length() > 1){
				Gram suffix = nGram.getGram(nGram.length() - 1);
				if(nGramSuffix.containsKey(n_Gram)) {
					nGramSuffix.get(n_Gram).add(suffix);
				}else{
					Set<Gram> suffix_list = new HashSet<>();
					suffix_list.add(suffix);
					nGramSuffix.put(n_Gram, suffix_list);
				}//end if-else
			}
		}//end if
	}
}
