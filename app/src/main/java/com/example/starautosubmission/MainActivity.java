package com.example.starautosubmission;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final String CHANNEL_ID = "immediate_notification_channel";

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
        requestNotificationPermission();
        NotificationScheduler.scheduleDailyReminder(this);


    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予
                NotificationScheduler.scheduleDailyReminder(this);
            } else {
                // 权限被拒绝
                // 可以显示一条消息，告知用户需要权限才能发送通知
                Toast.makeText(this, "通知权限被拒绝，无法发送通知", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new HomeFragment());
        fragments.add(new MapFragment());


        ViewPagerAdapter adapter = new ViewPagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // 禁用滑动
        viewPager.setUserInputEnabled(false);

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
            } else if (itemId == R.id.nav_remind_submission) {
                sendSubmissionReminder();
            }
            if (intent != null) {
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void sendSubmissionReminder() {
        // 创建通知渠道（仅适用于 Android 8.0 及以上版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Immediate Reminder Channel";
            String description = "Channel for immediate reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // 创建点击通知时打开的 Intent
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 创建通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // 确保这个图标存在
                .setContentTitle("投稿提醒")
                .setContentText("请记得今日投稿")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1002, builder.build());
            Toast.makeText(this, "提醒已发送", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "未授予通知权限，无法发送提醒", Toast.LENGTH_SHORT).show();
        }
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (result != null) {
                editor.putString(IMAGE_URL_KEY, result);
            } else {
                editor.remove(IMAGE_URL_KEY); // 清除旧的图像URL
            }
            editor.apply();
        }
    }

    public static String getCachedImageUrl(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(IMAGE_URL_KEY, null);
    }


}
