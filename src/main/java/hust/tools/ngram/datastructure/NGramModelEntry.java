package hust.tools.ngram.datastructure;

import java.io.Serializable;

/**
 *<ul>
 *<li>Description: n元与其概率的对数 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月25日
 *</ul>
 */
public class NGramModelEntry implements Serializable{

	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = -5033508647648577623L;

	/**
	 * n元
	 */
	private NGram nGram;
	
	/**
	 * n元的概率
	 */
	private double log_prob;
	
	/**
	 * n元的回退概率
	 */
	private double log_bo;
	
	public NGramModelEntry() {
	
	}
	
	public NGramModelEntry(double log_prob, NGram nGram) {
		this(log_prob, nGram, 0.0);
	}
	
	public NGramModelEntry(double log_prob, NGram nGram, double log_bo) {
		this.nGram = nGram;
		this.log_prob = log_prob;
		this.log_bo = log_bo;
	}
	
	public NGram getnGram() {
		return nGram;
	}

	public double getLog_prob() {
		return log_prob;
	}
	
	public double getLog_bo() {
		return log_bo;
	}

	public String toString() {
		if(0.0 == log_bo )
			return log_prob + "\t" + nGram;
		else
			return log_prob + "\t" + nGram + "\t" + log_bo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(log_bo);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(log_prob);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		NGramModelEntry other = (NGramModelEntry) obj;
		if (Double.doubleToLongBits(log_bo) != Double.doubleToLongBits(other.log_bo))
			return false;
		if (Double.doubleToLongBits(log_prob) != Double.doubleToLongBits(other.log_prob))
			return false;
		if (nGram == null) {
			if (other.nGram != null)
				return false;
		} else if (!nGram.equals(other.nGram))
			return false;
		return true;
	}
}
