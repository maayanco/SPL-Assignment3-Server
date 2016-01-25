package bgu.spl.server.threadperclient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;

import bgu.spl.container.GameFactory;
import bgu.spl.server.passive.StringMessage;
import bgu.spl.server.protocol.AsyncServerProtocol;
import bgu.spl.server.tokenizer.MessageTokenizer;

public class ConnectionHandler implements Runnable {
	Socket _socket;
	Encoder _encoder;
	MessageTokenizer<StringMessage> _tokenizer;
	AsyncServerProtocol<StringMessage> _protocol;
	private static final char NEW_LINE_DELIMITER='\n';
	private static final char CARRIAGE_RETURN_DELIMITER='\r';
	
	public ConnectionHandler(Encoder encoder, MessageTokenizer tockenizer, AsyncServerProtocol protocol, Socket socket) {
		this._socket = socket;
		this._tokenizer = tockenizer;
		this._protocol = protocol;
		this._encoder = encoder;
		System.out.println("Received new CLient");
	}
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
							e.printStackTrace();
						}

					}
				});

			}
		}

		//Close the socket - after the run loop is finished and before terminating this thread
		try {
			_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
