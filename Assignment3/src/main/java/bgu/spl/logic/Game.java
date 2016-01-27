package bgu.spl.logic;

/**
 * Interface that represents a Game type 
 */
public interface Game<T> {
	
	String processTxtResp(T message, Player currentPlayer);

	void processSelectResp(T message, Player currentPlayer);

	void sendGameMsg(T message, Player currentPlayer);

	void processStartGame();

}
