package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
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
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.messages.Request;

abstract class XWindowsChannelReaderWriter
	extends NonBlockingChannelWriter
	implements AutoCloseable, MessageProcessor, Selectable {
	
    private final int port;
    
	private SocketChannel socketChannel;

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
		
		final SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
		
		set.add(selectionKey);
		
		return set;
	}
	
	@Override
	public int read(SelectionKey selectionKey, Selector selector, ByteBuffer buffer) throws IOException {

	    final SocketChannel channel = getChannel(selectionKey, selector);
		final int bytesRead = channel.read(buffer);
		
		return bytesRead;
	}
	
	@Override
	public void unregister(SelectorProvider selectorProvider, Selector selector) {
		
	}
	
	@Override
	protected SocketChannel getChannel(SelectionKey selectionKey, Selector selector) {
		if (!socketChannel.keyFor(selector).equals(selectionKey)) {
			throw new IllegalArgumentException();
		}

		return socketChannel;
	}

	@Override
	public Integer getLengthOfMessage(ByteBuffer byteBuffer) {

	    final Integer length;
	    
	    if (receivedInitialMessage()) {
	        length = XWindowsProtocolUtil.getMessageLength(byteBuffer);
	    }
	    else {
	        final byte initialByte = byteBuffer.get(byteBuffer.position());
	        
	        if (initialByte == 0) {
	            
	            if (byteBuffer.remaining() > 1) {
	                final int reasonLength = byteBuffer.get(byteBuffer.position() + 1);
	            
	                final int completeLength = 8 + reasonLength + XWindowsProtocolUtil.getPadding(reasonLength);
	                
	                length = completeLength <= byteBuffer.remaining() ? completeLength : null;
	            }
	            else {
	                length = null;
	            }
	        }
	        else if (initialByte == 1) {
	            
	            if (byteBuffer.remaining() >= 8) {
	            
    	            // initial server message
    	            final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
    	            
    	            final int additionalDataLength = shortBuffer.get(shortBuffer.position() + 3);
    	            
    	            final int totalLength = 8 + additionalDataLength * 4;
    	            
    	            if (totalLength <= byteBuffer.remaining()) {
    	                length = totalLength;
    	            }
    	            else {
    	                length = null;
    	            }
	            }
	            else {
	                length = null;
	            }
	        }
	        else {
	            throw new UnsupportedOperationException("initialByte: " + initialByte);
	        }
	    }
	    
	    return length;
	}

	public void close() throws Exception {
		socketChannel.close();
	}
	
	int writeRequest(Request request, ByteOrder byteOrder) throws IOException {
	    return writeEncodeable(request, byteOrder);
	}
	
	
	int writeEncodeable(Encodeable encodeable, ByteOrder byteOrder) throws IOException {
		
		Objects.requireNonNull(encodeable);
		
		return write(byteOrder, Encodeable.makeDataWriter(encodeable));
	}
}
