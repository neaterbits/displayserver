package com.neaterbits.displayserver.io.common;

import java.io.IOException;
import java.nio.ByteBuffer;

final class WriteBuffer {

    private ByteBuffer writeBuffer;
    
    protected WriteBuffer(int initialSize) {
        this.writeBuffer = ByteBuffer.allocate(initialSize);
    }
    
    void write(byte [] data, int offset, int length, NonBlockingChannelWriterLog log) {
        
        if (log != null) {
            log.onQueueWriteEnter(data.length, offset, length, writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
        }

        final int spaceLeft = writeBuffer.remaining();
        
        if (length > spaceLeft) {
            
            final int newLength = (writeBuffer.capacity() + (length - spaceLeft)) * 3;

            final byte [] bytes = new byte[newLength];

            writeBuffer.flip();
            
            final int bytesInBuffer = writeBuffer.remaining();
            
            if (log != null) {
                log.onQueueWriteResize(data.length, offset, length, writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
            }

            if (bytesInBuffer != 0) {
                writeBuffer.get(bytes, writeBuffer.position(), bytesInBuffer);
            }
            
            writeBuffer = ByteBuffer.wrap(bytes, 0, bytesInBuffer);

            writeBuffer.compact();
        }
        
        writeBuffer.put(data, offset, length);

        if (log != null) {
            log.onQueueWriteExit(writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
        }
    }
    
    @FunctionalInterface
    interface Writer {
        int write(ByteBuffer byteBuffer) throws IOException;
    }
    
    boolean onChannelWriteable(Writer onWrite, NonBlockingChannelWriterLog log) throws IOException {
        
        writeBuffer.flip();
    
        if (log != null) {
            log.onChannelWriteEnter(writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
        }

        final int bytesWritten;
        
        final boolean allDataWritten;
        
        if (writeBuffer.hasRemaining()) {
            bytesWritten = onWrite.write(writeBuffer);
            
            allDataWritten = writeBuffer.hasRemaining();
        }
        else {
            allDataWritten = true;
            bytesWritten = 0;
        }
    
        writeBuffer.compact();

        if (log != null) {
            log.onChannelWriteExit(bytesWritten, writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
        }

        return allDataWritten;
    }
}
