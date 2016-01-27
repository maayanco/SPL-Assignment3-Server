package bgu.spl.server.encoder;

/**
 * Factory interface which creates new Encoder objects
 */
public interface EncoderFactory<T> {
	Encoder<T> create();
}
