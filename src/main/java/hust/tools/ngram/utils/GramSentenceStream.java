package hust.tools.ngram.utils;

import java.io.IOException;

/**
 *<ul>
 *<li>Description: 从流中读取元组的接口  
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月17日
 *</ul>
 */
public interface GramSentenceStream extends GramStream{
	
	/**
	 * 返回下一个元组。
	 * 重复调用该方法每次将从底层源中返回一个元组，直到返回null。  
	 * @return 下一个元或者null（读取结束）
	 * @throws IOException
	 */
	Gram[] nextSentence() throws IOException;
}
