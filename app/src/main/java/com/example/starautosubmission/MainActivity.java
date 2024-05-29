package com.example.starautosubmission;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private ImageButton menuButton;
    private static final String IMAGE_URL_KEY = "daily_image_url";
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查用户是否登录
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);

        if (!isLoggedIn) {
            // 如果没有用户登录，跳转到登录界面
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // 关闭当前活动
            return; // 结束 onCreate 方法
        }

        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        navigationView = findViewById(R.id.navigationView);
        menuButton = findViewById(R.id.menuButton);

        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerLayout.closeDrawer(navigationView);
            } else {
                drawerLayout.openDrawer(navigationView);
            }
        });

        setupViewPager();
        setupBottomNavigationView();
        setupNavigationView();
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
                if (position == 0) {
                    menuButton.setVisibility(View.VISIBLE);
                    HomeFragment homeFragment = (HomeFragment) adapter.getFragment(position);
                    // 确保 Fragment 的视图已经创建
                    if (homeFragment.getView() != null) {
                        View submitButton = homeFragment.getView().findViewById(R.id.submit_button);
                        if (submitButton != null) {
                            submitButton.setOnClickListener(v -> {
                                Intent intent = new Intent(MainActivity.this, SubmissionActivity.class);
                                startActivity(intent);
                            });
                        }
                    }
                } else {
                    menuButton.setVisibility(View.GONE);
                }
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
        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;
            if (itemId == R.id.nav_my_devices) {
                intent = new Intent(MainActivity.this, MyDevicesActivity.class);
            } else if (itemId == R.id.nav_about) {
                intent = new Intent(MainActivity.this, AboutActivity.class);
            } else if (itemId == R.id.nav_settings) {
                intent = new Intent(MainActivity.this, SettingsActivity.class);
            }
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "未实现的选项", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawers();
            return true;
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
                SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(IMAGE_URL_KEY, result);
                editor.apply();
            }
        }
    }

    public static String getCachedImageUrl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(IMAGE_URL_KEY, null);
    }
}
