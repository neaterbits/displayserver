package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.QueryPointerReply;
import com.neaterbits.displayserver.protocol.messages.requests.QueryPointer;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCorePointerMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    
    public XCorePointerMessageProcessor(XWindowsServerProtocolLog protocolLog, XWindowsConstAccess<?> xWindows) {
        super(protocolLog);

        this.xWindows = xWindows;
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.QUERY_POINTER
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
        
        case OpCodes.QUERY_POINTER: {
            
            final QueryPointer queryPointer = log(messageLength, opcode, sequenceNumber, QueryPointer.decode(stream));
            
            final XWindow window = xWindows.getClientOrRootWindow(queryPointer.getWindow());
            
            if (window == null) {
                sendError(client, Errors.Window, sequenceNumber, queryPointer.getWindow().getValue(), opcode);
            }
            else {
                final QueryPointerReply reply = new QueryPointerReply(
                        sequenceNumber,
                        new BOOL(true),
                        window.isRootWindow() ? window.getWINDOW() : window.getRootWINDOW(),
                        WINDOW.None,
                        new INT16((short)0), new INT16((short)0),
                        new INT16((short)0), new INT16((short)0),
                        new SETofKEYBUTMASK((short)0));
                
                sendReply(client, reply);
            }
            break;
        }
        }
    }
}
