package bgu.spl.container;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents a room and contains a game which might be in progress or not
 * And a list of players subscribed to this room
 */
public class Room {

	private String roomName;
	private Game game;
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
	public void startNewGame() {
		GameFactory gameFactory = new GameFactory();
		/* synchronized(game){ */
		/* synchronized(playersList){ */
		game = gameFactory.create(playersList);
		game.processStartGame();
		/* } */
		/* } */
	}

	/**
	 * Sends the provided message to all players currently in the room
	 * @param message
	 */
	public void sendToAllPlayers(String message) {
		synchronized (playersList) {
			for (Player player : playersList) {
				player.triggerCallback(message);
			}
		}
	}

	/**
	 * @return a clone of the  playersList
	 */
	public Queue<Player> getPlayersList() {
		return new ConcurrentLinkedQueue(playersList);
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
				synchronized (playersList) {
					if (playersList.contains(currentPlayer)) {
						playersList.remove(currentPlayer);
					}
					return true;
				}
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
		/* synchronized(game){ */
		if (game != null) {
			// A game is currently in progrees - can't add player
			return false;
		} else {
			// A game is not currently in progress
			/* synchronized(playersList){ */
			if (!playersList.contains(currentPlayer)) {
				playersList.add(currentPlayer);
			}
			/* } */
			return true;
		}
		/* } */
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
