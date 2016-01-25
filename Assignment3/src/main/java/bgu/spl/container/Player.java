package bgu.spl.container;

import java.io.IOException;
import java.util.LinkedList;

import bgu.spl.server.passive.Command;
import bgu.spl.server.passive.StringMessage;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class Player {

	private String playerName = "";
	private Room currentRoom;
	private ProtocolCallback callback;
	private LinkedList<Command> acceptedCommands = new LinkedList<Command>();

	public Player() {
		acceptedCommands.add(Command.NICK);
		acceptedCommands.add(Command.QUIT);
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

	public void leaveCurrentRoom() {
		currentRoom = null;
	}

	public ProtocolCallback getCallback() {
		return callback;
	}

	public void setCallback(ProtocolCallback callback) {
		if (this.callback == null) {
			this.callback = callback;
		}
	}

	public boolean isCommandAccepted(Command command) {
		return acceptedCommands.contains(command);
	}

	public void setAcceptedCommands(LinkedList<Command> newCommands) {
		synchronized (acceptedCommands) { // ??
			this.acceptedCommands = newCommands;
		}
	}

	public void triggerCallback(StringMessage messageToBeSent) {
		try {
			callback.sendMessage(messageToBeSent);
		} catch (IOException e) {
			System.out.println("Error occured - couldn't invoke callback");
		}
	}

}
