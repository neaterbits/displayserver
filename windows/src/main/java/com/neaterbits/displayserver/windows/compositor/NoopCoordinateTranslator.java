package com.neaterbits.displayserver.windows.compositor;

public final class NoopCoordinateTranslator implements CoordinateTranslator {

    @Override
    public int translateX(int x) {
        return x;
    }

    @Override
    public int translateY(int y) {
        return y;
    }

    @Override
    public double translateX(double x) {
        return x;
    }

    @Override
    public double translateY(double y) {
        return y;
    }
}
