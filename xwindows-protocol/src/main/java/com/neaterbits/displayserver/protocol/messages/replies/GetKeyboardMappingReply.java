package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.KEYSYM;

public final class GetKeyboardMappingReply extends XReply {

    private final BYTE keysymsPerKeycode;
    private final KEYSYM [] keysyms;

    public static GetKeyboardMappingReply decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BYTE keysymsPerKeycode = stream.readBYTE();
        
        final CARD16 sequenceNumber = stream.readCARD16();
        
        final CARD32 replyLength = stream.readCARD32();
        
        stream.readPad(24);
        
        final int numKeysyms = (int)replyLength.getValue();
    
        final KEYSYM [] keysyms = new KEYSYM[numKeysyms];
    
        for (int i = 0; i < numKeysyms; ++ i) {
            keysyms[i] = stream.readKEYSYM();
        }
        
        return new GetKeyboardMappingReply(sequenceNumber, keysymsPerKeycode, keysyms);
    }
    
    public GetKeyboardMappingReply(CARD16 sequenceNumber, BYTE keysymsPerKeycode, KEYSYM [] keysyms) {
    
        super(sequenceNumber);
        
        Objects.requireNonNull(keysymsPerKeycode);
        Objects.requireNonNull(keysyms);
        
        this.keysymsPerKeycode = keysymsPerKeycode;
        this.keysyms = keysyms;
    }

    public BYTE getKeysymsPerKeycode() {
        return keysymsPerKeycode;
    }

    public KEYSYM[] getKeysyms() {
        return keysyms;
    }

    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "keysymsPerKeycode", keysymsPerKeycode,
                "keysyms", keysyms.length
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeReplyHeader(stream);
        
        stream.writeBYTE(keysymsPerKeycode);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, keysyms.length);
        
        stream.pad(24);
        
        for (KEYSYM keysym : keysyms) {
            stream.writeKEYSYM(keysym);
        }
    }
}
