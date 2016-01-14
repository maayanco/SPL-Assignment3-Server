package bgu.spl.server.threadperclient;

public interface ServerProtocolFactory<T> {
	   AsyncServerProtocol<T> create();
}

