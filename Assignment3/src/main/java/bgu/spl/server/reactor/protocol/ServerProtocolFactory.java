package bgu.spl.server.reactor.protocol;

public interface ServerProtocolFactory<T> {
	AsyncServerProtocol<T> create();
}
