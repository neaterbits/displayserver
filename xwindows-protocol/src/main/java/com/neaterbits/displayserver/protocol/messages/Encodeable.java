package com.neaterbits.displayserver.protocol.messages;

import java.io.IOException;
import java.nio.ByteOrder;

import com.neaterbits.displayserver.io.common.DataWriter;
import com.neaterbits.displayserver.protocol.DataOutputXWindowsProtocolOutputStream;
import com.neaterbits.displayserver.protocol.XWindowsProtocolOutputStream;

public interface Encodeable {

	void encode(XWindowsProtocolOutputStream stream) throws IOException;
	
	default <T extends Encodeable> void encodeArray(T [] array, XWindowsProtocolOutputStream stream) throws IOException {
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

   public default byte [] writeToBuf(ByteOrder byteOrder) {
       return DataWriter.writeToBuf(byteOrder, makeDataWriter(this));
   }
}
