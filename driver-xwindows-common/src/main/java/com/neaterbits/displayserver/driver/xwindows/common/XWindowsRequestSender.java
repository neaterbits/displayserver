package com.neaterbits.displayserver.driver.xwindows.common;

import com.neaterbits.displayserver.protocol.messages.Request;

public interface XWindowsRequestSender {

	void sendRequest(Request request);
	
    void sendRequestWaitReply(Request request, ReplyListener replyListener);
}
