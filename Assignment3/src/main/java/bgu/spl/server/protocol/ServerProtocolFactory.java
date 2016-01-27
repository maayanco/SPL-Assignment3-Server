package bgu.spl.server.protocol;

/**
 * Factory interface with the ability to create new Protocol objects
 */
public interface ServerProtocolFactory<T> {
	AsyncServerProtocol<T> create();
}
