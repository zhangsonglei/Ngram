package hust.tools.ngram.pos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import hust.tools.ngram.io.ARPATextFileNGramModleWriter;
import hust.tools.ngram.io.BinaryFileNGramModelWriter;
import hust.tools.ngram.io.ObjectFileNGramModelWriter;
import hust.tools.ngram.model.AbstractLanguageModelTrainer;
import hust.tools.ngram.model.AbstractNGramModelWriter;
import hust.tools.ngram.model.KatzLanguageModelTrainer;
import hust.tools.ngram.model.KneserNeyLanguageModelTrainer;
import hust.tools.ngram.model.NGramCounter;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.utils.AbstractGramSentenceStream;
import hust.tools.ngram.utils.StringGramSentenceStream;

@SuppressWarnings("unused")
public class LMTrainer {
	
	public static void main(String[] args) throws IOException {		
		String wordFile = "E:\\wordCorpus.txt";
		String tagFile = "E:\\tagCorpus.txt";
		String wordTagFile = "E:\\wordTagCorpus.txt";
		AbstractGramSentenceStream wordStream = new StringGramSentenceStream(wordFile, "utf-8");
		AbstractGramSentenceStream tagStream = new StringGramSentenceStream(tagFile, "utf-8");
		AbstractGramSentenceStream wordTagStream = new WordTagGramSentenceStream(wordTagFile, "utf-8");
		
		NGramCounter wordCounter = new NGramCounter(wordStream, 3);
		NGramCounter tagGramCounter = new NGramCounter(tagStream, 4);
		NGramCounter wordTagCounter = new NGramCounter(wordTagStream, 3);
		
		AbstractLanguageModelTrainer wordLMTrainer = new KneserNeyLanguageModelTrainer(wordCounter, 3);
		AbstractLanguageModelTrainer tagLMTrainer = new KatzLanguageModelTrainer(tagGramCounter, 4);
		AbstractLanguageModelTrainer wordTagLMTrainer = new KneserNeyLanguageModelTrainer(wordTagCounter, 3);
		NGramLanguageModel wordLM = wordLMTrainer.trainModel();
		NGramLanguageModel tagLM = tagLMTrainer.trainModel();
		NGramLanguageModel wordTagLM = wordTagLMTrainer.trainModel();
				
		AbstractNGramModelWriter wordWriter = new BinaryFileNGramModelWriter(wordLM, new File("E:\\wordLM.bin"));
		AbstractNGramModelWriter tagWriter = new BinaryFileNGramModelWriter(tagLM, new File("E:\\tagLM.bin"));
		AbstractNGramModelWriter wordTagWriter = new ObjectFileNGramModelWriter(wordTagLM, new ObjectOutputStream(new FileOutputStream(new File("E:\\wordTagLM.obj"))));
//		AbstractNGramModelWriter wordWriter = new ARPATextFileNGramModleWriter(wordLM, new File("E:\\wordLM.arpa"));
//		AbstractNGramModelWriter tagWriter = new ARPATextFileNGramModleWriter(tagLM, new File("E:\\tagLM.arpa"));
//		AbstractNGramModelWriter wordTagWriter = new ARPATextFileNGramModleWriter(wordTagLM, new File("E:\\wordTagLM.arpa"));

		wordWriter.persist();
		tagWriter.persist();
		wordTagWriter.persist();
	}
}
