package hust.tools.ngram.datastructure;

import java.io.Serializable;
import java.util.Arrays;

/**
 *<ul>
 *<li>Description: n元类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月16日
 *</ul>
 */
public class NGram implements Comparable<NGram>, Serializable{
	
	/**
	 * 版本序列号
	 */
	private static final long serialVersionUID = -5179548252365264151L;
	
	/**
	 * n元数组
	 */
	private Gram[] grams;
	
	public NGram(Gram[] grams) {
		this.grams = grams;
	}
	
	/**
	 * 返回该n元的元组形式
	 * @return	元组
	 */
	public Gram[] getGrams() {
		return grams;
	}
	
	/**
	 * 根据索引将grams数组中指定位置替换为gram
	 * @param gram	待替换的元
	 * @param index	待替换的位置索引
	 */
	public void setGram(Gram gram, int index) {
		if(index < 0 || index >= length())
			System.err.println(getClass()+"\n修改n元出错:"+index);
		else
			grams[index] = gram;
	}
	
	/**
	 * <li> 返回n元数组的长度
	 * @return n元数组的长度
	 */
	public int length() {
		return grams.length;
	}
	
	/**
	 * <li>从给定索引处检索出元  
	 * @param index 给定索引
	 * @return 给定索引处的元
	 */
	public Gram getGram(int index) {
		return grams[index];
	}

	/**
	 * <li> 删除n元的第一位 
	 * @param ngram 待删除的n元
	 * @return 删除后第一位后的n元
	 */
	public NGram removeFirst() {
		if(this.length() < 2)
			return null;
		else {
			Gram[] gs = new Gram[this.length() - 1];
			for(int i = 0; i < gs.length; i++)
				gs[i] = this.grams[i + 1];
			
			return new NGram(gs);
		}
	}
	
	/**
	 * <li> 删除n元的最后一位 
	 * @param ngram 待删除的n元
	 * @return 删除后最后一位后的n元
	 */
	public NGram removeLast() {
		if(this.length() < 1)
			return null;
		else {
			Gram[] gs = new Gram[this.length() - 1];
			for(int i = 0; i < gs.length; i++)
				gs[i] = this.grams[i];
			
			return new NGram(gs);
		}
	}
	
	/**
	 * <li> 在n元前增加一位 
	 * @param ngram 待增加的n元
	 * @return 在n元前增加一位后的n+1元
	 */
	public NGram addFirst(Gram gram) {
		Gram[] gs = new Gram[this.length() + 1];
		gs[0] = gram;
		for(int i = 0; i < this.length(); i++)
			gs[i + 1] = this.grams[i];
			
		return new NGram(gs);
		
	}
	
	/**
	 * <li> 在n元后增加一位 
	 * @param ngram 待增加的n元
	 * @return 在n元后增加一位后的n+1元
	 */
	public NGram addLast(Gram gram) {
		Gram[] gs = new Gram[this.length() + 1];
		for(int i = 0; i < this.length(); i++)
			gs[i] = this.grams[i];
		gs[this.length()] = gram;
			
		return new NGram(gs);
	}
	
	/**
	 * <li>判断n元是否是以给定m（m<n）元开始的  
	 * @param nGram
	 * @return
	 */
	public boolean startWith(NGram nGram) {
		if(nGram != null && nGram.length() <= this.length()) {
			for(int i = 0; i < nGram.length(); i++) {
				if(!grams[i].equals(nGram.getGram(i)))
					return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * <li>判断n元是否是以给定m（m<n）元结尾的  
	 * @param nGram
	 * @return
	 */
	public boolean endWith(NGram nGram) {
		if(nGram != null && nGram.length() <= this.length()) {
			int temp = this.length() - nGram.length();
			for(int i = 0; i < nGram.length(); i++) {
				if(!grams[temp + i].equals(nGram.getGram(i)))
					return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * <li> 比较两个nGram的大小
	 * <li> this < nGram return -1
	 * <li> this = nGram return 0
	 * <li> this > nGram return 1  
	 * @param nGram 待比较的n元组
	 * @return -1,0,1
	 */
	@Override
	public int compareTo(NGram nGram) {
		int len1 = this.length();
		int len2 = nGram.length();
		
		int len = Math.min(len1, len2);
		for(int i = 0; i < len; i++) {
			int res = grams[i].compareTo(nGram.grams[i]);
			if(res != 0)
				return res > 0 ? 1 : -1;
		}
		
		if(len1 == len2)
			return 0;
		else
			return len1 - len2 > 0 ? 1 : -1;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(grams);
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
		NGram other = (NGram) obj;
		if (!Arrays.equals(grams, other.grams))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String result = "";
		
		for(Gram gram : grams)
			result += gram + " ";
			
		return result.trim();
	}
}
