package bgu.spl.server.threadperclient;

import java.io.IOException;

public interface Tokenizer {
	String nextToken() throws IOException;

	boolean isAlive();
}
