package com.neaterbits.displayserver.io.common;

public interface SelectableLog {

    void onTryReadEnter(int readBufferLimit, int readBufferRemaining, int readBufferPosition);

    void onAfterFlipBeforeProcessingOneMessage(int bytesRead, int readBufferLimit, int readBufferRemaining, int readBufferPosition);

    void onAfterProcessedOneMessage(int readBufferLimit, int readBufferRemaining, int readBufferPosition);

    void onAfterProcessedOneMessageFlip(int readBufferLimit, int readBufferRemaining, int readBufferPosition);

    void onBufferReallocated(int readBufferLimit, int readBufferRemaining, int readBufferPosition);

    void onTryReadExit(int readBufferLimit, int readBufferRemaining, int readBufferPosition);
    
    void onProcessedCompleteMessage(int messageLength, int readBufferLimit, int readBufferRemaining, int readBufferPosition);
}
