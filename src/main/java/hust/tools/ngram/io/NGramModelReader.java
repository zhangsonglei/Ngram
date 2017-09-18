package hust.tools.ngram.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import hust.tools.ngram.datastructure.ARPAEntry;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.datastructure.NGramModelEntry;
import hust.tools.ngram.model.AbstractNGramModelReader;
import hust.tools.ngram.model.DataReader;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.model.Vocabulary;

/**
 *<ul>
 *<li>Description: 从文件读取N元模型的抽象类，实现读取重构模型的方法
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月15日
 *</ul>
 */
public class NGramModelReader extends AbstractNGramModelReader{

	private Vocabulary vocabulary;
	
	private HashMap<NGram, ARPAEntry> nGramLogProbability; 
	
	public NGramModelReader(File file) throws IOException {
		super(file);
	}
	
	public NGramModelReader(DataReader dataReader) {
		super(dataReader);
	}

	@Override
	public NGramLanguageModel constructModel() throws IOException, ClassNotFoundException {
		nGramLogProbability = new HashMap<>();
		vocabulary = new Vocabulary();
		String smooth = readUTF();
		int n = readCount();
		int[] ngramTypeCounts = getNGramTypeCounts(n);
		statisticsNGramLogProbability(ngramTypeCounts);
		close();
		
		return new NGramLanguageModel(nGramLogProbability, n, smooth, vocabulary);
	}
	
	/**
	 * 根据n元的长度获取n元的类型数量
	 * @param n		待计算n元类型数量的的n元长度
	 * @return		所有长度为n的n元类型数量
	 * @throws IOException
	 */
	private int[] getNGramTypeCounts(int n) throws IOException {
		int[] nGramTypeCounts = new int[n];
		for(int i = 0; i < n; i++)
			nGramTypeCounts[i] = readCount();
		
		return nGramTypeCounts;
	}
	
	/**
	 * 解析模型，将读取的行解析为n元及其概率与回退权重存入内存中
	 * @param ngramTypeCounts			n元模型的大小
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void statisticsNGramLogProbability(int[] ngramTypeCounts) throws ClassNotFoundException, IOException {
		for(int i = 0; i < ngramTypeCounts.length; i++) {
			for(int j = 0; j < ngramTypeCounts[i]; j++) {
				NGramModelEntry modelEntry = new NGramModelEntry();
				modelEntry = readNGramModelEntry();
				ARPAEntry entry = new ARPAEntry(modelEntry.getLog_prob(), modelEntry.getLog_bo());
				
				NGram nGram = modelEntry.getnGram();
				nGramLogProbability.put(nGram, entry);
				
				if(1 == nGram.length())
					vocabulary.add(nGram.getGram(0));
			}
		}
	}
}
