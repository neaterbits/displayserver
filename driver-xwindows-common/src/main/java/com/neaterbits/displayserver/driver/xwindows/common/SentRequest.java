package com.neaterbits.displayserver.driver.xwindows.common;

import java.nio.ByteBuffer;

public final class SentRequest {

    private final int sequenceNumber;
    private final int length;
    private final ByteBuffer reply;
    
    public SentRequest(int sequenceNumber, int length, ByteBuffer reply) {
        this.sequenceNumber = sequenceNumber;
        this.length = length;
        this.reply = reply;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getLength() {
        return length;
    }

    public ByteBuffer getReply() {
        return reply;
    }
}
