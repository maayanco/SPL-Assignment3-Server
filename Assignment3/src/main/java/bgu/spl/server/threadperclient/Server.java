package bgu.spl.server.threadperclient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

import bgu.spl.container.Bluffer;
import bgu.spl.container.Game;
import bgu.spl.container.GameFactory;
import bgu.spl.container.GameFactory;
import bgu.spl.container.Player;
import bgu.spl.container.Room;
import bgu.spl.server.passive.StringMessage;
import bgu.spl.server.protocol.AsyncServerProtocol;
import bgu.spl.server.protocol.ServerProtocolFactory;
import bgu.spl.server.protocol.TBGPProtocol;
import bgu.spl.server.reactor.reactor.Reactor;
import bgu.spl.server.tokenizer.FixedSeparatorMessageTokenizer;
import bgu.spl.server.tokenizer.MessageTokenizer;
import bgu.spl.server.tokenizer.TokenizerFactory;

public class Server {
	
	private LinkedList<Room> gameRoomsList = new LinkedList<Room>();
	private int _port;
	private ServerProtocolFactory _protocolFactory;
	private TokenizerFactory _tokenizerFactory;
	
	public Server(int port, ServerProtocolFactory protocol,TokenizerFactory tokenizer){
		_port = port;
		_protocolFactory = protocol;
		_tokenizerFactory = tokenizer;
	}
	
	public static void main(String[] args) {
		
		int port;
		
		try{
			port=Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e){
			System.out.println("please provide a valid port number");
			return;
		}
		
		Encoder encoder = new BasicEncoder("UTF-8");
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(9091);
			int i = 0;
			while (true) {
				i++;
				System.out.println("Listening..");
				Socket socket = serverSocket.accept();

				Server server = startServer(port);
				Runnable connectionHandler = new ConnectionHandler(encoder, server._tokenizerFactory.create(), server._protocolFactory.create(), socket);
				new Thread(connectionHandler).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Server startServer(int port){
		ServerProtocolFactory<StringMessage> protocolMaker = new ServerProtocolFactory<StringMessage>(){
			public AsyncServerProtocol<StringMessage> create(){
				return new TBGPProtocol();
			}
		};

		final Charset charset = Charset.forName("UTF-8");
		TokenizerFactory<StringMessage> tokenizerMaker = new TokenizerFactory<StringMessage>() {
			public MessageTokenizer<StringMessage> create() {
				return new FixedSeparatorMessageTokenizer("\n", charset);
			}
		};
		
		Server server = new Server(port, protocolMaker, tokenizerMaker);
		return server;
	}

}
