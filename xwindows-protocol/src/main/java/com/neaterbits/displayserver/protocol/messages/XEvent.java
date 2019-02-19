package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.events.ButtonPress;
import com.neaterbits.displayserver.protocol.messages.events.ButtonRelease;
import com.neaterbits.displayserver.protocol.messages.events.ClientMessage;
import com.neaterbits.displayserver.protocol.messages.events.KeyPress;
import com.neaterbits.displayserver.protocol.messages.events.KeyRelease;
import com.neaterbits.displayserver.protocol.messages.events.MotionNotify;
import com.neaterbits.displayserver.protocol.messages.events.UnmapNotify;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;

public abstract class XEvent extends XServerToClientMessage {

    public abstract int getEventCode();
    
    public XEvent(CARD16 sequenceNumber) {
        super(sequenceNumber);
    }

    protected final void writeEventCode(XWindowsProtocolOutputStream stream) throws IOException {
        stream.writeBYTE(new BYTE((byte)getEventCode()));
    }
    
    public static XEvent decode(XWindowsProtocolInputStream stream, int code) throws IOException {
        
        final XEvent event;
        
        switch (code) {
        
        case Events.KEY_PRESS:
            event = KeyPress.decode(stream);
            break;
        
        case Events.KEY_RELEASE:
            event = KeyRelease.decode(stream);
            break;

        case Events.BUTTON_PRESS:
            event = ButtonPress.decode(stream);
            break;

        case Events.BUTTON_RELEASE:
            event = ButtonRelease.decode(stream);
            break;

        case Events.MOTION_NOTIFY:
            event = MotionNotify.decode(stream);
            break;

        case Events.CLIENT_MESSAGE:
            event = ClientMessage.decode(stream);
            break;
            
        case Events.UNMAP_NOTIFY:
            event = UnmapNotify.decode(stream);
            break;
        
        default:
            throw new UnsupportedOperationException("Unknown event code " + code);
        }

        return event;
    }
}
