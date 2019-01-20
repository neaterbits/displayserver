package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.XRequest;

public interface XWindowsRequestSender {

	void sendRequest(XRequest request);
	
    void sendRequestWaitReply(XRequest request, ReplyListener replyListener);
}
