package com.neaterbits.displayserver.events.xwindows;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.neaterbits.displayserver.driver.xwindows.common.ReplyListener;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsDriverConnection;
import com.neaterbits.displayserver.events.common.BaseInputDriver;
import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.events.common.KeyboardMapping;
import com.neaterbits.displayserver.events.common.Modifier;
import com.neaterbits.displayserver.events.common.ModifierScancodes;
import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.replies.GetKeyboardMappingReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetModifierMappingReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetKeyboardMapping;
import com.neaterbits.displayserver.protocol.messages.requests.GetModifierMapping;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.KEYSYM;

public final class XWindowsInputDriver extends BaseInputDriver implements InputDriver {
	
    private final XWindowsDriverConnection driverConnection;
    
    private ModifierScancodes modifierScancodes;
    
    private KeyboardMapping keyboardMapping;
    
    public XWindowsInputDriver(XWindowsDriverConnection driverConnection) {

        Objects.requireNonNull(driverConnection);
        
        driverConnection.registerEventListener(event -> {
            
        });
        
        this.driverConnection = driverConnection;
        
        asyncQueryModifierScancodes();
        
        asyncQueryKeyboardMapping();
    }

    @Override
    public boolean isInitialized() {
        return modifierScancodes != null;
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
            public void onReply(Reply reply) {
                
                System.out.println("## scancodes response" + reply);
                
                final GetModifierMappingReply modifierMappingReply = (GetModifierMappingReply)reply;

                initModifierScanCodes(makeModifierScancodes(modifierMappingReply));
            }
            
            @Override
            public void onError(Error error) {
                System.out.println("## scancodes error" + error);
            }
        });
    }
    
    private static ModifierScancodes makeModifierScancodes(GetModifierMappingReply modifierMappingReply) {
        final int codesPerModifier = modifierMappingReply.getKeycodesPerModifier().getValue();
        
        final CARD8 [] keycodes = modifierMappingReply.getKeycodes();
        
        final List<Modifier> modifiers = new ArrayList<>(8);

        int idx = 0;
        
        for (int modifierIdx = 0; modifierIdx < 8; ++ modifierIdx) {
            
            final short [] scancodes = new short[codesPerModifier];
            
            for (int code = 0; code < codesPerModifier; ++ code) {
                scancodes[code] = keycodes[idx ++].getValue();
            }
            
            modifiers.add(new Modifier(scancodes));
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
            public void onReply(Reply reply) {
                
                System.out.println("## keyboardmapping response" + reply);
                
                final GetKeyboardMappingReply keyboardMappingReply = (GetKeyboardMappingReply)reply;

                initKeyboardMapping(makeKeyboardMapping(count, serverMessage, keyboardMappingReply));
            }
            
            @Override
            public void onError(Error error) {
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
