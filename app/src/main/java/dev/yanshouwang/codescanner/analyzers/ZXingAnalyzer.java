package dev.yanshouwang.codescanner.analyzers;

import android.graphics.Rect;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class ZXingAnalyzer extends BaseAnalyzer {
    private static final String TAG = ZXingAnalyzer.class.getSimpleName();

    private Reader mReader;

    public ZXingAnalyzer(Rect focused) {
        super(focused);
        MultiFormatReader reader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        // 设置条码格式
        EnumSet<BarcodeFormat> formats = EnumSet.allOf(BarcodeFormat.class);
        //EnumSet<BarcodeFormat> formats = EnumSet.of(BarcodeFormat.QR_CODE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        reader.setHints(hints);
        mReader = reader;
    }

    @Override
    protected void analyze(byte[] data, int dataWidth, int dataHeight) {
        // 设置扫描框位置和大小
        LuminanceSource source = new PlanarYUVLuminanceSource(data, dataWidth, dataHeight, 0, 0, dataWidth, dataHeight, false);
        Binarizer binarizer = new HybridBinarizer(source);
        BinaryBitmap bitmap = new BinaryBitmap(binarizer);
        try {
            Result result = mReader.decode(bitmap);
            byte[] rawBytes = result.getRawBytes();
            String text = result.getText();
            Barcode barcode = new Barcode(rawBytes, text);
            fireBarcodeAnalyzed(barcode);
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            // 此处非 BUG, 无需记录日志
            //Crashlytics.logException(e);
            //Crashes.trackError(e);
        }
    }
}
