package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.MapState;
import com.neaterbits.displayserver.protocol.messages.replies.GetWindowAttributesReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetWindowAttributes;
import com.neaterbits.displayserver.protocol.messages.requests.WindowAttributes;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.xwindows.model.XWindow;

final class MessageProcessorWindows {

    static void getWindowAttributes(GetWindowAttributes getWindowAttributes, int opcode, CARD16 sequenceNumber, XClient client, XClientWindowsConstAccess xWindows, ServerToClient serverToClient) {
        
        final XWindow window = xWindows.getClientOrRootWindow(getWindowAttributes.getWindow());

        if (window == null) {
            serverToClient.sendError(client, Errors.Window, sequenceNumber, getWindowAttributes.getWindow().getValue(), opcode);
        }
        else {
            final WindowAttributes curAttributes = window.getCurrentWindowAttributes();
            
            final GetWindowAttributesReply reply = new GetWindowAttributesReply(
                    sequenceNumber,
                    curAttributes.getBackingStore(),
                    new VISUALID(0),
                    window.getWindowClass(),
                    curAttributes.getBitGravity(), curAttributes.getWinGravity(),
                    curAttributes.getBackingPlanes(), curAttributes.getBackingPixel(),
                    curAttributes.getSaveUnder(),
                    new BOOL(true),
                    MapState.Viewable,
                    curAttributes.getOverrideRedirect(),
                    curAttributes.getColormap(),
                    new SETofEVENT(0), // TODO
                    new SETofEVENT(0), // TODO
                    curAttributes.getDoNotPropagateMask());
            
            serverToClient.sendReply(client, reply);
        }
    }
}
