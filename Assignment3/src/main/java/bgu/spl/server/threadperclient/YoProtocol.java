package bgu.spl.server.threadperclient;

import java.io.IOException;

import bgu.spl.container.ContainerSingleton;
import bgu.spl.container.Player;
import bgu.spl.server.shared.AsyncServerProtocol;

public class YoProtocol implements AsyncServerProtocol {

	private boolean _shouldClose = false;

	public YoProtocol(Player player, ContainerSingleton container) {

	}

	@Override
	public void processMessage(Object msg, ProtocolCallback callback) {
		System.out.println("yyyy");
		System.out.println("the received message is: " + msg);

		try {
			callback.sendMessage("this is from the server");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean isEnd(Object msg) {
		return msg.equals("bye");
	}

	@Override
	public boolean shouldClose() {
		return _shouldClose;
	}

	@Override
	public void connectionTerminated() {
		_shouldClose = true;

	}

}
