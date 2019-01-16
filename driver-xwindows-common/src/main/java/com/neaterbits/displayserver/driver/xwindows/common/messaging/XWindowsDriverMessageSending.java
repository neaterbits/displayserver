package com.neaterbits.displayserver.driver.xwindows.common.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.neaterbits.displayserver.driver.xwindows.common.ReplyListener;
import com.neaterbits.displayserver.driver.xwindows.common.SentRequest;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsMessageListener;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsMessaging;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsNetwork;
import com.neaterbits.displayserver.driver.xwindows.common.XWindowsNetworkFactory;
import com.neaterbits.displayserver.io.common.MessageProcessor;
import com.neaterbits.displayserver.protocol.ByteBufferXWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.Events;
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

public class XWindowsDriverMessageSending implements XWindowsMessaging {

    private final XWindowsNetwork network;

    private final ByteOrder byteOrder;

    private ServerMessage serverMessage;

    private final List<RequestWithReply> requestsWithReply;

    private final XWindowsClientProtocolLog protocolLog;
    
    private final MessageProcessor messageProcessor;
    
    public XWindowsDriverMessageSending(
            XWindowsNetworkFactory networkFactory,
            XWindowsMessageListener messageListener,
            XWindowsClientProtocolLog protocolLog) throws IOException {
        
        Objects.requireNonNull(networkFactory);
        Objects.requireNonNull(messageListener);
        
        this.messageProcessor = new MessageProcessor() {
            
            @Override
            public void onMessage(ByteBuffer byteBuffer, int messageLength) {
                final ServerMessage initialMessage = XWindowsDriverMessageSending.this.onMessage(byteBuffer, messageLength);
                
                if (initialMessage != null) {
                    
                    XWindowsDriverMessageSending.this.serverMessage = serverMessage;
                    
                    messageListener.onInitialMessage(serverMessage);
                }
            }
            
            @Override
            public Integer getLengthOfMessage(ByteBuffer byteBuffer) {
                return XWindowsNetwork.getLengthOfMessage(byteBuffer, receivedInitialMessage());
            }
        };
        
        this.network = networkFactory.connect(messageProcessor);

        // may return null depending on implementation
        // if null, will receive message in onMessage()
        this.serverMessage = network.getInitialMessage();
        
        System.out.println("## initial server message " + serverMessage);
        
        if (serverMessage != null) {
            messageListener.onInitialMessage(serverMessage);
        }
        
        this.protocolLog = protocolLog;

        this.byteOrder = network.getByteOrder();

        this.requestsWithReply = new ArrayList<>();
    }

    public ServerMessage getServerMessage() {
        return serverMessage;
    }
    
    public int allocateResourceId() {
        return network.generateResourceId();
    }

    public void freeResourceId(int resourceId) {
        network.freeResourceId(resourceId);
    }

    private boolean receivedInitialMessage() {
        return serverMessage != null;
    }
    
    private ServerMessage onMessage(ByteBuffer byteBuffer, int messageLength) {

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
        
        System.out.println("## getRequestOpCodeFor " + sequenceNumber + " from " + requestsWithReply);
        
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

    private SentRequest sendRequestWithSequenceNumber(Request request) {
        
        final SentRequest sentRequest;
        
        if (protocolLog != null) {
            protocolLog.onSendRequest(request);
        }
        
        sentRequest = writeRequestToOutputBuffer(request, byteOrder);
        
        if (protocolLog != null) {
            protocolLog.onSentRequest(sentRequest.getLength(), sentRequest.getSequenceNumber(), request);
        }
        
        return sentRequest;
    }
    
    private SentRequest writeRequestToOutputBuffer(Request request, ByteOrder byteOrder) {
        return network.sendRequest(request, byteOrder);
    }
    
    @Override
    public void sendRequest(Request request) {
        sendRequestWithSequenceNumber(request);
    }

    @Override
    public void sendRequestWaitReply(Request request, ReplyListener replyListener) {

        final SentRequest sentRequest = sendRequestWithSequenceNumber(request);

        requestsWithReply.add(new RequestWithReply(sentRequest.getSequenceNumber(), request.getOpCode(), replyListener));

        if (sentRequest.getReply() != null) {
            
            // reply received synchronously
            
            final Integer messageLength = messageProcessor.getLengthOfMessage(sentRequest.getReply());
            
            if (messageLength == null) {
                throw new IllegalStateException();
            }
            
            messageProcessor.onMessage(sentRequest.getReply(), messageLength);
        }
    }

    @Override
    public boolean isPolling() {
        return network.isPolling();
    }
    
    @Override
    public void pollForEvents() {

        final ByteBuffer byteBuffer = network.pollForEvent();

        // System.out.println("## received event " + byteBuffer);
        
        if (byteBuffer != null) {
            // final XWindowsProtocolInputStream stream = new ByteBufferXWindowsProtocolInputStream(byteBuffer);
            
            final int eventCode = byteBuffer.get();
            
            switch (eventCode) {
            case Events.EXPOSE:
                System.out.println("## received expose event");
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown event code " + eventCode);
            }
        }
    }

    @Override
    public void close() throws Exception {
        network.close();
    }
}
