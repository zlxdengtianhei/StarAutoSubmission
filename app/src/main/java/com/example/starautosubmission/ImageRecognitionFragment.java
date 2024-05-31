package com.example.starautosubmission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImageRecognitionFragment extends Fragment {

    private OpenAIApiService openAIApiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_recognition, container, false);
        initializeRetrofit();
        setupUI(view);
        return view;
    }

    private void initializeRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        openAIApiService = retrofit.create(OpenAIApiService.class);
    }

    private void setupUI(View view) {
        EditText promptEditText = view.findViewById(R.id.editTextPrompt);
        Button recognizeButton = view.findViewById(R.id.buttonRecognize);
        TextView resultTextView = view.findViewById(R.id.textViewResult);

        recognizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prompt = promptEditText.getText().toString();
                OpenAIRequest request = new OpenAIRequest(prompt, 50);

                openAIApiService.describeImage(request).enqueue(new Callback<OpenAIResponse>() {
                    @Override
                    public void onResponse(Call<OpenAIResponse> call, Response<OpenAIResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            resultTextView.setText(response.body().getDescription());
                        } else {
                            resultTextView.setText("Error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<OpenAIResponse> call, Throwable t) {
                        resultTextView.setText("Failure: " + t.getMessage());
                    }
                });
            }
        });
    }
}
