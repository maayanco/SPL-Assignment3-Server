package bgu.spl.server.shared;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import bgu.spl.container.ContainerSingleton;
import bgu.spl.container.Player;
import bgu.spl.server.passive.Message;
import bgu.spl.server.threadperclient.AsyncServerProtocol;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class TBGPProtocol implements AsyncServerProtocol<String>{
	
	private static Player player;
	private static ContainerSingleton container = ContainerSingleton.getInstance();
	private String TERMINATION_MESSAGE = "quit"; 
	boolean shouldClose=false;
	
	
	public TBGPProtocol(Player player, ContainerSingleton container){
		this.player=player;
		this.container=container;
	}

	/**
	 * Receives a msg and determines if this message is a termination message 
	 * meaning sent by the client to indicate a termination of the client
	 */
	public boolean isEnd(String msg) {
		return msg.equals(TERMINATION_MESSAGE);
	}

	public void processMessage(String msg, ProtocolCallback<String> callback) {
		if(!msg.getClass().equals(String.class)){ //check that message is a string..
			///then don't do anything!! return like.. sysmessage that this is bad or something!
		}
		
		Message message = new Message(msg);
		if(!message.isValid()){
			try {
				callback.sendMessage("SYSMSG "+msg+" Rejected");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			container.processMessage(message, callback, player);
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
