package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.enums.OpCodes;
import com.neaterbits.displayserver.protocol.logging.XWindowsClientProtocolLog;
import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.messages.Request;
import com.neaterbits.displayserver.protocol.messages.ServerToClientMessage;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ClientConnectionError;
import com.neaterbits.displayserver.protocol.messages.protocolsetup.ServerMessage;
import com.neaterbits.displayserver.protocol.messages.replies.GetImageReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetKeyboardMappingReply;
import com.neaterbits.displayserver.protocol.messages.replies.GetModifierMappingReply;
import com.neaterbits.displayserver.protocol.types.CARD16;

class XWindowsDriverMessageProcessing {
    
    private final XWindowsClientProtocolLog protocolLog;

    private final ByteOrder byteOrder;

    private ServerMessage serverMessage;

    private int sequenceNumber;
    private final List<RequestWithReply> requestsWithReply;

    XWindowsDriverMessageProcessing(XWindowsClientProtocolLog protocolLog) {
        
        Objects.requireNonNull(protocolLog);
    
        
        this.protocolLog = protocolLog;
        
        this.byteOrder = ByteOrder.BIG_ENDIAN;

        this.sequenceNumber = 1;
        this.requestsWithReply = new ArrayList<>();
    }

    ByteOrder getByteOrder() {
        return byteOrder;
    }

    void sendRequest(Request request, XWindowsChannelReaderWriter readerWriter) {
        sendRequestWithSequenceNumber(request, readerWriter);
        
        ++ sequenceNumber;
    }

    void sendRequestWaitReply(Request request, ReplyListener replyListener, XWindowsChannelReaderWriter readerWriter) {

        final int seq = this.sequenceNumber;
        
        requestsWithReply.add(new RequestWithReply(seq, request.getOpCode(), replyListener));

        ++ sequenceNumber;

        sendRequestWithSequenceNumber(request, readerWriter);
    }

    ServerMessage getServerMessage() {
        return serverMessage;
    }

    boolean receivedInitialMessage() {
        return getServerMessage() != null;
    }

    ServerMessage onMessage(ByteBuffer byteBuffer, int messageLength) {

        ServerMessage initialMessage = null;
        
        if (byteBuffer.get(byteBuffer.position()) == 0) {
            
            if (receivedInitialMessage()) {
                try {
                    final Error error = readError(byteBuffer);
                    
                    final CARD16 sequenceNumber = error.getSequenceNumber();
                    
                    final RequestWithReply requestWithReply = getRequestWithReply(sequenceNumber);
                    
                    if (requestWithReply != null) {
                        requestWithReply.listener.onError(error);
                    }
                    
                } catch (IOException ex) {
                    throw new IllegalStateException(ex);
                }
            }
            else {
                readInitialMessageError(byteBuffer);
            }
        }
        else {
            try {
                if (serverMessage == null) {
                    serverMessage = processInitialServerMessage(byteBuffer, messageLength);
                    
                    System.out.println("## received servermessage depths " + serverMessage.getScreens()[0].getAllowedDepths().length);
                    
                    initialMessage = serverMessage;
                    
                }
                else {
                    final ServerToClientMessage message = processResponseMessage(
                            byteBuffer,
                            messageLength,
                            seq -> getRequestOpCodeFor(seq));
                    
                    final CARD16 sequenceNumber = message.getSequenceNumber();
                    
                    final RequestWithReply requestWithReply = getRequestWithReply(sequenceNumber);
                    
                    if (requestWithReply != null) {
                        requestWithReply.listener.onReply((Reply)message);
                    }

                }
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
        
        return initialMessage;
    }
    

    private static ServerMessage processInitialServerMessage(ByteBuffer byteBuffer, int messageLength) throws IOException {

        final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
        
        return ServerMessage.decode(stream);
    }

    private ServerToClientMessage processResponseMessage(
            ByteBuffer byteBuffer,
            int messageLength,
            Function<Integer, Integer> getRequestOpCode) throws IOException {
        
        final int opcode = byteBuffer.get();
        
        final ServerToClientMessage message;
        
        final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
        
        switch (opcode) {
        
        case 1:
            
            final int sequenceNumber = byteBuffer.getShort(byteBuffer.position() + 1);
            
            final int requestOpCode = getRequestOpCode.apply(sequenceNumber);
            
            final Reply reply;
            
            switch (requestOpCode) {
            case OpCodes.GET_IMAGE:
                reply = GetImageReply.decode(stream);
                break;
                
            case OpCodes.GET_KEYBOARD_MAPPING:
                reply = GetKeyboardMappingReply.decode(stream);
                break;
                
            case OpCodes.GET_MODIFIER_MAPPING:
                reply = GetModifierMappingReply.decode(stream);
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown request opcode " + requestOpCode);
            }
            
            if (protocolLog != null) {
                protocolLog.onReceivedReply(reply);
            }

            message = reply;
            break;
        
        default:
            throw new UnsupportedOperationException("Unknown opcode " + opcode);
        }
        
        return message;
    }

    private int getRequestOpCodeFor(int sequenceNumber) {
        
        for (RequestWithReply requestWithReply : requestsWithReply) {
            if (requestWithReply.sequenceNumber == sequenceNumber) {
                return requestWithReply.opCode;
            }
        }
        
        throw new IllegalStateException();
    }

    private RequestWithReply getRequestWithReply(CARD16 sequenceNumber) {
        final Iterator<RequestWithReply> iter = requestsWithReply.iterator();
        
        RequestWithReply requestWithReply = null;
        
        while (iter.hasNext()) {
            final RequestWithReply seq = iter.next();
            
            if (seq.sequenceNumber == sequenceNumber.getValue()) {
                
                requestWithReply = seq;
                
                iter.remove();
                
                break;
            }
        }
    
        return requestWithReply;
    }
    
    private void readInitialMessageError(ByteBuffer byteBuffer) {

        final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
        
        try {
            
            final byte errorCode = byteBuffer.get(byteBuffer.position() + 1);
            final int sequenceNumber = byteBuffer.getShort(byteBuffer.position() + 3);
            
            final ClientConnectionError clientError = ClientConnectionError.decode(stream);

            final String reason = clientError.getReason();
            
            if (protocolLog != null) {
                protocolLog.onInitialMessageError(errorCode, sequenceNumber, reason);
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    private com.neaterbits.displayserver.protocol.messages.Error readError(ByteBuffer byteBuffer) throws IOException {

        final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
        
        final com.neaterbits.displayserver.protocol.messages.Error error = com.neaterbits.displayserver.protocol.messages.Error.decode(stream);

        if (protocolLog != null) {
            protocolLog.onReceivedError(error);
        }
        
        return error;
    }

    private void sendRequestWithSequenceNumber(Request request, XWindowsChannelReaderWriter readerWriter) {
        try {
            final int messageLength = readerWriter.writeRequest(request, byteOrder);
            
            if (protocolLog != null) {
                protocolLog.onSendRequest(messageLength, request);
            }
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
