package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class LookupColorReply extends Reply {

    private final CARD16 exactRed;
    private final CARD16 exactGreen;
    private final CARD16 exactBlue;
    
    private final CARD16 visualRed;
    private final CARD16 visualGreen;
    private final CARD16 visualBlue;
    
    public LookupColorReply(
            CARD16 sequenceNumber,
            CARD16 exactRed, CARD16 exactGreen, CARD16 exactBlue,
            CARD16 visualRed, CARD16 visualGreen, CARD16 visualBlue) {
        super(sequenceNumber);
        
        Objects.requireNonNull(exactRed);
        Objects.requireNonNull(exactGreen);
        Objects.requireNonNull(exactBlue);
        
        Objects.requireNonNull(visualRed);
        Objects.requireNonNull(visualGreen);
        Objects.requireNonNull(visualBlue);
        
        this.exactRed = exactRed;
        this.exactGreen = exactGreen;
        this.exactBlue = exactBlue;
        this.visualRed = visualRed;
        this.visualGreen = visualGreen;
        this.visualBlue = visualBlue;
    }

    public CARD16 getExactRed() {
        return exactRed;
    }

    public CARD16 getExactGreen() {
        return exactGreen;
    }

    public CARD16 getExactBlue() {
        return exactBlue;
    }

    public CARD16 getVisualRed() {
        return visualRed;
    }

    public CARD16 getVisualGreen() {
        return visualGreen;
    }

    public CARD16 getVisualBlue() {
        return visualBlue;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 0);
        
        stream.writeCARD16(exactRed);
        stream.writeCARD16(exactGreen);
        stream.writeCARD16(exactBlue);
        
        stream.writeCARD16(visualRed);
        stream.writeCARD16(visualGreen);
        stream.writeCARD16(visualBlue);
        
        stream.pad(12);
    }
}
