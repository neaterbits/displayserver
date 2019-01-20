package com.neaterbits.displayserver.protocol.messages.requests;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.XRequest;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.util.logging.LogUtil;

public final class SendEvent extends XRequest {

    private final BOOL propagate;
    private final WINDOW destination;
    private final SETofEVENT eventMask;
    private final XEvent event;

    public static SendEvent decode(XWindowsProtocolInputStream stream) throws IOException {
        
        final BOOL propagate = stream.readBOOL();
        
        readRequestLength(stream);
        
        return new SendEvent(
                propagate,
                stream.readWINDOW(),
                stream.readSETofEVENT(),
                XEvent.decode(stream, stream.readBYTE().getValue()));
    }
    
    public SendEvent(BOOL propagate, WINDOW destination, SETofEVENT eventMask, XEvent event) {
    
        Objects.requireNonNull(propagate);
        Objects.requireNonNull(destination);
        Objects.requireNonNull(eventMask);
        Objects.requireNonNull(event);
        
        this.propagate = propagate;
        this.destination = destination;
        this.eventMask = eventMask;
        this.event = event;
    }

    public BOOL getPropagate() {
        return propagate;
    }

    public WINDOW getDestination() {
        return destination;
    }

    public SETofEVENT getEventMask() {
        return eventMask;
    }

    public XEvent getEvent() {
        return event;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "propagate", propagate,
                "destination", destination,
                "eventMask", eventMask,
                "event", LogUtil.outputParametersInBrackets(event.getDebugParams())
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        writeOpCode(stream);
        
        stream.writeBOOL(propagate);
        
        writeRequestLength(stream, 11);
        
        stream.writeWINDOW(destination);
        
        stream.writeSETofEVENT(eventMask);
        
        event.encode(stream);
    }

    @Override
    public int getOpCode() {
        return OpCodes.SEND_EVENT;
    }

    @Override
    public Class<? extends XReply> getReplyClass() {
        return null;
    }
}
