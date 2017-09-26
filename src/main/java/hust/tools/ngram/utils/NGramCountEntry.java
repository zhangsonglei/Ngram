package hust.tools.ngram.utils;

import java.io.Serializable;

/**
 *<ul>
 *<li>Description: n元与其数量 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public class NGramCountEntry implements Serializable{

	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = -5295811276532094222L;

	/**
	 * n元
	 */
	private NGram nGram;
	
	/**
	 * n元的计数
	 */
	private int count;
	
	public NGramCountEntry() {
	
	}
	
	public NGramCountEntry(NGram nGram, int count) {
		this.nGram = nGram;
		this.count = count;
	}
	
	public NGram getnGram() {
		return nGram;
	}

	public int getCount() {
		return count;
	}
	
	public String toString() {
		return nGram + "\t" +count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + ((nGram == null) ? 0 : nGram.hashCode());
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
		NGramCountEntry other = (NGramCountEntry) obj;
		if (count != other.count)
			return false;
		if (nGram == null) {
			if (other.nGram != null)
				return false;
		} else if (!nGram.equals(other.nGram))
			return false;
		return true;
	}
}
