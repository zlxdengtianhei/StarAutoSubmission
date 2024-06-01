package com.example.starautosubmission;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class LocationEntry extends BmobObject {
    private String locationName;
    private BmobUser user;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }
}
