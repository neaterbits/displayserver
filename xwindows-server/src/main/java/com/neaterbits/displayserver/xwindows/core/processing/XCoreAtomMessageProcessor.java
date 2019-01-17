package com.neaterbits.displayserver.xwindows.core.processing;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsServerProtocolLog;
import com.neaterbits.displayserver.protocol.messages.replies.InternAtomReply;
import com.neaterbits.displayserver.protocol.messages.requests.InternAtom;
import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.xwindows.model.Atoms;
import com.neaterbits.displayserver.xwindows.processing.XClientOps;
import com.neaterbits.displayserver.xwindows.processing.XOpCodeProcessor;

public final class XCoreAtomMessageProcessor extends XOpCodeProcessor {

    private final Atoms atoms;

    
    public XCoreAtomMessageProcessor(XWindowsServerProtocolLog protocolLog) {
        super(protocolLog);

        this.atoms = new Atoms();
    }

    @Override
    protected int[] getOpCodes() {
        return new int [] {
                OpCodes.INTERN_ATOM
        };
    }

    @Override
    protected void onMessage(XWindowsProtocolInputStream stream, int messageLength, int opcode, CARD16 sequenceNumber,
            XClientOps client) throws IOException {

        switch (opcode) {
        
        case OpCodes.INTERN_ATOM: {
            
            final InternAtom internAtom = log(messageLength, opcode, sequenceNumber, InternAtom.decode(stream));
            
            final ATOM atom;
            
            if (internAtom.getOnlyIfExists()) {
                final ATOM existing = atoms.getAtom(internAtom.getName());
                
                atom = existing != null ? existing : ATOM.None;
            }
            else {
                atom = atoms.addIfNotExists(internAtom.getName());
            }
            
            sendReply(client, new InternAtomReply(sequenceNumber, atom));
            break;
        }
        
        }
    }
}
