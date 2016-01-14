package bgu.spl.server.threadperclient;

import java.io.IOException;
import java.io.InputStreamReader;

public class MessageTokenizer implements Tokenizer{
	private final char _delimiter='\n';
	private InputStreamReader _ist;
	private boolean _closed;
	
	public MessageTokenizer(InputStreamReader _ist){
		this._ist=_ist;
	}
	
	public boolean isAlive(){
		return !_closed;
	}
	
	public String nextToken() throws IOException{
		if(!isAlive()){
			throw new IOException();
		}
		String ans=null;
		
		try{
			int c;
			StringBuffer sb = new StringBuffer();
			while((c=_ist.read())!=-1){
				if(c==_delimiter){
					break;
				}
				else{
					sb.append((char)c);
				}
				ans=sb.toString();
			}
		}
		catch(IOException e){
			_closed=true;
			throw e;
		}
		return ans;
	}
}
