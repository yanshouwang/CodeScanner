package dev.yanshouwang.codescanner.util;

import android.graphics.ImageFormat;

import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ImageUtils {
    public static byte[] extract(ImageProxy imageProxy) {
        int format = imageProxy.getFormat();
        if (format != ImageFormat.YUV_420_888) {
            throw new IllegalArgumentException("不支持此格式");
        }
        int stride = imageProxy.getPlanes()[2].getPixelStride();
        if (stride != 1 && stride != 2) {
            String message = String.format("异常 stride: %s", stride);
            throw new IllegalArgumentException(message);
        }
        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();
        int length = width * height * 3 / 2;
        byte[] dataY = ImageUtils.extract(imageProxy, 0);
        byte[] data = Arrays.copyOf(dataY, length);
        if (stride == 1) {
            byte[] dataU = ImageUtils.extract(imageProxy, 1);
            byte[] dataV = ImageUtils.extract(imageProxy, 2);
            int length1 = width * height / 4;
            if (dataV.length != length1 || dataU.length != length1) {
                String message = String.format("dataVU 长度异常: %s, %s", dataV.length, dataU.length);
                throw new IllegalArgumentException(message);
            }
            for (int i = 0; i < length1; i++) {
                data[dataY.length + i] = dataV[i];
                data[dataY.length + i + 1] = dataU[i];
            }
        } else {
            byte[] dataVU = ImageUtils.extract(imageProxy, 2);
            int length1 = width * height / 2;
            // BUG: 测试发现会少 1 像素
            if (dataVU.length != length1 && dataVU.length != length1 - 1) {
                String message = String.format("dataVU 长度异常: %s", dataVU.length);
                throw new IllegalArgumentException(message);
            }
            System.arraycopy(dataVU, 0, data, dataY.length, dataVU.length);
        }
        return data;
    }

    public static byte[] extract(ImageProxy imageProxy, int i) {
        ByteBuffer buffer = imageProxy.getPlanes()[i].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        return data;
    }
}
