package dev.yanshouwang.codescanner.analyzers;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.util.Collection;
import java.util.HashSet;

public abstract class BaseAnalyzer implements ImageAnalysis.Analyzer {
    private Collection<IAnalyzedListener<Barcode>> mListeners;
    private boolean mIsAnalyzing;

    public BaseAnalyzer() {
        this.mIsAnalyzing = false;
    }

    public void addBarcodeAnalyzedListener(IAnalyzedListener<Barcode> listener) {
        if (mListeners == null) {
            mListeners = new HashSet<>();
        }
        mListeners.add(listener);
    }

    public void removeBarcodeAnalyzedListener(IAnalyzedListener<Barcode> listener) {
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
        analyzeSynchronous(imageProxy, degrees);
        mIsAnalyzing = false;
    }

    protected abstract void analyzeSynchronous(ImageProxy imageProxy, int degrees);
}
