package com.neaterbits.displayserver.io.common;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface ProcessMessage<CLIENT> {

	void onMessage(CLIENT clientState, ByteBuffer byteBuffer, int messageLength);
	
}
