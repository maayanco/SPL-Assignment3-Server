package bgu.spl.container;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gson.Gson;

import bgu.spl.server.json.Database;
import bgu.spl.server.json.Input;
import bgu.spl.server.passive.Message;
import bgu.spl.server.passive.Result;
import bgu.spl.server.passive.ServerCommands;
import bgu.spl.server.threadperclient.ProtocolCallback;

public class Bluffer implements Game{
	
	private static final Logger Log = Logger.getLogger(Bluffer.class.getName());
	private GameState gameState = GameState.Not_Active;
	private LinkedList<Round> roundsList = new LinkedList<Round>(); 
	private LinkedList<Player> playersList = new LinkedList<Player>();
	private Map<Player, Integer> mapPlayersToScores = new HashMap<Player,Integer>(); //map 
	private Map<Player,ProtocolCallback> mapPlayersToCallbacks = new HashMap<Player,ProtocolCallback>();
	
	public Bluffer(LinkedList<Player> inpurPlayersList, Map<Player,ProtocolCallback> mapPlayersToCallbacks){
		/** Load the questions */
		String jsonPath = "bluffer.json";
		Database jsonObject = null;
		try {
			Gson gson = new Gson();
			BufferedReader br;
			br = new BufferedReader(new FileReader(jsonPath));
			jsonObject= gson.fromJson(br, Database.class);
		} catch (FileNotFoundException e) {
			System.out.println("Severe ERROR - couldn't read json file");
			//!!!
		}
		
		for(Player player : inpurPlayersList){
			this.playersList.add(player);
			mapPlayersToScores.put(player, 0);
		}
		
		this.mapPlayersToCallbacks=mapPlayersToCallbacks;
		
		if(jsonObject!=null){
			//Now read the contents of the jsonObject 
			Input[] inputArray = jsonObject.getQuestions();
			for(int i=0; i<inputArray.length; i++){
				Input currentInput = inputArray[i];
				roundsList.add(new Round(currentInput.getQuestionText(), currentInput.getRealAnswer(), playersList));
			}
		}
	}
	
	public void askQuestion(){
		Round currentRound = getCurrentRound();
		String question = currentRound.getQuestion();
		sendMessageToAllPlayers(ServerCommands.ASKTXT+" "+question);
	}
	
	private void triggerCallback(ProtocolCallback callback, String messageToBeSent){
		try {
			callback.sendMessage(messageToBeSent);
		} catch (IOException e) {
			
		}
	}
	
	public Round getCurrentRound(){
		return roundsList.getFirst();
	}
	
	public void processTxtResp(Message message, ProtocolCallback callback, Player currentPlayer) {
		if(gameState.equals(GameState.Not_Active)){
			triggerCallback(callback, "SYSMSG TXTRESP "+Result.REJECTED);
		}
		else{
			String bluffedAnswer=message.getParameter(0);
			Round currentRound = getCurrentRound();
			currentRound.addBluffedAnswer(currentPlayer, bluffedAnswer);
			if(currentRound.isAllBluffedMessagesArrived()){
				//Now we will send everyone a SELECTRESP
				triggerCallback(callback, "SYSMSG TXTRESP "+Result.ACCEPTED);
				sendMessageToAllPlayers("ASKCHOICES "+currentRound.getAllAnswers());
			}
		}
	}
	
	public void processSelectResp(Message message, ProtocolCallback callback, Player currentPlayer) {
		String selectedAnswerParam = message.getParameter(0);
		getCurrentRound().updateSelectedAnswer(selectedAnswerParam,currentPlayer);
		if(getCurrentRound().isAllPlayersSelectedAnswers()){
			finishRound();
		}
	}
	
	public void finishRound(){
		//delete the first question, send messages to all players
		Round currentRound = getCurrentRound();
		//Iterate over all the players, update the total score
		String summaryString="";
		for(Player player : playersList){
			int currentScore = mapPlayersToScores.get(player);
			int roundScore = currentRound.getScoreByPlayer(player);
			mapPlayersToScores.replace(player, currentScore+roundScore);
			summaryString=summaryString+player.getPlayerName()+" "+mapPlayersToScores.get(player)+"pts";
		}
		
		sendMessageToAllPlayers("GAMEMSG Summary:"+summaryString);
		
		roundsList.removeFirst();
		
	}
	
	
	private void sendMessageToAllPlayers(String messagToBeSent){
		//Iterate over the map
		Iterator it = mapPlayersToCallbacks.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			ProtocolCallback callback = (ProtocolCallback) entry.getValue();
			triggerCallback(callback, messagToBeSent);
		}
	}
	
	private void sendMessageToPlayer(String messageToBeSent, Player player){
		if(mapPlayersToCallbacks.containsKey(player)){
			ProtocolCallback callback = mapPlayersToCallbacks.get(player);
			triggerCallback(callback,messageToBeSent);
		}
		else{
			//Error - not found player
		}
	}
	
	public GameState getGameState(){
		return gameState;
	}
	
	public void setGameState(GameState gameState){
		this.gameState=gameState;
	}
	
	

}

