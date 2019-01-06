package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.neaterbits.displayserver.io.common.MessageProcessor;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriter;
import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.io.common.Selectable;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Request;

abstract class XWindowsChannelReaderWriter
	extends NonBlockingChannelWriter
	implements AutoCloseable, MessageProcessor, Selectable {
	
    private final int port;
    
	private SocketChannel socketChannel;
	
	private SelectionKey selectionKey;

	protected abstract boolean receivedInitialMessage();
	
	XWindowsChannelReaderWriter(int port, NonBlockingChannelWriterLog log) {
	    super(log);

        this.port = port;
    }

    @Override
	public Set<SelectionKey> register(SelectorProvider selectorProvider, Selector selector) throws IOException {
		
		final Set<SelectionKey> set = new HashSet<>();
		
		if (socketChannel != null) {
			throw new IllegalStateException();
		}

		this.socketChannel = selectorProvider.openSocketChannel();
				
		if (!socketChannel.connect(new InetSocketAddress("127.0.0.1", port))) {
			throw new IOException("Failed to connect");
		}

		socketChannel.configureBlocking(false);
		
		this.selectionKey = socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
		
		if (this.selectionKey == null) {
		    throw new IllegalStateException();
		}
		
		set.add(selectionKey);
		
		return set;
	}
	
	@Override
	public int read(SelectionKey selectionKey, ByteBuffer buffer) throws IOException {

	    if (this.selectionKey != selectionKey) {
	        throw new IllegalArgumentException();
	    }
	    
	    final SocketChannel channel = getChannel();
		final int bytesRead = channel.read(buffer);
		
		return bytesRead;
	}
	
	@Override
	public void unregister(SelectorProvider selectorProvider, Selector selector) {
		
	}
	
	@Override
	protected final SocketChannel getChannel() {
		return socketChannel;
	}
	
	@Override
    protected final SelectionKey getSelectionKey() {
        return selectionKey;
    }

    public void close() throws Exception {
		socketChannel.close();
	}
	
	int writeRequest(Request request, ByteOrder byteOrder) throws IOException {
	    return writeEncodeable(request, byteOrder);
	}
	
	
	int writeEncodeable(Encodeable encodeable, ByteOrder byteOrder) throws IOException {
		
		Objects.requireNonNull(encodeable);
		
		return writeToOutputBuffer(byteOrder, Encodeable.makeDataWriter(encodeable));
	}
}
