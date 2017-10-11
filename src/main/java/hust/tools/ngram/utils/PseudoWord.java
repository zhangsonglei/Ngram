package hust.tools.ngram.utils;

/**
 *<ul>
 *<li>Description: 伪词，用于对句子添加首位标签或者处理生字/生词 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年10月11日
 *</ul>
 */
public class PseudoWord {
	public static final Gram oov = new StringGram("<unk>");				//替换未出现在训练语料中的元
	public static final NGram oovNGram = new NGram(new Gram[]{oov});	//前者的unigram形式
	public static final Gram Start = new StringGram("<s>");				//句子开始标签
	public static final NGram sentStart = new NGram(new Gram[]{Start});	//前者的unigram形式
	public static final Gram End = new StringGram("</s>");				//句子结束标签
	public static final NGram sentEnd = new NGram(new Gram[]{End});		//前者的unigram形式
}
