package bgu.spl.server.threadperclient;

import java.io.IOException;
import java.net.Socket;

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
			//System.out.println("inside connection handler");
			try{
				//System.out.println("hi before catch");
				if(!_tokenizer.isAlive()){
					//System.out.println("tockenizer - i am not alive..");
					_protocol.connectionTerminated();
				}
				else{
					String msg = _tokenizer.nextToken();
					//System.out.println("i am right before processing message");
					_protocol.processMessage(msg, ans -> {
						if(ans!=null){
							//System.out.println("i am inside the callback");
							byte[] buf = _encoder.toBytes((String)ans+'\n');
							_socket.getOutputStream().write(buf);
							//_socket.getOutputStream().flush(); //danger!!
							//_socket.getOutputStream().write(buf,buf.length);
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
