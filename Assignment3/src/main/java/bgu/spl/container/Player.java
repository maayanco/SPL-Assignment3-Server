package bgu.spl.container;

import java.util.LinkedList;

import bgu.spl.server.passive.ClientCommand;
import bgu.spl.server.passive.Message;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class Player {
	
	private String playerName="";
	private Room currentRoom;
	private ProtocolCallback callback; 
	private LinkedList<ClientCommand> acceptedCommands = new LinkedList<ClientCommand>();
	
	public Player(){
		acceptedCommands.add(ClientCommand.NICK);
		acceptedCommands.add(ClientCommand.QUIT);
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
	
	public boolean isCommandAccepted(ClientCommand command){
		return acceptedCommands.contains(command);
	}
	
	public void setAcceptedCommands(LinkedList<ClientCommand> newCommands){
		this.acceptedCommands=newCommands;
	}
	
	
}
