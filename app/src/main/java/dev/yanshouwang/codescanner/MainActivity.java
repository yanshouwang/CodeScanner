package dev.yanshouwang.codescanner;

import android.Manifest;
import android.app.Application;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.UseCase;
import androidx.core.app.ActivityCompat;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dev.yanshouwang.codescanner.analyzers.Barcode;
import dev.yanshouwang.codescanner.analyzers.BaseAnalyzer;
import dev.yanshouwang.codescanner.analyzers.FirebaseMLAnalyzer;
import dev.yanshouwang.codescanner.util.PermissionUtils;

public class MainActivity extends AppCompatActivity {
    private static final String APP_SECRET = "0d26c882-82b0-403b-823a-872fcb21371a";
    private static final String[] REQUEST_SCAN_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private static final int REQUEST_SCAN_CODE = 138;

    private TextureView mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application application = getApplication();
        AppCenter.start(application, APP_SECRET, Analytics.class, Crashes.class);
        setContentView(R.layout.activity_main);
        mCameraView = findViewById(R.id.main_camera);
        mCameraView.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> updateTransform());
        boolean isGranted = PermissionUtils.checkIsPermissionsGranted(this, REQUEST_SCAN_PERMISSIONS);
        if (isGranted) {
            mCameraView.post(this::startScan);
        } else {
            ActivityCompat.requestPermissions(this, REQUEST_SCAN_PERMISSIONS, REQUEST_SCAN_CODE);
        }
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();
        float centerX = mCameraView.getWidth() / 2.0F;
        float centerY = mCameraView.getHeight() / 2.0F;
        float degrees;
        int rotation = mCameraView.getDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0.0F;
                break;
            case Surface.ROTATION_90:
                degrees = 90.0F;
                break;
            case Surface.ROTATION_180:
                degrees = 180.0F;
                break;
            case Surface.ROTATION_270:
                degrees = 270.0F;
                break;
            default:
                return;
        }
        matrix.postRotate(-degrees, centerX, centerY);
        mCameraView.setTransform(matrix);
    }

    private void startScan() {
        UseCase preview = buildPreviewUseCase();
        UseCase analysis = buildAnalysisUseCase();
        CameraX.bindToLifecycle(this, preview, analysis);
    }

    private UseCase buildPreviewUseCase() {
        PreviewConfig config = new PreviewConfig.Builder().build();
        Preview preview = new Preview(config);
        preview.setOnPreviewOutputUpdateListener(output -> {
            // To update the SurfaceTexture, we have to remove it and re-add it
            ViewGroup parent = (ViewGroup) mCameraView.getParent();
            parent.removeView(mCameraView);
            parent.addView(mCameraView, 0);
            SurfaceTexture texture = output.getSurfaceTexture();
            mCameraView.setSurfaceTexture(texture);
            updateTransform();
        });
        return preview;
    }

    private UseCase buildAnalysisUseCase() {
        ImageAnalysisConfig config = new ImageAnalysisConfig.Builder()
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .build();
        ImageAnalysis analysis = new ImageAnalysis(config);
        Executor executor = Executors.newSingleThreadExecutor();
        BaseAnalyzer analyzer = new FirebaseMLAnalyzer();
        analyzer.addBarcodeAnalyzedListener(this::onBarcodeAnalyzed);
        analysis.setAnalyzer(executor, analyzer);
        return analysis;
    }

    private void onBarcodeAnalyzed(Barcode barcode) {
        runOnUiThread(() -> Toast.makeText(this, barcode.getText(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SCAN_CODE) {
            boolean isGranted = PermissionUtils.checkIsPermissionsGranted(this, REQUEST_SCAN_PERMISSIONS);
            if (isGranted) {
                mCameraView.post(this::startScan);
            } else {
                Toast.makeText(this, "关键权限被用户拒绝", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
