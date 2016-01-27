package bgu.spl.server.encoder;

/**
 * Represents an Encoder 
 */
import java.nio.charset.Charset;

public class BasicEncoder implements Encoder<String> {
	
	private Charset _charset;

	/**
	 * Constructor - receives the charset to be used
	 * @param charsetDesc
	 */
	public BasicEncoder(String charsetDesc) {
		_charset = Charset.forName(charsetDesc);
	}

	/**
	 * returns a byte[] array representation of the provided string
	 */
	@Override
	public byte[] toBytes(String str) {
		return str.getBytes(_charset);
	}

	/**
	 * returns a String representation of the provided byte array
	 */
	@Override
	public String fromBytes(byte[] buf) {
		return new String(buf, 0, buf.length, _charset);
	}

	/**
	 * returns the current charset
	 */
	@Override
	public Charset getCharset() {
		return _charset;
	}
}
