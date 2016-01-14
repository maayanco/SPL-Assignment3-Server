package bgu.spl.server.reactor;

public interface ServerProtocolFactory<T> {
   AsyncServerProtocol<T> create();
}
