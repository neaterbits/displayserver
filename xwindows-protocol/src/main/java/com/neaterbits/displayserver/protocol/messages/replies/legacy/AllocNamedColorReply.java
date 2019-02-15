package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;

public final class AllocNamedColorReply extends BaseColorReply {

    private final CARD32 pixel;

    public AllocNamedColorReply(CARD16 sequenceNumber,
            CARD32 pixel,
            CARD16 exactRed, CARD16 exactGreen, CARD16 exactBlue,
            CARD16 visualRed, CARD16 visualGreen, CARD16 visualBlue) {
        super(sequenceNumber, exactRed, exactGreen, exactBlue, visualRed, visualGreen, visualBlue);
    
        Objects.requireNonNull(pixel);
        
        this.pixel = pixel;
    }

    @Override
    protected Object[] getServerToClientDebugParams() {
        return merge(
                wrap("pixel", pixel),
                super.getBaseDebugParams());
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        super.encode(stream, 8, st -> st.writeCARD32(pixel));
        
    }
}
