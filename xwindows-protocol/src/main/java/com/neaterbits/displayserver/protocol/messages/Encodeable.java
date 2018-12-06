package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;
import java.nio.ByteOrder;

import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.protocol.DataOutputXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.util.logging.LogUtil;

public abstract class Encodeable {

	public abstract void encode(XWindowsProtocolOutputStream stream) throws IOException;
	
	protected final <T extends Encodeable> void encodeArray(T [] array, XWindowsProtocolOutputStream stream) throws IOException {
		for (T element : array) {
			element.encode(stream);
		}
	}

   public static DataWriter makeDataWriter(Encodeable encodeable) {
        return dataOutputStream -> {
            final XWindowsProtocolOutputStream protocolOutputStream = new DataOutputXWindowsProtocolOutputStream(dataOutputStream);
            
            encodeable.encode(protocolOutputStream);
        };
    }

   protected final byte [] writeToBuf(ByteOrder byteOrder) {
       return DataWriter.writeToBuf(byteOrder, makeDataWriter(this));
   }
   
   public Object [] getDebugParams() {
       return null;
   }
   
   protected final Object [] wrap(Object ... objects) {
       return LogUtil.wrap(objects);
   }
   
   public final String toDebugString() {
       
       final StringBuilder sb = new StringBuilder();
       
       sb.append(getClass().getSimpleName());
       
       final Object [] debugParams = getDebugParams();
       
       if (debugParams != null) {
           sb.append(" [");
           
           LogUtil.outputParameters(sb::append, debugParams);
           
           sb.append(']');
       }
       
       return sb.toString();
   }
}
