package bgu.spl.server.protocol;

import java.io.IOException;

import bgu.spl.container.ContainerSingleton;
import bgu.spl.container.Player;
import bgu.spl.server.passive.Command;
import bgu.spl.server.passive.StringMessage;
import bgu.spl.server.passive.Result;
import bgu.spl.server.threadperclient.ProtocolCallback;

/** 
 * Protocol class - receives messages from the client and process them
 */
public class TBGPProtocol implements AsyncServerProtocol<StringMessage> {

	private Player player;
	private static ContainerSingleton container = ContainerSingleton.getInstance();
	private final static String TERMINATION_MESSAGE = "QUIT";
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
	public void processMessage(StringMessage msg, ProtocolCallback<StringMessage> callback) { 
		if (msg == null || msg.getCommand()==null) {
			triggerCallback(Command.SYSMSG + " " + Result.UNIDENTIFIED, callback);
		} else {
			player.setCallback(callback);
			if(isEnd(msg)){
				if(container.handleQuit(player)){
					//connectionTerminated(); //should be returned!
					triggerCallback(Command.SYSMSG +" "+ msg.getCommand()+" "+ Result.ACCEPTED, callback);
				}
				else{
					triggerCallback(Command.SYSMSG +" "+ msg.getCommand()+" "+ Result.REJECTED, callback);
				}
			}
			else if (!msg.isValid()) {
				triggerCallback(Command.SYSMSG + " " + Result.UNIDENTIFIED, callback);
			} else {
				container.processMessage(msg, player);
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
	public boolean isEnd(StringMessage message) {
		//return message.equals(TERMINATION_MESSAGE);
		return TERMINATION_MESSAGE.equals(message.getCommand().toString());
	}

	/**
	 * 
	 * @param msg - the message that should be sent to the client
	 * @param callback - the callback that should be invoked
	 */
	private void triggerCallback(String msg, ProtocolCallback<StringMessage> callback) {
		try {
			StringMessage message = new StringMessage(msg);
			callback.sendMessage(message);
		} catch (IOException e) {
			System.out.println("An error has occured while invoking ProtocolCallback");
		}
	}
	
	
	
}
