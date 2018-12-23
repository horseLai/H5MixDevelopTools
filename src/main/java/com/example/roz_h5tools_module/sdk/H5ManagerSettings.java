package com.example.roz_h5tools_module.sdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.roz_h5tools_module.sdk.interfs.IH5ManagerSettings;
import com.example.roz_h5tools_module.sdk.net.UrlsManager;
import com.kongzue.dialog.v2.WaitDialog;

import java.io.File;

import static android.support.constraint.Constraints.TAG;

/**
 * @Author horseLai
 * CreatedAt 2018/12/6 14:27
 * Desc: 这里实现了H5Manager中抽离的变动较大的逻辑，每增加一个H5模块包，就需要在这里配置
 * Update:
 */
public class H5ManagerSettings implements IH5ManagerSettings {

    private final File mRootPath;
    private Activity mContext;
    private final ProgressDialog mProgressDialog;
    private static final String CONFIG_NAME = "H5PackConfig";

    public interface PackType {
        String ADD_RECORD = "H1";
    }


    public H5ManagerSettings(Activity context) {
        this.mContext = context;

        mRootPath = mContext.getFilesDir();
        if (!mRootPath.exists()) {
            boolean mkdirs = mRootPath.mkdirs();
//            Toast.makeText(mContext, "mkdirs ok::" + mkdirs, Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "H5Manager: " + mRootPath.getAbsolutePath());

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("更新进度");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(100);
    }

    @Override
    public Activity getContext() {
        return mContext;
    }


    public String getRootLocation() {
        return mRootPath.getAbsolutePath() + "/h5Pack/";
    }


    @Override
    public File getModulePackRootLocation(String packType) {
        switch (packType) {
            case PackType.ADD_RECORD:
                return new File(getRootLocation(), "doctor_archive");
        }
        // TODO: 2018/12/5 添加其他模块
        return null;
    }

    //
    private String formUrl(String subUrl) {
        return String.format("file://%s%s", getRootLocation(), subUrl);
    }

    /**
     * 获取模块链接
     * @param packType PackType.xxx
     * @return
     */
    @Override
    public String getModuleUrl(String packType) {
        switch (packType) {
            case PackType.ADD_RECORD:
                return formUrl("doctor_archive/index.html");
            // TODO: 2018/12/5 添加其他模块
        }
        return "";
    }


    /**
     * 检查是否存在模块文件
     * @param packType PackType.xxx
     * @return
     */
    @Override
    public boolean containsModule(String packType) {
        switch (packType) {
            case PackType.ADD_RECORD:
                return new File(getModulePackRootLocation(packType), "/index.html").exists();
            // TODO: 2018/12/5 添加其他模块
        }
        return false;
    }

    @Override
    public String getModulePackDownloadUrl(String packType) {
        switch (packType) {
            case PackType.ADD_RECORD:
                return UrlsManager.MAIN_ADDRESS +  "Doctor/doctor_archive.zip";
        }
        // TODO: 2018/12/5 添加其他模块
        return null;
    }

    /**
     * 下载时暂存的zip文件名称
     * @param packType
     * @return
     */
    @Override
    public String getModelPackTempZipName(String packType) {
        switch (packType) {
            case PackType.ADD_RECORD:
                return "doctor_archive.zip";
        }
        // TODO: 2018/12/5 添加其他模块
        return null;
    }

    @Override
    public void showWaitingDialog() {
        WaitDialog.show(mContext, "正在加载...");
    }

    @Override
    public void hideWaitingDialog() {
        WaitDialog.dismiss();
    }


    @Override
    public void showProgressDialog() {
        mProgressDialog.show();
    }

    @Override
    public void updateProgressDialog(int progress) {
        mProgressDialog.setProgress(progress * 100);
        mProgressDialog.setMessage(String.format("正在下载 %s％", (int) (progress * 100)));
    }

    @Override
    public void hidProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public String getPackVersion(String packType, String defaultValue) {
        return mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
                .getString(packType, defaultValue);
    }

    @Override
    public void savePackVersion(String packType, String versionCode) {
        mContext.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(packType, versionCode)
                .commit();
    }
}
