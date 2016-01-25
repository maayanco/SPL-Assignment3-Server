package bgu.spl.server.passive;

import java.util.ArrayList;

import bgu.spl.server.tokenizer.Message;

public class StringMessage implements Message<StringMessage> {
	private boolean isValid = false;
	private Command command;
	private ArrayList<String> parameters = new ArrayList<String>();
	private int parameterLength;

	
	public StringMessage(String originalMessage) {
		originalMessage = originalMessage.replaceAll("(\r|\r\n|\n)", "");
		if(originalMessage==null){
			return;
		}
		
		// Check if string is valid
		String[] splitedMessageArr = originalMessage.split(" ");

		if (splitedMessageArr.length < 1) {
			System.out.println("Received Illegal Command");
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
	
	
	public boolean isValid() {
		return isValid;
	}

	private void setCommand(String strCommand) {
		//strCommand.replaceAll("(\r\n|\n|\r)", "");
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

	public Command getCommand() {
		return command;
	}

	public String getParameter(int index) {
		if (index < parameters.size()) {
			return parameters.get(index);
		}
		return null;
	}

	public int getParameterLength() {
		return parameterLength;
	}

	public String getParameters() {
		String parametersStr = "";
		for (int i = 0; i < parameters.size(); i++) {
			parametersStr +=parameters.get(i)+" ";
		}
		return parametersStr;

	}
	
	public String getMessage() {
		return getCommand()+" "+getParameters();
	}

	
}
