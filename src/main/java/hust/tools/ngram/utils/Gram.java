package hust.tools.ngram.utils;

import java.io.Serializable;

/**
 *<ul>
 *<li>Description: 元抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月10日
 *</ul>
 */
public abstract class Gram implements Comparable<Gram>, Serializable{
	
	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = -2284875744618199191L;

	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object object);
	
	@Override
	public abstract String toString();
}
