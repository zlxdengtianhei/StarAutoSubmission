package com.example.starautosubmission;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import android.util.Log;

import okhttp3.HttpUrl;

public class AuthUtil {
    private static final String TAG = "AuthUtil";

    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        Log.d(TAG, "Generated Date: " + date);

        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        Log.d(TAG, "String to sign: " + preStr);

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes("UTF-8"), "HmacSHA256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(preStr.getBytes("UTF-8"));
        String sha = Base64.encodeToString(hexDigits, Base64.DEFAULT);
        Log.d(TAG, "Generated SHA: " + sha);

        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"",
                apiKey, "hmac-sha256", "host date request-line", sha);
        Log.d(TAG, "Authorization: " + authorization);

        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder()
                .addQueryParameter("authorization", Base64.encodeToString(authorization.getBytes("UTF-8"), Base64.DEFAULT))
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();

        String authUrl = httpUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        Log.d(TAG, "Auth URL: " + authUrl);

        return authUrl;
    }
}
