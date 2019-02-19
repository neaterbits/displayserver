package com.neaterbits.displayserver.xwindows.processing;

import java.util.function.Function;

import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.XError;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.server.XEventSubscriptionsConstAccess;
import com.neaterbits.displayserver.xwindows.model.XWindow;

public abstract class XMessageSender {

    private final XWindowsServerProtocolLog protocolLog;

    protected XMessageSender(XWindowsServerProtocolLog protocolLog) {
        this.protocolLog = protocolLog;
    }

    protected final <T extends XRequest> T log(int messageLength, int opcode, CARD16 sequenceNumber, T request) {
        
        if (protocolLog != null) {
            protocolLog.onReceivedRequest(messageLength, opcode, sequenceNumber, request);
        }

        return request;
    }

    protected final void sendEvent(XClientOps client, WINDOW window, XEvent event) {
        
        if (protocolLog != null) {
            protocolLog.onSendEvent(event);
        }
        
        client.sendEvent(event);
    }

    protected final void sendReply(XClientOps client, XReply reply) {
        
        if (protocolLog != null) {
            protocolLog.onSendReply(reply);
        }
        
        client.sendReply(reply);
    }

    protected final void sendError(XClientOps client, BYTE errorCode, CARD16 sequenceNumber, long value, int opcode) {
        
        final XError error = new XError(errorCode, sequenceNumber, new CARD32(value), new CARD8((short)opcode));
        
        if (protocolLog != null) {
            protocolLog.onSendError(error);
        }
        
        client.sendError(error);
    }

    protected final void sendEventToSubscribing(
            XEventSubscriptionsConstAccess xEventSubscriptions,
            XWindow xWindow,
            int eventCode,
            Function<XClientOps, XEvent> makeEvent) {
        
        sendEventToSubscribing(xEventSubscriptions, xWindow.getWINDOW(), eventCode, makeEvent);
    }

    protected final void sendEventToSubscribing(
            XEventSubscriptionsConstAccess xEventSubscriptions,
            WINDOW window,
            int eventCode,
            Function<XClientOps, XEvent> makeEvent) {
        
        
        for (XClientOps client : xEventSubscriptions.getClientsInterestedInEvent(window, eventCode)) {
            
            final XEvent event = makeEvent.apply(client);

            sendEvent(client, window, event);
        }
    }
}
