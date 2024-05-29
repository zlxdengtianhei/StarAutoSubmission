package com.example.starautosubmission;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Context;

public class PersonalInfoFragment extends Fragment {

    private EditText etName, etSignature, etPhone;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal_info, container, false);

        etName = view.findViewById(R.id.et_name);
        etSignature = view.findViewById(R.id.et_signature);
        etPhone = view.findViewById(R.id.et_phone);
        btnSave = view.findViewById(R.id.btn_save_personal_info);

        btnSave.setOnClickListener(v -> savePersonalInfo());
        return view;
    }

    private void savePersonalInfo() {
        String name = etName.getText().toString().trim();
        String signature = etSignature.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Save the information to Bmob or locally
        // Example: save to SharedPreferences (for simplicity)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("signature", signature);
        editor.putString("phone", phone);
        editor.apply();

        Toast.makeText(getActivity(), "信息已保存", Toast.LENGTH_SHORT).show();
    }
}
