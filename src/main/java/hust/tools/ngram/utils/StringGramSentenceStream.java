package hust.tools.ngram.utils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *<ul>
 *<li>Description: 流式读取String类型的元组
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月16日
 *</ul>
 */
public class StringGramSentenceStream extends AbstractGramSentenceStream {

	public StringGramSentenceStream(String pathname, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
		super(pathname, encoding);
	}

	@Override
	protected Iterator<Gram[]> createGrams(List<String> lines) {
		List<Gram[]> list = new ArrayList<>();
		
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			line = ToDBC(line).replaceAll("\\s", "");
			String[] strings = line.split("");
			Gram[] grams = new Gram[strings.length];
			
			for(int j = 0; j < strings.length; j++) 
				grams[j] = new StringGram(strings[j]);
			
			list.add(grams);
		}

		return list.iterator();
	}
}
