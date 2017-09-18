package hust.tools.ngram.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.datastructure.NGramCountEntry;
import hust.tools.ngram.model.AbstractNGramCountReader;
import hust.tools.ngram.model.DataReader;
import hust.tools.ngram.model.NGramCounter;

/**
 *<ul>
 *<li>Description: 从文件中读取n元计数的抽象类，实现读取重构计数器方法 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class NGramCountReader extends AbstractNGramCountReader{
	
	private HashMap<NGram, Integer> nGramCountMap;
	
	private int n;
	
	private int totalCount;
	
	public NGramCountReader(File file) throws IOException {
		super(file);
	}
	
	public NGramCountReader(DataReader dataReader) {
		super(dataReader);
	}

	@Override
	public NGramCounter constructNGramCount() throws IOException, ClassNotFoundException {
		nGramCountMap = new HashMap<>();
		n = 0;
		totalCount = 0;
		
		int number = readCount();
		statisticsNGramCount(number);
		close();
		
		return new NGramCounter(nGramCountMap, n, totalCount);
	}

	/**
	 * 统计读取的n元计数  
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void statisticsNGramCount(int number) throws ClassNotFoundException, IOException {
		for(int i = 0; i < number; i++) {
			NGramCountEntry entry = readNGramCountEntry();
			NGram nGram = entry.getnGram();
			int count = entry.getCount();
			
			totalCount += count;
			
			//n元的最大长度
			n = n > nGram.length() ? n : nGram.length();
			
			nGramCountMap.put(nGram, count);
		}
	}
}
