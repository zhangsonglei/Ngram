package hust.tools.ngram.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import hust.tools.ngram.utils.ARPAEntry;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.PseudoWord;

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
	private HashMap<Integer, HashMap<Integer, Integer>> countOfCounts;
	
	/**
	 * 历史前缀数量，所有的*(n-1)Gram数量
	 */
	private HashMap<NGram, Integer> prefixCount;
	
	/**
	 * 历史接续数量 ,所有的*(n-2Gram)*的类型数
	 */
	private HashMap<NGram, Integer> continuationCount;
	
	/**
	 * 不同长度的n元的折扣系数
	 */
	private double[] dicountCoeff;
	
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
		prefixCount = new HashMap<>();
		continuationCount = new HashMap<>();
		countOfCounts = new HashMap<>();
		dicountCoeff = new double[n];
		statisticsNGramHistory();
		
		calcDiscount();
		
		Iterator<NGram> iterator = nGramCounter.iterator();
		//对于最高阶直接使用原始计数计算平滑概率
		while(iterator.hasNext()) { 
			NGram nGram = iterator.next();
			if(nGram.length() != n || (nGram.length() > 2 && nGramCounter.getNGramCount(nGram) < 2)) //不是最高阶或者n > 2的n元组若计数为1 直接忽略
				continue;
			
			double prob = calcKNNGramProbability(nGram);
			ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
			nGramLogProbability.put(nGram, entry);
		}

		
		if(n > 1) {
			//低阶使用折扣后的计数计算平滑概率
			iterator = nGramCounter.iterator();
			while(iterator.hasNext()) {//更新低阶的计数
				NGram nGram = iterator.next();
				if(nGram.startWith(PseudoWord.sentStart) || nGram.length() == n)
					continue;
				
				nGramCounter.setNGramCount(nGram, getPrefixCount(nGram));
			}
			
			prefixCount = new HashMap<>();
			continuationCount = new HashMap<>();
			countOfCounts = new HashMap<>();
			dicountCoeff = new double[n];
			statisticsNGramHistory();
			calcDiscount();

			iterator = nGramCounter.iterator();
			while(iterator.hasNext()) {//计算低阶的平滑概率
				NGram nGram = iterator.next();
				if(nGram.length() == n || (nGram.length() > 2 && nGramCounter.getNGramCount(nGram) < 2))	//n > 2的n元组若计数为1 直接忽略
					continue;
				
				double prob = calcKNNGramProbability(nGram);
				ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
				nGramLogProbability.put(nGram, entry);
			}

			//统计历史后缀
			statisticsNGramHistorySuffix();
			
			//计算回退权重
			for(Entry<NGram, ARPAEntry> entry : nGramLogProbability.entrySet()) {
				NGram nGram = entry.getKey();
				double bow = calcBOW(nGram);
				if(!(Double.isNaN(bow) || Double.isInfinite(bow)))
					entry.getValue().setLog_bo(Math.log10(bow));
			}
		}
		
		if(vocabulary.isSentence()) {
			//给开始标签一个较小的概率
			ARPAEntry StartEntry = new ARPAEntry(-99, 0.0);
			nGramLogProbability.put(PseudoWord.sentStart, StartEntry);
		}
		
		//增加一个未登录词<unk>，计数为词典大小（不算开始标签）乘以unigram的折扣率
		int M = nGramCounter.getTotalNGramCountByN(1);
		int count = vocabulary.size();
		if(vocabulary.contains(PseudoWord.End) && vocabulary.contains(PseudoWord.Start)) {
			M -= nGramCounter.getNGramCount(PseudoWord.sentStart);
		}
		double oovProb = (count*getDiscount(1) - getDiscount(1)) / M;
		ARPAEntry OOVEntry = new ARPAEntry(Math.log10(oovProb), 0.0);
		nGramLogProbability.put(PseudoWord.oovNGram, OOVEntry);
		
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
			int M = nGramCounter.getTotalNGramCountByN(1);
			if(vocabulary.contains(PseudoWord.End) && vocabulary.contains(PseudoWord.Start))
				M -= nGramCounter.getNGramCount(PseudoWord.sentStart);
			prob = Math.max(nGramCounter.getNGramCount(nGram) - getDiscount(order), 0) / M;
		}else if(order == n) {
			prob = Math.max(nGramCounter.getNGramCount(nGram) - getDiscount(order), 0) / nGramCounter.getNGramCount(nGram.removeLast());
		}else if(order > 1) {
			if(nGram.startWith(PseudoWord.sentStart))
				prob = Math.max(nGramCounter.getNGramCount(nGram) - getDiscount(order), 0) / nGramCounter.getNGramCount(nGram.removeLast());
			else
				prob = Math.max(nGramCounter.getNGramCount(nGram) - getDiscount(order), 0) / getContinuationCount(nGram.removeLast());
		}
		
		return prob;
	}
	
	/**
	 * 统计n元历史信息
	 */
	private void statisticsNGramHistory() {
		Iterator<NGram> iterator = nGramCounter.iterator();
		
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			int order = nGram.length();
			
			//统计不同长度n元出现1次和2次的类型数量
			int count = nGramCounter.getNGramCount(nGram);
			if(count <= 2) {
				if(countOfCounts.containsKey(count)) {
					if(countOfCounts.get(count).containsKey(order)) {
						int Nr = countOfCounts.get(count).get(order);
						countOfCounts.get(count).put(order, Nr + 1);
					}else
						countOfCounts.get(count).put(order, 1);
				}else {
					HashMap<Integer, Integer> map = new HashMap<>();
					map.put(order, 1);
					countOfCounts.put(count, map);
				}
			}
						
			if(order > 1){
				//统计历史前缀数量
				NGram _nGram = nGram.removeFirst();
				if(prefixCount.containsKey(_nGram))
					prefixCount.put(_nGram, prefixCount.get(_nGram) + 1);
				else
					prefixCount.put(_nGram, 1);
				
				//统计历史接续数量
				if(order > 2) {
					NGram _n_Gram = nGram.getSubNGramRemovedBoundary(nGram);
					if(continuationCount.containsKey(_n_Gram))
						continuationCount.put(_n_Gram, continuationCount.get(_n_Gram) + 1);
					else
						continuationCount.put(_n_Gram, 1);
				}
			}//end if
		}//end while
	}
	
	private int getPrefixCount(NGram nGram) {
		if(prefixCount.containsKey(nGram))
			return prefixCount.get(nGram);
		return 0;
	}
	
	private int getContinuationCount(NGram nGram) {
		if(continuationCount.containsKey(nGram))
			return continuationCount.get(nGram);
		return 0;
	}
	
	
	/**
	 * 返回给定n元长度的折扣数
	 * @param order	给定的n元的长度
	 * @return		给定n元长度的折扣数
	 */
	private double getDiscount(int order) {
		if(order >0 || order <= n)
			return dicountCoeff[order - 1];
		else
			return 0.0;
	}
	
	/**
	 * 计算不同n元的折扣数
	 */
	private void calcDiscount(){
		for(int i = 1; i <= n; i++) {
			int n1 = 0;
			if(countOfCounts.containsKey(1)) {
				if(countOfCounts.get(1).containsKey(i)) {
					n1 = countOfCounts.get(1).get(i);	
				}else {
					System.out.println("出现次数为1的"+i+"元为0，训练终止");
					System.exit(0);
				}
			}else {
				System.out.println("没有出现1次的n元，训练终止");
				System.exit(0);
			}
			
			int n2 = 0;
			if(countOfCounts.containsKey(2)) {
				if(countOfCounts.get(2).containsKey(i)) {
					n2 = countOfCounts.get(2).get(i);
				}else {
					System.out.println("出现次数为2的"+i+"元为0，训练终止");
					System.exit(0);
				}
			}else {
				System.out.println("没有出现2次的n元，训练终止");
				System.exit(0);
			}
					
			dicountCoeff[i - 1] = n1 / (n1 + 2.0 * n2);
		}
	}
	
	@SuppressWarnings("unused")
	private void showDiscount() {
		for(int i = 0; i < dicountCoeff.length; i++) {
			System.out.println("discount"+i+": "+dicountCoeff[i]);
		}
	}
}
