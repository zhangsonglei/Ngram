package hust.tools.ngram.pos;

import java.io.IOException;

import hust.tools.ngram.io.ARPATextFileNGramModleWriter;
import hust.tools.ngram.io.TextFileNGramModelWriter;
import hust.tools.ngram.model.AbstractLanguageModelTrainer;
import hust.tools.ngram.model.KatzLanguageModelTrainer;
import hust.tools.ngram.model.KneserNeyLanguageModelTrainer;
import hust.tools.ngram.model.NGramCounter;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.utils.AbstractGramSentenceStream;
import hust.tools.ngram.utils.StringGram;
import hust.tools.ngram.utils.StringGramSentenceStream;

public class POS {
	
	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		
		String wordFile = "E:\\wordCorpus.txt";
		String tagFile = "E:\\tagCorpus.txt";
		String wordTagFile = "E:\\wordTagCorpus.txt";
//		AbstractGramSentenceStream wordStream = new StringGramSentenceStream(wordFile, "utf-8");
//		AbstractGramSentenceStream tagStream = new StringGramSentenceStream(tagFile, "utf-8");
		AbstractGramSentenceStream wordTagStream = new WordTagGramSentenceStream(wordTagFile, "utf-8");
		
//		NGramCounter wordCounter = new NGramCounter(wordStream, 3);
//		NGramCounter tagGramCounter = new NGramCounter(tagStream, 4);
		NGramCounter wordTagCounter = new NGramCounter(wordTagStream, 3);
		long count = System.currentTimeMillis();
		System.out.println("计数："+(count - start)+" ms");
		
//		AbstractLanguageModelTrainer wordLMTrainer = new KneserNeyLanguageModelTrainer(wordCounter, 3);
//		AbstractLanguageModelTrainer tagLMTrainer = new KatzLanguageModelTrainer(tagGramCounter, 4);
		AbstractLanguageModelTrainer wordTagLMTrainer = new KneserNeyLanguageModelTrainer(wordTagCounter, 3);
//		NGramLanguageModel wordLM = wordLMTrainer.trainModel();
//		NGramLanguageModel tagLM = tagLMTrainer.trainModel();
		NGramLanguageModel wordTagLM = wordTagLMTrainer.trainModel();
		
		long lm = System.currentTimeMillis();
		System.out.println("训练："+(lm - count)+" ms");
		System.out.println("计数训练："+(lm - start)+" ms");
		
//		ARPATextFileNGramModleWriter wordWriter = new ARPATextFileNGramModleWriter(wordLM, "E:\\wordTrigram.arpa");
//		ARPATextFileNGramModleWriter tagWriter = new ARPATextFileNGramModleWriter(tagLM, "E:\\tagTrigram.arpa");
//		ARPATextFileNGramModleWriter wordTagWriter = new ARPATextFileNGramModleWriter(wordTagLM, "E:\\tagWordTrigram.arpa");
		TextFileNGramModelWriter wordTagWriter = new TextFileNGramModelWriter(wordTagLM, "E:\\tagWordTrigram.arpa");
//		wordWriter.persist();
//		tagWriter.persist();
		wordTagWriter.persist();
		long write = System.currentTimeMillis();
		System.out.println("写入："+(write - lm)+" ms");
		System.out.println("计数训练写入："+(write - start)+" ms");
		
		
//		NGram wordNGram = new NGram(new Gram[]{new StringGram("中国")});
//		System.out.println(wordCounter.getNGramCount(wordNGram));
//		System.out.println(wordLM.getNGramLogProbability(wordNGram));
//		
//		NGram tagNGram = new NGram(new Gram[]{new StringGram("<s>")});
//		NGram ngram1 = new NGram(new Gram[]{new StringGram("Tg")});
//		System.out.println(tagGramCounter.getNGramCount(tagNGram));
//		System.out.println(tagLM.getNGramLogProbability(tagNGram));
//		System.out.println(tagGramCounter.getTotalNGramCountByN(1));
//		System.out.println(tagGramCounter.getNGramCount(ngram1));
	}
}
