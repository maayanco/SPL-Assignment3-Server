package bgu.spl.container;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import bgu.spl.server.passive.Command;
import bgu.spl.server.passive.StringMessage;
import bgu.spl.server.passive.Result;
import bgu.spl.server.passive.StringMessage;

/**
 * This class is a thread safe singleton that manages all the logic of the game.
 * 
 *
 */
public class ContainerSingleton {

	private Queue<Room> roomsList = new ConcurrentLinkedQueue<Room>();
	private Queue<String> playersNames = new ConcurrentLinkedQueue<String>();
	private LinkedList<String> supportedGames = new LinkedList<String>();

	/**
	 * Class that holds a field instance which contains the ContainerSingleton object 
	 * (ConatinerSingleton constructor is invoked once, and from then on the same instance is
	 * returned). 
	 */
	public static class ContainerHolder { // should this be static? not sure!!!
		private static ContainerSingleton instance = new ContainerSingleton();
	}

	/**
	 * @return instance of the ContainerHolder class
	 */
	public static ContainerSingleton getInstance() {
		return ContainerHolder.instance;
	}

	/**
	 * Constructor 
	 */
	public ContainerSingleton() {
		supportedGames.add("Bluffer"); // Hack
	}

	/**
	 * Receives a message and checks if the command is acceptable by the user, 
	 * if so invokes the appropriate handle method 
	 * @param message
	 *            - the message received from the client
	 * @param currentPlayer
	 *            - the player associated with the client
	 */
	public void processMessage(StringMessage message, Player currentPlayer) {
		if (message.getCommand().equals(Command.NICK) && currentPlayer.isCommandAccepted(Command.NICK)) {
			handleNick(message, currentPlayer);
		} else if (message.getCommand().equals(Command.JOIN)
				&& currentPlayer.isCommandAccepted(Command.JOIN)) {
			handleJoin(message, currentPlayer);
		} else if (message.getCommand().equals(Command.MSG)
				&& currentPlayer.isCommandAccepted(Command.MSG)) {
			handleMsg(message, currentPlayer);
		} else if (message.getCommand().equals(Command.LISTGAMES)
				&& currentPlayer.isCommandAccepted(Command.LISTGAMES)) {
			handleListGames(message, currentPlayer);
		} else if (message.getCommand().equals(Command.STARTGAME)
				&& currentPlayer.isCommandAccepted(Command.STARTGAME)) {
			handleStartGame(message, currentPlayer);
		} else if (message.getCommand().equals(Command.TXTRESP)
				&& currentPlayer.isCommandAccepted(Command.TXTRESP)) {
			handleTxtresp(message, currentPlayer);
		} else if (message.getCommand().equals(Command.SELECTRESP)
				&& currentPlayer.isCommandAccepted(Command.SELECTRESP)) {
			handleSelectresp(message, currentPlayer);
		} else {
			sendSYSMSG(currentPlayer, Result.REJECTED, message, null);
		}
	}

	/**
	 * Check the map if the requested nick is in use by a different user If it
	 * is - reject. If it is free - set the current players nick and it's
	 * acceptable command types to be: JOIN, LISTGAMES, QUIT
	 * 
	 * @param message
	 *            - the message received from the client
	 * @param currentPlayer
	 *            - the player associated with the client
	 */
	public void handleNick(StringMessage message, Player currentPlayer) {
		String requestedNick = message.getParameter(0);
		synchronized (playersNames) {
			boolean foundNick = playersNames.contains(requestedNick);
			if (!foundNick) {
				currentPlayer.setPlayerName(message.getParameter(0));
				playersNames.add(requestedNick);
				Command[] newAcceptableCommands = { Command.JOIN, Command.LISTGAMES,
						Command.QUIT };
				setPlayerAcceptableCommands(currentPlayer, newAcceptableCommands);
				sendSYSMSG(currentPlayer, Result.ACCEPTED, message, null);
			} else {
				Command[] newAcceptableCommands = { Command.NICK, Command.QUIT };
				setPlayerAcceptableCommands(currentPlayer, newAcceptableCommands);
				sendSYSMSG(currentPlayer, Result.REJECTED, message, null);
			}
		}
	}

	/**
	 * Firstly the method removes the player from it's current room (if it
	 * exists) Then adds it to the requested room. If the requested room doesn't
	 * exist - create it and add it to the roomsList
	 * 
	 * @param message
	 *            - the message received from the client
	 * @param currentPlayer
	 *            - the player associated with the client
	 */
	public void handleJoin(StringMessage message, Player currentPlayer) {
		Room currentRoom = currentPlayer.getCurrentRoom();
		if (currentRoom != null) {
			synchronized (currentRoom) {
				// If player is currently in a room - remove
				if (!currentPlayer.getCurrentRoom().removePlayerFromRoom(currentPlayer)) {
					sendSYSMSG(currentPlayer, Result.REJECTED, message, null);
					return;
				}
			}
		}

		// The player is not in any room - we can add him to the requested room
		String requestedRoomName = message.getParameter(0);
		Room requestedRoom = getRoomByName(requestedRoomName);
		if (requestedRoom == null) {
			requestedRoom = new Room(requestedRoomName);
			roomsList.add(requestedRoom);
		}

		synchronized (requestedRoom) {
			boolean isAdditionSucsessfull = requestedRoom.addPlayerToRoom(currentPlayer);
			if (isAdditionSucsessfull) {
				currentPlayer.setCurrentRoom(requestedRoom);
				Command[] newAcceptableCommands = { Command.STARTGAME, Command.MSG,
						Command.LISTGAMES, Command.QUIT };
				setPlayerAcceptableCommands(currentPlayer, newAcceptableCommands);
				sendSYSMSG(currentPlayer, Result.ACCEPTED, message, null);
			} else {
				sendSYSMSG(currentPlayer, Result.REJECTED, message, null);
			}
		}
	}

	/**
	 * Receives the playersList from the room, iterates over all the players and
	 * triggers their callback with the provided message (except for the
	 * currentPlayer, that sent the message) Set new acceptable commands for the
	 * currentPlayer: STARTGAME, MSG, LISTGAMES, QUIT
	 * 
	 * @param message
	 *            - the message received from the client
	 * @param currentPlayer
	 *            - the player associated with the client
	 */
	public void handleMsg(StringMessage message, Player currentPlayer) {
		Room currentRoom = currentPlayer.getCurrentRoom();
		synchronized (currentRoom) {
			Queue<Player> roomsPlayersList = currentRoom.getPlayersList();
			for (Player player : roomsPlayersList) {
				if (!player.equals(currentPlayer)) {
					player.triggerCallback(new StringMessage(Command.USRMSG + " " + message.getParameters()));
				}
			}
		}

		// Set new acceptable commands for the currentPlayer: STARTGAME, MSG,LISTGAMES, QUIT
		Command[] newAcceptableCommands = { Command.STARTGAME, Command.MSG, Command.LISTGAMES,
				Command.QUIT };
		setPlayerAcceptableCommands(currentPlayer, newAcceptableCommands);
		sendSYSMSG(currentPlayer, Result.ACCEPTED, message, null);
	}

	/**
	 * Iterate over the list of supported games in to get a string
	 * representation of the list. send a sysmsg to the currentPlayer (That
	 * initiated the request).
	 * 
	 * @param message
	 *            - the message received from the client
	 * @param currentPlayer
	 *            - the player associated with the client
	 */
	public void handleListGames(StringMessage message, Player currentPlayer) {
		// Go over the list of games
		String supportedGamesStr = "";
		for (String gameName : supportedGames) {
			supportedGamesStr += gameName;
		}

		sendSYSMSG(currentPlayer, Result.ACCEPTED, message, supportedGamesStr);
	}

	/**
	 * If the currentPlayer doesn't have a room - reject request. Otherwise,
	 * call the StartGame method of the player's room (which uses the
	 * GameFactory to create a new game, and the game calls the processStartGame
	 * method of the Game interface)
	 * 
	 * @param message
	 *            - the message received from the client
	 * @param currentPlayer
	 *            - the player associated with the client
	 */
	public void handleStartGame(StringMessage message, Player currentPlayer) {
		Room currentRoom = currentPlayer.getCurrentRoom();
		if (currentRoom != null) {
			synchronized (currentRoom) {
				currentRoom.startNewGame(message);
			}
		} else {
			sendSYSMSG(currentPlayer, Result.REJECTED, message, null);
		}
	}

	/**
	 * Calls the processTxtResp method of the game, and set the players
	 * newAcceptable commands to be SELECTRESP
	 * 
	 * @param message
	 *            - the message received from the client
	 * @param currentPlayer
	 *            - the player associated with the client
	 */
	public void handleTxtresp(StringMessage message, Player currentPlayer) {
		//Don't think this needs synchronize - but check again
		Room currentRoom = currentPlayer.getCurrentRoom();
		String response = currentRoom.getGame().processTxtResp(message, currentPlayer);

		Command[] newAcceptableCommands = { Command.SELECTRESP };
		setPlayerAcceptableCommands(currentPlayer, newAcceptableCommands);
		sendSYSMSG(currentPlayer, Result.ACCEPTED, message, null);
		
		if (response != null && !response.equals("")) {
			currentRoom.sendToAllPlayers(response);
		}

	}
	
	/** 
	 * Forwards the SELECTRESP request to the game itself
	 * @param message
	 *            - the message received from the client
	 * @param currentPlayer
	 *            - the player associated with the client
	 */
	public void handleSelectresp(StringMessage message, Player currentPlayer) {
		//sendSYSMSG(currentPlayer, Result.ACCEPTED, message, null);
		currentPlayer.getCurrentRoom().getGame().processSelectResp(message, currentPlayer);
	
	}

	public boolean handleQuit(Player currentPlayer){
		if(currentPlayer.isCommandAccepted(Command.QUIT)){
			//we need to remove the player from everything!!!!
			removePlayer(currentPlayer);
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * This method sends a SYSMSG to the provided player
	 * @param currentPlayer - the player to which the sysmsg will be sent
	 * @param result - Accepted/Rejected/Unidentified
	 * @param originalMsg - original message received from the client
	 * @param additionalParameters - additional parameters to be sent with the sysmsg (After the result)
	 */
	private void sendSYSMSG(Player currentPlayer, Result result, StringMessage originalMsg, String additionalParameters) {
		String parameters = "";
		if (additionalParameters != null && additionalParameters != "") {
			parameters += " " + additionalParameters;
		}
		currentPlayer
				.triggerCallback(new StringMessage(Command.SYSMSG + " " + originalMsg.getCommand() + " " + result + parameters));
	}

	/**
	 * Converts the ClientCommand array into a linkedList of ClientCommands and set's the users acceptable commands
	 * to this new list
	 * @param currentPlayer - the player to be changed
	 * @param arr - array of acceptable Client commands
	 */
	public void setPlayerAcceptableCommands(Player currentPlayer, Command[] arr) {
		if (arr != null) {
			LinkedList<Command> newAcceptableCommands = new LinkedList<Command>();
			for (Command command : arr) {
				newAcceptableCommands.add(command);
			}
			currentPlayer.setAcceptedCommands(newAcceptableCommands);
		}
	}

	/**
	 * Iterates over the roomsList, if a room is found with the required name -
	 * the room is returned Otherwise - null is returned
	 * 
	 * @param name
	 *            - the name of the room which we want to get
	 * @return
	 */
	public Room getRoomByName(String name) {
		synchronized (roomsList) {
			for (Room room : roomsList) {
				if (room.getRoomName().equals(name)) {
					return room;
				}
			}
		}
		return null;
	}

	/**
	 * Is invoked when a quit command is received - 
	 * removes the provided player from the players room and from the players names list
	 * @param player
	 */
	public void removePlayer(Player player){
		//should this be synchronized? don't think so..
		playersNames.remove(player.getPlayerName());
		Room currentPlayersRoom = player.getCurrentRoom();
		if(currentPlayersRoom!=null){
			currentPlayersRoom.removePlayerFromRoom(player);
		}
	}
}
