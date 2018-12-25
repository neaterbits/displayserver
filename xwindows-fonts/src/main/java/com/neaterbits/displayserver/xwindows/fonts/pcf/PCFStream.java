package com.neaterbits.displayserver.xwindows.fonts.pcf;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

final class PCFStream extends FilterInputStream {
    
    private int offset;
    
    PCFStream(InputStream in) {
        super(in);
        
        this.offset = 0;
    }
    
    @Override
    public int read(byte[] b) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long skip(long n) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {

        throw new UnsupportedOperationException();
    }

    int readBytesOrEOF(byte[] b) throws IOException {
        
        int toRead = b.length;
        int dstOffset = 0;
        
        do {
            final int bytesRead = super.read(b, dstOffset, toRead);
            
            dstOffset += bytesRead;
            toRead -= bytesRead;
            
            this.offset += bytesRead;
            
        } while (toRead != 0);
            
        return b.length;
    }

    long skipBytes(long n) throws IOException {
        final long skipped = super.skip(n);
        
        offset += skipped;
        
        return skipped;
    }

    int readByte() throws IOException {
        ++ offset;
        
        return super.read();
    }
    
    int readInt(ByteOrder byteOrder) throws IOException {
        
        final int value;
        
        if (byteOrder.equals(ByteOrder.BIG_ENDIAN)) {
            value = super.read() << 24 | super.read() << 16 | super.read() << 8 | super.read();
        }
        else if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
            value = super.read() | (super.read() << 8) | (super.read() << 16) | (super.read() << 24);
        }
        else {
            throw new IllegalArgumentException();
        }
        
        offset += 4;
        
        return value;
    }
    
    int readString(StringBuilder sb) throws IOException {
        
        int c;
        
        int bytesRead = 0;
        
        while (0 != (c = super.read())) {
            sb.append((char)c);
            
            ++ bytesRead;
        }
        
        ++ bytesRead;

        offset += bytesRead;

        return bytesRead;
    }
    
    short readUnsignedByte() throws IOException {

        ++ offset;
        
        return (short)super.read();
    }
    
    short readShort(ByteOrder byteOrder) throws IOException {
        final short value;
        
        if (byteOrder.equals(ByteOrder.BIG_ENDIAN)) {
            value = (short)(super.read() << 8 | super.read());
        }
        else if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
            value = (short)(super.read() | (super.read() << 8));
        }
        else {
            throw new IllegalArgumentException();
        }
        
        offset += 2;
        
        return value;
    }

    int readUnsignedShort(ByteOrder byteOrder) throws IOException {
        final int value;
        
        if (byteOrder.equals(ByteOrder.BIG_ENDIAN)) {
            value = super.read() << 8 | super.read();
        }
        else if (byteOrder.equals(ByteOrder.LITTLE_ENDIAN)) {
            value = super.read() | (super.read() << 8);
        }
        else {
            throw new IllegalArgumentException();
        }

        offset += 2;
        
        return value;
    }

    int getOffset() {
        return offset;
    }
}
