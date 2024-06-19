package com.example.starautosubmission;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Context;

public class EmailSettingsFragment extends Fragment {

    private Spinner spinnerEmailService;
    private EditText etSenderEmail, etEmailPassword;
    private CheckBox checkboxPrimaryEmail;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_email_settings, container, false);

        spinnerEmailService = view.findViewById(R.id.spinner_email_service);
        etSenderEmail = view.findViewById(R.id.et_sender_email);
        etEmailPassword = view.findViewById(R.id.et_email_password);
        checkboxPrimaryEmail = view.findViewById(R.id.checkbox_primary_email);
        btnSave = view.findViewById(R.id.btn_save_email_settings);

        btnSave.setOnClickListener(v -> saveEmailSettings());
        return view;
    }

    private void saveEmailSettings() {
        String emailService = spinnerEmailService.getSelectedItem().toString();
        String senderEmail = etSenderEmail.getText().toString().trim();
        String emailPassword = etEmailPassword.getText().toString().trim();
        boolean isPrimaryEmail = checkboxPrimaryEmail.isChecked();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EmailPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("emailService", emailService);
        editor.putString("senderEmail", senderEmail);
        editor.putString("emailPassword", emailPassword);
        editor.putBoolean("isPrimaryEmail", isPrimaryEmail);
        editor.apply();

        Toast.makeText(getActivity(), "邮箱设置已保存", Toast.LENGTH_SHORT).show();
    }
}
