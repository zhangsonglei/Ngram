package hust.tools.ngram.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import hust.tools.ngram.model.AbstractNGramModelReader;
import hust.tools.ngram.model.DataReader;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.model.Vocabulary;
import hust.tools.ngram.utils.ARPAEntry;
import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.StringGram;

/**
 *<ul>
 *<li>Description: arpa格式n元模型的读取，实现模型的重构 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月27日
 *</ul>
 */
public class ARPANGramModelReader extends AbstractNGramModelReader {

	private Vocabulary vocabulary;
	
	private HashMap<NGram, ARPAEntry> nGramLogProbability;
	
	private int[] ngramTypeCounts;
	
	public ARPANGramModelReader(File file) throws IOException {
		super(file);
	}
	
	public ARPANGramModelReader(DataReader dataReader) {
		super(dataReader);
	}

	@Override
	public NGramLanguageModel constructModel() throws IOException, ClassNotFoundException {
		nGramLogProbability = new HashMap<>();
		vocabulary = new Vocabulary();
		String string = "";
		List<String> list = new LinkedList<>();
		while((string = readUTF()) != null) {
			if(!string.equals(""))
				list.add(string);
			else{
				statistics(list);
				System.out.println(list.size());
				list = new LinkedList<>();
			}	
		}
		close();
		return new NGramLanguageModel(nGramLogProbability, ngramTypeCounts.length, "backoff", vocabulary);
	}

	/**
	 * 统计n元及其概率  
	 * @param list
	 */
	private void statistics(List<String> list) {
		if(list.size() > 1) {
			switch(list.get(0)) {
			case "\\data\\":
				list.remove(0);
				getCount(list);
				break;
			default:
				list.remove(0);
				getARPAEntry(list);
				break;
			}
		}
	}

	/**
	 * 解析n元及其概率  
	 * @param list n元及其概率的String List
	 */
	private void getARPAEntry(List<String> list) {
		for(int i = 0; i < list.size(); i++) {
			String[] strings = list.get(i).trim().split("\t");//n元与概率分离
			String[] strs = strings[1].split("\\s+");//n元分离为元的数组
			Gram[] grams = new Gram[strs.length];
			for(int j = 0; j < strs.length; j++) {
				grams[j] = new StringGram(strs[j]);
			}
			
			NGram nGram = new NGram(grams);//得到n元
			if(1 == nGram.length())
				vocabulary.add(nGram.getGram(0));
			
			double log_prob = Double.parseDouble(strings[0]);			
			double log_bo = 0.0;
			if(strings.length == 3)	
				log_bo = Double.parseDouble(strings[2]);
			ARPAEntry entry = new ARPAEntry(log_prob, log_bo);
			
			nGramLogProbability.put(nGram, entry);
		}
	}

	/**
	 * 统计不同长度的n元的数量  
	 * @param list n元的长度及其数量的String List
	 */
	private void getCount(List<String> list) {
		ngramTypeCounts = new int[list.size()];
		
		for(int i = 0; i < list.size(); i++) {
			String[] strings = list.get(i).trim().split("\\s+")[1].split("=");
			int len = Integer.parseInt(strings[0]);
			int num = Integer.parseInt(strings[1]);
			ngramTypeCounts[len - 1] = num;
		}
	}
}
