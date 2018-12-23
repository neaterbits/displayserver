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

            System.out.println("## remaining " + writeBuffer.remaining() + ", capacity " + writeBuffer.capacity() + "/" + newLength);
            
            final byte [] bytes = new byte[newLength];

            System.out.println("writing bytes " + bytes.length);
            
            writeBuffer.flip();
            
            final int bytesInBuffer = writeBuffer.remaining();
            
            if (log != null) {
                log.onQueueWriteResize(data.length, offset, length, writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
            }

            System.out.println("writing bytes " + length + " to " + bytes.length + "/"
                    + writeBuffer.limit() + "/" + writeBuffer.remaining() + "/" + writeBuffer.position());

            System.out.println("## getting " + bytesInBuffer + "/" + writeBuffer.position());
            
            if (bytesInBuffer != 0) {
                writeBuffer.get(bytes, writeBuffer.position(), bytesInBuffer);
            }
            
            writeBuffer = ByteBuffer.wrap(bytes, 0, bytesInBuffer);

            System.out.println("after wrap " + 
                    + writeBuffer.limit() + "/" + writeBuffer.remaining() + "/" + writeBuffer.position() + "/" + writeBuffer.capacity());
            
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
    
    int onWriteable(Writer onWrite, NonBlockingChannelWriterLog log) throws IOException {
        
        writeBuffer.flip();
        
        if (log != null) {
            log.onChannelWriteEnter(writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
        }

        final int bytesWritten = onWrite.write(writeBuffer);

        writeBuffer.compact();

        if (log != null) {
            log.onChannelWriteExit(bytesWritten, writeBuffer.limit(), writeBuffer.remaining(), writeBuffer.position());
        }

        return bytesWritten;
    }
}
