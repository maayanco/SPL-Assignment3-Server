package bgu.spl.server.tokenizer;

/**
 * Interface representing a factory which creates MessageTokenizer Objects
 */
public interface TokenizerFactory<T> {
	MessageTokenizer<T> create();
}
