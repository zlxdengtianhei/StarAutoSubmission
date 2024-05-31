package com.example.starautosubmission;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "StringList";

    // TODO: Rename and change types of parameters

    // 搜索参数
    private String[] locationList = {"内蒙古明安图","河北冰山梁","内蒙古乌兰布统","内蒙古上都湖","河北千松坝"};
    private String location;
    private String city;

    // 控件
    private MapView mMapView;
    private EditText editCity;
    RecyclerView searchResult;
    InfoAdapter searchAdapter;
    Button addButton;

    // 百度地图相关
    PoiSearch mPoiSearch;
    List<PoiInfo> allPOI;
    BaiduMap mBaiduMap;


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locationList = getArguments().getStringArray(ARG_PARAM1);
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

        // Spinner
        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, locationList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
}