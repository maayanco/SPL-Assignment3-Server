package bgu.spl.server.shared;

import java.io.IOException;

import bgu.spl.container.ContainerSingleton;
import bgu.spl.container.Player;
import bgu.spl.server.passive.ClientCommand;
import bgu.spl.server.passive.Message;
import bgu.spl.server.passive.Result;
import bgu.spl.server.passive.ServerCommand;
import bgu.spl.server.threadperclient.ProtocolCallback;

/** 
 * Protocol class - receives messages from the client and process them
 */
public class TBGPProtocol implements AsyncServerProtocol<String> {

	private Player player;
	private static ContainerSingleton container = ContainerSingleton.getInstance();
	private String TERMINATION_MESSAGE = "QUIT";
	private boolean shouldClose = false;

	/**
	 * Constructor
	 */
	public TBGPProtocol() {
		player = new Player();
	}

	@Override
	/**
	 * Receives a String and converts it to a Message type.
	 * It invokes the container proceesMessage method to handle the message,
	 * or terminates if the messag is a termination message
	 */
	public void processMessage(String msg, ProtocolCallback<String> callback) { 
		if (msg == null || !msg.getClass().equals(String.class)) {
			triggerCallback(ServerCommand.SYSMSG + " " + Result.UNIDENTIFIED, callback);
		} else {
			player.setCallback(callback);
			Message message = new Message(msg);
			if(isEnd(msg)){
				if(player.isCommandAccepted(ClientCommand.QUIT)){
					connectionTerminated();
					container.removePlayer(player);
				}
				else{
					triggerCallback(ServerCommand.SYSMSG + " " + Result.REJECTED, callback);
				}
			}
			else if (!message.isValid()) {
				triggerCallback(ServerCommand.SYSMSG + " " + Result.UNIDENTIFIED, callback);
			} else {
				container.processMessage(message, player);
			}
		}

	}

	@Override
	/**
	 * Returns the value of the field shouldClose,
	 * Used to check if the thread should terminate
	 */
	public boolean shouldClose() {
		return shouldClose;
	}

	@Override
	/**
	 * Sets the field ShouldClose to true to indicate gracefully
	 */
	public void connectionTerminated() {
		shouldClose = true;
	}

	/**
	 * Receives a msg and determines if this message is a termination message
	 * meaning sent by the client to indicate a termination of the client
	 */
	@Override
	public boolean isEnd(String message) {
		return message.equals(TERMINATION_MESSAGE);
	}

	/**
	 * 
	 * @param msg - the message that should be sent to the client
	 * @param callback - the callback that should be invoked
	 */
	private void triggerCallback(String msg, ProtocolCallback<String> callback) {
		try {
			callback.sendMessage(msg);
		} catch (IOException e) {
			System.out.println("An error has occured while invoking ProtocolCallback");
		}
	}
}
