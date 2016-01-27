package bgu.spl.server.encoder;

import java.nio.charset.Charset;

/**
 * Represnts the Encoder interface
 * containing the fromBytes, toBytes and getCharset methods
 */
public interface Encoder<T> {
	byte[] toBytes(T str);

	T fromBytes(byte[] buf);

	Charset getCharset();
}
