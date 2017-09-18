package hust.tools.ngram.datastructure;

import hust.tools.ngram.utils.StringGram;

public class PseudoWord {
	public static final Gram oov = new StringGram("<unk>");
	public static final NGram oovNGram = new NGram(new Gram[]{new StringGram("<unk>")});
	public static final Gram sentStart = new StringGram("<s>");
	public static final Gram sentEnd = new StringGram("</s>");
}
