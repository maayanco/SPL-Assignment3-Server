package bgu.spl.container;

/*import bgu.spl.server.passive.StringMessage;*/

public interface Game<T> {
	String processTxtResp(T message, Player currentPlayer);

	void processSelectResp(T message, Player currentPlayer);

	void sendGameMsg(T message, Player currentPlayer);

	void processStartGame();

}
