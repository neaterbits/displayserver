package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

public interface Selectable extends NonBlockingWritable {

	Set<SelectionKey> register(SelectorProvider selectorProvider, Selector selector) throws IOException;
	
	int read(SelectionKey selectionKey, ByteBuffer buffer) throws IOException;

	void unregister(SelectorProvider selectorProvider, Selector selector);
	
}
