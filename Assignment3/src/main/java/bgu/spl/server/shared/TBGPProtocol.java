package bgu.spl.server.shared;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import bgu.spl.container.ContainerSingleton;
import bgu.spl.container.Player;
import bgu.spl.server.passive.ClientCommand;
import bgu.spl.server.passive.Message;
import bgu.spl.server.passive.Result;
import bgu.spl.server.passive.ServerCommand;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class TBGPProtocol implements AsyncServerProtocol<String>{
	
	private static Player player = new Player();
	private static ContainerSingleton container = ContainerSingleton.getInstance();
	private String TERMINATION_MESSAGE = "quit"; 
	private boolean shouldClose=false;
	private ClientCommand expectedCommand = ClientCommand.NICK; 
	
	public TBGPProtocol(){}

	/**
	 * Receives a msg and determines if this message is a termination message 
	 * meaning sent by the client to indicate a termination of the client
	 */
	public boolean isEnd(String msg) {
		return msg.equals(TERMINATION_MESSAGE);
	}
	
	private void triggerCallback(String msg, ProtocolCallback<String> callback){
		try {
			callback.sendMessage(msg);
		} catch (IOException e) {
			System.out.println("An error has occured while invoking ProtocolCallback");
		}
	}
	public void processMessage(String msg, ProtocolCallback<String> callback) {
		//If msg is not a String - return invalid
		if(!msg.getClass().equals(String.class)){ 
			triggerCallback(ServerCommand.SYSMSG+" "+Result.UNIDENTIFIED, callback);
		}
		else{
			//Set the player's callback 
			player.setCallback(callback);
			
			//Convert the msg string to a Message type
			Message message = new Message(msg);
			
			//If m
			if(!message.isValid()){
				triggerCallback(ServerCommand.SYSMSG+" "+Result.UNIDENTIFIED, callback);
			}
			else{
				//Message is valid - we forward it to the container
				container.processMessage(message, player);
			}
		}
		
	}


	public boolean shouldClose() {
		// TODO Auto-generated method stub
		return false;
	}

	public void connectionTerminated() {
		// TODO Auto-generated method stub
		
	}

	
}
