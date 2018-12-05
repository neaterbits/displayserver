package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Objects;

final class ClientConnection extends BaseSelectable {

	private final SocketChannel socket;
	private final MessageProcessor messageProcessor;
	private final NonBlockingWritable nonBlockingWritable;
	
	ClientConnection(SocketChannel socket, MessageProcessor messageProcessor, NonBlockingWritable nonBlockingWritable) {
		
		Objects.requireNonNull(socket);
		Objects.requireNonNull(messageProcessor);
		Objects.requireNonNull(nonBlockingWritable);
		
		this.socket = socket;
		this.messageProcessor = messageProcessor;
		this.nonBlockingWritable = nonBlockingWritable;
	}

	@Override
	MessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	@Override
	int read(SelectionKey selectionKey, Selector selector, ByteBuffer buffer) throws IOException {
		
		if (!socket.keyFor(selector).equals(selectionKey)) {
			throw new IllegalArgumentException();
		}
		
		return socket.read(buffer);
	}

	@Override
	void onWriteable(SelectionKey selectionKey, Selector selector) throws IOException {
	    nonBlockingWritable.onWriteable(selectionKey, selector);
	}
}
