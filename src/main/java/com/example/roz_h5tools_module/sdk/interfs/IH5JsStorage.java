package com.example.roz_h5tools_module.sdk.interfs;

public interface IH5JsStorage {

    void getPictureFromGallery();

    void getPictureFromCamera();

    void getFileFromStorage(String mimeType);

    void saveToLocal(String key, String value);

    String getFromLocal(String key, String defaultValue);

}
