package hust.tools.ngram.utils;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 *<ul>
 *<li>Description: 测试String类型的句子流的读取 ，带开始结束标签
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年9月1日
 *</ul>
 */
public class StringGramSentenceStreamTest {

	StringGramSentenceStream sGramStream;
	String encoding;
	List<String> list = new ArrayList<>();
	
	@Before
	public void setup() throws IOException  {
		String filePath = "src\\test\\java\\hust\\tools\\ngram\\utils\\testCorpus.txt";
		encoding = "utf-8";
		sGramStream = new StringGramSentenceStream(filePath, encoding);
		
		File file = new File(filePath);
		FileInputStream fileInputStream = new FileInputStream(file);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encoding);
		BufferedReader bReader = new BufferedReader(inputStreamReader);
		
		String line = "";
		while((line = bReader.readLine()) != null) {
			line = line.trim();
			if(line.equals(""))
				continue;
			list.add(line);
		}
		
		bReader.close();
	}
	
	@Test
	public void testNext() throws IOException{
		Gram[] grams = null;
		int index = 0;
		while((grams = sGramStream.nextSentence()) != null) {
			String[] strings = list.get(index++).replaceAll("\\s+", "").split("");
			assertEquals(grams.length, strings.length);
			for(int i = 0; i < strings.length; i++)
				assertEquals(grams[i], new StringGram(strings[i]));
		}
		
		assertEquals(index, list.size());	
	}	

}
