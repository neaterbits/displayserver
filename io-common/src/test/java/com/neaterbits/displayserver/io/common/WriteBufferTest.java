package com.neaterbits.displayserver.io.common;

import java.io.IOException;

import org.junit.Test;

import com.neaterbits.displayserver.util.logging.DebugLevel;

import static org.assertj.core.api.Assertions.assertThat;

public class WriteBufferTest {

    private static class Value<T> {
        private T value;
    }
    
    @Test
    public void testSimpleWrite() throws IOException {
        
        final WriteBuffer buffer = new WriteBuffer(100);
        
        final byte [] data = "test123".getBytes();
        
        
        final Value<byte[]> value = new Value<>();
        
        buffer.write(data, 0, data.length, null);
        
        buffer.onWriteable(byteBuffer -> {
            
            final int length = byteBuffer.remaining();
            
            value.value = new byte[length];
            
            byteBuffer.get(value.value);
            
            return length;
        },
        null);
        

        assertThat(value.value.length).isEqualTo(data.length);
        assertThat(value.value).isEqualTo(data);
    }
    
    @Test
    public void testSimpleExpandWrite() throws IOException {

        final NonBlockingChannelWriterLog log = new NonBlockingChannelWriterLogImpl("test", DebugLevel.TRACE);
        
        final WriteBuffer buffer = new WriteBuffer(15);
        
        final byte [] data = "012345678901234567890123456789".getBytes();
        
        final Value<byte[]> value = new Value<>();
        
        buffer.write(data, 0, data.length, log);
        
        buffer.onWriteable(byteBuffer -> {
            
            final int length = byteBuffer.remaining();
            
            value.value = new byte[length];
            
            byteBuffer.get(value.value);
            
            return length;
        },
        log);
        

        assertThat(value.value.length).isEqualTo(data.length);
        assertThat(value.value).isEqualTo(data);
    }

    @Test
    public void testPartialExpandWrite() throws IOException {

        final NonBlockingChannelWriterLog log = new NonBlockingChannelWriterLogImpl("test", DebugLevel.TRACE);
        
        final WriteBuffer buffer = new WriteBuffer(15);
        
        final byte [] data = "012345678901234567890123456789".getBytes();
        
        final Value<byte[]> value = new Value<>();
        
        buffer.write(data, 0, 10, log);
        
        System.out.println("### buffer write");
        
        buffer.write(data, 10, 20, log);
        
        buffer.onWriteable(byteBuffer -> {
            
            final int length = byteBuffer.remaining();
            
            value.value = new byte[length];
            
            byteBuffer.get(value.value);
            
            return length;
        },
        log);
        

        assertThat(value.value.length).isEqualTo(data.length);
        assertThat(value.value).isEqualTo(data);
    }
}
