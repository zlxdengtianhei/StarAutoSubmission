package com.example.starautosubmission;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

public class CoordinateEntry extends BmobObject {
    private BmobUser user;
    private double latitude;
    private double longitude;

    public void setLatLng(LatLng ll){
        this.latitude = ll.latitude;
        this.longitude = ll.longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }

    public BmobUser getUser() {
        return user;
    }

    public void setUser(BmobUser user) {
        this.user = user;
    }
}
