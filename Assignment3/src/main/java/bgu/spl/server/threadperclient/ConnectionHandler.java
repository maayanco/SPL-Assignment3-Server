package bgu.spl.server.threadperclient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.spl.logic.GameFactory;
import bgu.spl.passive.StringMessage;
import bgu.spl.server.encoder.Encoder;
import bgu.spl.server.protocol.AsyncServerProtocol;
import bgu.spl.server.tokenizer.MessageTokenizer;
import java.util.logging.Logger;

/**
 * Connection Handler class holds the tokenizer, protocol, socket and encoder
 * it is in charge of reading from the socket and adding the data to the tokenizer,
 * then extracting a new complete message using the tokenizer and sending it to the protocol to be processed
 * @author maacoh
 *
 */
public class ConnectionHandler implements Runnable {
	private Socket _socket;
	private Encoder _encoder;
	private MessageTokenizer<StringMessage> _tokenizer;
	private AsyncServerProtocol<StringMessage> _protocol;
	private static final char NEW_LINE_DELIMITER='\n';
	private static final char CARRIAGE_RETURN_DELIMITER='\r';
	private static final Logger DATA_LOGGER = Logger.getLogger("ThreadPerClient.ConnectionHandler");
	
	/**
	 * 
	 * @param encoder - class in charge of decoding provided data into and from bytes
	 * @param tockenizer - class in charge of returning new complete messages from the provided bytes
	 * @param protocol - class in charge of processing a provided message and responding appropriately 
	 * @param socket - deliver incoming data packets to the appropriate application process or thread.
	 */
	public ConnectionHandler(Encoder encoder, MessageTokenizer tockenizer, AsyncServerProtocol protocol, Socket socket) {
		this._socket = socket;
		this._tokenizer = tockenizer;
		this._protocol = protocol;
		this._encoder = encoder;
		DATA_LOGGER.log(Level.INFO, "Received new CLient");
	}
	
	/**
	 * Reads data from the socket and adds to the tokenizer
	 */
	public void addBytesToTokenizer(){
		try {
			int c;
			StringBuffer sb=new StringBuffer();
			while((c=_socket.getInputStream().read())!=-1){
				if((char)c==NEW_LINE_DELIMITER || (char)c==CARRIAGE_RETURN_DELIMITER){
					break;
				}
				sb.append((char)c);
			}
			
			if(sb.length()!=0){
				sb.append(NEW_LINE_DELIMITER);
				String str = sb.toString();
				byte[] bytes = _encoder.toBytes(str);
				ByteBuffer buf = ByteBuffer.wrap(bytes);
				_tokenizer.addBytes(buf);
			}
			
		} catch (IOException e) {
			DATA_LOGGER.log(Level.WARNING, "Exception has occured while reading from socket");
		}
	}

	/**
	 * Runs as long as the soket and protocol are active
	 * Receives new complete messages from the tokenizer and sends them to the protocol to be processed.
	 * provides a protocolCallback which when triggered with a message adds the message to the current client's outputStream
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		while (!_socket.isClosed() && !_protocol.shouldClose()) {
			addBytesToTokenizer();
			if (_tokenizer.hasMessage()) {
				StringMessage msg = _tokenizer.nextMessage();
				_protocol.processMessage(msg, response -> {
					if (response != null) {
						try {
							ByteBuffer bytes = _tokenizer.getBytesForMessage(response);
							WritableByteChannel channel = Channels.newChannel(_socket.getOutputStream());   
							channel.write(_tokenizer.getBytesForMessage(response));
						} catch (CharacterCodingException e) {
							DATA_LOGGER.log(Level.WARNING, "Exception while trying to send message to client");
						}

					}
				});

			}
		}

		//Closing the socket
		try {
			_socket.close();
		} catch (IOException e) {
			DATA_LOGGER.log(Level.WARNING, "Exception has occured while trying to close socket");
		}


	}
}
