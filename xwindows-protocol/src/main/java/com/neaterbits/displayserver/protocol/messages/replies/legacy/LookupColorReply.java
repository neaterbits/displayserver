package com.neaterbits.displayserver.protocol.messages.replies.legacy;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.CARD16;

public final class LookupColorReply extends BaseColorReply {

    public LookupColorReply(CARD16 sequenceNumber, CARD16 exactRed, CARD16 exactGreen, CARD16 exactBlue,
            CARD16 visualRed, CARD16 visualGreen, CARD16 visualBlue) {
        super(sequenceNumber, exactRed, exactGreen, exactBlue, visualRed, visualGreen, visualBlue);
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        super.encode(stream, 12, null);
    }

    @Override
    public Object[] getDebugParams() {
        return super.getBaseDebugParams();
    }
}
