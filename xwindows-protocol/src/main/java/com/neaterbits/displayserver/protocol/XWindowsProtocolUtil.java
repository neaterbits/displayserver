package com.neaterbits.displayserver.protocol;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import com.neaterbits.displayserver.protocol.types.CARD8;
import com.neaterbits.displayserver.xwindows.util.Padding;

public class XWindowsProtocolUtil {

    public static Integer getInitialMessageLength(ByteBuffer byteBuffer) {

        final Integer length;
        
        if (byteBuffer.remaining() >= 12) {
            
            final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
            
            final int authProtocolNameLength = shortBuffer.get(shortBuffer.position() + 3);
            final int authProtocolDataLength = shortBuffer.get(shortBuffer.position() + 4);
            
            final int authNameLength = authProtocolNameLength + XWindowsProtocolUtil.getPadding(authProtocolNameLength);
            final int authDataLength = authProtocolDataLength + XWindowsProtocolUtil.getPadding(authProtocolDataLength);
            
            final int totalLength = 12 + authNameLength + authDataLength;
        
            if (totalLength <= byteBuffer.remaining()) {
                length = totalLength;
            }
            else {
                length = null;
            }
        }
        else {
            length = null;
        }
    
        return length;
    }
    
	public static Integer getRequestLength(ByteBuffer byteBuffer) {
	    
	    final Integer length;
	    
	    if (byteBuffer.get(byteBuffer.position()) == 0) {
	        
	        // System.out.println("## error message : " + byteBuffer.remaining());
	        
	        length = byteBuffer.remaining() >= 32 ? 32 : null;
	    }
	    else {
	        
	        // System.out.println("## standard message " + byteBuffer.remaining() + "/" + byteBuffer.limit() + "/" + byteBuffer.position());
	        
	        if (byteBuffer.remaining() >= 4) {

	            final int messageLength32Bits = (int)byteBuffer.getShort(byteBuffer.position() + 2);

	            final int messageLengthBytes = messageLength32Bits * 4;

                // System.out.println("## messageLength: " + messageLengthBytes);
                
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

   public static Integer getReplyOrEventLength(ByteBuffer byteBuffer) {
        
        final Integer length;
        
        if (byteBuffer.get(byteBuffer.position()) == 0) {
            
            // System.out.println("## error message : " + byteBuffer.remaining());
            
            length = byteBuffer.remaining() >= 32 ? 32 : null;
        }
        else if (byteBuffer.get(byteBuffer.position()) == 1) {
            // System.out.println("## standard message " + byteBuffer.remaining() + "/" + byteBuffer.limit() + "/" + byteBuffer.position());
            
            if (byteBuffer.remaining() >= 4) {

                final long messageLength32Bits = (int)byteBuffer.getInt(byteBuffer.position() + 4);

                final long messageLengthBytes = messageLength32Bits * 4;

                // System.out.println("## messageLength: " + messageLengthBytes);
                
                if (messageLengthBytes <= byteBuffer.remaining()) {
                    
                    if (messageLengthBytes > Integer.MAX_VALUE) {
                        throw new IllegalStateException();
                    }
                    
                    length = (int)messageLengthBytes;
                }
                else {
                    length = null;
                }
            }
            else {
                length = null;
            }
        }
        else if (byteBuffer.get(byteBuffer.position()) > 1) {
            length = byteBuffer.remaining() >= 32 ? 32 : null;
        }
        else {
            throw new UnsupportedOperationException("TODO");
        }

        return length;
    }

	
	public static int getPadding(int length) {
	    return Padding.getPadding(length, 4);
	}

	public static int getPropertyEncodeDataLength(CARD8 format, int dataLength) {
	    
	    final int lengthValue;
	    
        switch (format.getValue()) {
        case 0:
            lengthValue = 0;
            break;
            
        case 8:
            lengthValue = dataLength;
            break;
            
        case 16:
            lengthValue = dataLength / 2;
            break;
            
        case 32:
            lengthValue = dataLength / 4;
            break;
            
        
        default:
            throw new UnsupportedOperationException();
        }

        return lengthValue;
	}

    public static int getPropertyDecodeDataLength(CARD8 format, int dataLength) {
        
        final int lengthValue;
        
        switch (format.getValue()) {
        case 0:
            lengthValue = 0;
            break;
            
        case 8:
            lengthValue = dataLength;
            break;
            
        case 16:
            lengthValue = dataLength * 2;
            break;
            
        case 32:
            lengthValue = dataLength * 4;
            break;
        
        default:
            throw new UnsupportedOperationException();
        }

        return lengthValue;
    }
}
