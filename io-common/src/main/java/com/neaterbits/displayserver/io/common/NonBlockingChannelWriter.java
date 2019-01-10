package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
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
	
	protected abstract SocketChannel getChannel();
	
	protected abstract SelectionKey getSelectionKey();
	
	public final int writeToOutputBuffer(ByteOrder byteOrder, DataWriter writeData) {
        
        final byte [] data = DataWriter.writeToBuf(writeData, byteOrder);
     
        return writeToOutputBuffer(data);
	}

    public final int writeToOutputBuffer(byte [] data) {
        
        writeToOutputBuffer(data, 0, data.length);

        if (data.length > 0) {
            
            // non blocking write so register interest in writes
            final SelectionKey selectionKey = getSelectionKey();
            
            if (selectionKey != null) {
                if ((selectionKey.interestOps() & SelectionKey.OP_WRITE) == 0) {
                    selectionKey.interestOps(selectionKey.interestOps() | SelectionKey.OP_WRITE);
                }
            }
        }
        
        return data.length;
    }

	
	private final void writeToOutputBuffer(byte [] data, int offset, int length) {
	    
	    writeBuffer.write(data, offset, length, log);
	    
	}

	@Override
	public final boolean onChannelWriteable(SelectionKey selectionKey) throws IOException {

	    final SocketChannel channel = getChannel();
		
		if (channel.isBlocking()) {
			throw new IllegalStateException();
		}
		
		final boolean allDataWritten = writeBuffer.onChannelWriteable(channel::write, log);
		
        if (allDataWritten) {
            // Remove writeability until more data needs to be written
            selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
        }
        
        return allDataWritten;
	}
}
