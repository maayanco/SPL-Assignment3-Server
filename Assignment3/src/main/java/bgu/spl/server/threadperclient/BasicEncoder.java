package bgu.spl.server.threadperclient;

import java.nio.charset.Charset;

public class BasicEncoder implements Encoder{
	private Charset _charset;
	
	public BasicEncoder(String charsetDesc){
		_charset = Charset.forName(charsetDesc);
	}
	
	public byte[] toBytes(String str){
		return str.getBytes(_charset);
	}
	
	public String fromBytes(byte[] buf){
		return new String(buf,0,buf.length,_charset);
	}
	
	public Charset getCharset(){
		return _charset;
	}
}
