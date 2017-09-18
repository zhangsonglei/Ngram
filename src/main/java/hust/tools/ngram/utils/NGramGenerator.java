package hust.tools.ngram.utils;

import java.util.ArrayList;
import java.util.List;

import hust.tools.ngram.datastructure.Gram;
import hust.tools.ngram.datastructure.NGram;

/**
 *<ul>
 *<li>Description:将给定序列生成所有的NGram
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月6日
 *</ul>
 */
public class NGramGenerator {
	
	/**
	   * <li>将给定的Gram数组和n，生成所有可能的n元组
	   * @param input 	待分割为N元组的序列
	   * @param n       N元组的大小
	   * @return		所有的N元组	
	   */
	public static List<NGram> generate(Gram[] input, int n)  {
		List<NGram> output = new ArrayList<>();
		
		for(int i = 0; i < input.length - n + 1; i++) {
			Gram[] ngram = new Gram[n];
			for(int j = i, index = 0; j < i + n; j++, index++)
				ngram[index] = input[j];

			output.add(new NGram(ngram));
		}//end for
		
		return output;
	}
}
