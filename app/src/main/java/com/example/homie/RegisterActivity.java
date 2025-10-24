package com.example.homie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText edtFullname, edtUsername, edtPassword;
    MaterialButton btnRegister;
    TextView tvBackLogin, tvRoleLabel;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFullname = findViewById(R.id.edtFullname);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackLogin = findViewById(R.id.tvBackLogin);
        tvRoleLabel = findViewById(R.id.tvRoleLabel);

        dbHelper = new DatabaseHelper(this);

        tvBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String fullname = edtFullname.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                dbHelper.createDatabase();
                int userCount = dbHelper.getUserCount();

                String role;
                if (userCount == 0) {
                    role = "Admin";
                    tvRoleLabel.setText("Loại tài khoản: Admin (mặc định)");
                } else {
                    role = "Staff";
                    tvRoleLabel.setText("Loại tài khoản: Staff (mặc định)");
                }

                String idUser = "U" + System.currentTimeMillis(); // auto ID
                boolean success = dbHelper.insertUser(idUser, username, password, fullname, role);

                if (success) {
                    Toast.makeText(this, "Tạo tài khoản thành công (" + role + ")", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
