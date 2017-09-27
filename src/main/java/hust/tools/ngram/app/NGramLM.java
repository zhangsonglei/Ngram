package hust.tools.ngram.app;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import hust.tools.ngram.io.ARPATextFileNGramModleReader;
import hust.tools.ngram.io.BinaryFileNGramModelReader;
import hust.tools.ngram.io.FileOperator;
import hust.tools.ngram.io.TextFileNGramModelReader;
import hust.tools.ngram.model.AbstractNGramModelReader;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.utils.Gram;
import hust.tools.ngram.utils.StringGram;

public class NGramLM {

	/**
	 * 根据给定的n元模型计算一句话的概率
	 * @param modelFile 给定的n元模型文件
	 * @param sentence	待计算概率的句子
	 * @return			给定句子的概率
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static double getSequenceProbability(String modelFile, String sentence, int n) throws IOException, ClassNotFoundException {
		NGramLanguageModel model = loadModel(modelFile);
		Gram[] grams = parseGrams(sentence);
		
		return model.getSequenceLogProbability(grams, n, true);
	}
	
	/**
	 * 根据给定的n元模型和序列，通过计算最大概率预测下一个token
	 * @param modelFile 给定的n元模型文件
	 * @param sentence	待预测的序列
	 * @return			最大概率对应的下一个token
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static String predictNext(String modelFile, String sentence, int n) throws ClassNotFoundException, IOException {
		NGramLanguageModel model = loadModel(modelFile);
		Gram[] grams = parseGrams(sentence);
		
		return model.getNextPrediction(grams, n, true).toString();
	}
	
	/**
	 * 根据给定的n元模型和测试语料，计算模型的困惑度
	 * @param modelFile	给定的n元模型文件
	 * @param testFile	测试语料文件
	 * @param order		n元最大长度
	 * @param pplFile	保存模型困惑度信息的文件
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static double perplexity(String modelFile, String testFile, int order) throws IOException, ClassNotFoundException {
		NGramLanguageModel model = loadModel(modelFile);
		
		List<String> list = FileOperator.readFileToList(testFile);
		List<Gram[]> test = new LinkedList<>();
		
		while(list.size() > 0) {
			Gram[] grams = parseGrams(list.remove(0).trim());
			test.add(grams);
		}
		
		double perplexity = model.getPerplexity(test, order, true);	
		return perplexity;
	}
	
	/**
	 * 加载模型文件，得到n元模型
	 * @param modelFile					待加载的模型文件
	 * @return							n元模型
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static NGramLanguageModel loadModel(String modelFile) throws ClassNotFoundException, IOException {
		AbstractNGramModelReader modelReader;
		if(modelFile.endsWith(".arpa"))
			modelReader = new ARPATextFileNGramModleReader(new File(modelFile));
		else if(modelFile.endsWith(".bin"))
			modelReader = new BinaryFileNGramModelReader(new File(modelFile));
		else
			modelReader = new TextFileNGramModelReader(new File(modelFile));
		
		return modelReader.constructModel();
	}
	
	/**
	 * 解析字符序列，转化为Gram数组
	 * @param sequence	待解析的字符序列
	 * @return 			Gram数组
	 */
	private static Gram[] parseGrams(String sequence) {
		Gram[] grams = null;
		if(sequence.length() == 0 || sequence == null)
			return grams;
		
		String[] strings = sequence.split("");
		grams = new Gram[strings.length];
		for(int i = 0; i < grams.length; i++)
			grams[i] = new StringGram(strings[i]);
		
		return grams;
	}
	
	/**
	 * <li>指定n元模型文件，和测试文件，计算模型的困惑度并输出。
	 * <li>示例:NGramModelTools ngram.lm binary 3 Y ppl test.txt utf-8
	 * @param args [0]-功能(prob-计算句子概率， predict-预测序列下一个词), [1]-模型文件路径, 
	 * 			   [2]-模型文件类型(文本文件text/二进制文件binary/序列化文件object), [3]-n元长度, [4]-是否按句处理语料(Y/N), 
	 * 			   [5]-待计算/预测的序列
	 * @param args [0]-ppl(计算模型困惑度), [1]-模型文件路径, [2]-模型文件类型(文本文件text/二进制文件binary/序列化文件object), 
	 * 			   [3]-n元长度, [4]-是否按句处理语料(Y/N), [5]-测试语料的文件路径, [6]测试语料的编码
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		int len = args.length;
		
		if(!(6 == len || 7 == len)) {
			showParmsErr(len);
		}else {
			String func = args[0].toLowerCase();
			if(!(func.equals("prob") || func.equals("predict") || func.equals("ppl"))) {
				System.err.println("错误的指令："+args[0]+"\nprob 计算句子概率 \npredict 预测序列下一个词\nppl 计算模型困惑度");
				System.exit(0);
			}else if(!((func.equals("prob") || func.equals("predict")) && len == 6 || func.equals("ppl") && len == 7)) {
				showParmsErr(len);
				System.exit(0);
			}
			
			String type = args[2].toUpperCase();
			int order = Integer.parseInt(args[3]);
			String sentence = args[4].toUpperCase();
		
			if(!(type.equals("TEXT") || type.equals("BINARY") || type.equals("OBJECT"))) {
				System.err.println("错误的输出文件类型："+args[5]+"\ntext-文本文件, binary-二进制文件, object-序列化文件");
			}else if(order < 1) {
				System.err.println("错误的n元长度："+args[2]+"\nn元长度为大于0的整数");
			}else if(!(sentence.equals("Y") || sentence.equals("N"))) {
				System.err.println("错误的语料处理方式："+args[3]+"\nY/y-以行为单位区分句子，N/n不区分句子");
			}else  {
				if(6 == len) {
					if(func.equals("prob")) {
						System.out.println(getSequenceProbability(args[1], args[5], order));
					}else {
						System.out.println(predictNext(args[1], args[5], order));
					}
				}else {
						System.out.println(perplexity(args[1], args[5], order));
				}
			}
		}
	}
	
	private static void showParmsErr(int len) {
		System.err.println("错误的参数个数："+len+
				"\n示例:NGramModelTools 功能prob/predict 模型文件路径  模型文件类型text/binary/object  n元长度  是否按句处理Y/N  待计算序列"+
				"\n示例:NGramModelTools 功能ppl 模型文件路径  模型文件类型  sn元长度  是否按句处理Y/N  测试语料的文件路径  测试语料的编码");
	}
}
