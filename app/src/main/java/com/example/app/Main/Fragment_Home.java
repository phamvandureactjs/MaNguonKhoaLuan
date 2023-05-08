package com.example.app.Main;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app.R;
import com.example.app.Value;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Fragment_Home extends Fragment {
    private View fm_home;
    private TextView timeUpdate, speedVal, phVal, TDSVal, totalVal, txtAlarm;
    private final DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private String device;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fm_home = inflater.inflate(R.layout.fragment_home, container, false);

        anhXa();
        read_Name();
        realTimeData();
        return fm_home;
    }

    // Ham anh xa
    private void anhXa() {
        timeUpdate = fm_home.findViewById(R.id.timeUpdate);
        speedVal = fm_home.findViewById(R.id.speedVal);
        phVal = fm_home.findViewById(R.id.phVal);
        TDSVal = fm_home.findViewById(R.id.TDSVal);
        totalVal = fm_home.findViewById(R.id.totalVal);
        txtAlarm = fm_home.findViewById(R.id.txt_Alarm);
    }

    // Đọc dữ liệu
    private void realTimeData() {
        mData.child(device + "/Present").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Value val = snapshot.getValue(Value.class);
                if(val != null) {
                    if (val.ph < 6.5) {
                        txtAlarm.setText("PH thấp quá ngưỡng cho phép");
                        txtAlarm.setTextColor(Color.RED);
                    }
                    else if (val.ph > 8) {
                        txtAlarm.setText("PH cao quá ngưỡng cho phép");
                        txtAlarm.setTextColor(Color.RED);
                    }
                    else if (val.tds > 500) {
                        txtAlarm.setText("Nước ô nhiễm");
                        txtAlarm.setTextColor(Color.RED);
                    }
                    else {
                        txtAlarm.setText("Bình thường");
                        txtAlarm.setTextColor(Color.rgb(0, 143, 0));
                    }

                    speedVal.setText(String.valueOf((double) Math.floor(val.speed * 100) / 100) + " lít/phút");
                    phVal.setText(String.valueOf((double) Math.floor(val.ph * 100) / 100));
                    TDSVal.setText(String.valueOf((double) Math.floor(val.tds * 100) / 100) + " ppm");

                    totalVal.setText(String.valueOf((double) Math.floor(val.total * 100) / 100) + " lít");
                    timeUpdate.setText("Lần nhật lần cuối: " + val.time);
                }
                else {
                    Value val_1 = new Value(0, 0, 0, 0, "11:20 20/11/2022");
                    mData.child(device + "/Present").setValue(val_1);
                }
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
                    device = name_1;
                }
            }
        }
    }
}
