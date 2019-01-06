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
	
	ClientConnection(
	        SocketChannel socket,
	        MessageProcessor messageProcessor,
	        NonBlockingWritable nonBlockingWritable,
	        SelectableLog selectableLog) {
	    
	    super(selectableLog);
		
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
	int read(SelectionKey selectionKey, ByteBuffer buffer) throws IOException {
		
		if (!socket.keyFor(selectionKey.selector()).equals(selectionKey)) {
			throw new IllegalArgumentException();
		}
		
		return socket.read(buffer);
	}

	@Override
	boolean onChannelWriteable(SelectionKey selectionKey) throws IOException {
	    final boolean allDataWritten = nonBlockingWritable.onChannelWriteable(selectionKey);
	    
	    return allDataWritten;
	}
}
