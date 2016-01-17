package bgu.spl.container;

import bgu.spl.server.passive.Message;

public interface Game {
	String processTxtResp(Message message, Player currentPlayer);

	void processSelectResp(Message message, Player currentPlayer);

	void sendGameMsg(String message, Player currentPlayer);

	void processStartGame();

}
