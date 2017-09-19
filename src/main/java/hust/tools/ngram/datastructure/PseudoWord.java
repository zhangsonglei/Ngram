package hust.tools.ngram.datastructure;

import hust.tools.ngram.utils.StringGram;

public class PseudoWord {
	public static final Gram oov = new StringGram("<unk>");
	public static final NGram oovNGram = new NGram(new Gram[]{oov});
	public static final Gram Start = new StringGram("<s>");
	public static final NGram sentStart = new NGram(new Gram[]{Start});
	public static final Gram End = new StringGram("</s>");
	public static final NGram sentEnd = new NGram(new Gram[]{End});
}
