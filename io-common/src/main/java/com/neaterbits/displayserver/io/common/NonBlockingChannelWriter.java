package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public abstract class NonBlockingChannelWriter implements NonBlockingWritable {

    private final NonBlockingChannelWriterLog log;
    
	private WriteBuffer writeBuffer;
	
	protected NonBlockingChannelWriter(NonBlockingChannelWriterLog log) {
	    
	    Objects.requireNonNull(log);
	    
	    this.log = log;
	    
	    this.writeBuffer = new WriteBuffer(10000);
    }
	
	protected abstract SocketChannel getChannel(SelectionKey selectionKey, Selector selector);
	
	protected final int write(ByteOrder byteOrder, DataWriter writeData) {
        
        final byte [] data = DataWriter.writeToBuf(byteOrder, writeData);
        
        write(data, 0, data.length);
        
        return data.length;
    }

	
	protected final void write(byte [] data, int offset, int length) {
	    
	    writeBuffer.write(data, offset, length, log);
	    
	}

	
	@Override
	public final void onWriteable(SelectionKey selectionKey, Selector selector) throws IOException {
		
	    
		final SocketChannel channel = getChannel(selectionKey, selector);
		
		if (channel.isBlocking()) {
			throw new IllegalStateException();
		}
		
		writeBuffer.onWriteable(channel::write, log);

	}
}
