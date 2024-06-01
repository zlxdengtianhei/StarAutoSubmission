package com.example.starautosubmission;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class Device extends BmobObject {
    private String name;
    private String type;
    private BmobUser user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }
}
