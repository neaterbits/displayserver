package com.neaterbits.displayserver.protocol.messages.replies;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class GetModifierMappingReply extends XReply {

    private final BYTE keycodesPerModifier;
    private final CARD8 [] keycodes;

    public static GetModifierMappingReply decode(XWindowsProtocolInputStream stream) throws IOException {

        final BYTE keycodesPerModifier = stream.readBYTE();
        
        final CARD16 sequenceNumber = stream.readCARD16();
        
        stream.readCARD32();
        
        stream.readPad(24);
        
        final int numKeycodes = 8 * keycodesPerModifier.getValue();
        
        final CARD8 [] keycodes = new CARD8[numKeycodes];
        
        for (int i = 0; i < numKeycodes; ++ i) {
            keycodes[i] = stream.readCARD8();
        }

        return new GetModifierMappingReply(sequenceNumber, keycodesPerModifier, keycodes);
    }
    
    public GetModifierMappingReply(CARD16 sequenceNumber, BYTE keycodesPerModifier, CARD8[] keycodes) {
        super(sequenceNumber);
    
        Objects.requireNonNull(keycodesPerModifier);
        Objects.requireNonNull(keycodes);
        
        if (keycodes.length != 8 * keycodesPerModifier.getValue()) {
            throw new IllegalArgumentException();
        }
        
        this.keycodesPerModifier = keycodesPerModifier;
        this.keycodes = keycodes;
    }

    public BYTE getKeycodesPerModifier() {
        return keycodesPerModifier;
    }

    public CARD8[] getKeycodes() {
        return keycodes;
    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return wrap(
                "keycodesPerModifier", keycodesPerModifier,
                "keycodes", Arrays.toString(keycodes)
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {
        
        writeReplyHeader(stream);

        stream.writeBYTE(keycodesPerModifier);
        
        writeSequenceNumber(stream);
        
        writeReplyLength(stream, 2 * keycodesPerModifier.getValue());
        
        stream.pad(24);
        
        for (CARD8 keycode : keycodes) {
            stream.writeCARD8(keycode);
        }
    }
}
