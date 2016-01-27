package bgu.spl.logic;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import bgu.spl.passive.StringMessage;

/**
 * Represents a room and contains a game which might be in progress or not
 * And a list of players subscribed to this room
 */
public class Room {

	private String roomName;
	private Game<StringMessage> game;
	private Queue<Player> playersList = new ConcurrentLinkedQueue<Player>();

	/**
	 * Constructor - sets roomName as the name of the room
	 * @param roomName
	 */
	public Room(String roomName) {
			this.roomName = roomName;
	}

	/**
	 * Initializes a new game instance using the GameFactory
	 */
	@SuppressWarnings("unchecked")
	public boolean startNewGame(StringMessage message) {
		GameFactory gameFactory = new GameFactory();
		game = gameFactory.create(playersList,message.getParameter(0));
		if(game==null){
			return false;
		}
		else{
			game.processStartGame();
			return true;
		}
	}

	/**
	 * Sends the provided message to all players currently in the room
	 * @param message - the message to be sent to all players
	 */
	public void sendToAllPlayers(String message) {
		synchronized (playersList) {
			for (Player player : playersList) {
				player.triggerCallback(new StringMessage(message));
			}
		}
	}

	/**
	 * @return a clone of the  playersList
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Queue<Player> getPlayersList() {
		return new ConcurrentLinkedQueue<Player>(playersList);
	}

	/**
	 * If a game is not currently in progress - remove the player from
	 * playersList and return true (if removal was successful). If a game is in
	 * progress - return false.
	 * 
	 * @param currentPlayer - the player which will be removed from the room
	 * @return true if successful, false otherwise
	 */
	public boolean removePlayerFromRoom(Player currentPlayer) {
		
		if (game != null) {
			// A game is currently in progress - can't remove player
			return false;
		} else {
			// A game is not in progress-
					if (playersList.contains(currentPlayer)) {
						playersList.remove(currentPlayer);
						currentPlayer.setCurrentRoom(null); 
					}
					return true;
		}
	}

	/**
	 * If a game is not currently in progress - add the player to the
	 * playersList and return true (if addition was successful). If a game is
	 * in progress- return false;
	 * 
	 * @param currentPlayer - the player which will be removed from the room
	 * @return true if successful, false otherwise
	 */
	public boolean addPlayerToRoom(Player currentPlayer) {
		if (game != null) {
			return false;
		} else {
			if (!playersList.contains(currentPlayer)) {
				playersList.add(currentPlayer);
			}
			return true;
		}
	}

	/**
	 * finishes the current game by setting the game field to null
	 */
	public void finishGame() {
		synchronized (game) {
			this.game = null;
		}
	}

	/**
	 * @return the room name
	 */
	public String getRoomName() {
		return roomName;
	}

	/**
	 * @return the current game
	 */
	public Game getGame() {
		return game;
	}

}
