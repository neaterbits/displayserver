package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.Error;
import com.neaterbits.displayserver.protocol.messages.Reply;

public interface ReplyListener {

    void onReply(Reply reply);
    
    void onError(Error error);

}
