package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class NonBlockingChannelWriter implements NonBlockingWritable {

	private ByteBuffer writeBuffer;
	
	protected NonBlockingChannelWriter() {
	    this.writeBuffer = ByteBuffer.allocate(10000);
    }
	
	protected abstract SocketChannel getChannel(SelectionKey selectionKey, Selector selector);
	
	protected final void write(ByteOrder byteOrder, DataWriter writeData) {
        
        final byte [] data = DataWriter.writeToBuf(byteOrder, writeData);
        
        write(data, 0, data.length);
    }

	
	protected final void write(byte [] data, int offset, int length) {
		
	    System.out.println("## driver.write(" + data.length + "/" + length + ")");

        System.out.println("## driver.write() limit: " + writeBuffer.limit() + " remaining: " + writeBuffer.remaining() + " position: " + writeBuffer.position());

		final int spaceLeft = writeBuffer.remaining();
		
		System.out.println("length " + length + ", spaceLeft " + spaceLeft);
		
		if (length > spaceLeft) {
			final byte [] bytes = new byte[(writeBuffer.capacity() + (length - spaceLeft)) * 3];
			
			System.out.println("## get bytes " + writeBuffer.limit());
			
			writeBuffer.get(bytes);
			
			writeBuffer = ByteBuffer.wrap(bytes);
		}
		
		writeBuffer.put(data, offset, length);

		System.out.println("## driver.write() exit limit: " + writeBuffer.limit() + " remaining: " + writeBuffer.remaining() + " position: " + writeBuffer.position());
	}

	
	@Override
	public final void onWriteable(SelectionKey selectionKey, Selector selector) throws IOException {
		
		final SocketChannel channel = getChannel(selectionKey, selector);
		
		if (channel.isBlocking()) {
			throw new IllegalStateException();
		}
		
		writeBuffer.flip();
		
		System.out.println("## write " + writeBuffer.limit());
		
		final int bytesWritten = channel.write(writeBuffer);
		
		System.out.println("## bytesWritten: " + bytesWritten);
		
		writeBuffer.compact();
	}
}
