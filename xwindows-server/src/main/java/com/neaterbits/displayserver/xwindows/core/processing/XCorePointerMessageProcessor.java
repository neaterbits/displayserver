package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.events.types.EventState;
import com.neaterbits.displayserver.protocol.messages.replies.GetPointerMappingReply;
import com.neaterbits.displayserver.protocol.messages.replies.QueryPointerReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetPointerMapping;
import com.neaterbits.displayserver.protocol.messages.requests.QueryPointer;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.server.XInputEventHandlerConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCorePointerMessageProcessor extends XOpCodeProcessor {

    private final XWindowsConstAccess<?> xWindows;
    private final XInputEventHandlerConstAccess xInputEventHandler;
    
    public XCorePointerMessageProcessor(XWindowsServerProtocolLog protocolLog, XWindowsConstAccess<?> xWindows, XInputEventHandlerConstAccess xInputEventHandler) {
        super(protocolLog);

        this.xWindows = xWindows;
        this.xInputEventHandler = xInputEventHandler;
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.QUERY_POINTER,
                
                OpCodes.GET_POINTER_MAPPING
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
                
                final EventState eventState = xInputEventHandler.getEventState(window.getWINDOW());
                
                final QueryPointerReply reply = new QueryPointerReply(
                        sequenceNumber,
                        new BOOL(true),
                        window.isRootWindow() ? window.getWINDOW() : window.getRootWINDOW(),
                        WINDOW.None,
                        eventState.getRootX(), eventState.getRootY(),
                        eventState.getEventX(), eventState.getEventY(),
                        eventState.getState());
                
                sendReply(client, reply);
            }
            break;
        }
        
        case OpCodes.GET_POINTER_MAPPING: {
            
            log(messageLength, opcode, sequenceNumber, GetPointerMapping.decode(stream));
            
            final CARD8 [] map = new CARD8[5];
            
            for (int i = 0; i < map.length; ++ i) {
                map[i] = new CARD8((short)(i + 1));
            }
            
            final GetPointerMappingReply reply = new GetPointerMappingReply(sequenceNumber, map);
            
            sendReply(client, reply);
            break;
        }
        }
    }
}
