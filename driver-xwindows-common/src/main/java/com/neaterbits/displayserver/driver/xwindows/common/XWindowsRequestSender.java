package com.neaterbits.displayserver.driver.xwindows.common;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.messages.Request;

public interface XWindowsRequestSender {

	void sendRequest(Request request) throws IOException;
	
}
