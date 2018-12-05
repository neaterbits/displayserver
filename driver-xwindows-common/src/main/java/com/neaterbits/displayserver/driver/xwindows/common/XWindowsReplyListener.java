package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.Reply;

public interface XWindowsReplyListener {

    void onReply(Reply reply);
    
}
