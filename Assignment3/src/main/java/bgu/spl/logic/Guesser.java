package bgu.spl.logic;

import java.util.Map;
import java.util.Queue;

import bgu.spl.passive.StringMessage;

public class Guesser implements Game<StringMessage>{
	
	private Queue<Player> playersList;
	private int numberToGuess;
	/*private Map<Player, Integer> mapPlayersToScores;
	private Map<Player,Integer> mapPlayersToGuessedNumbers;
	*/
	public Guesser(Queue<Player> inputPlayersList){
		for (Player player : inputPlayersList) {
			this.playersList.add(player);
		}
	}
	
	@Override
	public String processTxtResp(StringMessage message, Player currentPlayer) {
		if(message.getParameter(0).equals(String.valueOf(numberToGuess))){
			currentPlayer.triggerCallback(new StringMessage("GAMEMSG Correct! you guessed right"));
		}
		return "";
	}

	@Override
	public void processSelectResp(StringMessage message, Player currentPlayer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendGameMsg(StringMessage message, Player currentPlayer) {
		currentPlayer.triggerCallback(message);
		
	}

	@Override
	public void processStartGame() {
		numberToGuess =(int)(Math.random() * (10 - 0) + 0);
		for(Player player : playersList){
			sendGameMsg(new StringMessage("GAMEMSG Guess the number between 0-10"),player);
		}
	}

}
