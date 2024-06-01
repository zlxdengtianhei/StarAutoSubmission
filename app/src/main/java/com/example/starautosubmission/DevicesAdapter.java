package com.example.starautosubmission;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder> {

    private List<Device> devices = new ArrayList<>();

    public void setDevices(List<Device> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.textViewDeviceName.setText(device.getName());
        holder.textViewDeviceType.setText(device.getType());

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                device.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            devices.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, devices.size());
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDeviceName;
        TextView textViewDeviceType;
        Button buttonDelete;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDeviceName = itemView.findViewById(R.id.text_view_device_name);
            textViewDeviceType = itemView.findViewById(R.id.text_view_device_type);
            buttonDelete = itemView.findViewById(R.id.button_delete);
        }
    }
}
