package bgu.spl.server.reactor;

public interface TokenizerFactory<T> {
   MessageTokenizer<T> create();
}
