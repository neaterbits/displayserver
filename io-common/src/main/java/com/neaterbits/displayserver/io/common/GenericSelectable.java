package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Objects;

final class GenericSelectable extends BaseSelectable {

	private final String name;
	private final Selectable selectable;
	private final MessageProcessor messageProcessor;
	
	public GenericSelectable(String name, Selectable selectable, MessageProcessor messageProcessor) {
		
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
	int read(SelectionKey selectionKey, Selector selector, ByteBuffer buffer) throws IOException {
		
		return selectable.read(selectionKey, selector, buffer);
	}

	@Override
	void onWriteable(SelectionKey selectionKey, Selector selector) throws IOException {
		selectable.onWriteable(selectionKey, selector);
	}
}
