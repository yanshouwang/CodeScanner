package dev.yanshouwang.codescanner.common;

import dev.yanshouwang.codescanner.util.NV21Utils;

public class NV21Wrapper {
    private byte[] mData;
    private int mWidth;
    private int mHeight;

    public NV21Wrapper(byte[] data, int width, int height) {
        if (data.length != width * height * 3 / 2 || width % 2 != 0 || height % 2 != 0) {
            throw new IllegalArgumentException("NV21 参数错误");
        }
        this.mData = data;
        this.mWidth = width;
        this.mHeight = height;
    }

    public byte[] getData() {
        return mData;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public NV21Wrapper spin(int degrees) {
        byte[] data = this.getData();
        int dataWidth = this.getWidth();
        int dataHeight = this.getHeight();
        mData = NV21Utils.spin(data, dataWidth, dataHeight, degrees);
        if (degrees % 180 == 90) {
            mWidth += mHeight;
            mHeight = mWidth - mHeight;
            mWidth -= mHeight;
        }
        return this;
    }

    public NV21Wrapper clip(int left, int top, int width, int height) {
        byte[] data = this.getData();
        int dataWidth = this.getWidth();
        int dataHeight = this.getHeight();
        mData = NV21Utils.clip(data, dataWidth, dataHeight, left, top, width, height);
        mWidth = width;
        mHeight = height;
        return this;
    }
}
