package com.example.roz_h5tools_module.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Arrays;


/**
 * @Author horseLai
 * CreatedAt 2018/7/6 11:10
 * Desc: 用于权限检测和申请，注意，对于requestPermission和requestPermissions都不能在循环中调用，
 * 即使循环调用也只会执行行第一次调用的权限请求
 * Update:
 */
public class PermissionChecker
{
    public static final int PER_REQUEST_CODE = 110;

    public static boolean isBuildVersionMLater()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isPermissionGranted(@NonNull Context context, @NonNull String permission)
    {
        if (!isBuildVersionMLater())
            return true;

        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(@NonNull Activity context, @NonNull String[] permissions)
    {
        ActivityCompat.requestPermissions(context, permissions, PER_REQUEST_CODE);
    }

    public static void requestPermission(@NonNull Activity context, @NonNull String permission)
    {
        requestPermissions(context, new String[]{permission});
    }

    public static boolean shouldShowRequestPermissionRationale(@NonNull Activity context, @NonNull String permission)
    {
        return ActivityCompat.shouldShowRequestPermissionRationale(context, permission);
    }

    /**
     * @param context
     * @param permission
     * @param listener
     */
    public static void requestPermission(@NonNull Activity context, @NonNull String permission, @Nullable OnPermissionListener listener)
    {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (listener != null)
                listener.onPermissionGranted(permission);
            return;
        }

        if (isPermissionGranted(context, permission)) {
            if (listener != null)
                listener.onPermissionGranted(permission);
        } else {
            String[] ps = {permission};
            if (shouldShowRequestPermissionRationale(context, permission)) {
                if (listener != null)
                    listener.onShowAdditionalTips(context, ps);
            } else {
                ActivityCompat.requestPermissions(context, ps, PER_REQUEST_CODE);
            }
        }
    }

    /**
     * @param context
     * @param permissions
     * @param listener
     */
    public static void requestPermissions(@NonNull Activity context, @NonNull String[] permissions, @Nullable OnPermissionListener listener)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (listener == null)
                return;
            for (String permission : permissions)
                listener.onPermissionGranted(permission);
            return;
        }
        boolean granted = true;
        for (String permission : permissions) {
            granted &= isPermissionGranted(context, permission);
        }
        if (granted) {
            if (listener != null)
                for (String permission : permissions) {
                    listener.onPermissionGranted(permission);
                }
        } else {
            boolean shouldShow = false;
            for (String permission : permissions) {
                shouldShow |= shouldShowRequestPermissionRationale(context, permission);
            }
            if (shouldShow) {
                if (listener != null)
                    listener.onShowAdditionalTips(context, permissions);
            } else {
                ActivityCompat.requestPermissions(context, permissions, PER_REQUEST_CODE);
            }
        }
    }

    private static final String TAG = "PermissionChecker";

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, @NonNull OnPermissionListener listener)
    {
        Log.i(TAG, "onRequestPermissionsResult: " + Arrays.toString(grantResults));
        if (requestCode != PER_REQUEST_CODE)
            return;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                listener.onPermissionGranted(permissions[i]);
            } else {
                listener.onPermissionDenied(permissions[i]);
            }
        }
    }

    public static abstract class OnPermissionListener
    {
        /**
         * 挂载的权限会逐一通知
         *
         * @param permission
         */
        public abstract void onPermissionGranted(String permission);

        /**
         * 拒绝的权限会逐一通知
         *
         * @param permission
         */
        public void onPermissionDenied(String permission)
        {
            Log.i(TAG, "onPermissionDenied: " + permission);
        }

        /**
         * 用来提示用户权限被拒绝，有些功能使用不了
         *
         * @param context
         * @param permissions
         */
        public void onShowAdditionalTips(Activity context, String[] permissions)
        {
            // TODO: show tips to user
        }
    }


}
