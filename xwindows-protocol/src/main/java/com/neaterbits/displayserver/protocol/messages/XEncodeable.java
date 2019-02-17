package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;
import java.nio.ByteOrder;

import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.protocol.DataOutputXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolInputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.types.CARD32;
import com.neaterbits.displayserver.util.ArrayUtil;
import com.neaterbits.displayserver.util.logging.LogUtil;

public abstract class XEncodeable {

	public abstract void encode(XWindowsProtocolOutputStream stream) throws IOException;
	
	protected final <T extends XEncodeable> void encodeArray(T [] array, XWindowsProtocolOutputStream stream) throws IOException {
		for (T element : array) {
			element.encode(stream);
		}
	}
	
	@FunctionalInterface
	public interface EncodeFunction {
	    void encode(XWindowsProtocolOutputStream stream) throws IOException;
	}
	
	@FunctionalInterface
	public interface DecodeArrayElement<T> {
	    T decode(XWindowsProtocolInputStream stream) throws IOException;
	}

	protected static <T extends XEncodeable> T [] decodeArray(
	        XWindowsProtocolInputStream stream,
	        T [] array,
	        DecodeArrayElement<T> decode) throws IOException {
	    
	    for (int i = 0; i < array.length; ++ i) {
	        array[i] = decode.decode(stream);
	    }
	    
	    return array;
	}


    public static DataWriter makeDataWriter(XEncodeable encodeable) {
        return dataOutputStream -> {
            final XWindowsProtocolOutputStream protocolOutputStream = new DataOutputXWindowsProtocolOutputStream(dataOutputStream);
            
            encodeable.encode(protocolOutputStream);
        };
    }

   protected final byte [] writeToBuf(ByteOrder byteOrder) {
       return DataWriter.writeToBuf(makeDataWriter(this), byteOrder);
   }
   
   public Object [] getDebugParams() {
       return null;
   }
   
   public static <T extends XEncodeable>
   String outputArrayInBrackets(T [] encodeables) {
       
       final StringBuilder sb = new StringBuilder("[ ");
       
       for (T encodeable : encodeables) {
           final Object [] debugParams = encodeable.getDebugParams();
           
           sb.append(LogUtil.outputParametersInBrackets(debugParams));
           
           sb.append(" ");
       }

       sb.append(']');
       
       return sb.toString();
   }

   protected final Object [] merge(Object [] objs1, Object [] objs2) {

       return ArrayUtil.merge(objs1, objs2, Object[]::new);
   }
   
   protected final Object [] wrap(Object ... objects) {
       return LogUtil.wrap(objects);
   }
   
   public final String toDebugString() {
       
       final StringBuilder sb = new StringBuilder();
       
       sb.append(getClass().getSimpleName());
       
       final Object [] debugParams = getDebugParams();
       
       if (debugParams != null) {
           sb.append(" ");
           
           LogUtil.outputParametersInBrackets(sb::append, debugParams);
       }
       
       return sb.toString();
   }
   
   protected static String hex32(CARD32 value) {
       return value != null ? String.format("%08x", value.getValue()) : null;
   }
}
