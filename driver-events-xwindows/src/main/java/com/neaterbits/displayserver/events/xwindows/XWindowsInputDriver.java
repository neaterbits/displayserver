package com.neaterbits.displayserver.events.xwindows;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.driver.common.DisplayDeviceId;
import com.neaterbits.displayserver.driver.xwindows.common.ReplyListener;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.events.common.BaseInputDriver;
import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.events.common.InputEvent;
import com.neaterbits.displayserver.events.common.KeyPressEvent;
import com.neaterbits.displayserver.events.common.KeyReleaseEvent;
import com.neaterbits.displayserver.events.common.KeyboardMapping;
import com.neaterbits.displayserver.events.common.ModifierMapping;
import com.neaterbits.displayserver.events.common.ModifierScancodes;
import com.neaterbits.displayserver.events.common.PointerButtonPressEvent;
import com.neaterbits.displayserver.events.common.PointerButtonReleaseEvent;
import com.neaterbits.displayserver.events.common.PointerMotionEvent;
import com.neaterbits.displayserver.protocol.Events;
import com.neaterbits.displayserver.protocol.messages.XError;
import com.neaterbits.displayserver.protocol.messages.XEvent;
import com.neaterbits.displayserver.protocol.messages.XReply;
import com.neaterbits.displayserver.protocol.messages.events.ButtonPress;
import com.neaterbits.displayserver.protocol.messages.events.ButtonRelease;
import com.neaterbits.displayserver.protocol.messages.events.KeyModifier;
import com.neaterbits.displayserver.protocol.messages.events.KeyPress;
import com.neaterbits.displayserver.protocol.messages.events.KeyRelease;
import com.neaterbits.displayserver.protocol.messages.events.MotionNotify;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.replies.GetKeyboardMappingReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetModifierMappingReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetKeyboardMapping;
import com.neaterbits.displayserver.protocol.messages.requests.GetModifierMapping;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.KEYSYM;
import com.neaterbits.displayserver.protocol.types.SETofKEYBUTMASK;

public final class XWindowsInputDriver extends BaseInputDriver implements InputDriver {
	
    private final XWindowsDriverConnection driverConnection;
    
    private ModifierScancodes modifierScancodes;
    
    private KeyboardMapping keyboardMapping;
    
    public XWindowsInputDriver(XWindowsDriverConnection driverConnection) {

        Objects.requireNonNull(driverConnection);
        
        driverConnection.registerEventListener(event -> {
            triggerEvent(convertEvent(event));
        });
        
        this.driverConnection = driverConnection;
        
        asyncQueryModifierScancodes();
        
        asyncQueryKeyboardMapping();
    }

    private InputEvent convertEvent(XEvent xEvent) {
        
        final InputEvent inputEvent;

        if (xEvent.getEventCode() != Events.MOTION_NOTIFY) {
            System.out.println("## event " + xEvent.getEventCode());
        }
        
        switch (xEvent.getEventCode()) {
        
        case Events.KEY_PRESS: {
            final KeyPress keyPress = (KeyPress)xEvent;

            final int keyCode = keyPress.getDetail().getValue();
            
            inputEvent = new KeyPressEvent(keyCode, getKeyModifier(keyCode), getKeyModifiersState(keyPress.getState()));
            break;
        }
        
        case Events.KEY_RELEASE: {
            final KeyRelease keyRelease = (KeyRelease)xEvent;

            final int keyCode = keyRelease.getDetail().getValue();
            
            inputEvent = new KeyReleaseEvent(keyCode, getKeyModifier(keyCode), getKeyModifiersState(keyRelease.getState()));
            break;
        }
        
        case Events.BUTTON_PRESS:
            final ButtonPress buttonPress = (ButtonPress)xEvent;
            
            inputEvent = new PointerButtonPressEvent(buttonPress.getDetail().getValue());
            break;
        
        case Events.BUTTON_RELEASE:
            final ButtonRelease buttonRelease = (ButtonRelease)xEvent;
            
            inputEvent = new PointerButtonReleaseEvent(buttonRelease.getDetail().getValue());
            break;

        case Events.MOTION_NOTIFY:
            final MotionNotify motionNotify = (MotionNotify)xEvent;
            
            final DisplayDeviceId displayDeviceId = driverConnection.getDisplayDeviceId(motionNotify.getEvent());
            
            inputEvent = new PointerMotionEvent(
                    motionNotify.getRootX().getValue(),
                    motionNotify.getRootY().getValue(),
                    displayDeviceId);
            break;
            
        default:
            throw new UnsupportedOperationException();
        }
        
        return inputEvent;
    }
    
    private int getKeyModifier(int keyCode) {
        
        int keyModifier;
        
        switch (getKeyModifierIdx(keyCode)) {
        
        case 0: keyModifier = KeyModifier.SHIFT; break;
        case 1: keyModifier = KeyModifier.LOCK; break;
        case 2: keyModifier = KeyModifier.CTRL; break;
        case 3: keyModifier = KeyModifier.MOD1; break;
        case 4: keyModifier = KeyModifier.MOD2; break;
        case 5: keyModifier = KeyModifier.MOD3; break;
        case 6: keyModifier = KeyModifier.MOD4; break;
        case 7: keyModifier = KeyModifier.MOD5; break;
            
        default:
            keyModifier = 0;
            break;
        }

        return keyModifier;
    }
    
    private int getKeyModifierIdx(int keyCode) {
        
        final List<ModifierMapping> modifierMappings = modifierScancodes.getModifiers();
        
        for (int i = 0; i < modifierMappings.size(); ++ i) {
            
            final ModifierMapping modifierMapping = modifierMappings.get(i);
            
            for (int scanCode : modifierMapping.getScancodes()) {

                if (scanCode == keyCode) {
                    return i;
                }
            }
        }
        
        return -1;
    }

    private int getKeyModifiersState(SETofKEYBUTMASK mask) {
        
        final short value = mask.getValue();
        
        int modifiers = 0;
        
        if ((value & SETofKEYBUTMASK.SHIFT) != 0) {
            modifiers |= KeyModifier.SHIFT;
        }
        
        if ((value & SETofKEYBUTMASK.LOCK) != 0) {
            modifiers |= KeyModifier.LOCK;
        }

        if ((value & SETofKEYBUTMASK.CONTROL) != 0) {
            modifiers |= KeyModifier.CTRL;
        }

        if ((value & SETofKEYBUTMASK.MOD1) != 0) {
            modifiers |= KeyModifier.MOD1;
        }

        if ((value & SETofKEYBUTMASK.MOD2) != 0) {
            modifiers |= KeyModifier.MOD2;
        }

        if ((value & SETofKEYBUTMASK.MOD3) != 0) {
            modifiers |= KeyModifier.MOD3;
        }

        if ((value & SETofKEYBUTMASK.MOD4) != 0) {
            modifiers |= KeyModifier.MOD4;
        }

        if ((value & SETofKEYBUTMASK.MOD5) != 0) {
            modifiers |= KeyModifier.MOD5;
        }
        
        return modifiers;
    }

    @Override
    public boolean isInitialized() {
        return modifierScancodes != null;
    }

    @Override
    public void pollForEvents() {
        driverConnection.pollForEvents();
    }

    @Override
    public ModifierScancodes getModifierScancodes() {
        
        if (!isInitialized()) {
            throw new IllegalStateException();
        }
        
        return modifierScancodes;
    }
    

    public KeyboardMapping getKeyboardMapping() {

        if (!isInitialized()) {
            throw new IllegalStateException();
        }
        
        return keyboardMapping;
    }

    public void setKeyboardMapping(KeyboardMapping keyboardMapping) {
        this.keyboardMapping = keyboardMapping;
    }

    private void initModifierScanCodes(ModifierScancodes modifierScancodes) {
        
        Objects.requireNonNull(modifierScancodes);
        
        if (this.modifierScancodes != null) {
            throw new IllegalStateException();
        }

        this.modifierScancodes = modifierScancodes;
    }
    
    private void asyncQueryModifierScancodes() {
        
        System.out.println("## initModifierScancodes");
        
        driverConnection.sendRequestWaitReply(new GetModifierMapping(), new ReplyListener() {
            
            @Override
            public void onReply(XReply reply) {
                
                System.out.println("## scancodes response" + reply);
                
                final GetModifierMappingReply modifierMappingReply = (GetModifierMappingReply)reply;

                initModifierScanCodes(makeModifierScancodes(modifierMappingReply));
            }
            
            @Override
            public void onError(XError error) {
                System.out.println("## scancodes error" + error);
            }
        });
    }
    
    private static ModifierScancodes makeModifierScancodes(GetModifierMappingReply modifierMappingReply) {
        final int codesPerModifier = modifierMappingReply.getKeycodesPerModifier().getValue();
        
        final CARD8 [] keycodes = modifierMappingReply.getKeycodes();
        
        final List<ModifierMapping> modifiers = new ArrayList<>(8);

        int idx = 0;
        
        for (int modifierIdx = 0; modifierIdx < 8; ++ modifierIdx) {
            
            final short [] scancodes = new short[codesPerModifier];
            
            for (int code = 0; code < codesPerModifier; ++ code) {
                scancodes[code] = keycodes[idx ++].getValue();
            }
            
            modifiers.add(new ModifierMapping(scancodes));
        }
        
        final ModifierScancodes modifierScancodes = new ModifierScancodes(
                codesPerModifier,
                modifiers);

        return modifierScancodes;
    }
    
    
    private void initKeyboardMapping(KeyboardMapping keyboardMapping) {
        
        Objects.requireNonNull(keyboardMapping);
        
        if (this.keyboardMapping != null) {
            throw new IllegalStateException();
        }

        this.keyboardMapping = keyboardMapping;
    }
    
    private void asyncQueryKeyboardMapping() {
        
        final ServerMessage serverMessage = driverConnection.getServerMessage();
        
        final int count = serverMessage.getMaxKeyCode().getValue() - serverMessage.getMinKeyCode().getValue() + 1;
        
        final GetKeyboardMapping request = new GetKeyboardMapping(serverMessage.getMinKeyCode(), new CARD8((short)count));
        
        driverConnection.sendRequestWaitReply(request, new ReplyListener() {
            
            @Override
            public void onReply(XReply reply) {
                
                System.out.println("## keyboardmapping response" + reply);
                
                final GetKeyboardMappingReply keyboardMappingReply = (GetKeyboardMappingReply)reply;

                initKeyboardMapping(makeKeyboardMapping(count, serverMessage, keyboardMappingReply));
            }
            
            @Override
            public void onError(XError error) {
                System.out.println("## scancodes error" + error);
            }
        });
    }
    
    private static KeyboardMapping makeKeyboardMapping(int count, ServerMessage serverMessage, GetKeyboardMappingReply keyboardMappingReply) {
        final int keysymsPerKeycode = keyboardMappingReply.getKeysymsPerKeycode().getValue();
        
        final KEYSYM [] keysyms = keyboardMappingReply.getKeysyms();
        
        if (keysyms.length % keysymsPerKeycode != 0) {
            throw new IllegalStateException();
        }
        
        if (keysymsPerKeycode * count != keysyms.length) {
            throw new IllegalStateException();
        }

        final int [][] scancodeToKeysym = new int[count][];
        
        int idx = 0;
        
        for (int scancodeIdx = 0; scancodeIdx < count; ++ scancodeIdx) {
            
            scancodeToKeysym[scancodeIdx] = new int[keysymsPerKeycode];
            
            for (int keysymIdx = 0; keysymIdx < keysymsPerKeycode; ++ keysymIdx) {
                scancodeToKeysym[scancodeIdx][keysymIdx] = keysyms[idx ++].getValue();
            }
        }

        return new KeyboardMapping(
                serverMessage.getMinKeyCode().getValue(),
                serverMessage.getMaxKeyCode().getValue(),
                scancodeToKeysym);
    }
}
