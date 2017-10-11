package hust.tools.ngram.app;

import java.io.File;
import java.io.IOException;

import hust.tools.ngram.io.BinaryFileNGramCountWriter;
import hust.tools.ngram.io.ObjectFileNGramCountWriter;
import hust.tools.ngram.io.TextFileNGramCountWriter;
import hust.tools.ngram.model.NGramCountWriter;
import hust.tools.ngram.model.NGramCounter;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;
import hust.tools.ngram.utils.StringGramSentenceStream;
import hust.tools.ngram.utils.StringGramStream;

/**
 *<ul>
 *<li>Description: 从训练语料统计n元计数
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年9月1日
 *</ul>
 */
public class NGramCount {

	/**
	 * 根据训练语料，生成n元计数文件
	 * @param stream	训练语料元流
	 * @param encoding	训练语料文件编码
	 * @param order		n元最大长度
	 * @param countFile	n元计数文件的路径
	 * @param type		n元计数文件的类型
	 * @throws IOException
	 */
	public static void getCounterFile(GramStream stream, int order, String countFile, String type) throws IOException {		
		NGramCounter counter = new NGramCounter(stream, order);
		
		System.out.println(counter.iterator());
		writeCounter(counter, countFile, type);
	}
	
	/**
	 * 根据训练语料，生成n元计数文件
	 * @param stream	训练语料句子流
	 * @param encoding	训练语料文件编码
	 * @param order		n元最大长度
	 * @param countFile	n元计数文件的路径
	 * @param type		n元计数文件的类型
	 * @throws IOException
	 */
	public static void getCounterFile(GramSentenceStream stream, int order, String countFile, String type) throws IOException {
		NGramCounter counter = new NGramCounter(stream, order);
		writeCounter(counter, countFile, type);
	}
	
	/**
	 * 将n元计数文件写入文件
	 * @param counter	n元计数
	 * @param countFile	写入文件路径
	 * @param type		写入文件类型
	 * @throws IOException
	 */
	private static void writeCounter(NGramCounter counter, String countFile, String type) throws IOException {
		NGramCountWriter countWriter;			
		
		/**
		 * text-文本文件
		 * binary-二进制文件
		 * object-序列化文件
		 */
		if(type.equals("text"))
			countWriter = new TextFileNGramCountWriter(counter, new File(countFile));
		else if(type.equals("binary"))
			countWriter = new BinaryFileNGramCountWriter(counter, new File(countFile));
		else if(type.equals("object"))
			countWriter = new ObjectFileNGramCountWriter(counter, new File(countFile));
		else
			throw new IllegalArgumentException();
		
		countWriter.persist();
	}
	
	/**
	 * <li>指定语料文件，生成n元计数文件。
	 * <li>示例:NGramCount corpus.txt utf-8 3 Y trigram.count text
	 * @param args [0]-训练语料路径, [1]-语料编码, [2]-n元长度, [3]-是否按句处理语料(Y/N),
	 * 			   [4]-计数文件的路径, [5]-计数文件的类型(文本文件text/二进制文件binary/序列化文件object)
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {		
		if(args.length != 6) {
			System.err.println("错误的参数个数："+args.length+
					"\n示例:NGramCount 语料文件路径  语料文件编码  n元长度  是否按句处理Y/N  计数文件输出路径  计数文件类型(text/binary/object)");
			System.exit(0);
		}
		
		int order = Integer.parseInt(args[2]);
		String sentence = args[3].toLowerCase();
		String type = args[5].toLowerCase();
		
		if(order < 1) {
			System.err.println("错误的n元长度："+args[2]+"\nn元长度为大于0的整数");
		}else if(!(sentence.equals("y") || sentence.equals("n"))) {
			System.err.println("错误的语料处理方式："+args[3]+"\nY/y-以行为单位区分句子，N/n不区分句子");
		}else if(!(type.equals("text") || type.equals("binary") || type.equals("object"))) {
			System.err.println("错误的输出文件类型："+args[5]+"\ntext-文本文件, binary-二进制文件, object-序列化文件");
		}else {
			if(sentence.equals("y")) {
				GramSentenceStream stream = new StringGramSentenceStream(args[0], args[1]);
				getCounterFile(stream, order, args[4], type);
			}else {
				GramStream stream = new StringGramStream(args[0], args[1]);
				getCounterFile(stream, order, args[4], type);
			}
		}
	}
}
