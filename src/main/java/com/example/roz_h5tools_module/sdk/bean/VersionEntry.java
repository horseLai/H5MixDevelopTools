package com.example.roz_h5tools_module.sdk.bean;

public class VersionEntry {

    private String systemVersionIncrId;
    private String systemVersionCode;
    private String versionDescription;
    private String dataCreateTime;
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VersionEntry{");
        sb.append("systemVersionIncrId='").append(systemVersionIncrId).append('\'');
        sb.append(", systemVersionCode='").append(systemVersionCode).append('\'');
        sb.append(", versionDescription='").append(versionDescription).append('\'');
        sb.append(", dataCreateTime='").append(dataCreateTime).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getSystemVersionIncrId() {
        return systemVersionIncrId;
    }

    public VersionEntry setSystemVersionIncrId(String systemVersionIncrId) {
        this.systemVersionIncrId = systemVersionIncrId;
        return this;
    }

    public String getSystemVersionCode() {
        return systemVersionCode;
    }

    public VersionEntry setSystemVersionCode(String systemVersionCode) {
        this.systemVersionCode = systemVersionCode;
        return this;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public VersionEntry setVersionDescription(String versionDescription) {
        this.versionDescription = versionDescription;
        return this;
    }

    public String getDataCreateTime() {
        return dataCreateTime;
    }

    public VersionEntry setDataCreateTime(String dataCreateTime) {
        this.dataCreateTime = dataCreateTime;
        return this;
    }
}
