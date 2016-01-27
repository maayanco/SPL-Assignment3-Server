package bgu.spl.logic;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.spl.passive.Command;
import bgu.spl.passive.StringMessage;
import bgu.spl.server.threadperclient.ProtocolCallback;

/**
 * Class representing a player object
 * contains the player's name, callback and list of accepted commands
 */
public class Player {

	private String playerName = "";
	private Room currentRoom;
	private ProtocolCallback<StringMessage> callback;
	private Queue<Command> acceptedCommands = new ConcurrentLinkedQueue<Command>();
	private static final Logger DATA_LOGGER = Logger.getLogger("Player");
	
	/**
	 * Constructor
	 */
	public Player() {
		acceptedCommands.add(Command.NICK);
		acceptedCommands.add(Command.QUIT);
	}
	
	/**
	 * @return the current player's name
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * sets the name of the player
	 * @param playerName - the name of the player
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * @return the current room of the player
	 */
	public Room getCurrentRoom() {
		return currentRoom;
	}

	/**
	 * sets the current room to be the provided current room
	 * @param currentRoom - the room object to be set as the curren't player's room
	 */
	public void setCurrentRoom(Room currentRoom) {
		this.currentRoom = currentRoom;
	}

	/**
	 * sets the current room as null
	 */
	public void leaveCurrentRoom() {
		currentRoom = null;
	}
	
	/**
	 * @return the current players callback
	 */
	@SuppressWarnings("rawtypes")
	public ProtocolCallback getCallback() {
		return callback;
	}

	/**
	 * Sets the current players callback - but only if it wasn't already set
	 * @param callback
	 */
	public void setCallback(ProtocolCallback<StringMessage> callback) {
		if (this.callback == null) {
			this.callback = callback;
		}
	}

	/**
	 * @param command the provided command
	 * @return true if the provided command should be accepted by the player
	 */
	public boolean isCommandAccepted(Command command) {
		return acceptedCommands.contains(command);
	}

	/**
	 * Sets the provided commands list as the accepted commands of the player
	 * @param newCommands - lists of commands to be accepted by the player
	 */
	public void setAcceptedCommands(LinkedList<Command> newCommands) {
		synchronized (acceptedCommands) { // ??
			this.acceptedCommands = newCommands;
		}
	}

	/**
	 * Triggers the callback's sendMessage method with the provided message
	 * @param messageToBeSent - the message that should be sent to the client
	 */
	public void triggerCallback(StringMessage messageToBeSent) {
		synchronized(callback){
			try {
				callback.sendMessage(messageToBeSent);
			} catch (IOException e) {
				DATA_LOGGER.log(Level.INFO, "Error occured - couldn't invoke callback");
			}
		}
	}

}
