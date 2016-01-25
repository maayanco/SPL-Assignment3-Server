package bgu.spl.server.passive;

import java.util.ArrayList;

import bgu.spl.server.tokenizer.Message;

public class StringMessage implements Message<StringMessage> {
	private boolean isValid = false;
	private Command command;
	private ArrayList<String> parameters = new ArrayList<String>();
	private int parameterLength;

	
	public StringMessage(String originalMessage) {
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
		
		if (strCommand.equals(Command.NICK.toString())) {
			command = Command.NICK;
		} else if (strCommand.equals(Command.JOIN.toString())) {
			command = Command.JOIN;
		} else if (strCommand.equals(Command.MSG.toString())) {
			command = Command.MSG;
		} else if (strCommand.equals(Command.LISTGAMES.toString())) {
			command = Command.LISTGAMES;
		} else if (strCommand.equals(Command.STARTGAME.toString())) {
			command = Command.STARTGAME;
		} else if (strCommand.equals(Command.TXTRESP.toString())) {
			command = Command.TXTRESP;
		} else if (strCommand.equals(Command.SELECTRESP.toString())) {
			command = Command.SELECTRESP;
		} else if (strCommand.equals(Command.QUIT.toString())) {
			command = Command.QUIT;
		} else if(strCommand.equals(Command.ASKTXT.toString())){
			command = Command.ASKTXT;
		} else if(strCommand.equals(Command.ASKCHOICES.toString())){
			command=Command.ASKCHOICES;
		} else if(strCommand.equals(Command.SYSMSG.toString())){
		command=Command.SYSMSG;
		} else if(strCommand.equals(Command.GAMEMSG.toString())){
			command=Command.GAMEMSG;
		} else if(strCommand.equals(Command.USRMSG.toString())){
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
