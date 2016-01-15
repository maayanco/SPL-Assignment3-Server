package bgu.spl.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import bgu.spl.server.passive.ClientCommand;
import bgu.spl.server.passive.Message;
import bgu.spl.server.passive.Result;
import bgu.spl.server.passive.ServerCommand;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class Room {
	
	private static final Logger Log = Logger.getLogger(Bluffer.class.getName());
	private String roomName;
	//private GameState gameState;
	private Game game;
	private LinkedList<Player> playersList = new LinkedList<Player>();
	private LinkedList<String> supportedGames = new LinkedList<String>();
	
	
	public Room(String roomName){
		this.roomName=roomName;
	}
	
	public void addPlayer(Player player){
		playersList.add(player); 
	}
	
	public LinkedList<Player> getPlayers(){
		return new LinkedList<Player>(playersList);
	}
	
	public String getRoomName(){
		return roomName;
	}
	
	public Game getGame(){
		return game;
	}
	
	private void triggerCallback(ProtocolCallback callback, String messageToBeSent){
		try {
			callback.sendMessage(messageToBeSent);
		} catch (IOException e) {
			System.out.println("Error occured - couldn't invoke callbak");
		}
	}
	
	//Big problem!!! I Specificy BLUFFER HERREEE!
	public void startNewGame(){
		game = new Bluffer(playersList);
	}
	
	public void triggerAllCallbacks(String msg){
		for(Player player : playersList){
			triggerCallback(player.getCallback(), msg);
		}
	}
	
	public void handleMSG(Player currentPlayer, Message message){
		String messageToBeSent="";
		for(int i=0; i<message.getParameterLength(); i++){
			messageToBeSent+=" "+message.getParameter(i);
		}

		for(Player player : playersList){
			if(!player.equals(currentPlayer)){
				triggerCallback(player.getCallback(), ServerCommand.USRMSG+" "+messageToBeSent);
			}
		}
	}
	
}

