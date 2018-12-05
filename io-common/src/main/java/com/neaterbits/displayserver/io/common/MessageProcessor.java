package com.neaterbits.displayserver.io.common;

import java.nio.ByteBuffer;

public interface MessageProcessor {

	Integer getLengthOfMessage(ByteBuffer byteBuffer);
	
	void onMessage(ByteBuffer byteBuffer, int messageLength);

}
