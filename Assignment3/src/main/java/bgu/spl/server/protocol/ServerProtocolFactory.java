package bgu.spl.server.protocol;

public interface ServerProtocolFactory<T> {
	AsyncServerProtocol<T> create();
}
