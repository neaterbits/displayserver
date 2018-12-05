package com.neaterbits.displayserver.io.common;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.UnsupportedAddressTypeException;

final class LittleEndianDataOutputStream extends FilterOutputStream implements DataOutput {

    LittleEndianDataOutputStream(OutputStream delegate) {
        super(delegate);
    }

    @Override
    public void writeBoolean(boolean arg0) throws IOException {
        throw new UnsupportedAddressTypeException();
    }

    @Override
    public void writeByte(int b) throws IOException {
        write(b);
    }

    @Override
    public void writeBytes(String arg0) throws IOException {
        throw new UnsupportedAddressTypeException();
    }

    @Override
    public void writeChar(int arg0) throws IOException {
        throw new UnsupportedAddressTypeException();
    }

    @Override
    public void writeChars(String arg0) throws IOException {
        throw new UnsupportedAddressTypeException();
    }

    @Override
    public void writeDouble(double arg0) throws IOException {
        throw new UnsupportedAddressTypeException();
    }

    @Override
    public void writeFloat(float arg0) throws IOException {
        throw new UnsupportedAddressTypeException();
    }

    @Override
    public void writeInt(int i) throws IOException {
        write((i >> 0) & 0x000000FF);
        write((i >> 8) & 0x000000FF);
        write((i >> 16) & 0x000000FF);
        write((i >> 24) & 0x000000FF);
    }

    @Override
    public void writeLong(long arg0) throws IOException {
        throw new UnsupportedAddressTypeException();
    }

    @Override
    public void writeShort(int i) throws IOException {
        write((i >> 0) & 0x000000FF);
        write((i >> 8) & 0x000000FF);
    }

    @Override
    public void writeUTF(String arg0) throws IOException {
        throw new UnsupportedAddressTypeException();
    }
}
