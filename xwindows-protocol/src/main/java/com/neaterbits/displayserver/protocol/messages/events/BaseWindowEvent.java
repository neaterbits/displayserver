package com.neaterbits.displayserver.protocol.messages.events;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.events.types.EventState;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public abstract class BaseWindowEvent extends XEvent {

    private final EventState eventState;

    public BaseWindowEvent(CARD16 sequenceNumber, EventState eventState) {
        super(sequenceNumber);
    
        Objects.requireNonNull(eventState);
        
        this.eventState = eventState;
    }

    final EventState getEventState() {
        return eventState;
    }

    public final TIMESTAMP getTime() {
        return eventState.getTime();
    }

    public final WINDOW getRoot() {
        return eventState.getRoot();
    }

    public final WINDOW getEvent() {
        return eventState.getEvent();
    }

    public final WINDOW getChild() {
        return eventState.getChild();
    }

    public final INT16 getRootX() {
        return eventState.getRootX();
    }

    public final INT16 getRootY() {
        return eventState.getRootY();
    }

    public final INT16 getEventX() {
        return eventState.getEventX();
    }

    public final INT16 getEventY() {
        return eventState.getEventY();
    }

    public final SETofKEYBUTMASK getState() {
        return eventState.getState();
    }
}
