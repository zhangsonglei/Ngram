package hust.tools.ngram.datastructure;

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

	private static final long serialVersionUID = 6733030984766178958L;
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object object);
	
	@Override
	public abstract String toString();
}
