package com.neaterbits.displayserver.protocol;

import java.io.IOException;

public interface FieldWriter<T> {

    void write(T value) throws IOException;

}
