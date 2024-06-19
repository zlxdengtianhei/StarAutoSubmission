package com.example.starautosubmission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StepOneFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Uri imageUri;
    private Button changeImageButton;
    private Button saveImageButton;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_one, container, false);

        imageView = view.findViewById(R.id.imageView);
        changeImageButton = view.findViewById(R.id.changeImageButton);
        saveImageButton = view.findViewById(R.id.saveImageButton);

        imageView.setOnClickListener(v -> openImageChooser());
        changeImageButton.setOnClickListener(v -> openImageChooser());
        saveImageButton.setOnClickListener(v -> saveImageUri());

        sharedPreferences = getActivity().getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE);

        String savedUriString = sharedPreferences.getString("imageUri", null);
        if (savedUriString != null) {
            imageUri = Uri.parse(savedUriString);
            imageView.setImageURI(imageUri);
            changeImageButton.setVisibility(View.VISIBLE);
            saveImageButton.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            getActivity().getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            imageView.setImageURI(imageUri);
            changeImageButton.setVisibility(View.VISIBLE);
            saveImageButton.setVisibility(View.VISIBLE);
        }
    }

    private void saveImageUri() {
        if (imageUri != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("imageUri", imageUri.toString());
            editor.apply();
            Toast.makeText(getActivity(), "图片已保存", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "请先选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
