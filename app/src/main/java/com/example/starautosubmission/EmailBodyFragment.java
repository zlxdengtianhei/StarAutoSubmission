package com.example.starautosubmission;

import android.content.Context;
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

public class EmailBodyFragment extends Fragment {

    private EditText etCsvaGreeting, etCsvaClosing, etYkzgGreeting, etYkzgClosing, etGovGreeting, etGovClosing, etBjGreeting, etBjClosing;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_email_body, container, false);

        etCsvaGreeting = view.findViewById(R.id.et_csva_greeting);
        etCsvaClosing = view.findViewById(R.id.et_csva_closing);
        etYkzgGreeting = view.findViewById(R.id.et_ykzg_greeting);
        etYkzgClosing = view.findViewById(R.id.et_ykzg_closing);
        etGovGreeting = view.findViewById(R.id.et_gov_greeting);
        etGovClosing = view.findViewById(R.id.et_gov_closing);
        etBjGreeting = view.findViewById(R.id.et_bj_greeting);
        etBjClosing = view.findViewById(R.id.et_bj_closing);
        btnSave = view.findViewById(R.id.btn_save_email_body);

        btnSave.setOnClickListener(v -> saveEmailBody());
        return view;
    }

    private void saveEmailBody() {
        String csvaGreeting = etCsvaGreeting.getText().toString().trim();
        String csvaClosing = etCsvaClosing.getText().toString().trim();
        String ykzgGreeting = etYkzgGreeting.getText().toString().trim();
        String ykzgClosing = etYkzgClosing.getText().toString().trim();
        String govGreeting = etGovGreeting.getText().toString().trim();
        String govClosing = etGovClosing.getText().toString().trim();
        String bjGreeting = etBjGreeting.getText().toString().trim();
        String bjClosing = etBjClosing.getText().toString().trim();

        // Save the information to Bmob or locally
        // Example: save to SharedPreferences (for simplicity)
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("EmailBodyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("csvaGreeting", csvaGreeting);
        editor.putString("csvaClosing", csvaClosing);
        editor.putString("ykzgGreeting", ykzgGreeting);
        editor.putString("ykzgClosing", ykzgClosing);
        editor.putString("govGreeting", govGreeting);
        editor.putString("govClosing", govClosing);
        editor.putString("bjGreeting", bjGreeting);
        editor.putString("bjClosing", bjClosing);
        editor.apply();

        Toast.makeText(getActivity(), "邮件正文设置已保存", Toast.LENGTH_SHORT).show();
    }
}
