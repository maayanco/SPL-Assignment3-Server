package bgu.spl.server.threadperclient;

import java.io.IOException;
import java.net.Socket;

import bgu.spl.server.shared.AsyncServerProtocol;
import bgu.spl.server.shared.TBGPProtocol;

public class ConnectionHandler implements Runnable{
	Socket _socket;
	Encoder _encoder;
	Tokenizer _tokenizer;
	AsyncServerProtocol _protocol;
	
	public ConnectionHandler(Encoder encoder,Tokenizer tockenizer,AsyncServerProtocol protocol,Socket socket){
		this._socket=socket;
		this._tokenizer=tockenizer;
		this._protocol=protocol;
		this._encoder=encoder;
		System.out.println("Received new CLient");
	}
	
	
	@SuppressWarnings("unchecked")
	public void run() {
		while(!_socket.isClosed() && !_protocol.shouldClose()){
			System.out.println("Listening");
			try{
				if(!_tokenizer.isAlive()){
					_protocol.connectionTerminated();
				}
				else{
					String msg = _tokenizer.nextToken();
					_protocol.processMessage(msg, ans -> {
						if(ans!=null){
							byte[] buf = _encoder.toBytes((String)ans+'\n');
							_socket.getOutputStream().write(buf);
						}
					});
					
				}
			} catch(IOException e){
				System.out.println("excceeeption");
				_protocol.connectionTerminated();
				break;
			}
			
		}
		
		
		try {
			_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
