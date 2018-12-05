package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

abstract class BaseSelectable {

	private ByteBuffer readBuffer;

	abstract MessageProcessor getMessageProcessor();
	
	abstract int read(SelectionKey selectionKey, Selector selector, ByteBuffer buffer) throws IOException;
	
	abstract void onWriteable(SelectionKey selectionKey, Selector selector) throws IOException;

	
	BaseSelectable() {
		this.readBuffer = ByteBuffer.allocate(500);
		
	}
	
	final void readAndProcess(SelectionKey selectionKey, Selector selector) throws IOException {
		
		for (;;) {

		    System.out.println("## driver.readAndProcess() limit: " + readBuffer.limit() + " remaining: " + readBuffer.remaining() + " position: " + readBuffer.position());

			final int bytesRead = read(selectionKey, selector, readBuffer);

			if (bytesRead > 0) {
			    
			    readBuffer.flip();

		        System.out.println("## driver.readAndProcess() after flip, before message processing limit: " + readBuffer.limit() + " remaining: " + readBuffer.remaining() + " position: " + readBuffer.position());

				processAnyCompleteMessages(getMessageProcessor(), readBuffer);
				
                System.out.println("## driver.readAndProcess() after message processing limit: " + readBuffer.limit() + " remaining: " + readBuffer.remaining() + " position: " + readBuffer.position());

				// buffer.flip();
				readBuffer.compact();
				
                System.out.println("## driver.readAndProcess() after compact limit: " + readBuffer.limit() + " remaining: " + readBuffer.remaining() + " position: " + readBuffer.position());
			}
			else if (bytesRead == -1) {
			    selectionKey.cancel();
			    selectionKey.channel().close();
			}
			
			if (!readBuffer.hasRemaining()) {
				final byte [] bytes = new byte[readBuffer.capacity() * 3];
				
				readBuffer.flip();
				
				readBuffer.get(bytes, readBuffer.position(), readBuffer.remaining());
				
				readBuffer = ByteBuffer.wrap(bytes, 0, readBuffer.capacity());
				
				// buffer.compact();
				
				System.out.println("## new buffer: " + readBuffer.remaining());
				
				readBuffer.compact();
				
			}
			else {
			    break;
			}
		}
	}
	
	private void processAnyCompleteMessages(MessageProcessor messageProcessor, ByteBuffer buffer) {

	    do {
    		final Integer messageLength = messageProcessor.getLengthOfMessage(buffer);
    		
    		if (messageLength != null) {
    		    
    		    if (messageLength > buffer.remaining()) {
    		        throw new IllegalStateException();
    		    }
    		    
    			messageProcessor.onMessage(buffer, messageLength);

                System.out.println("## driver.processAnyCompleteMessages() after message processing limit: " + readBuffer.limit() + " remaining: " + readBuffer.remaining() + " position: " + readBuffer.position());
    		}
    		else {
    		    break;
    		}
	    } while (buffer.remaining() > 0);
	}
}
