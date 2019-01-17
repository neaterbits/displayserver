package com.neaterbits.displayserver.xwindows.processing;

import com.neaterbits.displayserver.protocol.messages.Encodeable;

public interface XConnectionOps {

    void send(Encodeable message);

}
