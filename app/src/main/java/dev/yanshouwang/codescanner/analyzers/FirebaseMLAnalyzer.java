package dev.yanshouwang.codescanner.analyzers;

import android.graphics.Rect;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirebaseMLAnalyzer extends BaseAnalyzer {
    public FirebaseMLAnalyzer(Rect focused) {
        super(focused);
    }

    @Override
    protected void analyze(byte[] data, int dataWidth, int dataHeight) {
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setWidth(dataWidth)
                .setHeight(dataHeight)
                .build();
        FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromByteArray(data, metadata);
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS).build();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
        Task<List<FirebaseVisionBarcode>> task = detector.detectInImage(firebaseImage);
        try {
            List<FirebaseVisionBarcode> barcodes = Tasks.await(task);
            for (FirebaseVisionBarcode firebaseBarcode : barcodes) {
                byte[] rawBytes = firebaseBarcode.getRawBytes();
                String text = firebaseBarcode.getRawValue();
                Barcode barcode = new Barcode(rawBytes, text);
                fireBarcodeAnalyzed(barcode);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            Crashes.trackError(e);
        }
    }
}
