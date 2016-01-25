package bgu.spl.container;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.google.gson.Gson;

import bgu.spl.server.json.Database;
import bgu.spl.server.json.Input;
import bgu.spl.server.passive.Command;
import bgu.spl.server.passive.Result;
import bgu.spl.server.passive.StringMessage;

/**
 * Implements the game interface 
 * This class represents the specific bluffer game logic
 */
public class Bluffer implements Game<StringMessage> {

	private Queue<Round> roundsList;
	private Queue<Player> playersList;
	private Map<Player, Integer> mapPlayersToScores;
	private static final int NUMBER_OF_ROUNDS = 3;

	/**
	 * Constructor
	 * Reads json file and 
	 * @param inputPlayersList
	 */
	public Bluffer(Queue<Player> inputPlayersList) {
		//Initiailze fields
		roundsList = new ConcurrentLinkedQueue<Round>();
		playersList = new ConcurrentLinkedQueue<Player>();
		mapPlayersToScores = new ConcurrentHashMap<Player, Integer>();
		
		for (Player player : inputPlayersList) {
			this.playersList.add(player);
			mapPlayersToScores.put(player, 0);
		}
		
		//Read the json file 
		readJson();
	}
	
	/**
	 * Read the json file into the java jsonObject and insert the data into the rounds list
	 * as Round objects 
	 */
	public void readJson(){
		// Load the json file into the jsonObject
		String jsonPath = "bluffer.json";
		Database jsonObject = null;
		try {
			Gson gson = new Gson();
			BufferedReader br;
			br = new BufferedReader(new FileReader(jsonPath));
			jsonObject = gson.fromJson(br, Database.class);
		} catch (FileNotFoundException e) {
			System.out.println("ERROR - couldn't read json file");
		}
		
		// Now read the contents of the jsonObject and insert into the rounds list
		if (jsonObject != null) {
			Input[] inputArray = jsonObject.getQuestions();
			for (int i = 0; i < inputArray.length && i<NUMBER_OF_ROUNDS; i++) {
				Input currentInput = inputArray[i];
				roundsList.add(new Round(currentInput.getQuestionText(), currentInput.getRealAnswer(), playersList));
			}
		}
	}

	@Override
	/**
	 * Invokes the askQuestion method and sets all players acceptable commands to be TXTRESP
	 */
	public void processStartGame() {
		askQuestion();

		// Set the new acceptable commands to:
		LinkedList<Command> newAcceptableCommands = new LinkedList<Command>();
		newAcceptableCommands.add(Command.TXTRESP);
		for (Player player : playersList) {
			player.setAcceptedCommands(newAcceptableCommands);
		}
	}

	/**
	 * returns the question associated to the current round of the game
	 */
	private void askQuestion() {
		Round currentRound = getCurrentRound();
		String question = currentRound.getQuestion();
		sendMessageToAllPlayers(Command.ASKTXT + " " + question);
	}

	@Override
	/**
	 * 
	 */
	public String processTxtResp(StringMessage message, Player currentPlayer) {
		String bluffedAnswer = message.getParameter(0);
		Round currentRound = getCurrentRound();
		currentRound.addBluffedAnswer(currentPlayer, bluffedAnswer);

		String response = "";
		if (currentRound.isAllBluffedMessagesArrived()) {
			response = Command.ASKCHOICES + " " + currentRound.getAllAnswers();
		}
		return response;
	}

	@Override
	public void processSelectResp(StringMessage message, Player currentPlayer) {
		String selectedAnswerParam = message.getParameter(0);
		if (!getCurrentRound().updateSelectedAnswer(selectedAnswerParam, currentPlayer)) {
			currentPlayer.triggerCallback(new StringMessage(Command.SYSMSG +" "+message.getCommand()+" " + Result.REJECTED));
			return;
		}
		else{
			currentPlayer.triggerCallback(new StringMessage(Command.SYSMSG +" "+message.getCommand()+" " + Result.ACCEPTED));
		}
		sendGameMsg(new StringMessage(Command.GAMEMSG + " " +"Correct answer: " + getCurrentRound().getRealAnswer()), currentPlayer);

		// set current player to receive txtresp
		Command[] newAcceptableCommands = {};
		setPlayerAcceptableCommands(currentPlayer, newAcceptableCommands);

		if (getCurrentRound().isAllPlayersSelectedAnswers()) {
			// Finish round - Go over players, calculate their score in the
			// round and send
			for (Player player : playersList) {
				int currentScore = mapPlayersToScores.get(player);
				int roundScore = getCurrentRound().getScoreByPlayer(player);
				mapPlayersToScores.replace(player, currentScore + roundScore);

				// we want to trigger the callbacks of each player
				int playersScore = getCurrentRound().getScoreByPlayer(player);
				if (getCurrentRound().isPlayerCorrect(player)) {
					sendGameMsg(new StringMessage(Command.GAMEMSG + " " +"correct! " + playersScore), player);
				} else {
					sendGameMsg(new StringMessage(Command.GAMEMSG + " " +"wrong! " + playersScore), player);
				}

			}

			roundsList.remove();

			// Start new round if possible
			if (getCurrentRound() != null) {
				askQuestion();

				// Set the new acceptable commands for all the players in the game
				for (Player player : playersList) {
					Command[] acceptableCommands = {Command.TXTRESP};
					setPlayerAcceptableCommands(player, acceptableCommands);
				}

			} else {
				// Finish the game! send summaries to everyone..
				for (Player player : playersList) {
					Command[] acceptableCommands = { Command.MSG, Command.JOIN, Command.STARTGAME, Command.LISTGAMES, Command.QUIT };
					setPlayerAcceptableCommands(player, acceptableCommands);
					sendGameMsg(new StringMessage(Command.GAMEMSG + " " +getGameSummary()), player);

				}
				currentPlayer.getCurrentRoom().finishGame();

			}
		}
	}

	//Command.GAMEMSG + " " + message
	
	@Override
	/**
	 * invokes the triggerCallback method of the current player with the
	 * provided message, sending a message of command type GAMEMSG
	 */
	public void sendGameMsg(StringMessage message, Player currentPlayer) {
		currentPlayer.triggerCallback(message);
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
	 *  @returns string representation of the players and the sum of their
	 *  scores of all the rounds
	 */
	private String getGameSummary() {
		String summary = "Summary:";
		//not synched because when the game summary occurs theres no chance for any change in the 
		//players in the playerList.. or in the mapPlayersToScores
		/*synchronized(playersList){*/
			for (Player player : playersList) {
				int currentPlayersScore = mapPlayersToScores.get(player);
				summary += " " + player.getPlayerName() + ": " + currentPlayersScore + "pts";
			}
		/*}*/
		return summary;
	}

	/**
	 * @return the current round (Which is the first Object in the rounds list)
	 * or null if the list is empty
	 */
	private Round getCurrentRound() {
		synchronized(roundsList){
			if (!roundsList.isEmpty())
				return roundsList.peek();
		}
		return null;
	}

	/**
	 * Go over the players list and trigger their callbacks with the provided message
	 * @param messageToBeSent
	 */
	private void sendMessageToAllPlayers(String messageToBeSent) {
		synchronized(playersList){
			for (Player player : playersList) {
				player.triggerCallback(new StringMessage(messageToBeSent));
			}
		}
	}


}
