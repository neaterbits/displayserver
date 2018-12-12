package com.neaterbits.displayserver.buffers;

public interface GetImageListener {

    void onResult(byte [] data);
    
    void onError();
}
