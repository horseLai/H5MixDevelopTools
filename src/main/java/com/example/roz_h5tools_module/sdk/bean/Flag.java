package com.example.roz_h5tools_module.sdk.bean;

/**
 * @Author horseLai
 * CreatedAt 2018/7/24 8:59
 * Desc: 消息标识
 * Update:
 *
 */
public class Flag {
    private String messageFlag;

    private String singleCount;

    public String getSingleCount() {
        return singleCount;
    }
    public Flag setSingleCount(String singleCount) {
        this.singleCount = singleCount;
        return this;
    }

    public Flag(String messageFlag) {
        this.messageFlag = messageFlag;
    }

    public String getMessageFlag() {
        return messageFlag;
    }

    public Flag setMessageFlag(String messageFlag) {
        this.messageFlag = messageFlag;
        return this;
    }

    @Override
    public String toString() {
        return "Flag{" +
                "messageFlag='" + messageFlag + '\'' +
                '}';
    }
}
