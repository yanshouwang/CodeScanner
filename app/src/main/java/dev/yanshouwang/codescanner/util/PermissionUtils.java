package dev.yanshouwang.codescanner.util;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static boolean checkIsPermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            int value = ContextCompat.checkSelfPermission(context, permission);
            if (value != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
