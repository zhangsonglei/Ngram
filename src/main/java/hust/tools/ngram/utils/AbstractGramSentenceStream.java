package hust.tools.ngram.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import hust.tools.ngram.datastructure.Gram;

/**
 *<ul>
 *<li>Description: 从训练语料中流式读取元组的抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月16日
 *</ul>
 */
public abstract class AbstractGramSentenceStream implements GramSentenceStream{
	
	private BufferedReader bufferedReader;
	
	/**
	 * 每次读取的行数
	 */
	private final int  slide_length = 1000;
	
	/**
	 * 流式读取的迭代器
	 */
	private Iterator<Gram[]> grams = Collections.<Gram[]>emptyList().iterator();
	
	/**
	 * 将训练语料解析成元序列的抽象方法
	 * @param lines 从训练语料中读取的行
	 * @return 解析后的元序列
	 */
	protected abstract Iterator<Gram[]> createGrams(List<String> lines);

	public AbstractGramSentenceStream(String pathname, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
		File file = new File(pathname);
		InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), encoding);
		this.bufferedReader = new BufferedReader(inputStreamReader);
	}

	public AbstractGramSentenceStream(File file, String encoding) throws FileNotFoundException, UnsupportedEncodingException {
		InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), encoding);
		this.bufferedReader = new BufferedReader(inputStreamReader);
	}

	/**
	 * 读取从流中下一个元，重复调用该方法直到返回null，每次从底层源中返回一个元
	 * @return 下一个元
	 * @throws IOException 如果读取过程中出错，抛出异常
	 */
	public final Gram[] next() throws IOException {

		if (grams.hasNext()) {
			return grams.next();
		}
		else {
			List<String> lines = new ArrayList<>();
			String line = "";
			int size = 0;
			while (!grams.hasNext() && (line = bufferedReader.readLine()) != null) {
				line  = line.trim();
				if(!line.equals(""))
					lines.add(line);
			
				if(++size == slide_length)//每次读取1000行
					grams = createGrams(lines);
			}
			
			//语料中剩余的小于slide_length的所有行
			if(size != 0)
				grams = createGrams(lines);

			if (grams.hasNext()) {
				return next();
			}
		}

		return null;
	}
	
	@Override
	public void reset() throws IOException, UnsupportedOperationException {
		grams = Collections.emptyIterator();
		bufferedReader.reset();
	}

	@Override
	public void close() throws IOException {
		bufferedReader.close();
	}
}
