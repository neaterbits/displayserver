package com.neaterbits.displayserver.driver.xwindows.common.messaging;

import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.ReplyListener;

final class RequestWithReply {
    
    final int sequenceNumber;
    final int opCode;
    final ReplyListener listener;
    
    public RequestWithReply(int sequenceNumber, int opCode, ReplyListener listener) {

        Objects.requireNonNull(listener);
        
        this.sequenceNumber = sequenceNumber;
        this.opCode = opCode;
        this.listener = listener;
    }
}
