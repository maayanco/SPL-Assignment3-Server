package bgu.spl.server.threadperclient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import bgu.spl.container.ContainerSingleton;
import bgu.spl.container.Player;
import bgu.spl.container.Room;
import bgu.spl.server.shared.TBGPProtocol;

public class Server {
	private LinkedList<Room> gameRoomsList = new LinkedList<Room>();

	public static void main(String[] args){
		
		Encoder encoder = new BasicEncoder("UTF-8");
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(9090);

			while(true){
				System.out.println("Listening");
				Socket socket = serverSocket.accept();
				Tokenizer tockenizer = new MessageTokenizer(new InputStreamReader(socket.getInputStream()));
				AsyncServerProtocol protocol = new TBGPProtocol(new Player(""), ContainerSingleton.getInstance());
				Runnable connectionHandler = new ConnectionHandler(encoder,tockenizer,protocol,socket);
				new Thread(connectionHandler).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}