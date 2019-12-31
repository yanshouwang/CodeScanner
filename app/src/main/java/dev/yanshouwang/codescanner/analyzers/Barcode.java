package dev.yanshouwang.codescanner.analyzers;

public class Barcode implements IAnalyzable {
    private byte[] mRawBytes;
    private String mText;

    public Barcode(byte[] rawBytes, String text) {
        this.mRawBytes = rawBytes;
        this.mText = text;
    }

    public byte[] getRawBytes() {
        return mRawBytes;
    }

    public String getText() {
        return mText;
    }
}
