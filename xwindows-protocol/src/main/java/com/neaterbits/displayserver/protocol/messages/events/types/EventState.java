package com.neaterbits.displayserver.protocol.messages.events.types;

import java.io.IOException;
import java.util.Objects;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.messages.XEncodeable;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.TIMESTAMP;
import com.neaterbits.displayserver.protocol.types.WINDOW;

public final class EventState extends XEncodeable {

    private final TIMESTAMP time;
    private final WINDOW root;
    private final WINDOW event;
    private final WINDOW child;
    
    private final INT16 rootX;
    private final INT16 rootY;
    private final INT16 eventX;
    private final INT16 eventY;
    
    private final SETofKEYBUTMASK state;

    private final BOOL sameScreen;
    
    public static EventState decode(XWindowsProtocolInputStream stream) throws IOException {
        
        return new EventState(
                stream.readTIMESTAMP(),
                stream.readWINDOW(),
                stream.readWINDOW(),
                stream.readWINDOW(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readINT16(),
                stream.readSETofKEYBUTMASK(),
                stream.readBOOL());
    }
    
    public EventState(
            TIMESTAMP time,
            WINDOW root, WINDOW event, WINDOW child,
            INT16 rootX, INT16 rootY,
            INT16 eventX, INT16 eventY,
            SETofKEYBUTMASK state,
            BOOL sameScreen) {

        Objects.requireNonNull(time);
        Objects.requireNonNull(root);
        Objects.requireNonNull(event);
        Objects.requireNonNull(child);
        Objects.requireNonNull(rootX);
        Objects.requireNonNull(rootY);
        Objects.requireNonNull(eventX);
        Objects.requireNonNull(eventY);
        Objects.requireNonNull(state);
        Objects.requireNonNull(sameScreen);
        
        this.time = time;
        this.root = root;
        this.event = event;
        this.child = child;
        this.rootX = rootX;
        this.rootY = rootY;
        this.eventX = eventX;
        this.eventY = eventY;
        this.state = state;
        this.sameScreen = sameScreen;
    }

    public TIMESTAMP getTime() {
        return time;
    }

    public WINDOW getRoot() {
        return root;
    }

    public WINDOW getEvent() {
        return event;
    }

    public WINDOW getChild() {
        return child;
    }

    public INT16 getRootX() {
        return rootX;
    }

    public INT16 getRootY() {
        return rootY;
    }

    public INT16 getEventX() {
        return eventX;
    }

    public INT16 getEventY() {
        return eventY;
    }

    public SETofKEYBUTMASK getState() {
        return state;
    }

    public BOOL getSameScreen() {
        return sameScreen;
    }

    @Override
    public Object[] getDebugParams() {
        return wrap(
                "time", time,
                "root", root,
                "event", event,
                "child", child,
                "rootX", rootX,
                "rootY", rootY,
                "eventX", eventX,
                "eventY", eventY,
                "state", state,
                "sameScreen", sameScreen
        );
    }

    @Override
    public void encode(XWindowsProtocolOutputStream stream) throws IOException {

        stream.writeTIMESTAMP(time);
        
        stream.writeWINDOW(root);
        stream.writeWINDOW(event);
        stream.writeWINDOW(child);
        
        stream.writeINT16(rootX);
        stream.writeINT16(rootY);
        
        stream.writeINT16(eventX);
        stream.writeINT16(eventY);
        
        stream.writeSETofKEYBUTMASK(state);
    
        stream.writeBOOL(sameScreen);
    }
}
