package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.XError;
import com.neaterbits.displayserver.protocol.messages.XReply;

public interface ReplyListener {

    void onReply(XReply reply);
    
    void onError(XError error);

}
