package bgu.spl.server.threadperclient;

import java.nio.charset.Charset;

public class BasicEncoder implements Encoder {
	private Charset _charset;

	public BasicEncoder(String charsetDesc) {
		_charset = Charset.forName(charsetDesc);
	}

	@Override
	public byte[] toBytes(String str) {
		return str.getBytes(_charset);
	}

	@Override
	public String fromBytes(byte[] buf) {
		return new String(buf, 0, buf.length, _charset);
	}

	@Override
	public Charset getCharset() {
		return _charset;
	}
}
