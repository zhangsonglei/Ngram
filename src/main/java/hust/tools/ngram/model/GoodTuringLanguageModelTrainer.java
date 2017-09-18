package hust.tools.ngram.model;

import java.io.IOException;
import java.util.Iterator;
import hust.tools.ngram.datastructure.ARPAEntry;
import hust.tools.ngram.datastructure.NGram;
import hust.tools.ngram.datastructure.PseudoWord;
import hust.tools.ngram.utils.GoodTuringCounts;
import hust.tools.ngram.utils.GramSentenceStream;
import hust.tools.ngram.utils.GramStream;

public class GoodTuringLanguageModelTrainer extends AbstractLanguageModelTrainer {
	/**
	 * n元出现频率的频率（出现r次的n元的个数）
	 */
	private GoodTuringCounts goodTuringCounts;
	
	public GoodTuringLanguageModelTrainer(GramStream gramStream, int  n) throws IOException {
		super(gramStream, n);
	}
	
	public GoodTuringLanguageModelTrainer(GramSentenceStream gramSentenceStream, int  n) throws IOException {
		super(gramSentenceStream, n);
	}
	
	public GoodTuringLanguageModelTrainer(NGramCounter nGramCounter, int n) {
		super(nGramCounter, n);
	}

	/**
	 * GoodTuring平滑中，模型参数含义：
	 * log_prob	  nGram    log_bo
	 * n元组概率的对数    n元组               无
	 */
	@Override
	public NGramLanguageModel trainModel() {
		goodTuringCounts = new GoodTuringCounts(nGramCounter.getNGramCountMap(), n);

		Iterator<NGram> iterator = nGramCounter.iterator();
		while(iterator.hasNext()) {
			NGram nGram = iterator.next();
			if(nGram.length() > 2 && nGramCounter.getNGramCount(nGram) < 2)//高阶(n > 2)n元组计数小于2 的忽略
				continue;
			
			double bo = 0.0;
			if(nGram.length() < n)
				bo = nGramCounter.getNGramCount(nGram);
			
			double prob = calcGoodTuringNGramProbability(nGram, nGramCounter, goodTuringCounts);
			ARPAEntry entry = new ARPAEntry(Math.log10(prob), bo);
			
			nGramLogProbability.put(nGram, entry);
		}
		
		double N1 = 0.0;
		double N0 = 0.0;
		for(int i = 1; i < n; i++) {
			N1 += goodTuringCounts.getNr(1, i);
			N0 += Math.pow(vocabulary.size(), i);
		}
		N0 -= nGramCounter.size();
		
		double prob = N1 / nGramCounter.getTotalNGramCount() / N0;
		ARPAEntry entry = new ARPAEntry(Math.log10(prob), 0.0);
		nGramLogProbability.put(PseudoWord.oovNGram, entry);
		
		nGramTypeCounts = new int[n];
		nGramTypes = new NGram[n][];
		for(int i = 0; i < n; i++) {
			nGramTypes[i] = statTypeAndCount(nGramLogProbability, i + 1);
			nGramTypeCounts[i] = nGramTypes[i].length;
		}
		
		return new NGramLanguageModel(nGramLogProbability, n, "gt", vocabulary);
	}
}
