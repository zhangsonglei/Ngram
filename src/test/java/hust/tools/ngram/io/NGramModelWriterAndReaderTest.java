package hust.tools.ngram.io;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ngram.datastructure.Gram;
import hust.tools.ngram.model.NGramCounter;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.model.AbstractLanguageModelTrainer;
import hust.tools.ngram.model.MLLanguageModelTrainer;
import hust.tools.ngram.utils.StringGram;

/**
 *<ul>
 *<li>Description: 测试模型的读写 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月31日
 *</ul>
 */
public class NGramModelWriterAndReaderTest {

	Gram[] grams;
	String smooth = "ml";
	AbstractLanguageModelTrainer modelTrainer;
	NGramLanguageModel trainModel;
	NGramLanguageModel loadModel;
	
	@Before
	public void setup() {
		grams = new Gram[]{new StringGram("1"), new StringGram("2"),new StringGram("3"), new StringGram("4"),
				  		   new StringGram("3"), new StringGram("2"),new StringGram("3"), new StringGram("5")};
		NGramCounter nGramCounter = new NGramCounter(grams, 3);
		modelTrainer = new MLLanguageModelTrainer(nGramCounter, 3);
		
		trainModel = modelTrainer.trainModel();
	}
	
	/**
	 * 测试arpa格式n元模型文本文件的读写  
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testARPATextFileNGramModleWriterAndReader() throws IOException, ClassNotFoundException {
		//写入模型
		ARPATextFileNGramModleWriter arpaNGramModelWiter = new ARPATextFileNGramModleWriter(trainModel, 
				new File("files\\lm\\TestIO_arpa_trigram.lm"));
		arpaNGramModelWiter.persist();
		
		//读取模型
		File file = new File("files\\lm\\TestIO_arpa_trigram.lm");
		ARPATextFileNGramModleReader reader = new ARPATextFileNGramModleReader(file);
		loadModel = reader.constructModel();
		
		//判断模型
		assertEquals(trainModel, loadModel);
	}
	
	/**
	 * 测试n元模型文本文件的读写  
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testTextFileNGramModleWriterAndReader() throws IOException, ClassNotFoundException {
		//写入模型
		TextFileNGramModelWriter tFNGramModelWiter = new TextFileNGramModelWriter(trainModel, 
				new File("files\\lm\\TestIO_trigramModel.txt"));
		tFNGramModelWiter.persist();
		
		//读取模型
		File file = new File("files\\lm\\TestIO_trigramModel.txt");
		TextFileNGramModelReader reader = new TextFileNGramModelReader(file);
		loadModel = reader.constructModel();
		
		//判断模型
		assertEquals(trainModel, loadModel);
	}
	
	/**
	 * 测试n元模型序列化文件的读写
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testObjectFileNGramModleWriterAndReader() throws IOException, ClassNotFoundException {
		//写入模型
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("files\\lm\\TestIO_objectModel.out")));
		ObjectFileNGramModelWriter oFNGramModelWriter = new ObjectFileNGramModelWriter(trainModel, oos);
		oFNGramModelWriter.persist();
				
		//读取模型
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("files\\lm\\TestIO_objectModel.out")));
		ObjectFileNGramModelReader reader = new ObjectFileNGramModelReader(ois);
		loadModel = reader.constructModel();
				
		//判断模型
		assertEquals(trainModel, loadModel);
	}
	
	/**
	 * 测试n元模型二进制文件的读写
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testBinaryFileNGramModleWriterAndReader() throws IOException, ClassNotFoundException {
		//写入模型
		BinaryFileNGramModelWriter bFNGramModelWiter = new BinaryFileNGramModelWriter(trainModel, 
				new File("files\\lm\\TestIO_binaryModel.bin"));
		bFNGramModelWiter.persist();
		
		//读取模型
		File file = new File("files\\lm\\TestIO_binaryModel.bin");
		BinaryFileNGramModelReader reader = new BinaryFileNGramModelReader(file);
		loadModel = reader.constructModel();
		
		//判断模型
		assertEquals(trainModel, loadModel);
	}	
}
