package com.example.roz_h5tools_module.sdk.interfs;

/**
 * @Author horseLai
 * CreatedAt 2018/10/22 14:33
 * Desc: H5 通过 JS 与 Android 原生UI交互的接口
 * Update:
 *
 */
public interface IH5JsUI {


    void alertDialog(String title, String msg);

    void showToast(String msg);

    void showWaitingDialog ( String msg);

    void hideWaitingDialog ( );

    void showLoadFailedPage(String statusCode);

    /**
     * 打开注册、登录界面
     */
    void requestLogin();

    /**
     * 扫描二维码
     */
    void requestQRScanner();

    /**
     * 选择图片
     */
    void requestPicSelection();

    /**
     * 选择文件
     */
    void requestFileSelection();


    /**
     * 图片预览
     * @param url
     */
    void requestImagePreview(String url);

    void gotoPage(String jsonParams);

    /**
     * 退出 Activity
     */
    void closeWindow();
    /**
     * 返回
     */
    void goBack();

    /**
     * 前进
     */
    void goForward();

    /**
     * 还能不能前进了
     */
    void canGoForward();

    /**
     * 还能不能返回了
     */
    void canGoBack();

    /**
     * 清除浏览器历史记录
     */
    void clearHistory();

    /**
     * 打电话
     * @param phoneNumber
     */
    void call(String phoneNumber);

}
