package dev.yanshouwang.codescanner.analyzers;

import androidx.camera.core.ImageAnalysis;

public interface IAnalyzer extends ImageAnalysis.Analyzer {
    void addAnalyzedListener(IAnalyzedListener<Barcode> listener);
    void removeAnalyzedListener(IAnalyzedListener<Barcode> listener);
}
