package io.github.gecko10000.GeckoSpawners.util;

public class ShortWrapper {

    public short value;

    public ShortWrapper(short initialValue) {
        this.value = initialValue;
    }

    public ShortWrapper(int initialValue) {
        this.value = (short) initialValue;
    }

    public ShortWrapper(String initialValue) {
        this.value = Short.parseShort(initialValue.substring(0, initialValue.length() - 1));
    }

    @Override
    public String toString() {
        return this.value + "s";
    }

}
