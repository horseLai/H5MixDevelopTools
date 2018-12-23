package com.example.roz_h5tools_module.sdk;

import android.content.DialogInterface;
import android.util.ArrayMap;

import com.example.roz_h5tools_module.sdk.bean.CommResponseEntry;
import com.example.roz_h5tools_module.sdk.bean.VersionEntry;
import com.example.roz_h5tools_module.sdk.interfs.IH5ManagerSettings;
import com.example.roz_h5tools_module.sdk.net.AppVersionLoader;
import com.example.roz_h5tools_module.sdk.net.SimpleSubscriber;
import com.example.roz_h5tools_module.sdk.util.EmptyCheckUtil;
import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.SelectDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import okhttp3.Call;
import rx.Observable;

/**
 * @Author horseLai
 * CreatedAt 2018/10/30 10:17
 * Desc: 用于管理 H5 资源的下载更新
 * <br>
 * 关于更新:
 * 1.每次打开应用时去检查是否有新版本;
 * 2.完整更新：下载完整zip包->删除本地所有文件->解压zip->拷贝到指定文件夹
 * 2.补丁更新：下载完整zip包->解压zip->将zip中包含的文件覆盖式地写入到原有的目录
 * Update:
 */
public class H5Manager {

    private final IH5ManagerSettings mSettings;
    private static final String DOWNLOAD_TAG = "downLoadTag";

    private H5Manager(IH5ManagerSettings settings) {
        this.mSettings = settings;
    }

    public static H5Manager get(IH5ManagerSettings settings) {
        return new H5Manager(settings);
    }

    /**
     * 检查更新
     *
     * @param currUserAccount 用户名
     * @param packType        包类型
     */
    public void checkVersion(String currUserAccount, final String packType) {
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put(RequestParamsConstants.CURR_USR_ACCOUNT, currUserAccount);
        params.put(RequestParamsConstants.TYPE, packType);
//        LogUtils.d("-->> Final Params::" + params);

        mSettings.showWaitingDialog();
        AppVersionLoader appVersionLoader = new AppVersionLoader();
        appVersionLoader.getDoctorH5Version(params)
                .subscribe(new SimpleSubscriber<CommResponseEntry<VersionEntry>>() {
                    @Override
                    public void onNext(CommResponseEntry<VersionEntry> o) {

                        mSettings.hideWaitingDialog();
                        VersionEntry data = o.getData();

                        final String packVersionCode = data.getSystemVersionCode();
                        String localVersion = mSettings.getPackVersion(packType, "");

                        if (!EmptyCheckUtil.isEmpty(localVersion) && !EmptyCheckUtil.isEmpty(packVersionCode)) { // 有版本号，则比较版本，并提示下载
                            final float newVersion = Float.valueOf(packVersionCode);
                            final float currVersion = Float.valueOf(localVersion);
//                            LogUtils.d("newVersion::" + newVersion + "  currVersion::" + currVersion);

                            if (newVersion > currVersion) {
                                SelectDialog.show(mSettings.getContext(), "更新提示",
                                        String.format("当前版本V%s，发现新版本V%s", localVersion, packVersionCode),
                                        "下载", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mSettings.showProgressDialog();
                                                downloadH5PackAsync(packType, packVersionCode);
                                                dialog.dismiss();
                                            }
                                        });
                            }

                        } else if (EmptyCheckUtil.isEmpty(localVersion) && !EmptyCheckUtil.isEmpty(packVersionCode)) { // 没有记录到版本号，默认为没有下载过
                            MessageDialog.show(mSettings.getContext(), "温馨提示", "您需要先下载当前功能模块",
                                    "下载", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mSettings.showProgressDialog();
                                            downloadH5PackAsync(packType, packVersionCode);
                                            dialog.dismiss();
                                        }
                                    }).setCanCancel(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mSettings.hideWaitingDialog();
                    }

                });
    }

    // 异步下载
    public void downloadH5PackAsync(final String packType, final String newVersionCode) {

        downLoadH5Pack(packType,
                mSettings.getModulePackDownloadUrl(packType),
                newVersionCode,
                true,
                mSettings.getModelPackTempZipName(packType),
                mSettings.getModulePackRootLocation(packType)
        );
//        LogUtils.d(">>> downLoadH5Pack");
    }

    // 下载
    public void downLoadH5Pack(final String packType, String url, final String newVersionCode, final boolean isFullReplace, String fileName, final File destDir) {

        OkHttpUtils.get().url(url).tag(DOWNLOAD_TAG).build().execute(new FileCallBack(mSettings.getRootLocation(), fileName) {

            @Override
            public void onError(Call call, Exception e, int id) {
                if (mOnUpdateListener != null)
                    mOnUpdateListener.onFailed(newVersionCode, packType);
                mSettings.hidProgressDialog();
            }

            @Override
            public void onResponse(final File response, int id) {

                Observable.fromCallable(new Callable<Object>() {
                    @Override
                    public Object call() {
//                        LogUtils.d(">>> unzip");

                        if (isFullReplace)
                            deleteAll(destDir);

                        unzip(response, destDir);
                        return null;
                    }
                }).subscribe(new SimpleSubscriber<Object>() {
                    @Override
                    public void onNext(Object o) {
                        if (mOnUpdateListener != null)
                            mOnUpdateListener.onSuccess(newVersionCode, packType);

                        response.delete();
                        mSettings.hidProgressDialog();

                        mSettings.savePackVersion(packType, newVersionCode);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        response.delete();
                        mSettings.hidProgressDialog();
                    }
                });
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                if (mOnUpdateListener != null)
                    mOnUpdateListener.onUpdating(progress, newVersionCode, packType);

//                LogUtils.d(String.format("progress:: %s", progress));
                mSettings.updateProgressDialog((int) (progress * 100));
            }
        });
    }

    // 解压
    private void unzip(File srcFile, File rootDir) {
        try {
            ZipFile zipFile = new ZipFile(srcFile);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry zipEntry;
//            System.out.println("size::" + zipFile.size());
            byte[] temp = new byte[1024];
            while (entries.hasMoreElements()) {
                zipEntry = entries.nextElement();
//                System.out.println(zipEntry.getName());
                copyAndReplaceTo(zipFile, zipEntry, rootDir, temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 替换
    private void copyAndReplaceTo(ZipFile zipFile, ZipEntry zipEntry, File rootDir, byte[] temp) throws IOException {
        String subFile = zipEntry.getName();
        File file = new File(rootDir, subFile);
        if (zipEntry.isDirectory()) {
            file.mkdirs();
            return;
        } else {
            File parentFile = file.getParentFile();
//            System.out.println(parentFile.getPath());
            parentFile.mkdirs();
        }
        if (file.isFile() && !file.exists()) {
            file.createNewFile();
        }

        RandomAccessFile target = new RandomAccessFile(file, "rw");
        InputStream inputStream = zipFile.getInputStream(zipEntry);

        int read = 0;
        while ((read = inputStream.read(temp)) != -1) {
            target.write(temp, 0, read);
        }
        target.close();
        inputStream.close();
    }


    /**
     * 删除目录下的所有文件和文件夹
     *
     * @param root
     */
    public void deleteAll(File root) {
        if (root == null) return;
        if (root.isFile()) {
            root.delete();
            return;
        }
        File[] files = root.listFiles();
        if (files == null || files.length == 0) {
            root.delete();
            return;
        }
        for (int i = 0; i < files.length; i++) {
            deleteAll(files[i]);
        }
        root.delete();
    }

    /**
     * 回收资源
     */
    public void release() {
        OkHttpUtils.getInstance().cancelTag(DOWNLOAD_TAG);
        mSettings.hidProgressDialog();
    }


    //-------------------------------------------------------------------------------------------------------
    private OnUpdateListener mOnUpdateListener;

    public H5Manager setOnUpdateListener(OnUpdateListener listener) {
        this.mOnUpdateListener = listener;
        return this;
    }

    public interface OnUpdateListener {
        void onUpdating(float progress, String newVersionCode, String packType);

        void onSuccess(String newVersionCode, String packType);

        void onFailed(String newVersionCode, String packType);
    }
}
