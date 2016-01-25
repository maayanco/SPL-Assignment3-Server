package bgu.spl.container;

import java.util.LinkedList;
import java.util.Queue;

public class GameFactory {
	private static String[] supportedGames= {"bluffer"};
		
	public static String getSupportedGames(){
			String supportedGamesString="";
			for(String gameName : supportedGames){
				supportedGamesString+=gameName+" ";
			}
			return supportedGamesString;
		}
	
	public Game create(Queue<Player> playersList, String gameType) {
			if(gameType.equalsIgnoreCase("bluffer")){
				return new Bluffer(playersList);
			}
			return null;
		}
}