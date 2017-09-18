package hust.tools.ngram.model;

import java.io.IOException;
import java.util.HashSet;
import hust.tools.ngram.datastructure.Gram;
import hust.tools.ngram.datastructure.PseudoWord;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;

/**
 *<ul>
 *<li>Description: 字典类，用户给定字典文件建立字典类，用于判断训练语料中的未登录词(oov) 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年9月5日
 *</ul>
 */
public class Vocabulary {
	
	private HashSet<Gram> vocabulary;
	
	private boolean isSentence = false;
	
	public Vocabulary() {
		this.vocabulary = new HashSet<>();
		add(PseudoWord.oov);
	}
	
	public Vocabulary(GramStream vocab) throws IOException {
		this.vocabulary = new HashSet<>();
		establishVocab(vocab);
		add(PseudoWord.oov);
	}
	
	public Vocabulary(GramSentenceStream vocab) throws IOException {
		this.vocabulary = new HashSet<>();
		this.isSentence = true;
		establishVocab(vocab);
		add(PseudoWord.oov);
	}
	
	public Vocabulary(Gram[] vocab) {
		this.vocabulary = new HashSet<>();
		establishVocab(vocab);
		add(PseudoWord.oov);
	}
	
	/**
	 * 添加元到字典中
	 * @param gram 待添加的元
	 */
	public void add(Gram gram) {
		if(!vocabulary.contains(gram))
			vocabulary.add(gram);
	}
	
	/**
	 * 字典的大小
	 * @return 字典的大小
	 */
	public int size() {
		if(isSentence)
			return vocabulary.size() - 1;
		return vocabulary.size();
	}

	/**
	 * 判断元是否在字典中
	 * @param gram
	 * @return 在-true/不在-false
	 */
	public boolean contains(Gram gram) {
		return vocabulary.contains(gram);
	}
	
	/**
	 * 建立字典
	 * @param stream	建立字典的语料
	 * @throws IOException
	 */
	private void establishVocab(GramStream stream) throws IOException {
		Gram gram = null;
		while((gram = stream.next()) != null) {
			if(!vocabulary.contains(gram))
				add(gram);
		}
	}
	
	/**
	 * 建立字典
	 * @param stream	建立字典的语料
	 * @throws IOException
	 */
	private void establishVocab(GramSentenceStream stream) throws IOException {
		Gram[] grams = null;
		while((grams = stream.next()) != null) {
			for(Gram gram : grams)
				if(!vocabulary.contains(gram))
					add(gram);
		}
		add(PseudoWord.sentEnd);
		add(PseudoWord.sentStart);
	}

	/**
	 * 建立字典
	 * @param grams	建立字典的语料
	 */
	private void establishVocab(Gram[] grams) {
		for(Gram gram: grams) {
			if(!vocabulary.contains(gram))
				add(gram);
		}
	}
}
