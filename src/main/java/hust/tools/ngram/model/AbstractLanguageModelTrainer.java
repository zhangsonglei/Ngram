package hust.tools.ngram.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import hust.tools.ngram.datastructure.ARPAEntry;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.utils.GoodTuringCounts;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;

/**
 *<ul>
 *<li>Description: n元模型的抽象训练器
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月26日
 *</ul>
 */
public abstract class AbstractLanguageModelTrainer {
	
	/**
	 * 字典
	 */
	protected Vocabulary vocabulary;
	
	/**
	 * n元大小(1,2,3... unigram, bigram, trigram)
	 */
	protected int n;
	
	/**
	 * n元及其概率的映射
	 */
	protected HashMap<NGram, ARPAEntry> nGramLogProbability;
	
	/**
	 * 不同长度的所有n元
	 */
	protected NGram[][] nGramTypes;
	
	/**
	 * 不同长度的所有n元的个数
	 */
	protected int[] nGramTypeCounts;
	
	/**
	 * n元计数器
	 */
	protected NGramCounter nGramCounter;
	
	/**
	 * 双参数构造器，训练n-gram模型
	 * @param gramStream	训练语料
	 * @param n 			n元的最大长度
	 * @throws IOException
	 */
	public AbstractLanguageModelTrainer(GramStream gramStream, int  n) throws IOException {
		this.n = n;
		this.nGramLogProbability = new HashMap<NGram, ARPAEntry>();
		this.nGramTypes = new NGram[n][];
		this.nGramTypeCounts = new int[n];
		this.nGramCounter = new NGramCounter(gramStream, n);
		this.vocabulary = nGramCounter.vocabulary;
	}
	
	/**
	 * 双参数构造器，训练n-gram模型
	 * @param gramSentenceStream	分句处理的语料
	 * @param n						n元的最大长度
	 * @throws IOException
	 */
	public AbstractLanguageModelTrainer(GramSentenceStream gramSentenceStream, int  n) throws IOException {
		this.n = n;
		this.nGramLogProbability = new HashMap<NGram, ARPAEntry>();
		this.nGramTypes = new NGram[n][];
		this.nGramTypeCounts = new int[n];
		this.nGramCounter = new NGramCounter(gramSentenceStream, n);
		this.vocabulary = nGramCounter.vocabulary;
	}
	
	/**
	 * 双参数构造器，根据n元数量，训练n元模型
	 * @param nGramCounter	n元计数器
	 * @param n				n元最大长度
	 */
	public AbstractLanguageModelTrainer(NGramCounter nGramCounter, int n) {
		this.n = n;
		this.nGramLogProbability = new HashMap<NGram, ARPAEntry>();
		this.nGramTypes = new NGram[n][];
		this.nGramTypeCounts = new int[n];
		this.nGramCounter = nGramCounter;
		this.vocabulary = nGramCounter.vocabulary;
	}
	
	/**
	 * 计算每一个n元概率的对数，
	 * 根据不同的平滑方式，实现各自的训练方法
	 * @throws IOException 
	 */
	public abstract NGramLanguageModel trainModel();
	
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
	
	/**
	 * 使用最大似然估计计算给定n元在词汇表中的概率
	 * @param nGram 		待计算概率的n元
	 * @param nGramCount 	n元及其计数
	 * @return 				给定n元在词汇表中的最大似然概率
	 */
	protected double calcMLNGramProbability(NGram nGram, NGramCounter nGramCounter) {
		double prob = 0.0;
		int nCount = nGramCounter.getNGramCount(nGram);
		
		if(0 == nCount || 0 == nGram.length() || nGram == null)
			return prob;
		else if(nGram.length() == 1)
			prob = (double)nCount / nGramCounter.getTotalNGramCountByN(1);
		else
			prob = (double)nCount / nGramCounter.getNGramCount(nGram.removeLast());

		return prob;
	}
	
	/**
	 * 使用Good Turing平滑算法计算给定ngram在词汇表中的概率
	 * @param nGram					待计算概率的n元
	 * @param nGramCount			n元的计数
	 * @return						Good Turing平滑概率			
	 */
	protected double calcGoodTuringNGramProbability(NGram nGram, NGramCounter nGramCounter, GoodTuringCounts goodTuringCounts) {
		int n = nGram.length();
		if(n > 0) {
			int count = nGramCounter.getNGramCount(nGram);
			double prob = 0.0;
			double gtCount = 0.0;
			
			gtCount = goodTuringCounts.getDiscountCoeff(count, n) * count;
			if(n > 1) {
				prob =  gtCount / nGramCounter.getNGramCount(nGram.removeLast());
			}else
				prob = gtCount / nGramCounter.getTotalNGramCountByN(1);

			return prob;
		}else
			throw new RuntimeException("n元组不合法："+nGram);
	}
}
