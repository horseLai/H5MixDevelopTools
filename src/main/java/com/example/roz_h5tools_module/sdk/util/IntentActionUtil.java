package com.example.roz_h5tools_module.sdk.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author horseLai
 * created at 2018/7/5 15:12
 * desc: 调用系统服务
 * update:
 */
public class IntentActionUtil {

    private static final String TAG = "IntentActionUtil";
    // 兼容 FileProvider
    public static final String AUTHORITY = "com.example.admin.fileprovider";

    public static void gotoSystemSetting(Activity context, String settingsAction) {
        Intent intent = new Intent(settingsAction);
        context.startActivity(intent);
    }

    public static void gotoApplicationDetailSetting(Activity context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivity(intent);
    }

    /**
     * @param context
     * @param textMessage
     * @param type        for example: "text/plain", "text/html"
     */
    public static void actionSend(Context context, String textMessage, String type) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
        if (null == type || type.isEmpty())
            type = "text/plain";
        sendIntent.setType(type);
        // Verify that the intent will resolve to an activity
        if (sendIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(sendIntent);
        }
    }


    /**
     * 返回 Uri 地址
     *
     * @param context
     * @param mimeType for example: image/*, text/plain, text/html
     */
    public static void getContent(@NonNull Activity context, @NonNull String mimeType, int reqCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivityForResult(intent, reqCode);
    }

    /**
     * 选择图片，返回图片Uri地址
     *
     * @param context
     */
    public static void picPicture(@NonNull Activity context, int reqCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivityForResult(intent, reqCode);
    }

    /**
     * 调用系统相机，返回相片的缩略图，在onActivityResult中通过
     * <pre>
     *      Bundle extras = data.getExtras();
     *      Bitmap imageBitmap = (Bitmap) extras.get("data");
     * </pre>
     * 拿到Bitmap图像, 注意，记得申请相机权限，并且通过intent.getData是拿不到Uri地址的
     *
     * @param context
     */
    public static void takePhotoAndReturnThumbnail(@NonNull Activity context, int reqCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(context.getPackageManager()) != null)
            context.startActivityForResult(intent, reqCode);
    }

    /**
     * Manifest中如下配置FileProvider
     * <pre>
     * <application>
     *      ...
     *  <provider
     *      android:name="android.support.v4.content.FileProvider"
     *      android:authorities="com.example.android.fileprovider"
     *      android:exported="false"
     *      android:grantUriPermissions="true">
     *      <meta-data
     *          android:name="android.support.FILE_PROVIDER_PATHS"
     *          android:resource="@xml/file_paths"></meta-data>
     *  </provider>
     * ...
     * </application>
     * </pre>
     * <p>
     * xml中如下配置：
     * <pre>
     *      <?xml version="1.0" encoding="utf-8"?>
     *      <paths xmlns:android="http://schemas.android.com/apk/res/android">
     *      <external-path name="my_images" path="Android/data/com.example.package.name/files/Pictures" />
     *      </paths>
     * </pre>
     *
     * @param context
     * @param imgFileName 存储的图片名称
     * @param authority   对应与Manifest中配置的 android:authorities=""参数，例如：
     * <pre>android:authorities="com.example.android.fileprovider" </pre>
     * @param reqCode
     */
    public static File sImageFile;

    public static void takePhotoAndReturnUri(@NonNull Activity context, @NonNull String prefix, @NonNull String authority, int reqCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            File image = null;
            try {
                File storeDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                image = File.createTempFile(prefix, ".jpg", storeDir);
                Log.d(TAG," -->>> image.getAbsolutePath: " + image.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (image == null) return;
            sImageFile = image;
            //这里不使用 context.getApplicationContext() 的话会报错,因为FileProvider声明在<Application>标签中，而不是<Activity>
            Uri imgUri = FileProvider.getUriForFile(context.getApplicationContext(), authority, image);

            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String packageName;
            for (ResolveInfo info : resolveInfos) {
                packageName = info.activityInfo.packageName;
                context.grantUriPermission(packageName, imgUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
            context.startActivityForResult(intent, reqCode);
        }
    }

    public static void takePhotoAndReturnUri(@NonNull Activity context, int reqCode) {
        takePhotoAndReturnUri(context, "temp_", AUTHORITY, reqCode);
    }

    /**
     * 添加图片到图库中
     *
     * @param context
     * @param imgFilePath
     */
    public static void addPicToGallery(@NonNull Activity context, @NonNull String imgFilePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imgFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    @SuppressLint("MissingPermission")
    public static void call(final Activity activity, String phoneNumber) {
        if (!PermissionChecker.isPermissionGranted(activity, Manifest.permission.CALL_PHONE)) {
            new AlertDialog.Builder(activity)
                    .setTitle("温馨提示")
                    .setMessage("您需要授予电话权限才能拨打电话!")
                    .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PermissionChecker.requestPermission(activity, Manifest.permission.CALL_PHONE);
                        }
                    })
                    .setNegativeButton("算了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNumber);
        intent.setData(data);
        ComponentName componentName = intent.resolveActivity(activity.getPackageManager());
        if (componentName == null){
            Toast.makeText(activity, "您的设备上没有对应功能的应用！", Toast.LENGTH_SHORT).show();
            return;
        }
        activity.startActivity(intent);
    }
}
