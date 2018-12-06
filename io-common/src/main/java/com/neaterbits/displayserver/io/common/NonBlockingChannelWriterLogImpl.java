package com.neaterbits.displayserver.io.common;

import com.neaterbits.displayserver.util.logging.BaseLogImpl;
import com.neaterbits.displayserver.util.logging.DebugLevel;

public class NonBlockingChannelWriterLogImpl extends BaseLogImpl implements NonBlockingChannelWriterLog {

    public NonBlockingChannelWriterLogImpl(String prefix, DebugLevel debugLevel) {
        super(prefix, debugLevel);
    }

    @Override
    public void onQueueWriteEnter(
            int dataLength, int offset, int length,
            int writeBufferLimit, int writeBufferRemaining, int writeBufferPosition) {
        
        trace("onQueueWriteEnter",
                    "dataLength", dataLength,
                    "offset", offset,
                    "length", length,
                    "bufferLimit", writeBufferLimit,
                    "bufferRemaining", writeBufferRemaining,
                    "bufferPosition", writeBufferPosition);
    }

    @Override
    public void onQueueWriteExit(int writeBufferLimit, int writeBufferRemaining, int writeBufferPosition) {
        trace("onQueueWriteExit",
                    "bufferLimit", writeBufferLimit,
                    "bufferRemaining", writeBufferRemaining,
                    "bufferPosition", writeBufferPosition);
    }

    @Override
    public void onChannelWriteEnter(int writeBufferLimit, int writeBufferRemaining, int writeBufferPosition) {
        
        trace("onChannelWriteEnter",
                    "bufferLimit", writeBufferLimit,
                    "bufferRemaining", writeBufferRemaining,
                    "bufferPosition", writeBufferPosition);
    }

    @Override
    public void onChannelWriteExit(int bytesWritten, int writeBufferLimit, int writeBufferRemaining,int writeBufferPosition) {

        trace("onChannelWriteExit",
                    "bytesWritten", bytesWritten,
                    "bufferLimit", writeBufferLimit,
                    "bufferRemaining", writeBufferRemaining,
                    "bufferPosition", writeBufferPosition);
    }
}
