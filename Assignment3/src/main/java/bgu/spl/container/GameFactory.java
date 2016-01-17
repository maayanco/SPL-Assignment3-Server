package bgu.spl.container;

import java.util.Queue;

public class GameFactory {

	private String[] gamesList = { "Bluffer" };

	Game create(Queue<Player> playersList) {
		/* if() */
		return new Bluffer(playersList);
	}
}
