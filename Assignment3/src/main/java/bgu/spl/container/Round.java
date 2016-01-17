package bgu.spl.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import bgu.spl.server.passive.Result;
import bgu.spl.server.passive.ServerCommand;

/**
 * Represents a round of the game includes: question, real answer, maps between players to bluffed answers, selected answers and round scores
 */
public class Round {
	
	private String questionText;
	private String realAnswer;
	private ArrayList<String> answerList = new ArrayList<String>();
	private Map<Player, String> mapPlayersToBluffedAnswers = new HashMap<Player, String>();
	private Map<Player, String> mapPlayersToSelectedAnswers = new HashMap<Player, String>();
	private Map<Player, Integer> mapPlayersToRoundScore = new HashMap<Player, Integer>();

	public boolean isPlayerCorrect(Player player) {
		String playersAnswer = mapPlayersToSelectedAnswers.get(player);
		return playersAnswer.equals(realAnswer);
	}

	public Round(String questionText, String realAnswer, Queue<Player> playersList) {
		this.questionText = questionText;
		this.realAnswer = realAnswer;
		/*
		 * add all the players to the map with null value as their suggested
		 * answers
		 */
		for (Player player : playersList) {
			mapPlayersToBluffedAnswers.put(player, null);
			mapPlayersToSelectedAnswers.put(player, null);
			mapPlayersToRoundScore.put(player, 0);
		}
	}

	private boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean isAllPlayersSelectedAnswers() {
		// Iterate over the mapPlayersToSelectedAnswers
		boolean foundMissingSelectedAnswer = false;
		Iterator it = mapPlayersToSelectedAnswers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue() == null) {
				foundMissingSelectedAnswer = true;
			}
		}
		return !foundMissingSelectedAnswer;

	}

	public boolean isAllBluffedMessagesArrived() {
		// we want that foundMissingAnswer will be
		boolean foundMissingAnswer = false;
		synchronized (mapPlayersToBluffedAnswers) {
			Iterator it = mapPlayersToBluffedAnswers.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				if (entry.getValue() == null) {
					foundMissingAnswer = true;
				}
			}
		}

		return !foundMissingAnswer;
	}

	public boolean updateSelectedAnswer(String selectedAnswerNumber, Player currentPlayer) {
		boolean isInvalid = false;
		selectedAnswerNumber = selectedAnswerNumber.replaceAll("(\\r|\\n)", "");
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

		if (isInvalid) {
			currentPlayer.triggerCallback(ServerCommand.SYSMSG + " " + Result.REJECTED);
		}

		return !isInvalid;

	}

	public Player getPlayerByAnswer(String answer) {
		Player player = null;
		// Go over the mapPlayersToBluffedAnswers, find the value answer - and
		// return it's
		// corresponding player key
		Iterator it = mapPlayersToBluffedAnswers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if (entry.getValue().equals(answer)) {
				player = (Player) entry.getKey();
			}
		}
		return player;
	}

	public String getAllAnswers() {
		answerList.add(realAnswer);
		Collections.shuffle(answerList);
		String ans = "";
		for (int i = 0; i < answerList.size(); i++) {
			ans = ans + " " + i + ". " + answerList.get(i);
		}

		return ans;
	}

	public void addBluffedAnswer(Player player, String bluffedAnswer) {
		synchronized (mapPlayersToBluffedAnswers) {
			mapPlayersToBluffedAnswers.replace(player, bluffedAnswer);
		}
		synchronized (answerList) {
			answerList.add(bluffedAnswer);
		}
	}

	public void addScoreToPlayer(Player player, int scoreToAdd) {
		if (mapPlayersToRoundScore.containsKey(player)) {
			int currentScore = mapPlayersToRoundScore.get(player);
			mapPlayersToRoundScore.replace(player, currentScore + scoreToAdd);
		} else {
			// ERROR - PLAYER NOT FOUND
		}
	}

	public int getScoreByPlayer(Player player) {
		int score = -1;
		if (mapPlayersToRoundScore.containsKey(player)) {
			score = mapPlayersToRoundScore.get(player);
		}
		return score;
	}

	public String getRealAnswer() {
		return realAnswer;
	}

	public String getQuestion() {
		return questionText;
	}

}
