package com.example.roz_h5tools_module.sdk.bean;

public class User {

    private String userAccount;
    private String userIncrId;
    private String OrgId;
    private String passWord;

    public String getUserAccount() {
        return userAccount;
    }

    public User setUserAccount(String userAccount) {
        this.userAccount = userAccount;
        return this;
    }

    public String getUserIncrId() {
        return userIncrId;
    }

    public User setUserIncrId(String userIncrId) {
        this.userIncrId = userIncrId;
        return this;
    }

    public String getOrgId() {
        return OrgId;
    }

    public User setOrgId(String orgId) {
        OrgId = orgId;
        return this;
    }

    public String getPassWord() {
        return passWord;
    }

    public User setPassWord(String passWord) {
        this.passWord = passWord;
        return this;
    }
}
