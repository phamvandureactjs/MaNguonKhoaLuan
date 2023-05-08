package com.example.app.Main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app.Account;
import com.example.app.Login.Login_Activity;
import com.example.app.Login.MyService;
import com.example.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Fragment_Account extends Fragment {

    private View fm_account;
    private ListView listView_2;
    String[] items = new String[]{"Cập nhật thiết bị", "Đổi mật khẩu", "Đăng xuất"};
    private DatabaseReference mData;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText txt_name_4;
    private EditText txt_pass_4, txt_pass_5, txt_pass_6;
    private TextView email_txt;
    private String device;
    Intent intent_Service;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fm_account = inflater.inflate(R.layout.fragment_account, container, false);

        mData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        anhXa();
        read_Name();
        listView_2_Show();
        return fm_account;
    }

    // Ánh xạ ID
    private void anhXa() {
        listView_2 = fm_account.findViewById(R.id.listView_2);
        email_txt = fm_account.findViewById(R.id.email_txt);
    }

    // Hiển thị list View
    private void listView_2_Show() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);
        listView_2.setAdapter(adapter);

        listView_2.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    dialog_0_Show();
                    break;
                case 1:
                    dialog_1_Show();
                    break;
                case 2:
                    mAuth.signOut();
                    mData.child(device + "/Account/status").setValue(0);
                    Toast.makeText(getActivity(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(getActivity(), Login_Activity.class);
                    startActivity(intent2);
                    intent_Service = new Intent(getActivity(), MyService.class);
                    getActivity().stopService(intent_Service);
                    break;
            }
        });
    }

    // Hiển thị dialog cập nhật thiết bị
    private void dialog_0_Show() {
        final Dialog dialog_0 = new Dialog(getActivity());
        dialog_0.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_0.setContentView(R.layout.update_device);

        Window window_0 = dialog_0.getWindow();
        if (window_0 == null) {
            return;
        }

        window_0.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window_0.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windownAttributes_0 = window_0.getAttributes();
        windownAttributes_0.gravity = Gravity.CENTER;
        window_0.setAttributes(windownAttributes_0);

        txt_name_4 = dialog_0.findViewById(R.id.txt_name_4);
        Button bt_cancel_1 = dialog_0.findViewById(R.id.bt_cancel_1);
        Button bt_update_name = dialog_0.findViewById(R.id.bt_update_name);

        dialog_0.show();

        txt_name_4.setText(device);

        bt_cancel_1.setOnClickListener(v -> dialog_0.dismiss());
        bt_update_name.setOnClickListener(v -> {
            String name = txt_name_4.getText().toString().trim();
            if (name.isEmpty())
                Toast.makeText(getActivity(), "Không được bỏ trống !!!", Toast.LENGTH_SHORT).show();
            else {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                read_Name();
                                dialog_0.dismiss();
                            }
                        });
            }
        });
    }

    // Lấy tên thiết bị
    private void read_Name() {
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        if (user1 != null) {
            for (UserInfo profile : user1.getProviderData()) {
                String name_1 = profile.getDisplayName();

                if (name_1 != null) {
                    device = name_1;
                    email_txt.setText(device);
                }
            }
        }
    }

    // Bật dialog thay đổi mật khẩu
    private void dialog_1_Show() {
        final Dialog dialog_1 = new Dialog(getActivity());
        dialog_1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_1.setContentView(R.layout.dialog_change_password);

        Window window_1 = dialog_1.getWindow();
        if (window_1 == null) {
            return;
        }

        window_1.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window_1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windownAttributes_1 = window_1.getAttributes();
        windownAttributes_1.gravity = Gravity.CENTER;
        window_1.setAttributes(windownAttributes_1);

        dialog_1.show();

        txt_pass_4 = dialog_1.findViewById(R.id.txt_pass_4);
        txt_pass_5 = dialog_1.findViewById(R.id.txt_pass_5);
        txt_pass_6 = dialog_1.findViewById(R.id.txt_pass_6);

        Button bt_cancel = dialog_1.findViewById(R.id.bt_cancel);
        Button bt_change_pass = dialog_1.findViewById(R.id.bt_change_pass);

        bt_cancel.setOnClickListener(v -> dialog_1.dismiss());
        bt_change_pass.setOnClickListener(v -> {
            String pass1 = txt_pass_4.getText().toString().trim();
            String pass2 = txt_pass_5.getText().toString().trim();
            String pass3 = txt_pass_6.getText().toString().trim();

            if (pass1.isEmpty() || pass2.isEmpty() || pass3.isEmpty())
                Toast.makeText(getActivity(), "Không được bỏ trống !!!", Toast.LENGTH_SHORT).show();
            else {
                mData.child(device + "/Account").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Account account = snapshot.getValue(Account.class);
                        assert account != null;

                        if (pass1.equals(account.pass)) {
                            if (pass2.equals(pass3)) {
                                user.updatePassword(pass2).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        dialog_1.dismiss();
                                        mAuth.signOut();
                                        mData.child(device + "/Account/status").setValue(0);
                                        Intent intent1 = new Intent(getActivity(), Login_Activity.class);
                                        startActivity(intent1);
                                    }
                                    else
                                        Toast.makeText(getActivity(), "Mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                                });
                            }
                            else
                                Toast.makeText(getActivity(), "Nhập lại mật khẩu mới không đúng", Toast.LENGTH_SHORT).show();

                        }
                        else
                            Toast.makeText(getActivity(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }
}
