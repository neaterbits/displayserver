package com.neaterbits.displayserver.protocol.messages.protocolsetup;

import java.io.IOException;
import java.util.Arrays;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.Encodeable;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;

public final class DEPTH extends Encodeable {

    private final CARD8 depth;
    private final CARD16 numberOfVisualTypes;
    private final VISUALTYPE [] visuals;
    
    public static DEPTH decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final CARD8 depth = stream.readCARD8();
        
        stream.readBYTE();
        
        final CARD16 numberOfVisualTypes = stream.readCARD16();
        
        stream.readCARD32();
        
        final VISUALTYPE [] visualTypes = new VISUALTYPE[numberOfVisualTypes.getValue()];
        
        for (int i = 0; i < visualTypes.length; ++ i) {
            visualTypes[i] = VISUALTYPE.decode(stream);
        }
        
        return new DEPTH(depth, numberOfVisualTypes, visualTypes);
    }
    
    public DEPTH(CARD8 depth, CARD16 numberOfVisualTypes, VISUALTYPE[] visuals) {
        this.depth = depth;
        this.numberOfVisualTypes = numberOfVisualTypes;
        this.visuals = visuals;
    }
    
    public CARD8 getDepth() {
        return depth;
    }
    
    public CARD16 getNumberOfVisualTypes() {
        return numberOfVisualTypes;
    }

    public VISUALTYPE[] getVisuals() {
        return visuals;
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        stream.writeCARD8(depth);
        stream.writeCARD8(new CARD8((byte)0));
        stream.writeCARD16(numberOfVisualTypes);
        
        stream.writeCARD32(new CARD32(0));
        
        encodeArray(visuals, stream);
    }

    @Override
    public String toString() {
        return "DEPTH [depth=" + depth + ", numberOfVisualTypes=" + numberOfVisualTypes + ", visuals="
                + Arrays.toString(visuals) + "]";
    }
}
