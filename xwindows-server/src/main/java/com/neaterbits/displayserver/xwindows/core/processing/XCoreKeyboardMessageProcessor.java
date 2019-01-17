package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;
import java.util.Arrays;

import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.events.common.KeyboardMapping;
import com.neaterbits.displayserver.events.common.Modifier;
import com.neaterbits.displayserver.events.common.ModifierScancodes;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.Errors;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.GetKeyboardMappingReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetModifierMappingReply;
import com.neaterbits.displayserver.protocol.messages.requests.GetKeyboardMapping;
import com.neaterbits.displayserver.protocol.messages.requests.GetModifierMapping;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.protocol.types.KEYSYM;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreKeyboardMessageProcessor extends XOpCodeProcessor {

    private final InputDriver inputDriver;
    
    public XCoreKeyboardMessageProcessor(XWindowsServerProtocolLog protocolLog, InputDriver inputDriver) {
        super(protocolLog);
    
        this.inputDriver = inputDriver;
    }

    @Override
    protected int[] getOpCodes() {

        return new int [] {
                OpCodes.GET_KEYBOARD_MAPPING,
                OpCodes.GET_MODIFIER_MAPPING
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

        case OpCodes.GET_KEYBOARD_MAPPING: {
            
            final GetKeyboardMapping getKeyboardMapping = log(messageLength, opcode, sequenceNumber, GetKeyboardMapping.decode(stream));
            
            final KeyboardMapping keyboardMapping = inputDriver.getKeyboardMapping();
         
            final int index = getKeyboardMapping.getFirstKeycode().getValue() - keyboardMapping.getMinScancode();
            
            final int count = getKeyboardMapping.getCount().getValue();
            
            if (index < 0) {
                sendError(client, Errors.Value, sequenceNumber, getKeyboardMapping.getFirstKeycode().getValue(), opcode);
            }
            else if (index + getKeyboardMapping.getCount().getValue() > keyboardMapping.getNumScancodes()) {
                sendError(client, Errors.Value, sequenceNumber, count, opcode);
            }
            else {
                
                final int keysymsPerKeycode = Arrays.stream(keyboardMapping.getScancodeToKeysym())
                        .skip(index)
                        .limit(count)
                        .map(keysyms -> keysyms.length)
                        .max(Integer::compare)
                        .orElse(0);
                
                
                final KEYSYM [] keysyms = new KEYSYM[count * keysymsPerKeycode];
                
                int dstIdx = 0;
                
                for (int i = 0; i < count; ++ i) {
                    final int [] srcKeysyms = keyboardMapping.getKeysyms(index + i);
                    
                    if (srcKeysyms.length > keysymsPerKeycode) {
                        throw new IllegalStateException();
                    }
                    
                    for (int j = 0; j < srcKeysyms.length; ++ j) {
                        keysyms[dstIdx ++] = new KEYSYM(srcKeysyms[j]);
                    }
                    
                    final int remaining = keysymsPerKeycode - srcKeysyms.length;

                    for (int j = 0; j < remaining; ++ j) {
                        keysyms[dstIdx ++] = KEYSYM.NoSymbol;
                    }
                }
                
                if (dstIdx != keysyms.length) {
                    throw new IllegalStateException();
                }
                
                final GetKeyboardMappingReply reply = new GetKeyboardMappingReply(
                        sequenceNumber,
                        new BYTE((byte)keysymsPerKeycode),
                        keysyms);
                
                sendReply(client, reply);
            }
            break;
        }
        
        case OpCodes.GET_MODIFIER_MAPPING: {
            
            log(messageLength, opcode, sequenceNumber, GetModifierMapping.decode(stream));
            
            final ModifierScancodes modifierScancodes = inputDriver.getModifierScancodes();
            
            final CARD8 [] keycodes = new CARD8[modifierScancodes.getCodesPerModifier() * 8];
            
            int dstIdx = 0;
            
            for (Modifier modifier : modifierScancodes.getModifiers()) {
                for (short scancode : modifier.getScancodes()) {
                    keycodes[dstIdx ++] = new CARD8(scancode);
                }
            }
            
            sendReply(client, new GetModifierMappingReply(
                    sequenceNumber,
                    new BYTE((byte)modifierScancodes.getCodesPerModifier()),
                    keycodes));
            break;
        }

        }
    }
}
