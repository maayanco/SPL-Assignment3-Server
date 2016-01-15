package bgu.spl.server.threadperclient;

import bgu.spl.server.shared.AsyncServerProtocol;

public interface ServerProtocolFactory<T> {
	   AsyncServerProtocol<T> create();
}

