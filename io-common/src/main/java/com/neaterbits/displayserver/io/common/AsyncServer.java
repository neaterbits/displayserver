package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

final class AsyncServer implements AutoCloseable {
	private final String name;
	
	final Function<SocketChannel, Client> onClientConnect;
	
	final ServerSocketChannel [] socketChannels;

	private final List<ClientConnection> clients;
	
	AsyncServer(
			String name,
			Function<SocketChannel, Client> onClientConnect,
			ServerSocketChannel [] socketChannels) throws IOException {
		
	    Objects.requireNonNull(onClientConnect);
	    
		this.name = name;
		this.onClientConnect = onClientConnect;
		this.socketChannels = socketChannels;
		
		this.clients = new ArrayList<>();
	}
	
	void addClient(ClientConnection client) {
		
		Objects.requireNonNull(client);
	
		clients.add(client);
	}
	

	@Override
	public void close() throws Exception {
		for (ServerSocketChannel socketChannel : socketChannels) {
			try {
				socketChannel.close();
			}
			catch (Exception ex) {
				
			}
		}
	}

	@Override
	public String toString() {
		return "Server [name=" + name + "]";
	}
}
