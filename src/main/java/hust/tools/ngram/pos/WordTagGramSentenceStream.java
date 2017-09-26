package hust.tools.ngram.pos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hust.tools.ngram.utils.AbstractGramSentenceStream;
import hust.tools.ngram.utils.Gram;

public class WordTagGramSentenceStream extends AbstractGramSentenceStream{

	public WordTagGramSentenceStream(File file, String encoding)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(file, encoding);
	}

	public WordTagGramSentenceStream(String pathname, String encoding)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(pathname, encoding);
	}

	@Override
	protected Iterator<Gram[]> createGrams(List<String> lines) {
		List<Gram[]> list = new ArrayList<>();
		
		for(int i = 0; i < lines.size(); i++) {
			String[] strings = lines.get(i).split("\\s+");
			Gram[] grams = new Gram[strings.length];
			
			for(int j = 0; j < strings.length; j++) {
				String[] wordTag = strings[j].split("/");
				grams[j] = new WordTagGram(wordTag[0], wordTag[1]);
			}
			
			list.add(grams);
		}

		return list.iterator();
	}

}
