package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class NonBlockingChannelWriter implements NonBlockingWritable {

    private final NonBlockingChannelWriterLog log;
    
	private ByteBuffer writeBuffer;
	
	protected NonBlockingChannelWriter(NonBlockingChannelWriterLog log) {
	    
	    this.log = log;
	    
	    this.writeBuffer = ByteBuffer.allocate(10000);
    }
	
	protected abstract SocketChannel getChannel(SelectionKey selectionKey, Selector selector);
	
	protected final void write(ByteOrder byteOrder, DataWriter writeData) {
        
        final byte [] data = DataWriter.writeToBuf(byteOrder, writeData);
        
        write(data, 0, data.length);
    }

	
	protected final void write(byte [] data, int offset, int length) {
	    
	    if (log != null) {
	        log.onQueueWriteEnter(data.length, offset, length, writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
	    }
		
		final int spaceLeft = writeBuffer.remaining();
		
		if (length > spaceLeft) {
			final byte [] bytes = new byte[(writeBuffer.capacity() + (length - spaceLeft)) * 3];
			
			writeBuffer.get(bytes);
			
			writeBuffer = ByteBuffer.wrap(bytes);
		}
		
		writeBuffer.put(data, offset, length);

		if (log != null) {
            log.onQueueWriteExit(writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
		}
	}

	
	@Override
	public final void onWriteable(SelectionKey selectionKey, Selector selector) throws IOException {
		
		final SocketChannel channel = getChannel(selectionKey, selector);
		
		if (channel.isBlocking()) {
			throw new IllegalStateException();
		}
		
		writeBuffer.flip();

		if (log != null) {
            log.onChannelWriteEnter(writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
        }

		final int bytesWritten = channel.write(writeBuffer);

		if (log != null) {
            log.onChannelWriteExit(bytesWritten, writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
        }
		
		writeBuffer.compact();
	}
}
