package hust.tools.ngram.model;

import java.util.List;

import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.NGram;

/**
 *<ul>
 *<li>Description: n元模型的接口，提供通用的方法接口
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月6日
 *</ul>
 */
public interface LanguageModel {
	
	/**
	 * 返回一个n元的概率的对数
	 * @param nGram	待求概率的n元
	 * @return n元的概率的对数
	 */
	double getNGramLogProbability(NGram nGram);
	
	/**
	 * 返回给定元组序列概率最大的下一个元
	 * @param sequence 给定的元组序列，以求概率最大的下一个元
	 * @return 给定元组序列概率最大的下一个元
	 */
	NGram getNextPrediction(Gram[] sequence, int n);
	
	/**
	 * 返回给定元组序列概率的对数
	 * @param sequece 待求概率对数的元组序列
	 * @return 给定元组序列概率的对数
	 */
	double getSequenceLogProbability(Gram[] sequece, int n);
	
	/**
	 * 计算语言模型在给定测试集上的困惑度
	 * @param testCorpus	测试集
	 * @param n				采用n元模型
	 * @return				困惑度大小
	 */
	double getPerplexity(List<Gram[]> testCorpus, int n);
}
