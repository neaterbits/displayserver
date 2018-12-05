package com.neaterbits.displayserver.protocol;

import java.nio.ByteBuffer;

public class XWindowsProtocolUtil {

	public static Integer getMessageLength(ByteBuffer byteBuffer) {
	    
	    final Integer length;
	    
	    if (byteBuffer.get(byteBuffer.position()) == 0) {
	        
	        System.out.println("## error message : " + byteBuffer.remaining());
	        
	        length = byteBuffer.remaining() >= 32 ? 32 : null;
	    }
	    else {
	        
	        System.out.println("## standard message " + byteBuffer.remaining() + "/" + byteBuffer.limit() + "/" + byteBuffer.position());
	        
	        if (byteBuffer.remaining() >= 4) {

	            final int messageLength32Bits = (int)byteBuffer.asShortBuffer().get(1);

	            final int messageLengthBytes = messageLength32Bits * 4;
	            
	            System.out.println("## opCode: " + byteBuffer.get(byteBuffer.position()));
                
                System.out.println("## messageLength: " + messageLengthBytes);
	            
	            if (messageLengthBytes <= byteBuffer.remaining()) {
    	            length = messageLengthBytes;
	            }
	            else {
	                length = null;
	            }
	        }
	        else {
	            length = null;
	        }
	        
	    }

	    return length;
	}
	
	public static int getPadding(int length) {
		final int pad = (4 - (length % 4)) % 4;

		return pad;
	}
}
