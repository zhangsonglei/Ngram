package hust.tools.ngram.utils;

/**
 *<ul>
 *<li>Description: 以一个String类型作为一个元 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年8月20日
 *</ul>
 */
public class StringGram extends Gram{
	
	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = 2433153820971791184L;
	
	/**
	 * 每个字（字母或符号）作为一个元
	 */
	private String gram;

	public StringGram(String gram) {
		this.gram = gram.intern();
	}
	
	public String getGram() {
		return this.gram;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gram == null) ? 0 : gram.hashCode());
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
		StringGram other = (StringGram) obj;
		if (gram == null) {
			if (other.gram != null)
				return false;
		} else if (!gram.equals(other.gram))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return gram;
	}

	@Override
	public int compareTo(Gram o) {
		StringGram sGram = (StringGram) o;
		
		if(this.gram.compareTo(sGram.gram) > 0) 
			return 1;
		else if(this.gram.compareTo(sGram.gram) < 0)
			return -1;
		else 
			return 0;
	}

}
