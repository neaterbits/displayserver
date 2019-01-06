package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Objects;

final class GenericSelectable extends BaseSelectable {

	private final String name;
	private final Selectable selectable;
	private final MessageProcessor messageProcessor;
	
	public GenericSelectable(String name, Selectable selectable, MessageProcessor messageProcessor, SelectableLog log) {
		
	    super(log);
	    
		Objects.requireNonNull(selectable);
		
		this.name = name;
		this.selectable = selectable;
		this.messageProcessor = messageProcessor;
	}

	String getName() {
		return name;
	}

	@Override
	MessageProcessor getMessageProcessor() {
		return messageProcessor;
	}

	@Override
	int read(SelectionKey selectionKey, ByteBuffer buffer) throws IOException {
	    
		return selectable.read(selectionKey, buffer);
	}

	@Override
	boolean onChannelWriteable(SelectionKey selectionKey) throws IOException {
		return selectable.onChannelWriteable(selectionKey);
	}
}
