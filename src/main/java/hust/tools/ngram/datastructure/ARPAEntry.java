package hust.tools.ngram.datastructure;

import java.io.Serializable;

/**
 *<ul>
 *<li>Description: n元的概率与回退权重的对数 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年8月20日
 *</ul>
 */
public class ARPAEntry implements Serializable{
	
	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = -7117687213725579814L;

	private double log_prob;

	private double log_bo;

	public double getLog_prob() {
		return log_prob;
	}

	public void setLog_bo(double log_bo) {
		this.log_bo = log_bo;
	}
	
	public double getLog_bo() {
		return log_bo;
	}
	
	public ARPAEntry(double log_prob, double log_bo) {
		this.log_prob = log_prob;
		this.log_bo = log_bo;
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
		ARPAEntry other = (ARPAEntry) obj;
		if (Double.doubleToLongBits(log_bo) != Double.doubleToLongBits(other.log_bo))
			return false;
		if (Double.doubleToLongBits(log_prob) != Double.doubleToLongBits(other.log_prob))
			return false;
		return true;
	}
}
