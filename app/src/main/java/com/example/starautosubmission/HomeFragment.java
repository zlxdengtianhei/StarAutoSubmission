package com.example.starautosubmission;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class HomeFragment extends Fragment {

    private SlidingImageView image;
    private ImageView backgroundImage;
    private Button submitButton;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = view.findViewById(R.id.image);
        backgroundImage = view.findViewById(R.id.background_image);
        submitButton = view.findViewById(R.id.submit_button);

        // Load cached image URL and display it
        String imageUrl = MainActivity.getCachedImageUrl(getContext());
        if (imageUrl != null) {
            image.loadImage(imageUrl);
            // Use Glide to load the background image with blur transformation
            Glide.with(this)
                    .load(imageUrl)
                    .transform(new MultiTransformation<>(new CenterCrop(), new BlurTransformation(25)))
                    .into(backgroundImage);
        }

        // Set onClickListener for the image view to open a web page
        image.setOnClickListener(v -> openWebPage("https://apod.nasa.gov/apod/astropix.html"));

        // Set onClickListener for the submit button to navigate to the submission page
        submitButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SubmissionActivity.class);
            startActivity(intent);
        });
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
