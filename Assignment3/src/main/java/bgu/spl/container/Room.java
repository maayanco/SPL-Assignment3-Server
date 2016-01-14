package bgu.spl.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import bgu.spl.server.passive.ClientCommand;
import bgu.spl.server.passive.Message;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class Room {
	
	private static final Logger Log = Logger.getLogger(Bluffer.class.getName());
	private String roomName;
	//private GameState gameState;
	private Game game;
	private LinkedList<Player> playersList = new LinkedList<Player>();
	private LinkedList<String> supportedGames = new LinkedList<String>();
	private Map<Player, ProtocolCallback> mapPlayerToCallback = new HashMap<Player,ProtocolCallback>();
	
	//we need to map the players to their callbacks..
	//because when we need to send a message to all the players
	
	public Room(String roomName){
		this.roomName=roomName;
	}
	
	public void addPlayer(Player player, ProtocolCallback callback){
		playersList.add(player);
		mapPlayerToCallback.put(player, callback); 
	}
	
	public LinkedList<Player> getPlayers(){
		return new LinkedList<Player>(playersList);
	}
	
	public String getRoomName(){
		return roomName;
	}
	
	public Game getGame(){
		return game;
	}
	
	
	//Big problem!!! I Specificy BLUFFER HERREEE!
	public void startNewGame(){
		game = new Bluffer(playersList, mapPlayerToCallback);
	}
	
	public void triggerAllCallbacks(String msg){
		Iterator it = mapPlayerToCallback.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			ProtocolCallback callback = (ProtocolCallback) entry.getValue();
			
			try {
				callback.sendMessage(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

	}
	
}

