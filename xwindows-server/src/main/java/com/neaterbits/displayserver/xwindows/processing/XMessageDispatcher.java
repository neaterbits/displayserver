package com.neaterbits.displayserver.xwindows.processing;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.types.CARD16;

public class XMessageDispatcher extends XMessageProcessor {

    private final Map<Integer, XOpCodeProcessor> messageProcessorByOpCode;
    
    public XMessageDispatcher(Collection<XOpCodeProcessor> opcodeProcessors) {
        super(null);

        this.messageProcessorByOpCode = new HashMap<>();
        
        for (XOpCodeProcessor opcodeProcessor : opcodeProcessors) {
            for (int opcode : opcodeProcessor.getOpCodes()) {
                messageProcessorByOpCode.put(opcode, opcodeProcessor);
            }
        }
    }

    @Override
    protected void onMessage(XWindowsProtocolInputStream stream, int messageLength, int opcode, CARD16 sequenceNumber, XClientOps client) throws IOException {

        final XMessageProcessor messageProcessor = messageProcessorByOpCode.get(opcode);
        
        if (messageProcessor == null) {
            throw new UnsupportedOperationException("No message processor for opcode " + opcode);
        }
        
        messageProcessor.onMessage(stream, messageLength, opcode, sequenceNumber, client);
    }
}
