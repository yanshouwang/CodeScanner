package dev.yanshouwang.codescanner.util;

public class NV21Utils {
    public static byte[] spin(byte[] data, int dataWidth, int dataHeight, int degrees) {
        degrees %= 360;
        if (degrees % 90 != 0) {
            String message = String.format("degrees 异常: %s", degrees);
            throw new IllegalArgumentException(message);
        }
        byte[] spin;
        if (degrees == 0) {
            spin = data;
        } else {
            if (degrees == 90) {
                spin = NV21Utils.spin90(data, dataWidth, dataHeight);
            } else if (degrees == 180) {
                spin = NV21Utils.spin180(data, dataWidth, dataHeight);
            } else {
                spin = NV21Utils.spin270(data, dataWidth, dataHeight);
            }
        }
        return spin;
    }

    private static byte[] spin90(byte[] data, int dataWidth, int dataHeight) {
        byte[] spin = new byte[data.length];
        int i = 0;
        // 旋转 Y 亮度分量
        for (int x = 0; x < dataWidth; x++) {
            for (int y = dataHeight - 1; y >= 0; y--) {
                int j = y * dataWidth + x;
                spin[i] = data[j];
                i++;
            }
        }
        // 旋转 V, U 色度浓度分量, stride = 2
        int yLength = dataWidth * dataHeight;
        int vuWidth = dataWidth / 2;
        int vuHeight = dataHeight / 2;
        for (int x = 0; x < vuWidth; x++) {
            for (int y = vuHeight - 1; y >= 0; y--) {
                int j = yLength + (y * vuWidth + x) * 2;
                spin[i] = data[j];
                spin[i + 1] = data[j + 1];
                i += 2;
            }
        }
        return spin;
    }

    private static byte[] spin180(byte[] data, int dataWidth, int dataHeight) {
        byte[] spin = new byte[data.length];
        int i = 0;
        // 旋转 Y 亮度分量
        for (int y = dataHeight - 1; y >= 0; y--) {
            for (int x = dataWidth - 1; x >= 0; x--) {
                int j = y * dataWidth + x;
                spin[i] = data[j];
                i++;
            }
        }
        // 旋转 V, U 色度浓度分量, stride = 2
        int yLength = dataWidth * dataHeight;
        int vuWidth = dataWidth / 2;
        int vuHeight = dataHeight / 2;
        for (int y = vuHeight - 1; y >= 0; y--) {
            for (int x = vuWidth - 1; x >= 0; x--) {
                int j = yLength + (y * vuWidth + x) * 2;
                spin[i] = data[j];
                spin[i + 1] = data[j + 1];
                i += 2;
            }
        }
        return spin;
    }

    private static byte[] spin270(byte[] data, int dataWidth, int dataHeight) {
        byte[] spin = new byte[data.length];
        int i = 0;
        // 旋转 Y 亮度分量
        for (int x = dataWidth - 1; x >= 0; x--) {
            for (int y = dataHeight - 1; y >= 0; y--) {
                int j = y * dataWidth + x;
                spin[i] = data[j];
                i++;
            }
        }
        // 旋转 V, U 色度浓度分量, stride = 2
        int yLength = dataWidth * dataHeight;
        int vuWidth = dataWidth / 2;
        int vuHeight = dataHeight / 2;
        for (int x = vuWidth - 1; x >= 0; x--) {
            for (int y = vuHeight - 1; y >= 0; y--) {
                int j = yLength + (y * vuWidth + x) * 2;
                spin[i] = data[j];
                spin[i + 1] = data[j + 1];
                i += 2;
            }
        }
        return spin;
    }

    public static byte[] clip(byte[] data, int dataWidth, int dataHeight, int left, int top, int width, int height) {
        if (data == null || dataWidth < 0 || dataHeight < 0 || width < 0 || height < 0) {
            throw new IllegalArgumentException("参数错误");
        }
        if (left < 0 || top < 0) {
            throw new IllegalArgumentException("越界");
        }
        int right = left + width;
        int bottom = top + height;
        if (right > dataWidth || bottom > dataHeight) {
            throw new IllegalArgumentException("越界");
        }
        if (left % 2 != 0 || top % 2 != 0 || width % 2 != 0 || height % 2 != 0) {
            throw new IllegalArgumentException("参数必须为 2 的倍数");
        }
        byte[] clip = new byte[width * height * 3 / 2];
        // 截取 Y 亮度分量
        int i = 0;
        for (int y = top; y < bottom; y++) {
            for (int x = left; x < right; x++) {
                int j = y * dataWidth + x;
                clip[i] = data[j];
                i++;
            }
        }
        // 截取 V,U 色度, 浓度分量
        int yLength = dataWidth * dataHeight;
        int vuLeft = left / 2;
        int vuTop = top / 2;
        int vuRight = right / 2;
        int vuBottom = bottom / 2;
        int dataVUWidth = dataWidth / 2;
        for (int y = vuTop; y < vuBottom; y++) {
            for (int x = vuLeft; x < vuRight; x++) {
                int j = yLength + (y * dataVUWidth + x) * 2;
                clip[i] = data[j];
                clip[i + 1] = data[j + 1];
                i += 2;
            }
        }
        return clip;
    }
}
