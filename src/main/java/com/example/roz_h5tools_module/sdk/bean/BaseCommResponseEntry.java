package com.example.roz_h5tools_module.sdk.bean;
/**
 * @Author horseLai
 * CreatedAt 2018/8/10 9:21
 * Desc: 基础响应数据实体
 * Update:
 *
 */
public class BaseCommResponseEntry<DATA,RESULT> {
    //返回代码
    private String status;
    //错误信息
    private String msg;
    //总行数
    private String totalCount;
    //返回数据
    private DATA data;
    private RESULT result;


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BaseCommResponseEntry{");
        sb.append("status='").append(status).append('\'');
        sb.append(", msg='").append(msg).append('\'');
        sb.append(", totalCount='").append(totalCount).append('\'');
        sb.append(", data=").append(data);
        sb.append(", result=").append(result);
        sb.append('}');
        return sb.toString();
    }

    public String getStatus() {
        return status;
    }

    public BaseCommResponseEntry<DATA, RESULT> setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public BaseCommResponseEntry<DATA, RESULT> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public BaseCommResponseEntry<DATA, RESULT> setTotalCount(String totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public DATA getData() {
        return data;
    }

    public BaseCommResponseEntry<DATA, RESULT> setData(DATA data) {
        this.data = data;
        return this;
    }

    public RESULT getResult() {
        return result;
    }

    public BaseCommResponseEntry<DATA, RESULT> setResult(RESULT result) {
        this.result = result;
        return this;
    }
}
