package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.events.common.InputEvent;
import com.neaterbits.displayserver.events.common.KeyPressEvent;
import com.neaterbits.displayserver.events.common.KeyReleaseEvent;
import com.neaterbits.displayserver.events.common.PointerButtonPressEvent;
import com.neaterbits.displayserver.events.common.PointerButtonReleaseEvent;
import com.neaterbits.displayserver.events.common.PointerMotionEvent;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.events.ButtonPress;
import com.neaterbits.displayserver.protocol.messages.events.ButtonRelease;
import com.neaterbits.displayserver.protocol.messages.events.KeyModifier;
import com.neaterbits.displayserver.protocol.messages.events.KeyPress;
import com.neaterbits.displayserver.protocol.messages.events.KeyRelease;
import com.neaterbits.displayserver.protocol.messages.events.MotionNotify;
import com.neaterbits.displayserver.protocol.messages.events.types.EventState;
import com.neaterbits.displayserver.protocol.types.BOOL;
import com.neaterbits.displayserver.protocol.types.BUTTON;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.INT16;
import com.neaterbits.displayserver.protocol.types.KEYCODE;
import com.neaterbits.displayserver.protocol.types.SETofEVENT;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;
import com.neaterbits.displayserver.protocol.types.WINDOW;
import com.neaterbits.displayserver.windows.Window;
import com.neaterbits.displayserver.windows.WindowManagement;
import com.neaterbits.displayserver.windows.WindowsDisplayArea;
import com.neaterbits.displayserver.windows.WindowsDisplayAreas;
import com.neaterbits.displayserver.xwindows.model.XWindow;
import com.neaterbits.displayserver.xwindows.model.XWindowsConstAccess;
import com.neaterbits.displayserver.xwindows.processing.XMessageSender;

final class XInputEventHandler extends XMessageSender implements XInputEventHandlerConstAccess {

    private final WindowsDisplayAreas displayAreas;
    private final XWindowsConstAccess<?> xWindows;
    private final WindowManagement windowManagement;
    private final XEventSubscriptions eventSubscriptions;
    private final XFocusState xFocusState;
    private final XTimestampGenerator timestampGenerator;
    
    private int rootX;
    private int rootY;
    private short state;
    
    XInputEventHandler(InputDriver inputDriver,
            WindowsDisplayAreas displayAreas,
            XWindowsConstAccess<?> xWindows, WindowManagement windowManagement,
            XEventSubscriptions eventSubscriptions,
            XFocusState xFocusState,
            XTimestampGenerator timestampGenerator,
            XWindowsServerProtocolLog protocolLog) {
        
        super(protocolLog);
        
        this.xWindows = xWindows;
        this.displayAreas = displayAreas;
        this.windowManagement = windowManagement;
        this.eventSubscriptions = eventSubscriptions;
        this.xFocusState = xFocusState;
        this.timestampGenerator = timestampGenerator;
        
        inputDriver.registerInputEventListener(this::processInputEvent);
    }
    
    private static final short XKEYMASK 
        =   SETofKEYBUTMASK.SHIFT
          | SETofKEYBUTMASK.LOCK
          | SETofKEYBUTMASK.CONTROL
          | SETofKEYBUTMASK.MOD1
          | SETofKEYBUTMASK.MOD2
          | SETofKEYBUTMASK.MOD3
          | SETofKEYBUTMASK.MOD4
          | SETofKEYBUTMASK.MOD5;

    private void updateKeyModifiersState(int modifiersState) {

        System.out.format("## update modifiers from 0x%08x\n", modifiersState);
        
        short xModifiers = 0;
        
        for (int i = 0; i < 32; ++ i) {
            int modifier = 1 << i;
            
            if ((modifiersState & modifier) != 0) {
                xModifiers |= toXKeyModifier(modifier);
            }
        }
        
        state &= ~XKEYMASK;
        state |= xModifiers;
        
        System.out.format("## updated state to 0x%08x\n", state);
    }
    
    private void processInputEvent(InputEvent inputEvent) {
 
        if (inputEvent instanceof KeyPressEvent) {
            
            final KeyPressEvent keyPressEvent = (KeyPressEvent)inputEvent;
            
            System.out.println("## keypress " + keyPressEvent.getKeyCode() + "/" + keyPressEvent.getModifier());
            
            updateKeyModifiersState(keyPressEvent.getModifiersState());

            final WINDOW focusWindow = xFocusState.getInputFocus();
            
            if (focusWindow != null) {
                sendEventToSubscribing(eventSubscriptions, focusWindow, SETofEVENT.KEY_PRESS,
                        client -> new KeyPress(
                                client.getSequenceNumber(),
                                new KEYCODE((short)keyPressEvent.getKeyCode()),
                                getEventState(focusWindow)));
            }
        }
        else if (inputEvent instanceof KeyReleaseEvent) {

            final KeyReleaseEvent keyReleaseEvent = (KeyReleaseEvent)inputEvent;

            System.out.println("## keyrelease " + keyReleaseEvent.getKeyCode());

            updateKeyModifiersState(keyReleaseEvent.getModifiersState());

            final WINDOW focusWindow = xFocusState.getInputFocus();
            
            if (focusWindow != null) {
                sendEventToSubscribing(eventSubscriptions, focusWindow, SETofEVENT.KEY_RELEASE,
                        client -> new KeyRelease(
                                client.getSequenceNumber(),
                                new KEYCODE((short)keyReleaseEvent.getKeyCode()),
                                getEventState(focusWindow)));
            }
        }
        else if (inputEvent instanceof PointerButtonPressEvent) {
            
            final PointerButtonPressEvent pointerButtonPressEvent = (PointerButtonPressEvent)inputEvent;
            
            final WINDOW focusWindow = xFocusState.getInputFocus();
            
            if (focusWindow != null) {
                sendEventToSubscribing(eventSubscriptions, focusWindow, SETofEVENT.BUTTON_PRESS,
                        client -> new ButtonPress(
                                client.getSequenceNumber(),
                                new BUTTON((short)pointerButtonPressEvent.getButton()),
                                getEventState(focusWindow)));
            }
        }
        else if (inputEvent instanceof PointerButtonReleaseEvent) {
            
            final PointerButtonReleaseEvent pointerButtonReleaseEvent = (PointerButtonReleaseEvent)inputEvent;
            
            final WINDOW focusWindow = xFocusState.getInputFocus();
            
            if (focusWindow != null) {

                sendEventToSubscribing(eventSubscriptions, focusWindow, SETofEVENT.BUTTON_RELEASE,
                        client -> new ButtonRelease(
                                client.getSequenceNumber(),
                                new BUTTON((short)pointerButtonReleaseEvent.getButton()),
                                getEventState(focusWindow)));
            }
        }
        else if (inputEvent instanceof PointerMotionEvent) {
            
            final PointerMotionEvent event = (PointerMotionEvent)inputEvent;
            final WindowsDisplayArea displayArea;
            
            if (event.getDisplayDeviceId() != null) {
                displayArea = displayAreas.findDisplayAreaFromDisplayDevice(event.getDisplayDeviceId());

                this.rootX = event.getX();
                this.rootY = event.getY();
            }
            else {
                throw new UnsupportedOperationException("TODO");
            }

            final Window window = windowManagement.findWindowAt(displayArea, rootX, rootY);
            
            if (window != null) {

                final XWindow xWindow = xWindows.getClientOrRootWindow(window);
                
                if (xWindow != null) {
                    sendEventToSubscribing(eventSubscriptions, xWindow, SETofEVENT.POINTER_MOTION, client -> 
                            new MotionNotify(client.getSequenceNumber(), new BYTE((byte)0), getEventState(xWindow.getWINDOW())));
                }
            }
        }
    }
    
    private static short toXKeyModifier(int keyModifier) {
        
        short xKeyModifier;
        
        switch (keyModifier) {
        case KeyModifier.SHIFT: xKeyModifier = SETofKEYBUTMASK.SHIFT; break;
        case KeyModifier.LOCK:  xKeyModifier = SETofKEYBUTMASK.LOCK; break;
        case KeyModifier.CTRL:  xKeyModifier = SETofKEYBUTMASK.CONTROL; break;
        case KeyModifier.MOD1:  xKeyModifier = SETofKEYBUTMASK.MOD1; break;
        case KeyModifier.MOD2:  xKeyModifier = SETofKEYBUTMASK.MOD2; break;
        case KeyModifier.MOD3:  xKeyModifier = SETofKEYBUTMASK.MOD3; break;
        case KeyModifier.MOD4:  xKeyModifier = SETofKEYBUTMASK.MOD4; break;
        case KeyModifier.MOD5:  xKeyModifier = SETofKEYBUTMASK.MOD5; break;
        
        default:
            throw new UnsupportedOperationException();
        }
        
        return xKeyModifier;
    }
    
    @Override
    public EventState getEventState(WINDOW eventWindow) {

        Objects.requireNonNull(eventWindow);
        
        final XWindow xEventWindow = xWindows.getClientOrRootWindow(eventWindow);

        final WINDOW rootWindow = xEventWindow.isRootWindow()
                ? eventWindow
                : xEventWindow.getRootWINDOW();

        return new EventState(
                timestampGenerator.getTimestamp(),
                rootWindow,
                eventWindow,
                WINDOW.None,
                new INT16((short)rootX),
                new INT16((short)rootY),
                new INT16((short)(rootX - xEventWindow.getWindow().getAbsoluteLeft())),
                new INT16((short)(rootY - xEventWindow.getWindow().getAbsoluteTop())),
                new SETofKEYBUTMASK(state),
                new BOOL(true));
        
    }
}
