package bgu.spl.container;

import bgu.spl.server.passive.Message;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class Player {
	
	private String playerName="";
	private Room currentRoom;
	private ProtocolCallback callback; 
	
	public Player(){
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Room getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(Room currentRoom) {
		this.currentRoom = currentRoom;
	}

	public ProtocolCallback getCallback() {
		return callback;
	}

	public void setCallback(ProtocolCallback callback) { 
		if(this.callback==null){
			this.callback = callback;
		}
	}
	
	
}
