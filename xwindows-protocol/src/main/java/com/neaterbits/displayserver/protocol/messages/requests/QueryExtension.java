package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.messages.replies.QueryExtensionReply;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class QueryExtension extends XRequest {

    private final String name;

    public static QueryExtension decode(XWindowsProtocolInputStream stream) throws IOException {
        
        stream.readBYTE();
        
        stream.readCARD16();

        final CARD16 nameLength = stream.readCARD16();
        
        stream.readCARD16();
        
        final String name = stream.readSTRING8(nameLength.getValue());
        
        final int padLength = XWindowsProtocolUtil.getPadding(nameLength.getValue());
        
        stream.readPad(padLength);
        
        return new QueryExtension(name);
    }
    
    public QueryExtension(String name) {
        
        Objects.requireNonNull(name);
        
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        writeUnusedByte(stream);
        
        final int padLength = XWindowsProtocolUtil.getPadding(name.length());
        
        stream.writeCARD16(new CARD16(2 + (name.length() + padLength)));
        
        stream.writeCARD16(new CARD16(0));
        
        stream.writeSTRING8(name);
        stream.pad(padLength);
    }

    @Override
    public int getOpCode() {
        return OpCodes.QUERY_EXTENSION;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return QueryExtensionReply.class;
    }
}
