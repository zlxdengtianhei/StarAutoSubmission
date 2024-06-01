package com.example.starautosubmission;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class MyDevicesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDevices;
    private DevicesAdapter devicesAdapter;
    private Button btnAddDevice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_devices);

        recyclerViewDevices = findViewById(R.id.recycler_view_devices);
        btnAddDevice = findViewById(R.id.btn_add_device);

        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(this));
        devicesAdapter = new DevicesAdapter();
        recyclerViewDevices.setAdapter(devicesAdapter);

        btnAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDeviceDialog();
            }
        });

        loadDevices();
    }

    private void showAddDeviceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_device, null);
        builder.setView(dialogView);

        Spinner spinnerDeviceType = dialogView.findViewById(R.id.spinner_device_type);
        EditText editTextDeviceName = dialogView.findViewById(R.id.edit_text_device_name);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);

        AlertDialog dialog = builder.create();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceType = spinnerDeviceType.getSelectedItem().toString();
                String deviceName = editTextDeviceName.getText().toString().trim();

                if (deviceName.isEmpty()) {
                    Toast.makeText(MyDevicesActivity.this, "设备名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                Device device = new Device();
                device.setName(deviceName);
                device.setType(deviceType);
                device.setUser(BmobUser.getCurrentUser(BmobUser.class));

                device.save(new SaveListener<String>() {
                    @Override
                    public void done(String objectId, BmobException e) {
                        if (e == null) {
                            Toast.makeText(MyDevicesActivity.this, "设备添加成功", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadDevices();
                        } else {
                            Toast.makeText(MyDevicesActivity.this, "设备添加失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void loadDevices() {
        BmobUser currentUser = BmobUser.getCurrentUser(BmobUser.class);
        BmobQuery<Device> query = new BmobQuery<>();
        query.addWhereEqualTo("user", currentUser);
        query.findObjects(new FindListener<Device>() {
            @Override
            public void done(List<Device> devices, BmobException e) {
                if (e == null) {
                    devicesAdapter.setDevices(devices);
                } else {
                    Toast.makeText(MyDevicesActivity.this, "设备加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
