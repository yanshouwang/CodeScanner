package dev.yanshouwang.codescanner.util;

import java.util.Arrays;

public class ArrayUtils {
    public static <T> T[] concat(T[] first, T[]... others) {
        int length = first.length;
        for (T[] other : others) {
            length += other.length;
        }
        T[] sum = Arrays.copyOf(first, length);
        int offset = first.length;
        for (T[] other : others) {
            System.arraycopy(other, 0, sum, offset, other.length);
            offset += other.length;
        }
        return sum;
    }

    public static byte[] concat(byte[] first, byte[]... others) {
        int length = first.length;
        for (byte[] other : others) {
            length += other.length;
        }
        byte[] sum = Arrays.copyOf(first, length);
        int offset = first.length;
        for (byte[] other : others) {
            System.arraycopy(other, 0, sum, offset, other.length);
            offset += other.length;
        }
        return sum;
    }
}
