package bgu.spl.server.tokenizer;

public interface TokenizerFactory<T> {
	MessageTokenizer<T> create();
}
