package hust.tools.ngram.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import hust.tools.ngram.datastructure.Gram;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;
import hust.tools.ngram.utils.NGramGenerator;
import hust.tools.ngram.datastructure.PseudoWord;

/**
 *<ul>
 *<li>Description: 统计1-n元数量
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月30日
 *</ul>
 */
public class NGramCounter {
	
	/**
	 * n元最大长度
	 */
	private int n;
	
	/**
	 * n元组与其在训练语料中的数量信息的映射
	 */
	private HashMap<NGram, Integer> nGramCountMap;
	
	/**
	 * 不同长度的n元的总数量
	 * 0-unigram
	 * 1-bigram
	 * 2-trigram
	 * ...
	 */
	private int[] nGramCounts;
	
	/**
	 * 不同长度n元的数组
	 * 0-unigram
	 * 1-bigram
	 * 2-trigram
	 * ...
	 */
	private NGram[][] nGramTypes;
	
	/**
	 * 所有n元总数量
	 */
	private int totalNGramCount;
	
	/**
	 * 字典
	 */
	protected Vocabulary vocabulary;
	
	/**
	 * 统计所有的n元的数量
	 * @param gramSentenceStream 元组流，从中读取句子
	 * @param n	n元的大小
	 * @throws IOException
	 */
	public NGramCounter(GramSentenceStream gramSentenceStream, int n) throws IOException {
		this.n = n;
		this.nGramCountMap = new HashMap<>();
		this.nGramCounts = new int[n];
		this.nGramTypes = new NGram[n][];
		this.totalNGramCount = 0;
		this.vocabulary = new Vocabulary();
		statisticsSentenceNGram(gramSentenceStream);
	}
	
	/**
	 * 统计所有的n元的数量
	 * @param grams 元数组，从中统计n元
	 * @param n n元的大小
	 */
	public NGramCounter(Gram[] grams, int n) {
		this.n = n;
		this.nGramCountMap = new HashMap<>();
		this.nGramCounts = new int[n];
		this.nGramTypes = new NGram[n][];
		this.totalNGramCount = 0;
		this.vocabulary = new Vocabulary();
		statisticsNGram(grams);
	}
	
	/**
	 * 初始化当前实例
	 * @param gramStream 元流，从中读取元
	 * @param n n元的大小
	 * @throws IOException 
	 */
	public NGramCounter(GramStream gramStream, int n) throws IOException {
		this.n = n;
		this.nGramCountMap = new HashMap<>();
		this.nGramCounts = new int[n];
		this.nGramTypes = new NGram[n][];
		this.totalNGramCount = 0;
		this.vocabulary = new Vocabulary();
		statisticsNGram(gramStream);		
		statisticsNGramsTypes();
	}
	
	/**
	 * 初始化当前实例
	 * @param nGramCountMap	n元与其数量信息的映射
	 * @param n 			n元的最大长度
	 */
	public NGramCounter(HashMap<NGram, Integer> nGramCountMap, int n, int totalNGramCount) {
		this.nGramCountMap = nGramCountMap;
		this.n = n;
		this.totalNGramCount = totalNGramCount;
		this.nGramCounts = new int[n];
		this.vocabulary = new Vocabulary();
		statisticsCount();
		this.nGramTypes = new NGram[n][];
		statisticsNGramsTypes();		
	}
	
	/**
	 * 返回n元与其计数映射
	 * @return n元与其计数映射
	 */
	protected HashMap<NGram, Integer> getNGramCountMap() {
		return nGramCountMap;
	}
	
	/**
	 * 返回n元的最大长度
	 * @return n元的最大长度
	 */
	public int getOrder() {
		return n;
	}

	/**
	 * 返回不同长度的n元的数量
	 * @return 不同长度的n元的数量
	 */
	protected int[] getNGramTypeCount() {
		int[] result = new int[n];
		for(int i = 0; i < n; i++)
			result[i] = getNGramTypeCountByN(i+1);
		
		return result;
	}
	
	/**
	 * 返回给定长度的n元的数量
	 * @return 给定长度的n元的数量
	 */
	public int getNGramTypeCountByN(int n) {
		return nGramTypes[n - 1].length;
	}
	
	/**
	 * 返回不同长度n元的数组
	 * @return 不同长度n元的数组
	 */
	protected NGram[][] getNGramTypes() {
		return nGramTypes;
	}
	
	/**
	 * 根据n元的长度获取所有n元的类型
	 * @param n n元的长度
	 * @return 所有长度为n的n元类型
	 */
	protected NGram[] getNGramTypeByN(int n) {
		return nGramTypes[n - 1];
	}
	
	/**
	 * 返回样本中给定n元长度的所有n元总和  
	 * @param n 待求数量的n元长度
	 * @return 给定n元长度的所有n元总和  
	 */
	public int getTotalNGramCountByN(int n) {
		return nGramCounts[n - 1];
	}
	
	/**
	 * 返回给定n元在训练语料中的数量 
	 * @param nGram 待求数量的n元
	 * @return 给定n元在训练语料中的数量
	 */
	public int getNGramCount(NGram nGram) {
		if(nGramCountMap.containsKey(nGram))
			return nGramCountMap.get(nGram);
		else
			return 0;
	}

	/**
	 * 返回所有n元的总数
	 * @return 所有n元的总数
	 */
	public int getTotalNGramCount() {
		return totalNGramCount;
	}
	
	/**
	 * 返回所有n元类型数量  
	 * @return 所有n元类型数量
	 */
	public int size() {
		return nGramCountMap.size();
	}
	
	/**
	 * <li>判断给定n元是否存在
	 * <li>返回true 或 false（存在或不存在）
	 * @param nGram 待判断的n元
	 * @return true 或 false（存在或不存在） 
	 */
	public boolean contains(NGram nGram) {
		return nGramCountMap.containsKey(nGram);
	}	

	/**
	 * 统计不同长度的n元总数
	 */
	private void statisticsCount() {
		for(Entry<NGram, Integer> entry : nGramCountMap.entrySet()) {
			NGram nGram = entry.getKey();
			int value = entry.getValue();
			int len = nGram.length();
			nGramCounts[len - 1] += value;
			
			//建立字典
			if(1 == len)
				vocabulary.add(nGram.getGram(0));
			vocabulary.add(PseudoWord.oov);
		}
	}
	
	/**
	 * 统计所有的n元的数量
	 * @param gramSentenceStream	元组成的句子流
	 * @param isVocab				是否引入字典
	 * @throws IOException			
	 */
	private void statisticsSentenceNGram(GramSentenceStream gramSentenceStream) throws IOException {
		Gram[] grams = null;
		while((grams = gramSentenceStream.next())!=null) {
			Gram[] sentence = new Gram[grams.length + 2];
			sentence[0] = PseudoWord.Start;
			for(int i = 0; i < grams.length; i++)
				sentence[i + 1] = grams[i];
			sentence[grams.length + 1] = PseudoWord.End;
			statisticsNGram(sentence);
		}
	}
	
	/**
	 * 统计所有的n元的数量
	 * @param grams 元数组，从中统计n元
	 */
	private void statisticsNGram(Gram[] grams) {
		for(int i = 1; i <= n; i++) {//依次统计1——n元的个数
			List<NGram> nGrams = NGramGenerator.generate(grams, i);	
			totalNGramCount += nGrams.size();
			nGramCounts[i - 1] += nGrams.size();
			
			List<NGram> list = new ArrayList<>();
			for(NGram nGram : nGrams) {
				if(nGramCountMap.containsKey(nGram)) {
					int count = nGramCountMap.get(nGram);
					nGramCountMap.put(nGram, count + 1);
				}else {	
					list.add(nGram);
					nGramCountMap.put(nGram, 1);
					
					//建立字典
					Gram[] temp = nGram.getGrams();
					for(Gram gram : temp)
						vocabulary.add(gram);
				}
			}//end for(nGram)
			
			nGramTypes[i - 1] = list.toArray(new NGram[list.size()]);
		}//end for(i)
	}
		
	/**
	 * 统计所有的n元的数量
	 * @param gramStream 元流，从中依次读取元
	 * @param map n元与其数量的映射
	 * @param n 最大n元的长度
	 * @throws IOException 如果读取过程中出错，抛出异常
	 */
	private void statisticsNGram(GramStream gramStream) throws IOException {	
		int i = 0;
		Gram[] grams = new Gram[n];
		NGram last = null;//记录最后一个n元，splitLastNGram方法使用
		Gram gram = null;
		while((gram = gramStream.next()) != null) {
			grams[i++] = gram;
			vocabulary.add(gram);
			
			if(i == n) {
				totalNGramCount++;
				nGramCounts[n - 1]++;
				NGram nGram = new NGram(grams);
				last = nGram;
			
				if(nGramCountMap.containsKey(nGram)) 
					nGramCountMap.put(nGram, nGramCountMap.get(nGram) + 1);
				else
					nGramCountMap.put(nGram, 1);
				
				statisticsShorterNGrams(grams);
				
				Gram[] temp = grams;
				grams = new Gram[n];
				for(int j = 0; j < n - 1; j++)
					grams[j] = temp[j+1];
				
				i--;//递减，继续读取下一个元拼接为n元
			}
		}//end while
		
		gramStream.close();
		if(last != null)
			splitLastNGram(last);
	}
	
	/**
	 * 将读取的最后一个n元的后n-1位，分割成所有可能的1——n元
	 * @param nGram 待分割的n元
	 * @param n 待分割的n元的大小
	 * @param map 所有n元与其数量的映射
	 */
	private void splitLastNGram(NGram nGram) {
		for(int i = 1; i < n; i++) {
			List<NGram> lNGrams = NGramGenerator.generate(nGram.removeFirst().getGrams(), i);
			for(NGram ngram :lNGrams) {
				totalNGramCount++;
				nGramCounts[i - 1]++;
					
				if(nGramCountMap.containsKey(ngram)) {
					nGramCountMap.put(ngram, nGramCountMap.get(ngram) + 1);
				}else {
					nGramCountMap.put(ngram, 1);
				}
			}
		}
	}
	
	/**
	 * 生成所有的1——（n-1）元 
	 * @param grams 待分割的元组
	 * @param n
	 * @param list 存储n元的列表
	 */
	private void statisticsShorterNGrams(Gram[] grams) {
		for(int i = 1; i < n; i++) {
			Gram[] ngram = new Gram[i];
			for(int j = 0; j < i; j++)
				ngram[j] = grams[j];
			
			NGram nGram= new NGram(ngram);
			totalNGramCount++;
			nGramCounts[i - 1]++;
				
			if(nGramCountMap.containsKey(nGram)) {
				nGramCountMap.put(nGram, nGramCountMap.get(nGram) + 1);
			}else {
				nGramCountMap.put(nGram, 1);
			}
		}//end for
	}
	
	/**
	 * 统计不同长度的n元
	 * 0-unigram
	 * 1-bigram
	 * 2-trigram
	 * @param n n元的最大长度
	 */
	private void statisticsNGramsTypes() {
		for(int i = 0; i < n; i++) {
			nGramTypes[i] = getNGramTypesByN(i + 1);
		}
	}
	
	/**
	 * 返回给定n元长度的n元组
	 * @param n 待求n元的长度
	 * @return 给定n元长度的n元组
	 */
	private NGram[] getNGramTypesByN(int n) {
		Iterator<NGram> iterator = iterator();
		List<NGram> list = new ArrayList<>();
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			if(nGram.length() == n)
				list.add(nGram);
		}
		
		return list.toArray(new NGram[list.size()]);
	}
	
	/**
	 * 返回所有的NGram的迭代器
	 * @return 所有NGram的迭代器
	 */
	public Iterator<NGram> iterator() {
		return nGramCountMap.keySet().iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + n;
		result = prime * result + ((nGramCountMap == null) ? 0 : nGramCountMap.hashCode());
		result = prime * result + Arrays.hashCode(nGramCounts);
		result = prime * result + totalNGramCount;
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
		NGramCounter other = (NGramCounter) obj;
		if (n != other.n)
			return false;
		if (nGramCountMap == null) {
			if (other.nGramCountMap != null)
				return false;
		} else if (!nGramCountMap.equals(other.nGramCountMap))
			return false;
		if (!Arrays.equals(nGramCounts, other.nGramCounts))
			return false;
		if (totalNGramCount != other.totalNGramCount)
			return false;
		return true;
	}
}