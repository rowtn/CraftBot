package in.parapengu.craftbot.protocol.v4;

import in.parapengu.craftbot.event.EventHandler;
import in.parapengu.craftbot.event.bot.connection.BotConnectServerEvent;
import in.parapengu.craftbot.protocol.stream.PacketInputStream;
import in.parapengu.craftbot.protocol.stream.PacketOutputStream;

public class BotListener {

	private PacketInputStream input;
	private PacketOutputStream output;

	public BotListener(PacketInputStream input, PacketOutputStream output) {
		this.input = input;
		this.output = output;
	}

	@EventHandler
	public void onConnect(BotConnectServerEvent event) {
		
	}

}
