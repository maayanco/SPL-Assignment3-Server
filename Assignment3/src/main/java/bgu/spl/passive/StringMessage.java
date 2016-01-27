package bgu.spl.passive;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import bgu.spl.server.tokenizer.Message;

/**
 * This class represents a message of type string and contains the command field, and a parameters arraylist
 * This class receives a string, splits it and sets the appropriate parts into the StringMessage fields: command, parametrs and also checks
 * the validity of the provided data. 
 *
 */
public class StringMessage implements Message<StringMessage> {
	private boolean isValid = false;
	private Command command;
	private ArrayList<String> parameters = new ArrayList<String>();
	private int parameterLength;
	private static final String NEW_LINE_DELIMITER="\n";
	private static final String CARRIAGE_RETURN_DELIMITER="\r";
	private static final String CARRIAGE_RETURN_NEW_LINE_DELIMITER="\r\n";
	private static final Logger DATA_LOGGER = Logger.getLogger("StringMessage");
	
	/**
	 * Constructor - receives a string, deletes all newLine delimiters.
	 * Splits the string into an array and sets the command and the parameters
	 * @param originalMessage
	 */
	public StringMessage(String originalMessage) {
		originalMessage=originalMessage.replaceAll(CARRIAGE_RETURN_DELIMITER, "");
		originalMessage=originalMessage.replaceAll(CARRIAGE_RETURN_NEW_LINE_DELIMITER, "");
		originalMessage=originalMessage.replaceAll(NEW_LINE_DELIMITER, "");
		if(originalMessage==null){
			return;
		}
		
		// Check if string is valid
		String[] splitedMessageArr = originalMessage.split(" ");

		if (splitedMessageArr.length < 1) {
			DATA_LOGGER.log(Level.INFO,"Received Illegal Command");
			return;
		}

		this.isValid = true;

		parameters = new ArrayList<String>();

		setCommand(splitedMessageArr[0]);

		for (int i = 1; i < splitedMessageArr.length; i++) {
			parameters.add(splitedMessageArr[i]);
		}

		parameterLength = parameters.size();

	}
	
	
	/**
	 * @return the validity status of the message
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Receives a string representing a command and checks if that is an acceptable Command type
	 * if not - Sets validity as false
	 * @param strCommand - string representing the requested command
	 */
	private void setCommand(String strCommand) {
		if (strCommand.equalsIgnoreCase(Command.NICK.toString())) {
			command = Command.NICK;
		} else if (strCommand.equalsIgnoreCase(Command.JOIN.toString())) {
			command = Command.JOIN;
		} else if (strCommand.equalsIgnoreCase(Command.MSG.toString())) {
			command = Command.MSG;
		} else if (strCommand.equalsIgnoreCase(Command.LISTGAMES.toString())) {
			command = Command.LISTGAMES;
		} else if (strCommand.equalsIgnoreCase(Command.STARTGAME.toString())) {
			command = Command.STARTGAME;
		} else if (strCommand.equalsIgnoreCase(Command.TXTRESP.toString())) {
			command = Command.TXTRESP;
		} else if (strCommand.equalsIgnoreCase(Command.SELECTRESP.toString())) {
			command = Command.SELECTRESP;
		} else if (strCommand.equalsIgnoreCase(Command.QUIT.toString())) {
			command = Command.QUIT;
		} else if(strCommand.equalsIgnoreCase(Command.ASKTXT.toString())){
			command = Command.ASKTXT;
		} else if(strCommand.equalsIgnoreCase(Command.ASKCHOICES.toString())){
			command=Command.ASKCHOICES;
		} else if(strCommand.equalsIgnoreCase(Command.SYSMSG.toString())){
		command=Command.SYSMSG;
		} else if(strCommand.equalsIgnoreCase(Command.GAMEMSG.toString())){
			command=Command.GAMEMSG;
		} else if(strCommand.equalsIgnoreCase(Command.USRMSG.toString())){
			command=Command.USRMSG;
		}else {
			isValid = false;
		}
	}

	/**
	 * @return the command 
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * @param index the index of the parameter to be returned
	 * @return the parameter at the provided index
	 */
	public String getParameter(int index) {
		if (index < parameters.size()) {
			return parameters.get(index);
		}
		return null;
	}

	/**
	 * @return the length of the parameters ArrayList
	 */
	public int getParameterLength() {
		return parameterLength;
	}

	/**
	 * @return all the parameters
	 */
	public String getParameters() {
		String parametersStr = "";
		for (int i = 0; i < parameters.size(); i++) {
			parametersStr +=parameters.get(i)+" ";
		}
		return parametersStr;

	}
	
	/**
	 * @return the command and parameters
	 */
	public String getMessage() {
		return getCommand()+" "+getParameters();
	}

	
}
