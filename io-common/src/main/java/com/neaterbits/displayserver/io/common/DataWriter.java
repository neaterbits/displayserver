package com.neaterbits.displayserver.io.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

@FunctionalInterface
public interface DataWriter {
    
    void writeData(DataOutput dataOutputStream) throws IOException;
    
    public static byte [] writeToBuf(DataWriter writeData, ByteOrder byteOrder) {

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final DataOutput dataOutputStream;
        
        if (byteOrder.equals(ByteOrder.BIG_ENDIAN)) {
            dataOutputStream = new DataOutputStream(baos);
        }
        else {
            dataOutputStream = new LittleEndianDataOutputStream(baos);
        }
        
        try {
            writeData.writeData(dataOutputStream);
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
        
        final byte [] data = baos.toByteArray();
        
        return data;
    }

}
