package hust.tools.ngram.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

import hust.tools.ngram.utils.ARPAEntry;
import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.NGramGenerator;
import hust.tools.ngram.utils.PseudoWord;

/**
 *<ul>
 *<li>Description: 已经使用平滑算法训练的n元模型（在NGramLanguageModelTrainer.java实现） 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月30日
 *</ul>
 */
public class NGramLanguageModel implements LanguageModel {
	
	/**
	 * 字典
	 */
	private Vocabulary vocabulary;
	
	/**
	 * 模型中使用的平滑方法
	 */
	private String smooth;

	/**
	 * 最大的n元长度
	 */
	private int n;
	
	/**
	 * n元与其概率的对数的映射
	 */
	private HashMap<NGram, ARPAEntry> nGramLogProbability;
	
	public HashMap<NGram, ARPAEntry> getnGramLogProbability() {
		return nGramLogProbability;
	}
	
	public NGramLanguageModel(HashMap<NGram, ARPAEntry> nGramLogProbability, int n,	String smooth, Vocabulary vocabulary) {
		this.nGramLogProbability = nGramLogProbability;
		this.n = n;
		this.smooth = smooth;
		this.vocabulary = vocabulary;	
	}
	
	/**
	 * 从n元及其概率对数的映射中获取所有的n元的迭代器
	 * @param map n元及其概率的对数
	 * @return 所有的n元的迭代器
	 */
	private Iterator<NGram> iterator() {
		return nGramLogProbability.keySet().iterator();
	}
	
	
	public int getOrder(){
		return n;
	}
	/**
	 * 返回平滑方法
	 * @return 平滑方法
	 */
	public String getSmooth() {
		return smooth;
	}
	
	/**
	 * 返回true-模型包含给定n元 ；false-模型不包含给定n元
	 * @param nGram 待判断是否存在与模型中的n元
	 * @return true/false
	 */
	protected boolean contains(NGram nGram) {
		return nGramLogProbability.containsKey(nGram);
	}
	
	@Override
	public double getSequenceLogProbability(Gram[] sequence, int order, boolean boundary) {
		double probability = 0.0;
		
		List<NGram> nGrams = splitSequence(sequence, order, boundary);
		if(nGrams.size() > 0) {
			for(NGram nGram : nGrams)
				probability += getNGramLogProbability(nGram);
			
			if (Double.isNaN(probability) || Double.isInfinite(probability))
				probability = 0.0;
			else if (probability != 0)
				probability = Math.pow(10, probability);
		}
		
		return probability;
	}
	
	@Override
	public NGram getNextPrediction(Gram[] sequence, int order, boolean boundary) {
		NGram predict = null;
		double maxProb = Double.NEGATIVE_INFINITY;
		
		Iterator<NGram> iterator = this.iterator();
		while (iterator.hasNext()) {
			NGram nGram = iterator.next();
			Gram[] grams = new Gram[sequence.length + nGram.length()];
			for (int i = 0; i < sequence.length; i++)
				grams[i] = sequence[i];
			for (int i = 0; i < nGram.length(); i++)
				grams[i + sequence.length] = nGram.getGram(i);

			double prob = getSequenceLogProbability(grams, order, boundary);	      
			if (prob > maxProb) {
				maxProb = prob;
				predict = nGram;
			}
		}
	
		return predict;
	}
	
	@Override
	public double getPerplexity(List<Gram[]> testSet, int order, boolean boundary) {
		//句子数量
		int sentences = 0;
		//词的数量
		int words = 0;
		//未登录词数量
		int OOVs = 0;
		//所有句子的概率乘积的对数
		double logprob = 0.0;
		//ppl = 10^(-logprob / (words - OOVs + sentences))
		double ppl = 0.0;
		
		for(Gram[] grams : testSet) {
			sentences++;
			for(Gram gram : grams) {
				words++;
				if(!vocabulary.contains(gram))
					OOVs++;
			}

			List<NGram> nGrams = splitSequence(grams, order, boundary);
			double nGramLogProb = 0.0;
			for(NGram nGram : nGrams) {
				nGramLogProb = getNGramLogProbability(nGram);
				if (!(Double.isInfinite(nGramLogProb) || Double.isNaN(nGramLogProb)))
					logprob += nGramLogProb;
			}
		}
		
		if(vocabulary.contains(PseudoWord.oov))
			OOVs = 0;
		
		ppl = Math.pow(10, (-logprob / (words - OOVs + sentences)));
		System.out.println(sentences+" sentences, " + words+" words, " + OOVs+" OOVs" +
			   ", logprob= " + logprob + " ppl= " + ppl);
		return ppl;
	}
	
	@Override
	public double getNGramLogProbability(NGram nGram) {
		if(contains(nGram))
			return nGramLogProbability.get(nGram).getLog_prob();
		
		return calcOOVLogProbability(nGram);
	}
	
	/**
	 * 返回给定n元的回退权重对数/laplace模型中为该n元的频数
	 * @param nGram	待求回退权重的n元
	 * @return	回退权重的对数
	 */
	private double getNGramLogBo(NGram nGram) {
		if(contains(nGram))
			return nGramLogProbability.get(nGram).getLog_bo();
		else
			return 0.0;
	}

	/**
	 * <li> 计算OOV（a_z）概率的对数：  
	 * <li> 如果是最大似然估计直接返回 0
	 * <li> &emsp;如果是unigram 
	 * <li> &emsp;&emsp;返回<unk>的概率
	 * <li> &emsp;否则
	 * <li>	&emsp;&emsp;如果a_存在
	 * <li>	&emsp;&emsp;&emsp;返回bow(a_)*prob(_z)
	 * <li>	&emsp;&emsp;否则
	 * <li>	&emsp;&emsp;&emsp;返回prob(_z)
	 * @param nGram 待求概率的对数的n元(a_z)
	 * @return OOV概率的对数  
	 */	
	private double calcOOVLogProbability(NGram nGram) {
		String smoothing = smooth.toLowerCase();

		if(smoothing.equals("ml")) {
			//直接返回0
			return Math.log10(0);
		}else if(smoothing.equals("interpolate")) {
			if(1 == nGram.length())
				return getNGramLogProbability(PseudoWord.oovNGram);
			
			return getNGramLogProbability(nGram.removeFirst());
		}else {
			if(1 == nGram.length()) {
				return getNGramLogProbability(PseudoWord.oovNGram);
			}else {
				NGram n_Gram = nGram.removeLast();
				NGram _nGram = nGram.removeFirst();
				if(contains(n_Gram))
					return getNGramLogBo(n_Gram) + getNGramLogProbability(_nGram);
				else
					return getNGramLogProbability(_nGram);
			}
		}
	}
	
	/**
	 * <li>根据给定n元阶数将元序列切分成n元组，计算序列概率
	 * <li>n=3, abcde——>a/ab/abc/bcd/cde
	 * @param sequence	待切分的序列
	 * @param order		n元阶数
	 * @return			所有切分的n元
	 */
	private List<NGram> splitSequence(Gram[] sequence, int order, boolean boundary) {
		List<NGram> list = new LinkedList<>();		
		
		if(boundary) {//为句子加上边界<s>...</s>
			if(sequence.length >= order) {//序列长度大于等于order
				//长度小于order的n元
				Gram[] grams = new Gram[order - 1]; 
				for(int i = 0; i < grams.length; i++)
					grams[i] = sequence[i];
				
				NGram nGram = new NGram(grams);
				for(int i = 0; i < order - 1; i++) {
					list.add(nGram.addFirst(PseudoWord.Start));
					nGram = nGram.removeLast();
				}
				
				//长度为order的n元
				List<NGram> nGrams = NGramGenerator.generate(sequence, order);
				for(NGram ngram : nGrams)
					list.add(ngram);
					
				NGram end = list.get(list.size() - 1);
				if(end.length() == order)
					list.add(end.removeFirst().addLast(PseudoWord.End));
				else
					list.add(end.addLast(PseudoWord.End));
			}else {//序列长度小于order
				NGram nGram = new NGram(sequence);
				for(int i = 0; i < sequence.length; i++) {
					list.add(nGram.addFirst(PseudoWord.Start));
					nGram = nGram.removeLast();
				}
				
				NGram end = list.get(0);
				if(end.length() == order)
					list.add(end.removeFirst().addLast(PseudoWord.End));
				else
					list.add(end.addLast(PseudoWord.End));
			}
		}else {//不加边界
			if(sequence.length >= order) {//序列长度大于等于order
				//长度小于order的n元
				Gram[] grams = new Gram[order - 1]; 
				for(int i = 0; i < grams.length; i++)
					grams[i] = sequence[i];
				
				NGram nGram = new NGram(grams);
				for(int i = 0; i < order - 1; i++) {
					list.add(nGram);
					nGram = nGram.removeLast();
				}
				
				//长度为order的n元
				List<NGram> nGrams = NGramGenerator.generate(sequence, order);
				for(NGram ngram : nGrams)
					list.add(ngram);
			}else {//序列长度小于order
				NGram nGram = new NGram(sequence);
				for(int i = 0; i < sequence.length; i++) {
					list.add(nGram);
					nGram = nGram.removeLast();
				}
			}//end if-else in no-boundary
		}
		
		return list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + n;
		result = prime * result + ((nGramLogProbability == null) ? 0 : nGramLogProbability.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NGramLanguageModel other = (NGramLanguageModel) obj;
		if (n != other.n)
			return false;
		if (nGramLogProbability == null) {
			if (other.nGramLogProbability != null)
				return false;
		} else if (!nGramLogProbability.equals(other.nGramLogProbability))
			return false;
		return true;
	}
}
