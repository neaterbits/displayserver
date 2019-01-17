package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.QueryExtensionReply;
import com.neaterbits.displayserver.protocol.messages.requests.QueryExtension;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreExtensionMessageProcessor extends XOpCodeProcessor {

    public XCoreExtensionMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);
    }

    @Override
    protected int[] getOpCodes() {
        
        return new int [] {
                OpCodes.QUERY_EXTENSION
        };
    }

    @Override
    protected void onMessage(
            XWindowsProtocolInputStream stream,
            int messageLength,
            int opcode,
            CARD16 sequenceNumber,
            XClientOps client) throws IOException {
        
        switch (opcode) {
        case OpCodes.QUERY_EXTENSION: {
            log(messageLength, opcode, sequenceNumber, QueryExtension.decode(stream));
        
            sendReply(client, 
                    new QueryExtensionReply(
                            sequenceNumber,
                            new BOOL((byte)0),
                            new CARD8((byte)0),
                            new CARD8((byte)0),
                            new CARD8((byte)0)));
            break;
        }
        }
    }
}
