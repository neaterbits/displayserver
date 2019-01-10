package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.replies.InternAtomReply;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class InternAtom extends Request {

    private final BOOL onlyIfExists;
    private final String name;

    public static InternAtom decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BOOL onlyIfExists = stream.readBOOL();
        
        stream.readCARD16();
        
        final CARD16 nameLength = stream.readCARD16();
        
        stream.readCARD16();
        
        final String name = stream.readSTRING8(nameLength.getValue());

        final int pad = XWindowsProtocolUtil.getPadding(nameLength.getValue());
        
        stream.readPad(pad);
        
        return new InternAtom(onlyIfExists, name);
    }
    
    public InternAtom(BOOL onlyIfExists, String name) {
        
        Objects.requireNonNull(onlyIfExists);
        Objects.requireNonNull(name);
        
        this.onlyIfExists = onlyIfExists;
        this.name = name;
    }

    public boolean getOnlyIfExists() {
        return onlyIfExists.isSet();
    }

    public String getName() {
        return name;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap("onlyIfExists", getOnlyIfExists(), "name", name);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBOOL(onlyIfExists);
        
        final int pad = XWindowsProtocolUtil.getPadding(name.length());
        
        stream.writeCARD16(new CARD16(2 + (name.length() + pad) / 4));
        
        stream.writeCARD16(new CARD16(name.length()));
        
        stream.writeCARD16(new CARD16(0));
        
        stream.writeSTRING8(name);
        
        stream.pad(pad);
    }

    @Override
    public int getOpCode() {
        return OpCodes.INTERN_ATOM;
    }

    @Override
    public Class<? extends Reply> getReplyClass() {
        return InternAtomReply.class;
    }
}
