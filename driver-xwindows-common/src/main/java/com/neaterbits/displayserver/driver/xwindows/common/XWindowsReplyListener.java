package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.XReply;

public interface XWindowsReplyListener {

    void onReply(XReply reply);
    
}
