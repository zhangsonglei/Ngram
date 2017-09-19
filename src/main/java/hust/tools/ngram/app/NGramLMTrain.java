package hust.tools.ngram.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import hust.tools.ngram.io.ARPATextFileNGramModleWriter;
import hust.tools.ngram.io.BinaryFileNGramModelWriter;
import hust.tools.ngram.io.ObjectFileNGramModelWriter;
import hust.tools.ngram.io.TextFileNGramModelWriter;
import hust.tools.ngram.model.AbstractLanguageModelTrainer;
import hust.tools.ngram.model.AbstractNGramModelWriter;
import hust.tools.ngram.model.InterpolationLanguageModelTrainer;
import hust.tools.ngram.model.KatzLanguageModelTrainer;
import hust.tools.ngram.model.KneserNeyLanguageModelTrainer;
import hust.tools.ngram.model.LaplaceLanguageModelTrainer;
import hust.tools.ngram.model.MLLanguageModelTrainer;
import hust.tools.ngram.model.NGramCounter;
import hust.tools.ngram.model.NGramLanguageModel;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;
import hust.tools.ngram.utils.StringGramSentenceStream;
import hust.tools.ngram.utils.StringGramStream;

public class NGramLMTrain {

	/**
	 * 根据给定的训练语料和平滑方法，生成n元模型文件 
	 * @param stream	训练语料
	 * @param order		n元的最大长度
	 * @param smooth	平滑方法
	 * @param modelFile	保存n元模型的文件路径
	 * @param type		模型文件的类型
	 * @throws IOException
	 */
	public static void getModelFile(GramStream stream, int order, String smooth, String modelFile, 
			String type) throws IOException {
		NGramCounter nGramCounter = new NGramCounter(stream, order);
		AbstractLanguageModelTrainer trainer = selectSmoothingModel(nGramCounter, order, smooth);		
		NGramLanguageModel lModel = trainer.trainModel();
		
		writeModel(lModel, modelFile, type);
	}
	
	/**
	 * 根据给定的训练语料和平滑方法，生成n元模型文件 
	 * @param stream	训练语料
	 * @param order		n元的最大长度
	 * @param smooth	平滑方法
	 * @param modelFile	保存n元模型的文件路径
	 * @param type		模型文件的类型
	 * @throws IOException
	 */
	public static void getModelFile(GramSentenceStream stream, int order, String smooth, String modelFile,
			String type) throws IOException {
		NGramCounter nGramCounter = new NGramCounter(stream, order);
		AbstractLanguageModelTrainer trainer = selectSmoothingModel(nGramCounter, order, smooth);
		NGramLanguageModel lModel = trainer.trainModel();
		
		writeModel(lModel, modelFile, type);
	}
	
//	/**
//	 * 训练语料，生成n元模型文件
//	 * @param corpusFile	训练语料
//	 * @param order 		n元的最大长度
//	 * @param smooth		平滑方法
//	 * @param modelFile		保存n元模型的文件
//	 * @param type		模型文件的类型
//	 * @throws IOException
//	 * @throws ClassNotFoundException 
//	 */
//	public void getModelFileByCounter(String countFile ,int order, String smooth, String modelFile, 
//			String type) throws IOException, ClassNotFoundException {
//		NGramCountReader countReader;
//		NGramCounter nGramCounter;
//		if(countFile.endsWith(".bin"))
//			countReader = new BinaryFileNGramCountReader(new File(countFile));
//		else
//			countReader = new TextFileNGramCountReader(new File(countFile));
//		nGramCounter = countReader.constructNGramCount();
//		
//		AbstractLanguageModelTrainer trainer = selectSmoothingModel(nGramCounter, order, smooth);
//		NGramLanguageModel lModel = trainer.trainModel();
//		
//		writeModel(lModel, modelFile, type);
//	}

	
	/**
	 * 将n元模型写入文件
	 * @param lModel	待写入的n元模型
	 * @param modelFile	写入路径
	 * @param type 		模型文件的类型
	 * @throws IOException
	 */
	private static void writeModel(NGramLanguageModel lModel, String modelFile, String type) throws IOException {
		AbstractNGramModelWriter modelWriter;
		
		/**
		 * text-文本文件
		 * binary-二进制文件
		 * object-序列化文件
		 * arpa-ARPA格式文本文件
		 */
		if(type.equals("text"))
			modelWriter = new TextFileNGramModelWriter(lModel, new File(modelFile));
		else if(type.equals("binary"))
			modelWriter = new BinaryFileNGramModelWriter(lModel, new File(modelFile));
		else if(type.equals("object"))
			modelWriter = new ObjectFileNGramModelWriter(lModel, new ObjectOutputStream(new FileOutputStream(new File(modelFile))));
		else if(type.equals("arpa"))
			modelWriter = new ARPATextFileNGramModleWriter(lModel, new File(modelFile));
		else
			throw new IllegalArgumentException();
			
		modelWriter.persist();
	}
	
	/**
	 * 根据平滑方法选择模型训练器
	 * @param nGramCounter	n元计数器
	 * @param order			最大n元长度
	 * @param smooth		平滑方法
	 * @return				模型训练器
	 * @throws IOException
	 */
	private static AbstractLanguageModelTrainer selectSmoothingModel(NGramCounter nGramCounter, int order, String smooth) throws IOException {
		AbstractLanguageModelTrainer trainer = null;
		switch (smooth.toLowerCase()) {
		case "ml":
			trainer = new MLLanguageModelTrainer(nGramCounter, order);
			break;
		case "laplace":
			trainer = new LaplaceLanguageModelTrainer(nGramCounter, order);
			break;
		case "katz":
			trainer = new KatzLanguageModelTrainer(nGramCounter, order);
			break;
		case "interpolate":
			trainer = new InterpolationLanguageModelTrainer(nGramCounter, order);
			break;
		case "kn":
			trainer = new KneserNeyLanguageModelTrainer(nGramCounter, order);
			break;
		default:
			System.err.println("错误的平滑方法:"+smooth+"\nml 最大似然估计\nlaplace 加一平滑\ngt 古德图灵\n"
					+ "interpolate 插值平滑\nkatz 回退平滑\nkn Kneser-Ney平滑");
			System.exit(0);
		}
		
		return trainer;
	}
	
	/**
	 * <li>指定语料文件或语料和词典，生成n元模型文件。<br>
	 * 示例:NGramModel corpus.txt utf-8 3 Y gt trigram.lm text<br>
	 * 示例:NGramModel corpus.txt utf-8 3 N gt trigram.lm binary dict dict.txt gbk<br>
	 * @param args [0]-训练语料路径, [1]-语料编码, [2]-n元长度, [3]-是否按句处理语料(Y/N), [4]-平滑方法,
	 * 			   [5]-模型文件的路径, [6]-模型文件的类型(文本文件text/二进制文件binary/序列化文件object)
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {		
		int len = args.length;
		if(7 != len) {
			System.err.println("错误的参数个数："+len+
					"\n示例:NGramModel 语料文件路径  语料文件编码  n元长度  是否按句处理Y/N  平滑方法  模型文件输出路径  模型文件类型(text/binary/object)");
			System.exit(0);
		}
		
		int order = Integer.parseInt(args[2]);
		String sentence = args[3].toLowerCase();
		String type = args[6].toLowerCase();
		
		if(order < 1) {
			System.err.println("错误的n元长度："+args[2]+"\nn元长度为大于0的整数:");
		}else if(!(sentence.equals("y") || sentence.equals("n"))) {
			System.err.println("错误的语料处理方式："+args[3]+"\nY/y-以行为单位区分句子，N/n不区分句子");
		}else if(!(type.equals("text") || type.equals("binary") || type.equals("object"))) {
			System.err.println("错误的输出文件类型："+args[6]+"\ntext-文本文件, binary-二进制文件, object-序列化文件");
		}else {
			if(sentence.equals("y")) {
				GramSentenceStream stream = new StringGramSentenceStream(args[0], args[1]);
				getModelFile(stream, order, args[4], args[5], type);
			}else {
				GramStream stream = new StringGramStream(args[0], args[1]);
				getModelFile(stream, order, args[4], args[5], type);
			}
		}		
	}
}
