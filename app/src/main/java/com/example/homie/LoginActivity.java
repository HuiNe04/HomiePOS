package com.example.homie;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText edtUsername, edtPassword;
    MaterialButton btnLogin;
    TextView tvRegister;

    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d("TEST_APP", "LoginActivity started!");

        // ✅ Bước 1: Khởi tạo & copy database nếu chưa có
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase(); // copy HOMI.db từ assets nếu chưa có
            db = dbHelper.openDatabase(); // mở DB
            Log.d("DB_STATUS", "✅ Database copied and opened successfully in LoginActivity!");
            dbHelper.logAllTables(); // in danh sách bảng ra logcat
        } catch (Exception e) {
            Log.e("DB_ERROR", "❌ Lỗi khởi tạo DB: " + e.getMessage());
            Toast.makeText(this, "Lỗi DB: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // ✅ Bước 2: Ánh xạ View
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        // ✅ Bước 3: Sự kiện chuyển sang RegisterActivity
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // ✅ Bước 4: Xử lý đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Cursor cursor = dbHelper.checkLogin(username, password);
                if (cursor != null && cursor.moveToFirst()) {
                    String fullname = cursor.getString(cursor.getColumnIndexOrThrow("FULLNAME"));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow("ROLE"));
                    cursor.close();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_name", fullname);
                    intent.putExtra("user_role", role);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("DB_ERROR", "❌ Lỗi đăng nhập: " + e.getMessage());
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
