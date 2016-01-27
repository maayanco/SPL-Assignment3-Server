package bgu.spl.logic;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Class with the ability to create new games 
 */
public class GameFactory {
	
	private static String[] supportedGames= {"bluffer"};
	
	/**
	 * @return string representation of the list of supported games 
	 */
	public static String getSupportedGames(){
			String supportedGamesString="";
			for(String gameName : supportedGames){
				supportedGamesString+=gameName+" ";
			}
			return supportedGamesString;
		}
	
	/**
	 * Creates a new game
	 * @param playersList - list of players
	 * @param gameType - the type of the game
	 * @return - new game of the requested type
	 */
	public Game create(Queue<Player> playersList, String gameType) {
			if(gameType.equalsIgnoreCase("bluffer")){
				return new Bluffer(playersList);
			}
			return null;
		}
}