package bgu.spl.server.passive;
import java.util.ArrayList;
import java.util.LinkedList;

public class Message {
	
	private boolean isValid=false;
	private ClientCommand command;
	private ArrayList<String> parameters = new ArrayList<String>();
	
	public Message(String originalMessage){
		//Check if string is valid
		String[] splitedMessageArr = originalMessage.split(" ");
		
		if(splitedMessageArr.length<1){
			System.out.println("Received Illegal Command");
			return;
		}
		
		this.isValid=true;
		
		parameters = new ArrayList<String>();
		
		setCommand(splitedMessageArr[0]);
		

		for(int i=1; i<splitedMessageArr.length; i++){
			parameters.add(splitedMessageArr[i]);
		}

		
	}
	
	public boolean isValid(){
		return isValid;
	}
	
	
	private void setCommand(String strCommand){
		if(strCommand.equals(ClientCommand.NICK.toString())){
			command=ClientCommand.NICK;
		}
		else if(strCommand.equals(ClientCommand.JOIN.toString())){
			command=ClientCommand.JOIN;
		}
		else if(strCommand.equals(ClientCommand.MSG.toString())){
			command=ClientCommand.MSG;
		}
		else if(strCommand.equals(ClientCommand.LISTGAMES.toString())){
			command=ClientCommand.LISTGAMES;
		}
		else if(strCommand.equals(ClientCommand.STARTGAME.toString())){
			command=ClientCommand.STARTGAME;
		}
		else if(strCommand.equals(ClientCommand.TXTRESP.toString())){
			command=ClientCommand.TXTRESP;
		}
		else if(strCommand.equals(ClientCommand.SELECTRESP.toString())){
			command=ClientCommand.SELECTRESP;
		}
		else if(strCommand.equals(ClientCommand.QUIT.toString())){
			command=ClientCommand.QUIT;
		}
		else{
			isValid=false;
		}
	}
	
	public ClientCommand getCommand(){
		return command;
	}
	
	public String getParameter(int index){
		if(index<parameters.size()){
			return parameters.get(index);
		}
		return null;
	}
	
}