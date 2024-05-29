package com.example.starautosubmission;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class StepTwoFragment extends Fragment {

    private static final String TAG = "StepTwoFragment";

    private EditText etTitle, etDescription, etLocation, etDate, etAperture, etShutter, etIso, etOthers;
    private Spinner spinnerCamera, spinnerLens;
    private Button btnNextStep;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_two, container, false);

        etTitle = view.findViewById(R.id.et_title);
        etDescription = view.findViewById(R.id.et_description);
        etLocation = view.findViewById(R.id.et_location);
        etDate = view.findViewById(R.id.et_date);
        etAperture = view.findViewById(R.id.et_aperture);
        etShutter = view.findViewById(R.id.et_shutter);
        etIso = view.findViewById(R.id.et_iso);
        etOthers = view.findViewById(R.id.et_others);
        spinnerCamera = view.findViewById(R.id.spinner_camera);
        spinnerLens = view.findViewById(R.id.spinner_lens);
        btnNextStep = view.findViewById(R.id.btn_save_info);

        // Load cameras and lenses from the server
        loadDevicesFromServer();

        etDate.setOnClickListener(v -> showDatePickerDialog());

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                saveSubmissionInfo();
                // 继续到下一步
                // navigateToNextStep();
            }
        });

        return view;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> etDate.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth),
                year, month, day);
        datePickerDialog.show();
    }

    private void loadDevicesFromServer() {
        BmobQuery<Device> query = new BmobQuery<>();
        query.addWhereEqualTo("user", BmobUser.getCurrentUser(BmobUser.class));
        query.findObjects(new FindListener<Device>() {
            @Override
            public void done(List<Device> devices, BmobException e) {
                if (e == null) {
                    Log.d(TAG, "Devices loaded successfully: " + devices.size());
                    List<String> cameras = new ArrayList<>();
                    List<String> lenses = new ArrayList<>();

                    for (Device device : devices) {
                        if ("相机".equals(device.getType())) {
                            cameras.add(device.getName());
                        } else if ("镜头".equals(device.getType())) {
                            lenses.add(device.getName());
                        }
                    }

                    ArrayAdapter<String> cameraAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cameras);
                    cameraAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCamera.setAdapter(cameraAdapter);

                    ArrayAdapter<String> lensAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lenses);
                    lensAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerLens.setAdapter(lensAdapter);
                } else {
                    Log.e(TAG, "Error loading devices: " + e.getMessage());
                    Toast.makeText(getContext(), "加载设备失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("标题不能为空");
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            etDescription.setError("说明不能为空");
            return false;
        }
        if (etLocation.getText().toString().trim().isEmpty()) {
            etLocation.setError("地点不能为空");
            return false;
        }
        if (etDate.getText().toString().trim().isEmpty()) {
            etDate.setError("日期不能为空");
            return false;
        }
        if (spinnerCamera.getSelectedItem() == null) {
            Toast.makeText(getContext(), "请选择相机", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (spinnerLens.getSelectedItem() == null) {
            Toast.makeText(getContext(), "请选择镜头", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etAperture.getText().toString().trim().isEmpty()) {
            etAperture.setError("光圈不能为空");
            return false;
        }
        if (etShutter.getText().toString().trim().isEmpty()) {
            etShutter.setError("快门不能为空");
            return false;
        }
        if (etIso.getText().toString().trim().isEmpty()) {
            etIso.setError("ISO不能为空");
            return false;
        }
        return true;
    }

    private void saveSubmissionInfo() {
        try {
            String title = etTitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String camera = spinnerCamera.getSelectedItem().toString();
            String lens = spinnerLens.getSelectedItem().toString();
            String aperture = etAperture.getText().toString().trim();
            String shutter = etShutter.getText().toString().trim();
            String iso = etIso.getText().toString().trim();
            String others = etOthers.getText().toString().trim();

            // Save submission info locally or to database
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SubmissionPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("title", title);
            editor.putString("description", description);
            editor.putString("location", location);
            editor.putString("date", date);
            editor.putString("camera", camera);
            editor.putString("lens", lens);
            editor.putString("aperture", aperture);
            editor.putString("shutter", shutter);
            editor.putString("iso", iso);
            editor.putString("others", others);
            editor.apply();

            Toast.makeText(getActivity(), "信息已保存", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error saving submission info", e);
        }
    }
}
