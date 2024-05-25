package com.example.starautosubmission;
import com.example.starautosubmission.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private static final String IMAGE_URL_KEY = "daily_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        setupViewPager();
        setupBottomNavigationView();
        fetchDailyImage();
    }

    private void setupViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new MapFragment());
        fragments.add(new ProfileFragment());

        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_map);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                        break;
                }
            }
        });
    }

    private void setupBottomNavigationView() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    viewPager.setCurrentItem(0);
                    return true;
                } else if (itemId == R.id.nav_map) {
                    viewPager.setCurrentItem(1);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    viewPager.setCurrentItem(2);
                    return true;
                }
                return false;
            }
        });
    }

    private void fetchDailyImage() {
        new FetchImageTask(this).execute("https://apod.nasa.gov/apod/astropix.html");
    }

    private static class FetchImageTask extends AsyncTask<String, Void, String> {
        private Context context;

        FetchImageTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                Document document = Jsoup.connect(urls[0]).get();
                Elements elements = document.select("img");
                if (!elements.isEmpty()) {
                    String src = elements.get(0).attr("src");
                    return "https://apod.nasa.gov/apod/" + src;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(IMAGE_URL_KEY, result);
                editor.apply();
            }
        }
    }

    public static String getCachedImageUrl(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(IMAGE_URL_KEY, null);
    }
}
