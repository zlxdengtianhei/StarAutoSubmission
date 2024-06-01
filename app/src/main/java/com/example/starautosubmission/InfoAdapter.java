package com.example.starautosubmission;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder>{

    private List<PoiInfo> InfoList;

    private MapView mMapView;

    public InfoAdapter(List<PoiInfo> infoList, MapView mMapView) {
        InfoList = infoList;
        this.mMapView = mMapView;
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder {
        View InfoView;
        TextView searchResultAddress;
        TextView searchResultLocation;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            InfoView = itemView;
            searchResultAddress = (TextView) itemView.findViewById(R.id.search_result_address);
            searchResultLocation = (TextView) itemView.findViewById(R.id.search_result_location);
        }
    }

    @NonNull
    @Override
    public InfoAdapter.InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, parent, false);
        final InfoViewHolder holder = new InfoViewHolder(view);
        holder.InfoView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                RecyclerView searchResult = (RecyclerView) parent.findViewById(R.id.searchResult);
                searchResult.setVisibility(View.GONE);
                //定义Marker坐标点，在地图上标记出来，然后
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_marker);
                PoiInfo info = InfoList.get(position);
                LatLng point = new LatLng(info.getLocation().latitude, info.getLocation().longitude);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                BaiduMap mBaiduMap = mMapView.getMap();
                mBaiduMap.addOverlay(option);
                MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(point);
                mBaiduMap.animateMapStatus(status1, 500);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InfoAdapter.InfoViewHolder holder, int position) {
        PoiInfo info = InfoList.get(position);
        holder.searchResultAddress.setText(info.getAddress());
        holder.searchResultLocation.setText(info.getLocation().latitude + "," + info.getLocation().longitude);
    }

    @Override
    public int getItemCount() {
        return InfoList.size();
    }
}
