package bgu.spl.server.reactor;

import bgu.spl.server.shared.AsyncServerProtocol;

public interface ServerProtocolFactory<T> {
	AsyncServerProtocol<T> create();
}
