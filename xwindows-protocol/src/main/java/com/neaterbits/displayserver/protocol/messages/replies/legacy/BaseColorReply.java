package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.CARD16;

public abstract class BaseColorReply extends XReply {

    private final CARD16 exactRed;
    private final CARD16 exactGreen;
    private final CARD16 exactBlue;
    
    private final CARD16 visualRed;
    private final CARD16 visualGreen;
    private final CARD16 visualBlue;
    
    public BaseColorReply(
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

    public final CARD16 getExactRed() {
        return exactRed;
    }

    public final CARD16 getExactGreen() {
        return exactGreen;
    }

    public final CARD16 getExactBlue() {
        return exactBlue;
    }

    public final CARD16 getVisualRed() {
        return visualRed;
    }

    public final CARD16 getVisualGreen() {
        return visualGreen;
    }

    public final CARD16 getVisualBlue() {
        return visualBlue;
    }
    
    final Object[] getBaseDebugParams() {
        return wrap(
                "exactRed", exactRed,
                "exactGreen", exactGreen,
                "exactBlue", exactBlue,
                "visualRed", visualRed,
                "visualGreen", visualGreen,
                "visualBlue", visualBlue
        );
    }
    
    final void encode(XWindowsProtocolOutputStream stream, int padding, EncodeFunction encodeAtStart) throws IOException {

        writeReplyHeader(stream);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 0);
        
        if (encodeAtStart != null) {
            encodeAtStart.encode(stream);
        }
        
        stream.writeCARD16(exactRed);
        stream.writeCARD16(exactGreen);
        stream.writeCARD16(exactBlue);
        
        stream.writeCARD16(visualRed);
        stream.writeCARD16(visualGreen);
        stream.writeCARD16(visualBlue);
        
        stream.pad(padding);
    }

    
}
