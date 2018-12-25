package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;

public final class QueryFontReply extends Reply {

    private final CHARINFO minBounds;
    private final CHARINFO maxBounds;
    
    private final CARD16 minCharOrByte2;
    private final CARD16 maxCharOrByte2;
    
    private final CARD16 defaultChar;
    
    private final CARD16 numberOfFontProps;
    
    private final BYTE drawDirection;
    
    private final CARD8 minByte1;
    private final CARD8 maxByte1;
    
    private final BOOL allCharsExist;
    private final INT16 fontAscent;
    private final INT16 fontDescent;
    
    private final CARD32 numberOfCharInfos;
    
    private final FONTPROP [] properties;
    private final CHARINFO [] charInfos;

    
    public QueryFontReply(
            CARD16 sequenceNumber,
            CHARINFO minBounds, CHARINFO maxBounds,
            CARD16 minCharOrByte2, CARD16 maxCharOrByte2,
            CARD16 defaultChar,
            CARD16 numberOfFontProps,
            BYTE drawDirection,
            CARD8 minByte1, CARD8 maxByte1,
            BOOL allCharsExist,
            INT16 fontAscent, INT16 fontDescent,
            CARD32 numberOfCharInfos,
            FONTPROP[] properties,
            CHARINFO[] charInfos) {
        super(sequenceNumber);
        
        Objects.requireNonNull(minBounds);
        Objects.requireNonNull(maxBounds);
        Objects.requireNonNull(minCharOrByte2);
        Objects.requireNonNull(maxCharOrByte2);
        Objects.requireNonNull(defaultChar);
        Objects.requireNonNull(numberOfFontProps);
        Objects.requireNonNull(drawDirection);
        Objects.requireNonNull(minByte1);
        Objects.requireNonNull(maxByte1);
        Objects.requireNonNull(allCharsExist);
        Objects.requireNonNull(fontAscent);
        Objects.requireNonNull(fontDescent);
        Objects.requireNonNull(numberOfCharInfos);
        Objects.requireNonNull(properties);
        Objects.requireNonNull(charInfos);
        
        this.minBounds = minBounds;
        this.maxBounds = maxBounds;
        this.minCharOrByte2 = minCharOrByte2;
        this.maxCharOrByte2 = maxCharOrByte2;
        this.defaultChar = defaultChar;
        this.numberOfFontProps = numberOfFontProps;
        this.drawDirection = drawDirection;
        this.minByte1 = minByte1;
        this.maxByte1 = maxByte1;
        this.allCharsExist = allCharsExist;
        this.fontAscent = fontAscent;
        this.fontDescent = fontDescent;
        this.numberOfCharInfos = numberOfCharInfos;
        this.properties = properties;
        this.charInfos = charInfos;
    }
    
    @Override
    public Object[] getDebugParams() {
        return wrap(
                "minBounds", minBounds,
                "maxBounds", maxBounds,
                "minCharOrByte2", minCharOrByte2,
                "maxCharOrByte2", maxCharOrByte2,
                "defaultChar", defaultChar,
                "numberOfFontProps", numberOfFontProps,
                "drawDirection", drawDirection,
                "minByte1", minByte1,
                "maxByte1", maxByte1,
                "allCharsExist", allCharsExist,
                "fontAscent", fontAscent,
                "fontDescent", fontDescent,
                "numberOfCharInfos", numberOfCharInfos,
                "properties", outputArrayInBrackets(properties),
                "charInfos", charInfos.length
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);
        
        writeUnusedByte(stream);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 7 + 2 * numberOfFontProps.getValue() + 3 * numberOfCharInfos.getValue());
        
        minBounds.encode(stream);
        
        stream.pad(4);
        
        maxBounds.encode(stream);
        
        stream.pad(4);
        
        stream.writeCARD16(minCharOrByte2);
        stream.writeCARD16(maxCharOrByte2);
        stream.writeCARD16(defaultChar);
        stream.writeCARD16(numberOfFontProps);
        
        stream.writeBYTE(drawDirection);
        stream.writeCARD8(minByte1);
        stream.writeCARD8(maxByte1);

        stream.writeBOOL(allCharsExist);
        
        stream.writeINT16(fontAscent);
        stream.writeINT16(fontDescent);
        
        stream.writeCARD32(numberOfCharInfos);
        
        for (FONTPROP fontprop : properties) {
            fontprop.encode(stream);
        }
        
        for (CHARINFO charinfo : charInfos) {
            charinfo.encode(stream);
        }
    }
}
