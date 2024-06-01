package com.example.starautosubmission;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    // 搜索参数
    private List<String> locationList;
    private String location;
    private String city;

    // 控件
    private MapView mMapView;
    private EditText editCity;
    RecyclerView searchResult;
    InfoAdapter searchAdapter;
    Button addButton;
    Spinner spinner;

    // 百度地图相关
    PoiSearch mPoiSearch;
    List<PoiInfo> allPOI;
    BaiduMap mBaiduMap;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // MapView
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchResult.setVisibility(View.GONE);
            }
        });
        initPOI();
        addAllMarkerFromCloudServer();

        // Spinner
        fetchCurrentUserLocations();

        // EditText
        editCity = (EditText) view.findViewById(R.id.city);

        // Add button
        addButton = (Button) view.findViewById(R.id.AddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = (String) spinner.getSelectedItem();
                city = editCity.getText().toString();
                searchOne(city, location);
                searchResult.setVisibility(View.VISIBLE);
            }
        });

        // RecyclerView
        searchResult = (RecyclerView) view.findViewById(R.id.searchResult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        searchResult.setLayoutManager(layoutManager);

        // marker
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //marker被点击时回调的方法
            //若响应点击事件，返回true，否则返回false
            //默认返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                return true;
            }
        });
    }


    private void searchOne(String c, String l){
        if(c.isEmpty()){
            c = "中国";
        }
        mPoiSearch.searchInCity(new PoiCitySearchOption().city(c).keyword(l));
    }

    private void initPOI(){
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    allPOI = poiResult.getAllPoi();
                }
                else {
                    PoiInfo temp = new PoiInfo();
                    temp.setAddress("无结果");
                    allPOI.set(0,temp);
                }
                searchAdapter = new InfoAdapter(allPOI, mMapView);
                searchResult.setAdapter(searchAdapter);
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }

            //废弃
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }
        });
    }

    private void addAllMarkerFromCloudServer(){
        // 在地图上显示所有用户的标点
    }

    public void fetchCurrentUserLocations() {
        BmobQuery<LocationEntry> query = new BmobQuery<>();
        query.addWhereEqualTo("user",
                BmobUser.getCurrentUser(BmobUser.class));
        query.findObjects(new FindListener<LocationEntry>() {
            @Override
            public void done(List<LocationEntry> object,
                             BmobException e) {
                if (e == null) {
                    // 查询成功
                    Log.d("Bmob","get data successfully");
                    for (LocationEntry l : object) {
                        locationList.add(l.getLocationName());
                    }
                } else {
                    // 查询失败
                    Log.e("Bmob", "Error: " + e.getMessage());
                }
            }
        });
    }
}