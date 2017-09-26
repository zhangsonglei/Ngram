package hust.tools.ngram.pos;

import hust.tools.ngram.utils.Gram;

/**
 *<ul>
 *<li>Description: Word/Tag类型作为元 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年9月26日
 *</ul>
 */
public class WordTagGram extends Gram{

	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = 1226101113530596115L;

	private String word;
	
	private String tag;
	
	public WordTagGram(String word, String tag) {
		this.tag = tag;
		this.word = word;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		WordTagGram other = (WordTagGram) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Gram o) {
		WordTagGram wordTagGram = (WordTagGram) o;
		
		if(this.word.compareTo(wordTagGram.word) > 0) 
			return 1;
		else if(this.word.compareTo(wordTagGram.word) < 0)
			return -1;
		else if(this.tag.compareTo(wordTagGram.tag) > 0)
			return 1;
		else if(this.tag.compareTo(wordTagGram.tag) < 0)
			return -1;
		else 
			return 0;
	}
	
	@Override
	public String toString() {
		return word +"/"+tag;
	}
}
