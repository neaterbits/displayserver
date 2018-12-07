package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

abstract class BaseSelectable {

    private final SelectableLog log;
	private ByteBuffer readBuffer;

	abstract MessageProcessor getMessageProcessor();
	
	abstract int read(SelectionKey selectionKey, Selector selector, ByteBuffer buffer) throws IOException;
	
	abstract void onWriteable(SelectionKey selectionKey, Selector selector) throws IOException;

	
	BaseSelectable(SelectableLog log) {
	    
	    this.log = log;
	    
		this.readBuffer = ByteBuffer.allocate(500);
	}
	
	final void readAndProcess(SelectionKey selectionKey, Selector selector) throws IOException {
		
		for (;;) {

		    if (log != null) {
		        log.onTryReadEnter(readBuffer.limit(), readBuffer.remaining(), readBuffer.position());
		    }

			final int bytesRead = read(selectionKey, selector, readBuffer);

			if (bytesRead > 0) {

			    if (log != null) {
		            log.onBeforeFlipBeforeProcessingOneMessage(bytesRead, readBuffer.limit(), readBuffer.remaining(), readBuffer.position());
                }

			    readBuffer.flip();

	            if (log != null) {
	                log.onAfterFlipBeforeProcessingOneMessage(bytesRead, readBuffer.limit(), readBuffer.remaining(), readBuffer.position());
	            }

				processAnyCompleteMessages(getMessageProcessor(), readBuffer);
				
				if (log != null) {
				    log.onAfterProcessedOneMessage(readBuffer.limit(), readBuffer.remaining(), readBuffer.position());
				}
				
				// buffer.flip();
				readBuffer.compact();

                if (log != null) {
                    log.onAfterProcessedOneMessageFlip(readBuffer.limit(), readBuffer.remaining(), readBuffer.position());
                }
			}
			else if (bytesRead == -1) {
			    selectionKey.cancel();
			    selectionKey.channel().close();
			}
			
			if (!readBuffer.hasRemaining()) {
				final byte [] bytes = new byte[readBuffer.capacity() * 3];
				
				readBuffer.flip();
				
				final int toCopy = readBuffer.remaining();
				
				final ByteOrder byteOrder = readBuffer.order();
				
				readBuffer.get(bytes, readBuffer.position(), toCopy);

				readBuffer = ByteBuffer.wrap(bytes, 0, toCopy);

				readBuffer.order(byteOrder);
				
				// buffer.compact();
				
				if (log != null) {
				    log.onBufferReallocated(readBuffer.limit(), readBuffer.remaining(), readBuffer.position());
				}
				
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
    			
    			if (log != null) {
    			    log.onProcessedCompleteMessage(messageLength, readBuffer.limit(), readBuffer.remaining(), readBuffer.position());
    			}
    		}
    		else {
    		    break;
    		}
	    } while (buffer.remaining() > 0);
	}
}
