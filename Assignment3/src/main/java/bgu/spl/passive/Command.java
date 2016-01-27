package bgu.spl.passive;

/**
 * Represents a Command type, contains all the commands that the server or client can send/receive 
 */
public enum Command {
	NICK, JOIN, MSG, LISTGAMES, STARTGAME, TXTRESP, SELECTRESP, QUIT, ASKTXT, ASKCHOICES, SYSMSG, GAMEMSG, USRMSG;
}
