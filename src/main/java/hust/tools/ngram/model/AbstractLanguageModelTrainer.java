package hust.tools.ngram.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import hust.tools.ngram.utils.ARPAEntry;
import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;
import hust.tools.ngram.utils.NGram;

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
	 * n元计数器
	 */
	protected NGramCounter nGramCounter;
	
	/**
	 * n元的历史后缀
	 */
	protected HashMap<NGram, Set<Gram>> nGramSuffix;
	
	/**
	 * 双参数构造器，训练n-gram模型
	 * @param gramStream	训练语料
	 * @param n 			n元的最大长度
	 * @throws IOException
	 */
	public AbstractLanguageModelTrainer(GramStream gramStream, int  n) throws IOException {
		this.n = n;
		this.nGramLogProbability = new HashMap<NGram, ARPAEntry>();
		this.nGramCounter = new NGramCounter(gramStream, n);
		this.vocabulary = nGramCounter.vocabulary;
		this.nGramSuffix = new HashMap<>();
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
		this.nGramCounter = new NGramCounter(gramSentenceStream, n);
		this.vocabulary = nGramCounter.vocabulary;
		this.nGramSuffix = new HashMap<>();
	}
	
	/**
	 * 双参数构造器，根据n元数量，训练n元模型
	 * @param nGramCounter	n元计数器
	 * @param n				n元最大长度
	 */
	public AbstractLanguageModelTrainer(NGramCounter nGramCounter, int n) {
		this.n = n;
		this.nGramLogProbability = new HashMap<NGram, ARPAEntry>();
		this.nGramCounter = nGramCounter;
		this.vocabulary = nGramCounter.vocabulary;
		this.nGramSuffix = new HashMap<>();
	}
	
	/**
	 * 计算每一个n元概率的对数，
	 * 根据不同的平滑方式，实现各自的训练方法
	 * @throws IOException 
	 */
	public abstract NGramLanguageModel trainModel();
		
	/**
	 * 返回给定n元的历史后缀链表 
	 * @param nGram 待求历史后缀的n元
	 * @return 给定n元的历史后缀链表
	 */
	protected Set<Gram> getNGramHistorySuffix(NGram nGram) {
		if(nGramSuffix.containsKey(nGram))
			return nGramSuffix.get(nGram);
		return null;
	}
	
	/**
	 * 返回n元与其所有历史后缀组成的n+1元的数量之和
	 * @return 总数量
	 */
	protected int getnGramSuffixTotalCount(NGram nGram) {
		int total = 0;
		Set<Gram> grams = nGramSuffix.get(nGram);
		if(grams != null)
			for(Gram gram : grams)
				total += nGramCounter.getNGramCount(nGram.addLast(gram));
		
		return total;
	}
	
	/**
	 * 返回给定n元历史后缀的数量
	 * @param nGram 给定n元
	 * @return 数量
	 */
	protected int getHistorySuffixCount(NGram nGram) {
		if(nGramSuffix.containsKey(nGram))
			return nGramSuffix.get(nGram).size();
		
		return 0;
	}
	
	/**
	 * 统计n元的所有历史后缀
	 */
	protected void statisticsNGramHistorySuffix() {
		Iterator<NGram> iterator = nGramLogProbability.keySet().iterator();
		
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			if(nGram.length() > 1){
				NGram n_Gram = nGram.removeLast();
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
	
	/**
	 * 返回给定n元的回退权重
	 * @param nGram 待求回退权重的n元
	 * @return		给定n元的回退权重
	 */
    protected double calcBOW(NGram nGram) {
    	//例子：求w1 w2的回退权重
    	double sum_N = 0.0;		//所有出现的以w1 w2为前缀的trigram (w1 w2 *) 的概率之和
    	double sum_N_1 = 0.0;	//所有出现的以w1 w2为前缀的trigram (w1 w2 *) 的低阶：bigram (w2 *)的概率之和
		
    	Set<Gram> suffixs = getNGramHistorySuffix(nGram);
    	if(suffixs != null) {
			for(Gram gram : suffixs) {
				NGram ngram = nGram.addLast(gram);
				if(nGramLogProbability.containsKey(ngram))
					sum_N += Math.pow(10, nGramLogProbability.get(ngram).getLog_prob());
				
				ngram = nGram.addLast(gram).removeFirst();
				if(nGramLogProbability.containsKey(ngram))
					sum_N_1 += Math.pow(10, nGramLogProbability.get(ngram).getLog_prob());
			}
			
			return (1 - sum_N) / (1 - sum_N_1);
		}else
			return 1.0;
    }
    
    /**
     * 返回给定n元的插值权重
	 * @param nGram 待求插值权重的n元
	 * @return		给定n元的插值权重
     */
    protected double calcInterpolateW(NGram nGram) {
    	//例子：求w1 w2的回退权重
    	double sum_N = 0.0;		//所有出现的以w1 w2为前缀的trigram (w1 w2 *) 的概率之和
		
    	Set<Gram> suffixs = getNGramHistorySuffix(nGram);
    	if(suffixs != null) {
			for(Gram gram : suffixs) {
				NGram ngram = nGram.addLast(gram);
				if(nGramLogProbability.containsKey(ngram))
					sum_N += Math.pow(10, nGramLogProbability.get(ngram).getLog_prob());
			}
			
			return 1 - sum_N;
		}else
			return 1.0;
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
}
