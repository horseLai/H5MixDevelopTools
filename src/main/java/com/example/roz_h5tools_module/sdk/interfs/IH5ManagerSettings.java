package com.example.roz_h5tools_module.sdk.interfs;

import android.app.Activity;

import java.io.File;

/**
 * @Author horseLai
 * CreatedAt 2018/12/6 14:27
 * Desc: 从H5Manager中抽离的变动较大的逻辑，提升可维护性
 * Update:
 */
public interface IH5ManagerSettings {


    Activity getContext();

    /**
     * 获取所有包所在的根路径
     *
     * @return
     */
    String getRootLocation();

    /**
     * 获取对应模块包的根路径
     *
     * @param packType 模块类型
     * @return
     */
    File getModulePackRootLocation(String packType);

    /**
     * 获取某个模块的链接（实际是带有首页index.html的链接）
     *
     * @param packType 模块类型
     * @return
     */
    String getModuleUrl(String packType);

    /**
     * 检测是否存在对应的模块文件
     *
     * @param packType 模块类型
     * @return
     */
    boolean containsModule(String packType);

    /**
     * 获取对应模块的服务器下载链接
     *
     * @param packType
     * @return
     */
    String getModulePackDownloadUrl(String packType);

    /**
     * 获取对应模块下载时暂存的文件名称
     *
     * @param packType
     * @return
     */
    String getModelPackTempZipName(String packType);

    /**
     * 显示加载时的等待窗口
     */
    void showWaitingDialog();

    /**
     * 隐藏加载时的等待窗口
     */
    void hideWaitingDialog();


    void showProgressDialog( );

    /**
     * 显示下载进度弹框
     * @param progress
     */
    void updateProgressDialog(int progress);

    /**
     * 隐藏下载进度弹框
     */
    void hidProgressDialog();


    String getPackVersion(String packType, String defaultValue);

    void savePackVersion(String packType, String versionCode);
}
