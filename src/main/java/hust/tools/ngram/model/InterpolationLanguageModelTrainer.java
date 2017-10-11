package hust.tools.ngram.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import hust.tools.ngram.utils.ARPAEntry;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.PseudoWord;

/**
 *<ul>
 *<li>Description: 使用线性插值平滑方法训练模型
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年8月20日
 *</ul>
 */
public class InterpolationLanguageModelTrainer extends AbstractLanguageModelTrainer{

	/**
	 * 线性插值中的权重
	 * 0-unigram， 1-bigram， 2-trigram，...
	 */
	private double[] lamdas;
	
	private NGramCounter held_out;
	
	public InterpolationLanguageModelTrainer(GramStream gramStream, int  order) throws IOException {
		super(gramStream, order);
		held_out = new NGramCounter(gramStream, n);
	}
	
	public InterpolationLanguageModelTrainer(GramStream gramStream, GramStream held, int  order) throws IOException {
		super(gramStream, order);
		held_out = new NGramCounter(held, n);
	}
	
	public InterpolationLanguageModelTrainer(GramSentenceStream gramStream, GramSentenceStream held, int  order) throws IOException {
		super(gramStream, order);
		held_out = new NGramCounter(held, n);
	}

	public InterpolationLanguageModelTrainer(NGramCounter nGramCounter, int order) throws IOException {
		super(nGramCounter, order);
		held_out = nGramCounter;
	}
	
	public InterpolationLanguageModelTrainer(NGramCounter nGramCounter, GramStream held, int order) throws IOException {
		super(nGramCounter, order);
		held_out = new NGramCounter(held, n);
	}
	
	public InterpolationLanguageModelTrainer(NGramCounter nGramCounter, GramSentenceStream held, int order) throws IOException {
		super(nGramCounter, order);
		held_out = new NGramCounter(held, n);
	}
	
	/**
	 * Interpretation平滑中，模型参数含义：
	 * log_prob	  nGram    log_bo
	 * n元组概率的对数    n元组               无
	 */
	@Override
	public NGramLanguageModel trainModel() {
		lamdas = new double[n];
		calculateLamda(held_out);

		Iterator<NGram> iterator = nGramCounter.iterator();
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			if(nGram.length() > 2 && nGramCounter.getNGramCount(nGram) < 2)//高阶(n > 2)n元组计数小于2 的忽略
				continue;
			
			double prob = calcInterpolationNGramProbability(nGram, nGramCounter);
			ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
			
			nGramLogProbability.put(nGram, entry);
		}
		
		ARPAEntry oovEntry = new ARPAEntry(Math.log10(1.0*getLamda(PseudoWord.oovNGram) / nGramCounter.getTotalNGramCountByN(1)), 0.0);
		nGramLogProbability.put(PseudoWord.oovNGram, oovEntry);
		
		//计算回退权重
		for(Entry<NGram, ARPAEntry> entry : nGramLogProbability.entrySet()) {
			NGram nGram = entry.getKey();
			double bow = calcBOW(nGram);
			if(bow > 0.0)
				entry.getValue().setLog_bo(Math.log10(bow));
			else
				System.out.println(nGram+":bow= "+bow);
		}
		
		return new NGramLanguageModel(nGramLogProbability, n, "interpolate", vocabulary);
	}

	/**
	 * 使用Interpolation平滑算法计算给定ngram在词汇表中的概率
	 * @param ngram			待计算概率的n元
	 * @param nGramCount	n元的计数
	 * @return 				Laplace平滑概率
	 */
	private double calcInterpolationNGramProbability(NGram nGram, NGramCounter nGramCounter){	
		int n = nGram.length();
		
		if(n == 1) {
			return calcMLNGramProbability(nGram, nGramCounter);
		}else if(n > 1) {
			return getLamda(nGram) * calcMLNGramProbability(nGram, nGramCounter) + 
					(1 - getLamda(nGram)) * calcInterpolationNGramProbability(nGram.removeFirst(), nGramCounter);
		}else
			throw new RuntimeException("n元长度出错:"+nGram);
	}
	
	/**
	 * 获取n元对应的插值参数lamda
	 * @param nGram	待获取插值参数的n元
	 * @return		n元对应的插值参数lamda
	 */
	private double getLamda(NGram nGram) {
		int len = nGram.length();
		if(len < lamdas.length)
			return lamdas[len - 1];
		else
			throw new RuntimeException("interpolate中没有对应n元长度的参数lamda:"+nGram);
	}

	/**
	 * <li>计算插值平滑中的权重参数  
	 * @param nGramCount held-out语料训练的数据
	 */
	private void calculateLamda(NGramCounter nGramCounter) {
		NGram[] nGrams = nGramCounter.getNGramTypeByN(n);
		for(NGram nGram : nGrams) {							//对每一个计数大于零的最大元遍历
			double max = 0.0;								//最大的概率
			int max_index = 0;								//最大概率对应的n元长度
			double accumulation = nGramCounter.getNGramCount(nGram);	//lamda累加的值
			for(int i = 0; i < n; i++){						//对每一个最大元的子元计算概率
				NGram removednGram = nGram;
				for(int j = 0; j < i; j++)
					removednGram = removednGram.removeFirst();
				
				double count = nGramCounter.getNGramCount(removednGram);
				int n_count = 0;
				double prob = 0.0;
				if(1  == removednGram.length())
					n_count	= nGramCounter.getNGramTypeCountByN(n);
				else
					n_count = nGramCounter.getNGramCount(removednGram.removeLast());

				if(1 == n_count)
					prob = 0.0;
				else
					prob = (count - 1) / (n_count - 1);
				
				if(max < prob) {
					max = prob;
					max_index = n - i -1;
				}
			}//end for
			lamdas[max_index] += accumulation;	
		}//end for

		//正规化 
		double sum = 0.0;
		for(int i = 0; i < lamdas.length; i++)
			sum += lamdas[i];
		for(int i = 0; i < lamdas.length; i++)
			lamdas[i] = lamdas[i] / sum;
	}
}
