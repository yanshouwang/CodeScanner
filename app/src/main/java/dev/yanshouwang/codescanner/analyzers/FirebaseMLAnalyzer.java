package dev.yanshouwang.codescanner.analyzers;

import android.media.Image;

import androidx.camera.core.ImageProxy;

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
    @Override
    protected void analyzeSynchronous(ImageProxy imageProxy, int degrees) {
        if (imageProxy == null || imageProxy.getImage() == null) {
            return;
        }
        Image image = imageProxy.getImage();
        int rotation = degrees2Rotation(degrees);
        FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromMediaImage(image, rotation);
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

    private int degrees2Rotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException("Rotation must be 0, 90, 180 or 270.");
        }
    }
}
