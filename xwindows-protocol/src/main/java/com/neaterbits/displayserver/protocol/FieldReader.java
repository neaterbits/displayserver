package com.neaterbits.displayserver.protocol;

import java.io.IOException;

public interface FieldReader<T> {

    T read() throws IOException;
    
}
