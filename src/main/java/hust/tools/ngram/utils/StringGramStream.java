package hust.tools.ngram.utils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *<ul>
 *<li>Description: 流式读取String类型的元
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月16日
 *</ul>
 */
public class StringGramStream extends AbstractGramStream {

	public StringGramStream(String pathname, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
		super(pathname, encoding);
	}

	@Override
	protected Iterator<Gram> createGrams(List<String> lines) {
		List<Gram> list = new ArrayList<>();
		
		for(int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			line = ToDBC(line).replaceAll("\\s", "");
			String[] strings = line.split("");
			
			for(int j = 0; j < strings.length; j++) 
				list.add(new StringGram(strings[j]));
		}

		return list.iterator();
	}
}
