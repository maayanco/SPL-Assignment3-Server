package bgu.spl.server.threadperclient;

import java.io.IOException;

import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Queue;

import bgu.spl.logic.Bluffer;
import bgu.spl.logic.Game;
import bgu.spl.logic.GameFactory;
import bgu.spl.logic.Player;
import bgu.spl.logic.Room;
import bgu.spl.passive.StringMessage;
import bgu.spl.server.encoder.BasicEncoder;
import bgu.spl.server.encoder.Encoder;
import bgu.spl.server.encoder.EncoderFactory;
import bgu.spl.server.protocol.AsyncServerProtocol;
import bgu.spl.server.protocol.ServerProtocolFactory;
import bgu.spl.server.protocol.TBGPProtocol;
import bgu.spl.server.reactor.Reactor;
import bgu.spl.server.tokenizer.FixedSeparatorMessageTokenizer;
import bgu.spl.server.tokenizer.MessageTokenizer;
import bgu.spl.server.tokenizer.TokenizerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ThreadPerClient server, contains configurations for tockenizer, protocol and encoder
 * Creates a ConnectionHandler and thread containing it, for each new client it accept's.
 */
public class Server {
	private int _port;
	private ServerProtocolFactory<StringMessage> _protocolFactory;
	private TokenizerFactory<StringMessage> _tokenizerFactory;
	private EncoderFactory<String> _encoderFactory;
	private static final Logger DATA_LOGGER = Logger.getLogger("ThreadPerClient.Server");
	private static final String NEW_LINE_DELIMITER="\n";
	
	/**
	 * Constructor - initializes a new server object
	 * @param port - the number of the port on which the server will listen
	 * @param protocol - the protocol factory object
	 * @param tokenizer - the tokenizer factory
	 */
	public Server(int port, ServerProtocolFactory protocol,TokenizerFactory tokenizer, EncoderFactory encoder){
		_port = port;
		_protocolFactory = protocol;
		_tokenizerFactory = tokenizer;
		_encoderFactory = encoder;
	}
	
	/**
	 * Implements the serverProtocolFactory, TokenizerFactory and EncoderFactory
	 * Returns the parameters to the server constructor
	 * @param port - the port on which the server will listen
	 * @return new server object with the initialized objects
	 */
	public static Server startServer(int port){
		ServerProtocolFactory<StringMessage> protocolMaker = new ServerProtocolFactory<StringMessage>(){
			public AsyncServerProtocol<StringMessage> create(){
				return new TBGPProtocol();
			}
		};

		final Charset charset = Charset.forName("UTF-8");
		TokenizerFactory<StringMessage> tokenizerMaker = new TokenizerFactory<StringMessage>() {
			public MessageTokenizer<StringMessage> create() {
				return new FixedSeparatorMessageTokenizer(NEW_LINE_DELIMITER, charset);
			}
		};
		
		EncoderFactory<String> encoderMaker = new EncoderFactory<String>(){
			public Encoder<String> create(){
				return new BasicEncoder("UTF-8");
			}
		};
		
		Server server = new Server(port, protocolMaker, tokenizerMaker, encoderMaker);
		return server;
	}
	
	/**
	 * Receives port from args
	 * Accepts new clients, creates ConnectionHandler for new Clients
	 * and runs a new thread for the client with the new ConnectionHandler 
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {
		
		int port;
		
		try{
			port=Integer.parseInt(args[0]);
		}
		catch(NumberFormatException e){
			DATA_LOGGER.log(Level.INFO, "please provide a valid port number");
			return;
		}
		
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				DATA_LOGGER.log(Level.INFO, "Listening..");
				Socket socket = serverSocket.accept();
				Server server = startServer(port);
				Runnable connectionHandler = new ConnectionHandler(server._encoderFactory.create(), server._tokenizerFactory.create(), server._protocolFactory.create(), socket);
				new Thread(connectionHandler).start();
			}
		} catch (IOException e) {
			DATA_LOGGER.log(Level.WARNING, "Exception has occured while attempting to accept new client");
		}
	}
	
	

}
