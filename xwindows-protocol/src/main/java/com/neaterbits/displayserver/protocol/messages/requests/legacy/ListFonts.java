package com.neaterbits.displayserver.protocol.messages.requests.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.legacy.ListFontsReply;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class ListFonts extends XRequest {

    private final CARD16 maxNames;
    private final String pattern;

    public static ListFonts decode(XWindowsProtocolInputStream stream) throws IOException {
        
        readUnusedByte(stream);
        
        readRequestLength(stream);
        
        final CARD16 maxNames = stream.readCARD16();
        
        final CARD16 length = stream.readCARD16();
        
        final int patternLength = length.getValue();
        
        final int padding = XWindowsProtocolUtil.getPadding(patternLength);
        
        final String pattern = stream.readSTRING8(patternLength);
        
        stream.readPad(padding);
        
        return new ListFonts(maxNames, pattern);
    }
    
    public ListFonts(CARD16 maxNames, String pattern) {

        Objects.requireNonNull(maxNames);
        Objects.requireNonNull(pattern);
        
        this.maxNames = maxNames;
        this.pattern = pattern;
    }

    public CARD16 getMaxNames() {
        return maxNames;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "maxNames", maxNames,
                "pattern", pattern
        );
    }



    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        final int padding = XWindowsProtocolUtil.getPadding(pattern.length());
        
        writeRequestLength(stream, 2 + (pattern.length() + padding) / 4);
        
        stream.writeCARD16(maxNames);
        
        stream.writeCARD16(new CARD16(pattern.length()));
        
        stream.writeSTRING8(pattern);
        
        stream.pad(padding);
    }

    @Override
    public int getOpCode() {
        return OpCodes.LIST_FONTS;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return ListFontsReply.class;
    }
}
