package dev.yanshouwang.codescanner.util;

import com.google.common.truth.Truth;

import org.junit.Test;

import static org.junit.Assert.*;

public class NV21UtilsTest {
    @Test
    public void spin90() {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0, 2, 4, 6};
        byte[] expected = new byte[]{5, 1, 6, 2, 7, 3, 8, 4, 0, 2, 4, 6};
        byte[] actual = NV21Utils.spin(data, 4, 2, 90);
        Truth.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void spin180() {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0, 2, 4, 6};
        byte[] expected = new byte[]{8, 7, 6, 5, 4, 3, 2, 1, 4, 6, 0, 2};
        byte[] actual = NV21Utils.spin(data, 4, 2, 180);
        Truth.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void spin270() {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 0, 2, 4, 6};
        byte[] expected = new byte[]{8, 4, 7, 3, 6, 2, 5, 1, 4, 6, 0, 2};
        byte[] actual = NV21Utils.spin(data, 4, 2, 270);
        Truth.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void clip() {
        byte[] data = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 0, 2, 4, 6, 1, 3, 5, 7};
        byte[] expected = new byte[]{11, 12, 15, 16, 5, 7};
        byte[] actual = NV21Utils.clip(data, 4, 4, 2, 2, 2, 2);
        Truth.assertThat(actual).isEqualTo(expected);
    }
}