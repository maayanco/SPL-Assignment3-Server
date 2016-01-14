package bgu.spl.container;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import bgu.spl.server.passive.ClientCommand;
import bgu.spl.server.passive.CommandType;
import bgu.spl.server.passive.Message;
import bgu.spl.server.passive.Result;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class ContainerSingleton {
	
	private static final Logger Log = Logger.getLogger(ContainerSingleton.class.getName());
	private LinkedList<Room> roomsList = new LinkedList<Room>();;
	private LinkedList<String> playersNames = new LinkedList<String>();
	private LinkedList<String> supportedGames = new LinkedList<String>();
	
	public static class RoomsHolder{ //should this be static? not sure!!!
		private static ContainerSingleton instance = new ContainerSingleton();
	}
	
	public static ContainerSingleton getInstance(){
		return RoomsHolder.instance;
	}
	
	public ContainerSingleton(){
		supportedGames.add("Bluffer"); //Hack
	}
	
	private void triggerCallback(Player player, String msgToSend){
		try {
			player.getCallback().sendMessage(msgToSend);
		} catch (IOException e) {

		}
	}
	
	public void handleNick(Message message, Player currentPlayer){
		boolean foundNick=false;
		String requestedNick=message.getParameter(0);
		//Go over the list of players
		for(String playerName : playersNames){
			if(playerName.equals(requestedNick)){
				foundNick=true;
			}
		}
		
		if(foundNick==false){ //if the nick is 'free'
			currentPlayer.setPlayerName(message.getParameter(0));
			playersNames.add(requestedNick);
			triggerCallback(currentPlayer,"SYSMSG NICK"+Result.ACCEPTED);
		}
		else{
			triggerCallback(currentPlayer,"SYSMSG NICK"+Result.REJECTED);
		}
		
	}

	public Room getRoomByName(String name){
		boolean foundRoom=false;

		for(Room room : roomsList){
			if(room==null){
				System.out.println("dddddddddddamnnnn");
			}
			if(room.getRoomName().equals(name)){
				return room;
			}
		}
		return null;
	}
	
	private boolean addPlayerToRoomIfPossible(Room newRoom, Player player){
		Game currentRoomsGame = newRoom.getGame(); //should be null
		if(currentRoomsGame!=null && !currentRoomsGame.getGameState().equals(GameState.Not_Active)){
			return false;
		}
		Room currentPlayersRoom = player.getCurrentRoom();
		if(currentPlayersRoom!=null && currentPlayersRoom.getGame()!=null ){
			return false;
		}
		if(player.getCurrentRoom()!=null && player.getCurrentRoom().getGame()!=null && !player.getCurrentRoom().getGame().getGameState().equals(GameState.Not_Active)){ //in this case we can't 
			return false;
		}
		else{
			player.setCurrentRoom(newRoom);
			newRoom.addPlayer(player);
			return true;
		}
	}
	
	public void handleJoin(Message message, Player player){
		boolean playerIsCurrentlyInARoom=(player.getCurrentRoom()!=null); //if the room we received is not null then he is in a room!
		Room room = getRoomByName(message.getParameter(0));
		if(room!=null){ //we want to join this room
			if(addPlayerToRoomIfPossible(room, player)){
				triggerCallback(player, "SYSMSG JOIN "+Result.ACCEPTED);
			}
			else{
				triggerCallback(player, "SYSMSG JOIN "+Result.REJECTED);
			}
		}
		else{ //there is no room with this name! create this room..
				/* create a new room */
				//Game game = player.getRoom().getGame();
				Room newRoom = new Room(message.getParameter(0));
				roomsList.add(newRoom);
				if(playerIsCurrentlyInARoom){/* Check if we can take the player out from his current room and switch to the new room */
					if(addPlayerToRoomIfPossible(newRoom,player)){
						triggerCallback(player, "SYSMSG JOIN "+Result.ACCEPTED);
					}
					else{
						triggerCallback(player, "SYSMSG JOIN "+Result.REJECTED);
					}
				}
				else{
					player.setCurrentRoom(newRoom);
					newRoom.addPlayer(player);
					triggerCallback(player, "SYSMSG JOIN "+Result.ACCEPTED);
				}
		}
		
	}

	public void handleMsg(Message message, Player player){
		String messageToBeSent=message.getParameter(0);
		player.getCurrentRoom().triggerAllCallbacks(messageToBeSent);
		triggerCallback(player, "SYSMSG MSG"+Result.ACCEPTED);
		
		//Is there any case in which we should reject?
	}
	
	public void handleListGames(Message message,Player player){
		//Go over the list of games
		String supportedGamesStr="";
		for(String gameName : supportedGames){
			supportedGamesStr+=gameName;
		}
		
		triggerCallback(player, "SYSMSG LISTGAMES "+Result.ACCEPTED+supportedGamesStr);
		
		//Is there a case where this is rejected??
	}
	
	public void handleStartGame(Message message, Player player){
		player.getCurrentRoom().startNewGame(); //bad - inside it is specific to bluffer!!
		player.getCurrentRoom().getGame().setGameState(GameState.Active);
		player.getCurrentRoom().getGame().askQuestion();
		triggerCallback(player, "SYSMSG STARTGAME "+Result.ACCEPTED);
		//Is there a scenario in which this should be rejected?
	}
	
	
	public void processMessage(Message message, Player currentPlayer){
		if(message.getCommand().equals(ClientCommand.NICK)){
			handleNick(message,currentPlayer);
		}
		else if(message.getCommand().equals(ClientCommand.JOIN)){
			handleJoin(message, currentPlayer);
		}
		else if(message.getCommand().equals(ClientCommand.MSG)){
			handleMsg(message, currentPlayer);
		}
		else if(message.getCommand().equals(ClientCommand.LISTGAMES)){
			handleListGames(message, currentPlayer);
		}
		else if(message.getCommand().equals(ClientCommand.STARTGAME)){
			handleStartGame(message, currentPlayer);
		}
		else if(message.getCommand().equals(ClientCommand.TXTRESP)){
			//Forward the message to the game!
			currentPlayer.getCurrentRoom().getGame().processTxtResp(message, currentPlayer);
		}
		else if(message.getCommand().equals(ClientCommand.SELECTRESP)){
			//Forward the message to the game!
			currentPlayer.getCurrentRoom().getGame().processSelectResp(message, currentPlayer);
		}
		else if(message.getCommand().equals(ClientCommand.QUIT)){
	
		}
	}
	
}
