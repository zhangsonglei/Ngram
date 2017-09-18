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

import hust.tools.ngram.datastructure.Gram;

/**
 *<ul>
 *<li>Description: 测试String类型的元流的读取 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月31日
 *</ul>
 */
public class StringGramStreamTest {

	StringGramStream sGramStream;
	String encoding;
	List<String> list = new ArrayList<>();
	
	
	@Before
	public void setup() throws IOException  {
		String filePath = "files\\corpus\\testCorpus.txt";
		encoding = "utf-8";
		sGramStream = new StringGramStream(filePath, encoding);
		
		File file = new File(filePath);
		FileInputStream fileInputStream = new FileInputStream(file);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, encoding);
		BufferedReader bReader = new BufferedReader(inputStreamReader);
		
		String line = "";
		while((line = bReader.readLine()) != null) {
			if(line.trim().equals(""))
				continue;
			String[] strings = line.replaceAll(" ", "").split("");
			for(String string : strings)
				list.add(string);
		}
		
		bReader.close();
	}
	
	@Test
	public void testNext() throws IOException{
		Gram gram = null;
		int index = 0;
		while((gram = sGramStream.next()) != null) {
			StringGram sGram = new StringGram(list.get(index++)); 
			assertEquals(sGram, gram);
		}
		
		assertTrue(index == list.size());	
	}	
}