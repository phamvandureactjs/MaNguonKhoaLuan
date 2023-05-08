package com.example.app.Login;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.app.Main.MainActivity;
import com.example.app.R;
import com.example.app.Value;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyService extends Service {

    DatabaseReference mData;
    private static final String CHANNEL_ID = "simplified_coding";
    private static final String CHANNEL_NAME = "Simplified Coding";
    private static final String CHANNEL_DESC = "Android Push Notification Tutorial";
    private String device_Name;

    public MyService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mData = FirebaseDatabase.getInstance().getReference();
        mData.child("Test").setValue(1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        realTimeData();

        return super.onStartCommand(intent, flags, startId);
    }

    private void realTimeData() {
        read_Name();
        mData.child(device_Name + "/Present").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Value val = snapshot.getValue(Value.class);
                assert val != null;
                if (val.ph < 6.5) sendNotification_1();
                else if (val.ph > 8) sendNotification_2();

                if (val.tds >= 500) sendNotification_3();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Lấy tên thiết bị
    private void read_Name() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String name_1 = profile.getDisplayName();

                if (name_1 != null) {
                    device_Name = name_1;
                }
            }
        }
    }

    private void sendNotification_1() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplication(), CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("Cảnh báo PH");
        mBuilder.setContentText("PH thấp quá ngưỡng cho phép");
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplication());
        managerCompat.notify(1, mBuilder.build());
    }

    private void sendNotification_2() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplication(), CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("Cảnh báo PH");
        mBuilder.setContentText("PH cao quá ngưỡng cho phép");
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplication());
        managerCompat.notify(2, mBuilder.build());
    }

    private void sendNotification_3() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplication(), CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle("Cảnh báo TDS");
        mBuilder.setContentText("Nước ô nhiễm");
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getApplication());
        managerCompat.notify(3, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        mData.child("Test").setValue(0);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}