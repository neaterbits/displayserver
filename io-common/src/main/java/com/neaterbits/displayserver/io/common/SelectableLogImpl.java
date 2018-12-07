package com.neaterbits.displayserver.io.common;

import com.neaterbits.displayserver.util.logging.BaseLogImpl;
import com.neaterbits.displayserver.util.logging.DebugLevel;

public final class SelectableLogImpl extends BaseLogImpl implements SelectableLog {

    public SelectableLogImpl(String prefix, DebugLevel debugLevel) {
        super(prefix, debugLevel);
    }

    @Override
    public void onTryReadEnter(int readBufferLimit, int readBufferRemaining, int readBufferPosition) {

        trace("readAndProcess",
                    "limit", readBufferLimit,
                    "remaining", readBufferRemaining,
                    "position", readBufferPosition);
    }
    
    
    @Override
    public void onAfterFlipBeforeProcessingOneMessage(int bytesRead, int readBufferLimit, int readBufferRemaining,
            int readBufferPosition) {

        trace("afterFlipBeforeProcessingOneMessage",
                    "bytesRead", bytesRead,
                    "limit", readBufferLimit,
                    "remaining", readBufferRemaining,
                    "position", readBufferPosition);
    }

    @Override
    public void onAfterProcessedOneMessage(int readBufferLimit, int readBufferRemaining, int readBufferPosition) {

        trace("afterProcessedOneMessage",
                    "limit", readBufferLimit,
                    "remaining", readBufferRemaining,
                    "position", readBufferPosition);
    }

    @Override
    public void onAfterProcessedOneMessageFlip(int readBufferLimit, int readBufferRemaining, int readBufferPosition) {

        trace("afterProcessedOneMessageFlip",
                    "limit", readBufferLimit,
                    "remaining", readBufferRemaining,
                    " position", readBufferPosition);
    }

    @Override
    public void onBufferReallocated(int readBufferLimit, int readBufferRemaining, int readBufferPosition) {

        trace("readbufferReallocated",
                    "limit", readBufferLimit,
                    "remaining", readBufferRemaining,
                    "position", readBufferPosition);
    }

    @Override
    public void onTryReadExit(int readBufferLimit, int readBufferRemaining, int readBufferPosition) {
        
        trace("readAndProcess",
                    "limit", readBufferLimit,
                    "remaining", readBufferRemaining,
                    "position", readBufferPosition);
    }

    @Override
    public void onProcessedCompleteMessage(int messageLength, int readBufferLimit, int readBufferRemaining,
            int readBufferPosition) {

        trace("processedCompleteMessage",
                    "messageLength", messageLength,
                    "limit", readBufferLimit,
                    "remaining",  readBufferRemaining,
                    "position", readBufferPosition);
    }
}
