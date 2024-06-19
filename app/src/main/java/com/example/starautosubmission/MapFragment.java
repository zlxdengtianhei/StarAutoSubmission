package com.example.starautosubmission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    // 搜索参数
    private List<String> locationList = new ArrayList<>();
    private String location;
    private String city;

    // 控件
    private MapView mMapView;
    private EditText editCity;
    RecyclerView searchResult;
    InfoAdapter searchAdapter;
    Button addButton;
    Spinner spinner;
    Button modeSwitchButton;

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

        // Spinner
        spinner = view.findViewById(R.id.spinner);

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

                // 悬浮窗
                LatLng position = marker.getPosition();
                View infoWindowView = LayoutInflater.from(getContext()).inflate(R.layout.marker_info, null);
                InfoWindow infoWindow = new InfoWindow(infoWindowView, position, -100);

                // 设置悬浮窗文本
                TextView marker_info = (TextView) infoWindowView.findViewById(R.id.text_marker_info);
                String positionInfo = marker.getPosition().latitude + "," + marker.getPosition().longitude;
                marker_info.setText(positionInfo);

                // 显示悬浮窗
                mBaiduMap.showInfoWindow(infoWindow);

                // 设置悬浮窗的点击监听器，点击时隐藏悬浮窗
                infoWindowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBaiduMap.hideInfoWindow();
                    }
                });

                Button deleteMarker = (Button) infoWindowView.findViewById(R.id.button_delete_marker);
                deleteMarker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LatLng p = marker.getPosition();
                        getOneThenRemove(p,marker);
                        mBaiduMap.hideInfoWindow();
                    }
                });

                return true;
            }
        });

        modeSwitchButton = (Button) view.findViewById(R.id.button_switch_mode);
        modeSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCurrentUserLocations();
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



    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.show_mode_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.show_all){
                    modeSwitchButton.setText("显示全部");
                    addAllMarkerFromCloudServer();
                    return true;
                } else if (id == R.id.hide_all) {
                    modeSwitchButton.setText("全部隐藏");
                    mBaiduMap.clear();
                    return true;
                }else if (id == R.id.show_personal){
                    modeSwitchButton.setText("显示个人");
                    addUserMarkerFromCloudServer();
                    return true;
                }else {
                    return false;
                }
            }
        });
        popupMenu.show();
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
                    Log.d("Bmob", "get data successfully");
                    for (LocationEntry l : object) {
                        locationList.add(l.getLocationName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, locationList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                } else {
                    // 查询失败
                    Log.e("Bmob", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getOneThenRemove(LatLng ll,Marker marker) {
        BmobQuery<CoordinateEntry> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user",BmobUser.getCurrentUser()).addWhereEqualTo("latitude", ll.latitude).addWhereEqualTo("longitude",ll.longitude);
        bmobQuery.findObjects(new FindListener<CoordinateEntry>() {
            @Override
            public void done(List<CoordinateEntry> list, BmobException e) {
                if (e == null) {
                    Log.d("Bmob","search successfully");
                    for(CoordinateEntry c : list){
                        c.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e == null){
                                    marker.remove();
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(getContext(), "无权删除此点", Toast.LENGTH_SHORT).show();
                    Log.e("Bmob", e.toString());
                }
            }
        });
    }

    private void addAllMarkerFromCloudServer(){
        // 先全部清除
        mBaiduMap.clear();
        // 获取当前用户的全部标点
        BmobQuery<CoordinateEntry> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(new FindListener<CoordinateEntry>() {
            @Override
            public void done(List<CoordinateEntry> list, BmobException e) {
                if (e == null) {
                    Log.d("Bmob","search successfully");
                    List<OverlayOptions> options = new ArrayList<OverlayOptions>();
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_marker);
                    for(CoordinateEntry c : list){
                        LatLng l = c.getLatLng();
                        options.add(new MarkerOptions()
                                .position(l)
                                .icon(bitmap)
                        );
                    }
                    mBaiduMap.addOverlays(options);
                } else {
                    Log.e("Bmob", e.toString());
                }
            }
        });
    }

    private void addUserMarkerFromCloudServer(){
        // 先全部清除
        mBaiduMap.clear();
        // 获取当前用户的全部标点
        BmobQuery<CoordinateEntry> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("user",BmobUser.getCurrentUser());
        bmobQuery.findObjects(new FindListener<CoordinateEntry>() {
            @Override
            public void done(List<CoordinateEntry> list, BmobException e) {
                if (e == null) {
                    Log.d("Bmob","search successfully");
                    List<OverlayOptions> options = new ArrayList<OverlayOptions>();
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_marker);
                    for(CoordinateEntry c : list){
                        LatLng l = c.getLatLng();
                        options.add(new MarkerOptions()
                                .position(l)
                                .icon(bitmap)
                        );
                    }
                    mBaiduMap.addOverlays(options);
                } else {
                    Log.e("Bmob", e.toString());
                }
            }
        });
    }
}

