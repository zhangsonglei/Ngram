package hust.tools.ngram.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ngram.datastructure.Gram;
import hust.tools.ngram.model.NGramCounter;
import hust.tools.ngram.utils.StringGram;

/**
 *<ul>
 *<li>Description: 测试n元计数文件的读写 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月31日
 *</ul>
 */
public class NGramCountWriterAndReaderTest {	

	Gram[] grams;
	NGramCounter trainNGramCounter;
	NGramCounter loadNGramCounter;
	
	@Before
	public void setup() throws IOException {
		grams = new Gram[]{new StringGram("1"), new StringGram("2"),new StringGram("3"), new StringGram("4"),
				  		   new StringGram("5"), new StringGram("6"),new StringGram("1"), new StringGram("3"), 
				  		   new StringGram("1"), new StringGram("4"), new StringGram("5"), new StringGram("6")};
		trainNGramCounter = new NGramCounter(grams, 3);
	}
	
	/**
	 * 测试n元计数文本文件的读写  
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testTextFileNGramCountWriterAndReader() throws IOException, ClassNotFoundException {
		//写入文件
		TextFileNGramCountWriter tFNGramCountWriter = new TextFileNGramCountWriter(trainNGramCounter, 
				new File("files\\count\\TestIO_trigramCount.txt"));
		tFNGramCountWriter.persist();
		
		//读取文件
		TextFileNGramCountReader reader = new TextFileNGramCountReader(new File("files\\count\\TestIO_trigramCount.txt"));
		loadNGramCounter = reader.constructNGramCount();
		
		//判断读写
		assertEquals(trainNGramCounter, loadNGramCounter);
	}
	
	/**
	 * 测试n元序列化文件的读写  
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testObjectFileNGramCountWriterAndReader() throws IOException, ClassNotFoundException {
		//写入文件
		ObjectFileNGramCountWriter oFNGramCountWriter = new ObjectFileNGramCountWriter(trainNGramCounter, 
				new File("files\\count\\TestIO_objectCount.out"));
		oFNGramCountWriter.persist();
		
		//读取文件
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("files\\count\\TestIO_objectCount.out")));
		ObjectFileNGramCountReader reader = new ObjectFileNGramCountReader(ois);
		loadNGramCounter = reader.constructNGramCount();
		
		//判断读写
		assertEquals(trainNGramCounter, loadNGramCounter);
	}
	
	/**
	 * 测试n元计数二进制文件的读写  
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testBinaryFileNGramCountWriterAndReader() throws IOException, ClassNotFoundException {
		//写入文件
		BinaryFileNGramCountWriter bFNGramCountWriter = new BinaryFileNGramCountWriter(trainNGramCounter, 
				new File("files\\count\\TestIO_binaryCount.bin"));
		bFNGramCountWriter.persist();
		
		//读取文件
		BinaryFileNGramCountReader reader = new BinaryFileNGramCountReader(new File("files\\count\\TestIO_binaryCount.bin"));
		loadNGramCounter = reader.constructNGramCount();
		
		//判断读写
		assertEquals(trainNGramCounter, loadNGramCounter);
	}
}
