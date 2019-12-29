package dev.yanshouwang.codescanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.vision.scan.HmsMLVisionScan;
import com.huawei.hms.ml.vision.scan.HmsMLVisionScanDetectorOptions;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 138;
    private static final int REQUEST_CODE_SCAN = 765;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnScan = findViewById(R.id.main_scan);
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CAMERA);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HmsMLVisionScanDetectorOptions options = new HmsMLVisionScanDetectorOptions.Builder()
                        .setBarcodeFormats(HmsMLVisionScan.FORMAT_QR_CODE)
                        .build();
                ScanUtil.startScan(MainActivity.this, REQUEST_CODE_SCAN, options);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            for (int grantResult : grantResults) {
                
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK && data != null) {
            HmsMLVisionScan scan = data.getParcelableExtra(ScanUtil.RESULT);
            if (scan == null || TextUtils.isEmpty(scan.rawValue))
                return;
            Toast.makeText(this, scan.rawValue, Toast.LENGTH_SHORT).show();
        }
    }
}
