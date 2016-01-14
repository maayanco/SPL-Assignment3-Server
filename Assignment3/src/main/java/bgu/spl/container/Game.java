package bgu.spl.container;

import bgu.spl.server.passive.Message;
import bgu.spl.server.threadperclient.ProtocolCallback;

public interface Game {
	void processTxtResp(Message message, ProtocolCallback callback, Player currentPlayer);
	void processSelectResp(Message message, ProtocolCallback callback, Player currentPlayer);
	GameState getGameState();
	void setGameState(GameState gameState);
	void askQuestion();
}
