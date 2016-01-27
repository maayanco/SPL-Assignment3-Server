package bgu.spl.server.reactor;

import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;

import bgu.spl.server.protocol.ServerProtocolFactory;
import bgu.spl.server.tokenizer.TokenizerFactory;

/**
 * a simple data structure that hold information about the reactor, including
 * getter methods
 */
public class ReactorData<T> {

	private final ExecutorService _executor;
	private final Selector _selector;
	private final ServerProtocolFactory<T> _protocolMaker;
	private final TokenizerFactory<T> _tokenizerMaker;

	/**
	 * @return the executor service
	 */
	public ExecutorService getExecutor() {
		return _executor;
	}

	/**
	 * @return the selector object
	 */
	public Selector getSelector() {
		return _selector;
	}

	/**
	 * @param _executor - The provided execturo service
	 * @param _selector - the provided selector object
	 * @param protocol - the protocol factory object
	 * @param tokenizer - the tokenizer factory object
	 */
	public ReactorData(ExecutorService _executor, Selector _selector, ServerProtocolFactory<T> protocol,
			TokenizerFactory<T> tokenizer) {
		this._executor = _executor;
		this._selector = _selector;
		this._protocolMaker = protocol;
		this._tokenizerMaker = tokenizer;
	}

	/**
	 * @return the procotol factory
	 */
	public ServerProtocolFactory<T> getProtocolMaker() {
		return _protocolMaker;
	}

	/**
	 * @return the tokenizer factory
	 */
	public TokenizerFactory<T> getTokenizerMaker() {
		return _tokenizerMaker;
	}

}
