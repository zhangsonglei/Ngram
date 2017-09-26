package hust.tools.ngram.model;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.NGram;
import hust.tools.ngram.utils.StringGram;

/**
 *<ul>
 *<li>Description: 测试n元模型的学习
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年7月30日
 *</ul>
 */
public class NGramLanguageModelTest {
	
	NGramLanguageModel nGramLM;
	AbstractLanguageModelTrainer modelTrainer;
	NGramCounter nGramCounter;
	Gram[] vocab = new Gram[]{new StringGram("1"),new StringGram("2"),new StringGram("3"),
			  new StringGram("4"),new StringGram("5"),new StringGram("6"),
			  new StringGram("7"),new StringGram("8"),new StringGram("9"),new StringGram("0")};
	
	Gram[] grams = new Gram[]{new StringGram("1"), new StringGram("2"),new StringGram("3"), new StringGram("4"),
			  new StringGram("3"), new StringGram("2"),new StringGram("3"), new StringGram("5")};
	
	Gram[] grams1 = new Gram[]{new StringGram("1")};
	Gram[] grams2 = new Gram[]{new StringGram("2"), new StringGram("3")}; 
	Gram[] grams3 = new Gram[]{new StringGram("2"), new StringGram("3"), new StringGram("4")};
	Gram[] grams4 = new Gram[]{new StringGram("3"), new StringGram("3")};
	Gram[] grams5 = new Gram[]{new StringGram("6"), new StringGram("1")};
	NGram nGram1 = new NGram(grams1);	//seen
	NGram nGram2 = new NGram(grams2);	//seen
	NGram nGram3 = new NGram(grams3);	//seen
	NGram nGram4 = new NGram(grams4);	//unseen
	NGram nGram5 = new NGram(grams5);	//oov
	
	@Before
	public void setup() throws IOException {
		nGramCounter = new NGramCounter(grams, 3);
	}
	
	/**
	 * 根据平滑方法选择模型训练器
	 * @param nGramCounter	n元计数器
	 * @param order			最大n元长度
	 * @param smooth		平滑方法
	 * @return				模型训练器
	 * @throws IOException
	 */
	private AbstractLanguageModelTrainer selectSmoothingModel(NGramCounter nGramCounter, int order, String smooth) throws IOException {
		AbstractLanguageModelTrainer trainer = null;
		switch (smooth.toLowerCase()) {
		case "ml":
			trainer = new MLLanguageModelTrainer(nGramCounter, order);
			break;
		case "laplace":
			trainer = new LaplaceLanguageModelTrainer(nGramCounter, order);
			break;
		case "interpolate":
			trainer = new InterpolationLanguageModelTrainer(nGramCounter, order);
			break;
		case "katz":
			trainer = new KatzLanguageModelTrainer(nGramCounter, order);
			break;
		case "kn":
			trainer = new KneserNeyLanguageModelTrainer(nGramCounter, order);
			break;
		default:
			throw new IllegalArgumentException("错误的平滑方法:"+smooth);
		}
	
		return trainer;
	}
	
	/**
	 * 测试最大似然模型
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testMLModel() throws IOException, ClassNotFoundException {
		String smooth = "ml";
		modelTrainer = selectSmoothingModel(nGramCounter, 3, smooth);
		nGramLM = modelTrainer.trainModel();

		//测试获取n元的概率的对数
		assertTrue(Math.log10(1.0/8) == nGramLM.getNGramLogProbability(nGram1));
		assertTrue(Math.log10(2.0/2) == nGramLM.getNGramLogProbability(nGram2));
		assertTrue(Math.log10(1.0/2) == nGramLM.getNGramLogProbability(nGram3));
		assertTrue(Math.log10(0.0) == nGramLM.getNGramLogProbability(nGram4));
		assertTrue(Math.log10(0.0) == nGramLM.getNGramLogProbability(nGram5));
		
		Gram[] sequence = new Gram[]{new StringGram("1"), new StringGram("2"), new StringGram("3"),
				 new StringGram("2"), new StringGram("3"), new StringGram("6"), };
		Gram[] sentence = new Gram[]{new StringGram("2"), new StringGram("3"), 
									 new StringGram("4"), new StringGram("3")};
		//测试计算序列的概率
		assertTrue(0.0 == nGramLM.getSequenceLogProbability(sequence, 3));
		assertTrue(0.125 == nGramLM.getSequenceLogProbability(sentence, 3));
		
		//预测下一个词
		NGram next = new NGram(new Gram[]{new StringGram("2"), new StringGram("3")});
		assertEquals(next, nGramLM.getNextPrediction(sentence, 3));
	}
	
	/**
	 * 测试Laplace平滑模型
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	@Test
	public void testLaplaceModel() throws IOException, ClassNotFoundException {
		String smooth = "laplace";
		modelTrainer = selectSmoothingModel(nGramCounter, 3, smooth);
		nGramLM = modelTrainer.trainModel();

		//测试获取n元的概率的对数
		assertTrue(Math.log10((1.0 + 1)/(8 + 6)) == nGramLM.getNGramLogProbability(nGram1));
		assertTrue(Math.log10((1.0 + 2)/(2 + 6)) == nGramLM.getNGramLogProbability(nGram2));
		assertTrue(Math.log10((1.0 + 1)/(2 + 6)) == nGramLM.getNGramLogProbability(nGram3));
		assertTrue(Math.log10((1.0 + 0)/(3 + 6)) == nGramLM.getNGramLogProbability(nGram4));
		assertTrue(Math.log10(1.0/(8 + 6)) == nGramLM.getNGramLogProbability(nGram5));
				
		Gram[] sequence = new Gram[]{new StringGram("1"), new StringGram("2"), new StringGram("3"),
				new StringGram("2"), new StringGram("3"), new StringGram("6")};
		Gram[] sentence = new Gram[]{new StringGram("2"), new StringGram("3"), 
				new StringGram("4"), new StringGram("3")};

		//测试计算序列的概率
		assertEquals(1.0 / 19208, nGramLM.getSequenceLogProbability(sequence, 3), 0.000000000000000001);
		//预测下一个词
		NGram next = new NGram(new Gram[]{new StringGram("2")});
		assertEquals(next, nGramLM.getNextPrediction(sentence, 3));
	}

	
	/**
	 * 测试Katz平滑模型
	 * @throws IOException 
	 */
	@Test
	public void testKatzModel() throws IOException {
		String smooth = "Katz";
		modelTrainer = selectSmoothingModel(nGramCounter, 3, smooth);
		nGramLM = modelTrainer.trainModel();
	
	}
	
	/**
	 * 测试Interpolation平滑模型
	 * @throws IOException 
	 */
	@Test
	public void testInterpolationModel() throws IOException {
		String smooth = "Interpolation";
		modelTrainer = selectSmoothingModel(nGramCounter, 3, smooth);
		nGramLM = modelTrainer.trainModel();
		
	}


	/**
	 * 测试Kneser-Ney平滑模型
	 * @throws IOException 
	 */
	@Test
	public void testKneserNeyModel() throws IOException {
		String smooth = "KN";
		modelTrainer = selectSmoothingModel(nGramCounter, 3, smooth);
		nGramLM = modelTrainer.trainModel();
	
	}
}
