package bgu.spl.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.spl.passive.Command;
import bgu.spl.passive.Result;
import bgu.spl.passive.StringMessage;

/**
 * Represents a round of the game includes: question, real answer, maps between players to bluffed answers, selected answers and round scores
 */
public class Round {
	
	private String questionText;
	private String realAnswer;
	private ArrayList<String> answerList = new ArrayList<String>();
	private Map<Player, String> mapPlayersToBluffedAnswers = new ConcurrentHashMap<Player, String>();
	private Map<Player, String> mapPlayersToSelectedAnswers = new ConcurrentHashMap<Player, String>();
	private Map<Player, Integer> mapPlayersToRoundScore = new ConcurrentHashMap<Player, Integer>();
	private static final Logger DATA_LOGGER = Logger.getLogger("Logic.Round");
	private static final String NEW_LINE_DELIMITER="\n";
	private static final String CARRIAGE_RETURN_DELIMITER="\r";
	
	/**
	 * Constructor
	 * @param questionText - the text of the question
	 * @param realAnswer - the real answer
	 * @param playersList - list of players
	 */
	public Round(String questionText, String realAnswer, Queue<Player> playersList) {
		this.questionText = questionText;
		this.realAnswer = realAnswer;
		
		for (Player player : playersList) {
			mapPlayersToBluffedAnswers.put(player, "");
			mapPlayersToSelectedAnswers.put(player, "");
			mapPlayersToRoundScore.put(player, 0);
		}
	}

	/**
	 * @param player - the current player
	 * @return true if the current player's answer is right
	 */
	public boolean isPlayerCorrect(Player player) {
		String playersAnswer = mapPlayersToSelectedAnswers.get(player);
		return playersAnswer.equalsIgnoreCase(realAnswer);
	}
	
	/**
	 * @param value - the string that should be parsed
	 * @return true if parsing is successfull, false otherwise
	 */
	private boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * @return true if all bluffed answers have arrived, false otherwise
	 */
	@SuppressWarnings("rawtypes")
	public boolean isAllPlayersSelectedAnswers() {
		boolean foundMissingSelectedAnswer = false;
		Iterator it = mapPlayersToSelectedAnswers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue() == "") {
				foundMissingSelectedAnswer = true;
			}
		}
		return !foundMissingSelectedAnswer;

	}

	/**
	 * @return true if all answers have arrived, false otherwise
	 */
	@SuppressWarnings("rawtypes")
	public boolean isAllBluffedMessagesArrived() {
		boolean foundMissingAnswer = false;
		synchronized (mapPlayersToBluffedAnswers) {
			Iterator it = mapPlayersToBluffedAnswers.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				if (entry.getValue() == "") {
					foundMissingAnswer = true;
				}
			}
		}

		return !foundMissingAnswer;
	}

	/**
	 * Receives an answer number, check's it's validity and maps the player to that answer number
	 * and updates their score
	 * @param selectedAnswerNumber - the number of the selected answer
	 * @param currentPlayer - the current player
	 * @return true if the update was successfull, false otherwise
	 */
	public boolean updateSelectedAnswer(String selectedAnswerNumber, Player currentPlayer) {
		boolean isInvalid = false;
		selectedAnswerNumber=selectedAnswerNumber.replaceAll(NEW_LINE_DELIMITER, "");
		selectedAnswerNumber=selectedAnswerNumber.replaceAll(CARRIAGE_RETURN_DELIMITER, "");
		if (tryParseInt(selectedAnswerNumber.toString())) {
			int selectedAnswerNum = Integer.parseInt(selectedAnswerNumber);
			if (selectedAnswerNum >= answerList.size()) {
				isInvalid = true;
			} else {
				String selectedAnswer = answerList.get(selectedAnswerNum);
				mapPlayersToSelectedAnswers.replace(currentPlayer, selectedAnswer);
				if (selectedAnswer.equals(realAnswer)) {
					addScoreToPlayer(currentPlayer, 10);
				} else {
					Player playerWhichConceivedAnswer = getPlayerByAnswer(selectedAnswer);
					addScoreToPlayer(playerWhichConceivedAnswer, 5);
				}
			}
		} else {
			isInvalid = true;
		}

		return !isInvalid;

	}

	/**
	 * @param answer - the string with which the player is located
	 * @return the player which provided the string answer
	 */
	@SuppressWarnings("rawtypes")
	public Player getPlayerByAnswer(String answer) {
		Player player = null;
		Iterator it = mapPlayersToBluffedAnswers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue().equals(answer)) {
				player = (Player) entry.getKey();
			}
		}
		return player;
	}
	
	
	/**
	 * @return String representation of all the answers the players provided
	 */
	public String getAllAnswers() {
		answerList.add(realAnswer);
		Collections.shuffle(answerList);
		String ans = "";
		for (int i = 0; i < answerList.size(); i++) {
			ans = ans + " " + i + ". " + answerList.get(i);
		}

		return ans;
	}

	/**
	 * Maps the provided answer to the player
	 * @param player - the current player
	 * @param bluffedAnswer - the bluffed answer the player provided
	 */
	public void addBluffedAnswer(Player player, String bluffedAnswer) {
			mapPlayersToBluffedAnswers.replace(player, bluffedAnswer);
			answerList.add(bluffedAnswer);
	}

	/**
	 * Maps the provided score to the player
	 * @param player - the current player
	 * @param scoreToAdd - the score that should be mapped to the player
	 */
	public void addScoreToPlayer(Player player, int scoreToAdd) {
		if (mapPlayersToRoundScore.containsKey(player)) {
			int currentScore = mapPlayersToRoundScore.get(player);
			mapPlayersToRoundScore.replace(player, currentScore + scoreToAdd);
		} else {
			DATA_LOGGER.log(Level.INFO,"Error occured - player doesn't exist in round score's map");
		}
	}

	/**
	 * @param player - the current Player 
	 * @return the score of the player
	 */
	public int getScoreByPlayer(Player player) {
		int score = -1;
		if (mapPlayersToRoundScore.containsKey(player)) {
			score = mapPlayersToRoundScore.get(player);
		}
		return score;
	}

	/**
	 * @return the realAnswer string
	 */
	public String getRealAnswer() {
		return realAnswer;
	}
	
	/** 
	 * @return the questionText string
	 */
	public String getQuestion() {
		return questionText;
	}

}
