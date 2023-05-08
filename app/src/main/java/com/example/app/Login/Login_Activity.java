package com.example.app.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.Account;
import com.example.app.Main.MainActivity;
import com.example.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login_Activity extends AppCompatActivity {

    private TextView txt_forgot, txt_sign_in;
    private Button bt_login;
    private EditText account_logIn, password_logIn;
    private FirebaseAuth mAuth;
    private final DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private Account account_1;
    private String childTxt;
    Intent intent_Service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        anhXa();
        register();
        Forgot_Pass();
        login();
    }

    // Hàm ánh xạ ID
    private void anhXa() {
        // Event
        txt_forgot = findViewById(R.id.txt_forgot);
        txt_sign_in = findViewById(R.id.txt_sign_in);
        bt_login = findViewById(R.id.bt_login);

        // Account
        account_logIn = findViewById(R.id.account_logIn);
        password_logIn = findViewById(R.id.password_logIn);
    }

    // Hàm đi đến trang đăng ký
    private void register() {
        txt_sign_in.setOnClickListener(view -> {
            Intent intent = new Intent(Login_Activity.this, Register_Activity.class);
            startActivity(intent);
        });
    }

    // Hàm gửi email đặt lại mật khẩu
    private void Forgot_Pass() {
        txt_forgot.setOnClickListener(v -> {
            String email1 = account_logIn.getText().toString();
            // Kiểm tra xem đã nhập email chưa
            if (email1.isEmpty())
                Toast.makeText(Login_Activity.this, "Vui lòng nhập địa chỉ mail", Toast.LENGTH_SHORT).show();
            else {
                mAuth.sendPasswordResetEmail(email1).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(Login_Activity.this, "Đã gửi mã xác nhận, vui lòng check mail", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(Login_Activity.this, "Thất bại", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // Hàm đăng nhập
    private void login() {
        bt_login.setOnClickListener(v -> {
            String email1 = account_logIn.getText().toString();
            String pass1 = password_logIn.getText().toString();
            // Kiểm tra xem đã nhập email và password chưa
            if (email1.isEmpty()) {
                Toast.makeText(Login_Activity.this, "Vui lòng nhập địa chỉ Email", Toast.LENGTH_SHORT).show();
            } else if (pass1.isEmpty()) {
                Toast.makeText(Login_Activity.this, "Vui lòng nhập Password", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.signInWithEmailAndPassword(email1, pass1).addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login_Activity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                            startActivity(intent);
                            read_Name();
                            account_1 = new Account(email1, pass1, 1);
                            mData.child(childTxt + "/Account").setValue(account_1);

                            intent_Service = new Intent(getApplicationContext(), MyService.class);
                            startService(intent_Service);
                        } else {
                            Toast.makeText(Login_Activity.this, "Tên tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                    childTxt = name_1;
                }
            }
        }
    }
}