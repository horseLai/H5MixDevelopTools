package com.example.roz_h5tools_module.sdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.example.roz_h5tools_module.sdk.bean.User;
import com.example.roz_h5tools_module.sdk.interfs.IH5JsStorage;
import com.example.roz_h5tools_module.sdk.util.EmptyCheckUtil;
import com.example.roz_h5tools_module.sdk.util.IntentActionUtil;
import com.example.roz_h5tools_module.sdk.util.PermissionChecker;
/**
 * @Author horseLai
 * CreatedAt 2018/11/30 10:33
 * Desc:
 * Update:
 */
public class H5JsStorage implements IH5JsStorage {

    public static final int REQ_PIC = 1234;
    public static final int REQ_CAM = 1235;
    public static final int REQ_FILE = 1236;

    private final Activity mContext;
    private final User mUser;

    public H5JsStorage(Activity context, User user) {
        this.mContext = context;
        this.mUser = user;
    }

    @JavascriptInterface
    @Override
    public void getPictureFromGallery() {
        if (checkNoReadPermission()) return;
        IntentActionUtil.picPicture(mContext, REQ_PIC);
    }

    private boolean checkNoReadPermission() {
        if (!PermissionChecker.isPermissionGranted(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionChecker.requestPermissions(mContext, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            });
            return true;
        }
        return false;
    }

    @JavascriptInterface
    @Override
    public void getPictureFromCamera() {
        if (!PermissionChecker.isPermissionGranted(mContext, Manifest.permission.CAMERA)
                || !PermissionChecker.isPermissionGranted(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            PermissionChecker.requestPermissions(mContext, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

            });
            return;
        }
        IntentActionUtil.takePhotoAndReturnUri(mContext, REQ_CAM);
    }

    @JavascriptInterface
    @Override
    public void getFileFromStorage(String mimeType) {
        if (checkNoReadPermission()) return;
        IntentActionUtil.getContent(mContext, mimeType, REQ_FILE);
    }

    @JavascriptInterface
    @Override
    public void saveToLocal(String key, String value) {
        if (EmptyCheckUtil.isEmpty(key)) return;
        mContext.getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(key, value)
                .apply();
    }


    @JavascriptInterface
    @Override
    public String getFromLocal(String key, String defaultValue) {
        if (EmptyCheckUtil.isEmpty(key)) return defaultValue;
        return  mContext.getPreferences(Context.MODE_PRIVATE).getString(key, defaultValue);
    }



    @JavascriptInterface
    public String getUserAccountInfo(){
        return String.format("{\"userAccount\":\"%s\", \"password\":\"%s\", \"userIncrId\":\"%s\", \"orgId\":\"%s\"}", mUser.getUserAccount(), mUser.getPassWord(), mUser.getUserIncrId(), mUser.getOrgId());
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CAM: {
                    dealWithCam(data);
                    break;
                }
                case REQ_PIC: {
                    dealWithGallery(data);
                    break;
                }
                case REQ_FILE: {
                    dealWithFile(data);
                    break;
                }
            }
        }
    }

    private void dealWithFile(Intent data) {

    }

    private void dealWithGallery(Intent data) {

    }

    private void dealWithCam(Intent data) {

    }
}
