package bgu.spl.container;

import bgu.spl.server.passive.Message;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class Player {
	
	private String playerName;
	private Room currentRoom;
	
	public Player(String playerName){
		this.playerName=playerName;
	}
	
	public void setPlayerName(String name){
		this.playerName=name;
	}
	
	public String getPlayerName(){
		return this.playerName;
	}
	
	public void setRoom(Room room){
		currentRoom=room;
	}
	
	public Room getRoom(){
		return currentRoom;
	}
	
}
