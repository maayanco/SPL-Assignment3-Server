package bgu.spl.container;

import bgu.spl.server.passive.Message;
import bgu.spl.server.threadperclient.ProtocolCallback;

public interface Game {
	boolean processTxtResp(Message message, Player currentPlayer);
	void processSelectResp(Message message, Player currentPlayer);
	GameState getGameState();
	void setGameState(GameState gameState);
	void askQuestion();
	String getAllAnswers();
}
