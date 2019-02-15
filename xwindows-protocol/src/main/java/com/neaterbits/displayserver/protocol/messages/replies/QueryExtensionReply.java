package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class QueryExtensionReply extends XReply {

    private final BOOL present;
    private final CARD8 majorOpCode;
    private final CARD8 firstEvent;
    private final CARD8 firstError;
    
    public QueryExtensionReply(CARD16 sequenceNumber, BOOL present, CARD8 majorOpCode, CARD8 firstEvent,
            CARD8 firstError) {
        super(sequenceNumber);
        this.present = present;
        this.majorOpCode = majorOpCode;
        this.firstEvent = firstEvent;
        this.firstError = firstError;
    }

    public boolean isPresent() {
        return present.isSet();
    }

    public CARD8 getMajorOpCode() {
        return majorOpCode;
    }

    public CARD8 getFirstEvent() {
        return firstEvent;
    }

    public CARD8 getFirstError() {
        return firstError;
    }
    
    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap("present", isPresent(), "majorOpCode", majorOpCode, "firstEvent", firstEvent, "firstError", firstError);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeBYTE(new BYTE((byte)0));
        
        writeSequenceNumber(stream);
        
        stream.writeCARD32(new CARD32(0));

        stream.writeBOOL(present);
        stream.writeCARD8(majorOpCode);
        stream.writeCARD8(firstEvent);
        stream.writeCARD8(firstError);
        stream.pad(20);
    }
}
