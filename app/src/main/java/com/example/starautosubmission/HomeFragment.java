package com.example.starautosubmission;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

    private static final String TAG = "HomeFragment";

    private SlidingImageView image;
    private ImageView backgroundImage;
    private Button submitButton;
    private Button viewHistoryButton;
    private WebView webView;
    private Button closeWebViewButton;
    private View webViewContainer;

    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = view.findViewById(R.id.image);
        backgroundImage = view.findViewById(R.id.background_image);
        submitButton = view.findViewById(R.id.submit_button);
        viewHistoryButton = view.findViewById(R.id.view_history_button);
        webView = view.findViewById(R.id.webview);
        closeWebViewButton = view.findViewById(R.id.close_webview_button);
        webViewContainer = view.findViewById(R.id.webview_container);

        String imageUrl = MainActivity.getCachedImageUrl(getContext());
        if (imageUrl != null) {
            image.loadImage(imageUrl);
            // Use Glide to load the background image with blur transformation
            Glide.with(this)
                    .load(imageUrl)
                    .transform(new MultiTransformation<>(new CenterCrop(), new BlurTransformation(25)))
                    .into(backgroundImage);
        }

        // Configure WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted: url = " + url);
                if (!url.equals("about:blank")) {
                    webViewContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: url = " + url);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);

        image.setOnClickListener(v -> openWebPage("https://apod.nasa.gov/apod/astropix.html"));

        viewHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://apod.nasa.gov/apod/archivepixFull.html"));
            startActivity(intent);
        });

        submitButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SubmissionActivity.class);
            startActivity(intent);
        });

        closeWebViewButton.setOnClickListener(v -> {
            closeWebView();
        });
    }

    private void openWebPage(String url) {
        Log.d(TAG, "openWebPage: url = " + url);
        webView.loadUrl(url);
        webViewContainer.setVisibility(View.VISIBLE);
    }

    private void closeWebView() {
        Log.d(TAG, "closeWebView called");
        webView.stopLoading();
        webView.loadUrl("about:blank"); // Clear the WebView content
        webView.clearHistory();
        webView.clearCache(true);
        webViewContainer.setVisibility(View.GONE);
    }
}
