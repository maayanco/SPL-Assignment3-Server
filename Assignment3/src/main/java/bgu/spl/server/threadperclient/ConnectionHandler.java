package bgu.spl.server.threadperclient;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.CharacterCodingException;

import bgu.spl.server.passive.StringMessage;
import bgu.spl.server.protocol.AsyncServerProtocol;
import bgu.spl.server.tokenizer.MessageTokenizer;

public class ConnectionHandler implements Runnable {
	Socket _socket;
	Encoder _encoder;
	/*Tokenizer _tokenizer;*/
	MessageTokenizer<StringMessage> _tokenizer;
	AsyncServerProtocol<StringMessage> _protocol;

	public ConnectionHandler(Encoder encoder, MessageTokenizer tockenizer, AsyncServerProtocol protocol, Socket socket) {
		this._socket = socket;
		this._tokenizer = tockenizer;
		this._protocol = protocol;
		this._encoder = encoder;
		System.out.println("Received new CLient");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		while (!_socket.isClosed() && !_protocol.shouldClose()) {

			if (_tokenizer.hasMessage()) {
				/*System.out.println("Listening");
			_protocol.connectionTerminated();*/
				/*} else {*/
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
