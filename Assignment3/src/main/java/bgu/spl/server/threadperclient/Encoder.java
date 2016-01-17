package bgu.spl.server.threadperclient;

import java.nio.charset.Charset;

public interface Encoder {
	byte[] toBytes(String str);

	String fromBytes(byte[] buf);

	Charset getCharset();
}
