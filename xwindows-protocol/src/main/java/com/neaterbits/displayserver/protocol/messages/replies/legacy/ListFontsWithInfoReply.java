package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.INT16;

public final class ListFontsWithInfoReply extends XReply {

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
    
    private final CARD32 repliesHint;
    
    private final FONTPROP [] properties;

    private final String name;

    public ListFontsWithInfoReply(CARD16 sequenceNumber, CHARINFO minBounds, CHARINFO maxBounds, CARD16 minCharOrByte2,
            CARD16 maxCharOrByte2, CARD16 defaultChar, CARD16 numberOfFontProps, BYTE drawDirection, CARD8 minByte1,
            CARD8 maxByte1, BOOL allCharsExist, INT16 fontAscent, INT16 fontDescent, CARD32 repliesHint,
            FONTPROP[] properties, String name) {
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
        Objects.requireNonNull(repliesHint);
        Objects.requireNonNull(properties);
        Objects.requireNonNull(name);

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
        this.repliesHint = repliesHint;
        this.properties = properties;
        this.name = name;
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
                "repliesHint", repliesHint,
                "properties", properties,
                "name", name
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeBYTE(new BYTE((byte)name.length()));
        
        final int padding = XWindowsProtocolUtil.getPadding(name.length());
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 7 + 2 * properties.length + (name.length() + padding) / 4);
        
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
        
        stream.writeCARD32(repliesHint);
        
        for (FONTPROP fontprop : properties) {
            fontprop.encode(stream);
        }
        
        stream.writeSTRING8(name);

        stream.pad(padding);
    }
}
