package com.neaterbits.displayserver.io.common;

public interface NonBlockingChannelWriterLog {

    void onQueueWriteEnter(int dataLength, int offset, int length, int writeBufferLimit, int writeBufferRemaining, int writeBufferPosition);

    void onQueueWriteResize(int dataLength, int offset, int length, int writeBufferLimit, int writeBufferRemaining, int writeBufferPosition);

    void onQueueWriteExit(int writeBufferLimit, int writeBufferRemaining, int writeBufferPosition);

    void onChannelWriteEnter(int writeBufferLimit, int writeBufferRemaining, int writeBufferPosition);
    
    void onChannelWriteExit(int bytesWritten, int writeBufferLimit, int writeBufferRemaining, int writeBufferPosition);
}
