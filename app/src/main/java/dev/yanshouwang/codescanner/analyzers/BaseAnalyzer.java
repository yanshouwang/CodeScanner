package dev.yanshouwang.codescanner.analyzers;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;

import com.crashlytics.android.Crashlytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.Collection;
import java.util.HashSet;

import dev.yanshouwang.codescanner.util.ImageUtils;
import dev.yanshouwang.codescanner.util.NV21Utils;
import dev.yanshouwang.codescanner.common.NV21Wrapper;

public abstract class BaseAnalyzer implements IAnalyzer {
    private static final String TAG = BaseAnalyzer.class.getSimpleName();

    private Collection<IAnalyzedListener<Barcode>> mListeners;
    private Rect mFocused;
    private boolean mIsAnalyzing;

    public BaseAnalyzer(Rect focused) {
        this.mFocused = focused;
        this.mIsAnalyzing = false;
    }

    public void addAnalyzedListener(IAnalyzedListener<Barcode> listener) {
        if (mListeners == null) {
            mListeners = new HashSet<>();
        }
        mListeners.add(listener);
    }

    public void removeAnalyzedListener(IAnalyzedListener<Barcode> listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }

    protected void fireBarcodeAnalyzed(Barcode barcode) {
        if (mListeners == null) {
            return;
        }
        for (IAnalyzedListener<Barcode> listener : mListeners) {
            listener.analyzed(barcode);
        }
    }

    @Override
    public void analyze(ImageProxy imageProxy, int degrees) {
        if (mIsAnalyzing) {
            return;
        }
        mIsAnalyzing = true;
        int format = imageProxy.getFormat();
        if (format != ImageFormat.YUV_420_888) {
            // CameraX 生成 YUV420_888 格式数据
            String message = String.format("imageProxy 格式错误: %s", format);
            Log.e(TAG, message);
            Throwable throwable = new IllegalArgumentException(message);
            Crashlytics.logException(throwable);
            Crashes.trackError(throwable);
            return;
        }
        byte[] data = ImageUtils.extract(imageProxy);
        int dataWidth = imageProxy.getWidth();
        int dataHeight = imageProxy.getHeight();
        int left = mFocused.left;
        int top = mFocused.top;
        int width = mFocused.width();
        int height = mFocused.height();
        NV21Wrapper focused = new NV21Wrapper(data, dataWidth, dataHeight)
                .spin(degrees)
                .clip(left, top, width, height);
        data = focused.getData();
        analyze(data, width, height);
        mIsAnalyzing = false;
    }

    protected abstract void analyze(byte[] data, int dataWidth, int dataHeight);
}
