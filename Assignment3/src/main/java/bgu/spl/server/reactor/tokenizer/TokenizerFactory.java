package bgu.spl.server.reactor.tokenizer;

public interface TokenizerFactory<T> {
	MessageTokenizer<T> create();
}
