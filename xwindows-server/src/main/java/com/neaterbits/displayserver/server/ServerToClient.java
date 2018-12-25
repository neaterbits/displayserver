package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.protocol.messages.Event;
import com.neaterbits.displayserver.protocol.messages.Reply;
import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.CARD16;
import com.neaterbits.displayserver.protocol.types.WINDOW;

interface ServerToClient {

    void sendEvent(XClient client, WINDOW window, Event event);

    void sendReply(XClient client, Reply reply);

    void sendError(XClient client, BYTE errorCode, CARD16 sequenceNumber, long value, int opcode);

}
