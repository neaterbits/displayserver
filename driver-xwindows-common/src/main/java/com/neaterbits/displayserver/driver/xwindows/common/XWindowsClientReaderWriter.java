package com.neaterbits.displayserver.driver.xwindows.common;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import com.neaterbits.displayserver.io.common.NonBlockingChannelWriterLog;
import com.neaterbits.displayserver.protocol.XWindowsProtocolUtil;

abstract class XWindowsClientReaderWriter extends XWindowsChannelReaderWriter {

    XWindowsClientReaderWriter(int port, NonBlockingChannelWriterLog log) {
        super(port, log);
    }
    
    @Override
    public Integer getLengthOfMessage(ByteBuffer byteBuffer) {

        final Integer length;
        
        if (receivedInitialMessage()) {
            length = XWindowsProtocolUtil.getReplyOrEventLength(byteBuffer);
        }
        else {
            final byte initialByte = byteBuffer.get(byteBuffer.position());
            
            if (initialByte == 0) {
                
                if (byteBuffer.remaining() > 1) {
                    final int reasonLength = byteBuffer.get(byteBuffer.position() + 1);
                
                    final int completeLength = 8 + reasonLength + XWindowsProtocolUtil.getPadding(reasonLength);
                    
                    length = completeLength <= byteBuffer.remaining() ? completeLength : null;
                }
                else {
                    length = null;
                }
            }
            else if (initialByte == 1) {
                
                if (byteBuffer.remaining() >= 8) {
                
                    // initial server message
                    final ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
                    
                    final int additionalDataLength = shortBuffer.get(shortBuffer.position() + 3);
                    
                    final int totalLength = 8 + additionalDataLength * 4;
                    
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
            }
            else {
                throw new UnsupportedOperationException("initialByte: " + initialByte);
            }
        }
        
        return length;
    }

}
