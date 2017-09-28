package hust.tools.ngram.pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;
import hust.tools.ngram.io.ARPATextFileNGramModleReader;
import hust.tools.ngram.io.BinaryFileNGramModelReader;
import hust.tools.ngram.io.ObjectFileNGramModelReader;
import hust.tools.ngram.io.TextFileNGramModelReader;
import hust.tools.ngram.model.AbstractNGramModelReader;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.StringGram;

public class POS {
	
	/**
	 * 基于词的tri-gram模型
	 */
	private static NGramLanguageModel wordLM;
	
	/**
	 * 基于词的词性的four-gram模型
	 */
	private static NGramLanguageModel tagLM;
	
//	/**
//	 * 基于词及其词性的tri-gram模型
//	 */
//	private static NGramLanguageModel wordTagLM;
	
	/**
	 * 统计训练语料中每个词的词频
	 */
	private static HashMap<Gram, HashMap<Gram, Integer>> wordTowardsTagsCount;
	
	/**
	 * 统计训练语料中每个词性的频率
	 */
	private static HashMap<Gram, HashMap<Gram, Integer>> tagTowardsWordsCount;
	
	/**
	 * 返回训练语料中给定词的数量
	 * @param word	给定的词
	 * @return		训练语料中给定词的数量
	 */
	private static int getWordCount(Gram word) {
		int counts = 0;
		HashMap<Gram, Integer> map = wordTowardsTagsCount.get(word);
		for(int count : map.values())
			counts += count;
		
		return counts;
	}
	
	/**
	 * 返回训练语料中给定词性的数量
	 * @param tag	给定的词性
	 * @return		训练语料中给定词的数量
	 */
	private static int getTagCount(Gram tag) {
		int counts = 0;
		HashMap<Gram, Integer> map = tagTowardsWordsCount.get(tag);
		for(int count : map.values())
			counts += count;
		
		return counts;
	}
	
	/**
	 * 返回训练语料中给定词被标记为给定词性的数量
	 * @param word	给定的词
	 * @param tag	给定的词性
	 * @return		训练语料中给定词被标记为给定词性的数量
	 */
	private static int getTagCountByWord(Gram word, Gram tag) {
		if(wordTowardsTagsCount.containsKey(word)) {
			if(wordTowardsTagsCount.get(word).containsKey(tag))
				return wordTowardsTagsCount.get(word).get(tag);
		}
		
		return 0;
	}
	
	/**
	 * 返回训练语料中给定词性其对应词为给定词的数量
	 * @param word	给定的词
	 * @param tag	给定的词性
	 * @return		训练语料中给定词性其对应词为给定词的数量
	 */
	private static int getWordCountByTag(Gram tag, Gram word) {
		if(tagTowardsWordsCount.containsKey(tag)) {
			if(tagTowardsWordsCount.get(tag).containsKey(word))
				return tagTowardsWordsCount.get(tag).get(word);
		}
		
		return 0;
	}
	
	
	/**
	 * 加载序列化模型文件，得到n元模型
	 * @param modelFile					待加载的模型文件
	 * @return							n元模型
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static NGramLanguageModel loadNGramLM(String modelFile) throws ClassNotFoundException, IOException {
		AbstractNGramModelReader modelReader;
		if(modelFile.endsWith(".arpa"))
			modelReader = new ARPATextFileNGramModleReader(new File(modelFile));
		else if(modelFile.endsWith(".bin"))
			modelReader = new BinaryFileNGramModelReader(new File(modelFile));
		else if(modelFile.endsWith(".obj"))
			modelReader = new ObjectFileNGramModelReader(new ObjectInputStream(new FileInputStream(new File(modelFile))));
		else
			modelReader = new TextFileNGramModelReader(new File(modelFile));
		
		return modelReader.constructModel();
	}
	
	/**
	 * 统计训练语料中词与词性的数量以及
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private static void statWordsAndTags(String path) throws IOException {
		wordTowardsTagsCount = new HashMap<>();
		tagTowardsWordsCount = new HashMap<>();
		InputStreamReader iReader = new InputStreamReader(new FileInputStream(new File(path)));
		BufferedReader reader = new BufferedReader(iReader);
		
		String line = null;
		while((line = reader.readLine()) != null) {
			String[] wordTags = line.trim().split("\\s+");
			for(int i = 0; i < wordTags.length; i++) {
				String[] wordTag = wordTags[i].split("/");
				Gram word = new StringGram(wordTag[0]);
				Gram tag = new StringGram(wordTag[1]);
				
				if(wordTowardsTagsCount.containsKey(word)) {
					if(wordTowardsTagsCount.get(word).containsKey(tag)) 
						wordTowardsTagsCount.get(word).put(tag, wordTowardsTagsCount.get(word).get(tag) + 1);
					else 
						wordTowardsTagsCount.get(word).put(tag, 1);		
				}else {
					HashMap<Gram, Integer> tagMap = new HashMap<>();
					tagMap.put(tag, 1);
					wordTowardsTagsCount.put(word, tagMap);
				}
				
				if(tagTowardsWordsCount.containsKey(tag)) {
					if(tagTowardsWordsCount.get(tag).containsKey(word)) 
						tagTowardsWordsCount.get(tag).put(word, tagTowardsWordsCount.get(tag).get(word) + 1);
					else 
						tagTowardsWordsCount.get(tag).put(word, 1);
				}else {
					HashMap<Gram, Integer> wordMap = new HashMap<>();
					wordMap.put(word, 1);
					tagTowardsWordsCount.put(tag, wordMap);
				}
			}
		}
		reader.close();
	}
	
	/**
	 * 返回根据给定n元模型计算的序列的概率
	 * @param sequence	给定的序列
	 * @param lModel	给定的n元模型
	 * @param order		指定n的大小
	 * @param boundary	指定是否为序列加边界
	 * @return			根据给定n元模型计算的序列的概率
	 */
	private static double calcSequenceProb(Gram[] sequence, NGramLanguageModel lModel, int n, boolean boundary) {		
		if(sequence.length < 1 || sequence == null) {
			System.out.println("给定序列元素为空");
			System.exit(0);
		}
		
		//n大于模型的最高阶时，采用模型的最高阶
		n = n > lModel.getOrder() ? lModel.getOrder() : n;
		
		return lModel.getSequenceLogProbability(sequence, n, boundary);
	}
	
	/**
	 * 返回P(T|W) = p(t1|w1)*p(t2|w2)*...*p(tn|wn)
	 * @param words 给定词序列
	 * @param tags	给定词性序列
	 * @return		P(T|W)
	 */
	private static double calcTagsByWordsSequenceProb(Gram[] words, Gram[] tags) {
		double prob = 1.0;
		
		if(words.length != tags.length) {
			System.out.println("两个序列中的元素不等");
			return 0;
		}
		
		for(int i = 0; i < words.length; i++) {
			Gram word = words[i];
			Gram tag = tags[i];
			int N = getWordCount(word);
			int n = getTagCountByWord(word, tag);
			prob *= (1.0 * n / N);
		}
		
		return prob;
	}
	
	/**
	 * 返回P(W|T) = p(w1|t1)*p(w2|t2)*...*p(wn|tn)
	 * @param tags	给定词性序列
	 * @param words 给定词序列
	 * @return		P(W|T)
	 */
	private static double calcWordsByTagsSequenceProb(Gram[] tags, Gram[] words) {
		double prob = 1.0;
		
		if(tags.length != words.length) {
			System.out.println("两个序列中的元素不等");
			return 0;
		}
		
		for(int i = 0; i < words.length; i++) {
			Gram word = words[i];
			Gram tag = tags[i];
			int N = getTagCount(tag);
			int n = getWordCountByTag(tag, word);
			prob *= (1.0 * n / N);
		}
		
		return prob;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		wordLM = loadNGramLM("E:\\wordLM.bin");
		tagLM = loadNGramLM("E:\\tagLM.bin");
//		wordTagLM = loadNGramLM("E:\\wordTagLM.obj");
		statWordsAndTags("E:\\wordTagCorpus.txt");
		
		Gram[] wordSequence = new Gram[]{new StringGram("年轻人"), new StringGram("是"),
				 new StringGram("中国"), new StringGram("的"), new StringGram("希望")};		//正确: 年轻人/是/中国/的/希望
		
		Gram[] wordSequence1 = new Gram[]{new StringGram("年轻人"), new StringGram("是"),
				 new StringGram("中"), new StringGram("国的"), new StringGram("希望")};		//错误1：年轻人/是/中/国的/希望
		Gram[] wordSequence2 = new Gram[]{new StringGram("年轻人"), new StringGram("是中"),
				 new StringGram("国"), new StringGram("的"), new StringGram("希望")};		//错误2：年轻人/是中/国/的/希望
		Gram[] wordSequence3 = new Gram[]{new StringGram("年轻"), new StringGram("人是"),
				 new StringGram("中国"), new StringGram("的"), new StringGram("希望")};		//错误3：年轻/人是/中国/的/希望
		Gram[] wordSequence4 = new Gram[]{new StringGram("年轻"),new StringGram("人"), new StringGram("是"),
				 new StringGram("中国"), new StringGram("的"), new StringGram("希望")};		//错误4：年轻/人/是/中/国的/希望
		
		Gram[] tagSequence = new Gram[]{new StringGram("n"), new StringGram("v"),
				 new StringGram("ns"), new StringGram("u"), new StringGram("n")};	//正确
		
		Gram[] tagSequence1 = new Gram[]{new StringGram("v"), new StringGram("v"),
				 new StringGram("ns"), new StringGram("u"), new StringGram("n")};	//错误1 n-->v
		Gram[] tagSequence2 = new Gram[]{new StringGram("a"), new StringGram("v"),
				 new StringGram("ns"), new StringGram("u"), new StringGram("n")};	//错误2 n-->a
		Gram[] tagSequence3 = new Gram[]{new StringGram("n"), new StringGram("v"),
				 new StringGram("nr"), new StringGram("u"), new StringGram("n")};	//错误3 ns-->nr
		Gram[] tagSequence4 = new Gram[]{new StringGram("a"), new StringGram("v"),
				 new StringGram("ns"), new StringGram("u"), new StringGram("v")};	//错误4 n-->a, n-->v
//		Gram[] wordTagSequence = new Gram[]{new WordTagGram("年轻人", "n"), new WordTagGram("是", "v"),
//				 new WordTagGram("中国", "ns"), new WordTagGram("的", "u"), new WordTagGram("希望", "n")};
		
		System.out.println("正确词串："+calcSequenceProb(wordSequence, wordLM, 3, true));
		System.out.println("错误词串1："+calcSequenceProb(wordSequence1, wordLM, 3, true));
		System.out.println("错误词串2："+calcSequenceProb(wordSequence2, wordLM, 3, true));
		System.out.println("错误词串3："+calcSequenceProb(wordSequence3, wordLM, 3, true));
		System.out.println("错误词串4："+calcSequenceProb(wordSequence4, wordLM, 3, true));

		
		System.out.println("\n正确词性："+calcSequenceProb(tagSequence, tagLM, 4, true));
		System.out.println("错误词性串1："+calcSequenceProb(tagSequence1, tagLM, 4, true));
		System.out.println("错误词性串2："+calcSequenceProb(tagSequence2, tagLM, 4, true));
		System.out.println("错误词性串3："+calcSequenceProb(tagSequence3, tagLM, 4, true));
		System.out.println("错误词性串4："+calcSequenceProb(tagSequence4, tagLM, 4, true));

//		System.out.println(calcSequenceProb(wordTagSequence, wordTagLM, 3, true));
		System.out.println("\nP(T|W)："+ calcTagsByWordsSequenceProb(wordSequence, tagSequence));
		System.out.println("P(W|T)："+ calcWordsByTagsSequenceProb(tagSequence, wordSequence));
	}
}
