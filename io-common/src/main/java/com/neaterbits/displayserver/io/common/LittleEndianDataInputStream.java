package com.neaterbits.displayserver.io.common;

import java.io.DataInput;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class LittleEndianDataInputStream extends FilterInputStream implements DataInput {

    public LittleEndianDataInputStream(InputStream in) {
        super(in);
    }

    @Override
    public boolean readBoolean() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte readByte() throws IOException {
        return (byte)super.read();
    }

    @Override
    public char readChar() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double readDouble() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public float readFloat() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readFully(byte[] buffer, int offset, int length) throws IOException {
        super.read(buffer, offset, length);
    }

    @Override
    public void readFully(byte[] buffer) throws IOException {
        super.read(buffer);
    }

    @Override
    public int readInt() throws IOException {
        return super.read() | (super.read() << 8) | (super.read() << 16) | (super.read() << 24);
    }

    @Override
    public String readLine() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long readLong() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public short readShort() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String readUTF() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int skipBytes(int arg0) throws IOException {
        throw new UnsupportedOperationException();
    }
}
