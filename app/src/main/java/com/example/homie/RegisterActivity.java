package com.example.homie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText edtFullname, edtUsername, edtPassword;
    Spinner spnRole;
    MaterialButton btnRegister;
    TextView tvBackLogin;

    DatabaseHelper dbHelper; // Lớp này Khanh sẽ làm ở phần DB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ
        edtFullname = findViewById(R.id.edtFullname);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        spnRole = findViewById(R.id.spnRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackLogin = findViewById(R.id.tvBackLogin);

        dbHelper = new DatabaseHelper(this);

        // Kiểm tra xem có tài khoản nào chưa
        int userCount = dbHelper.getUserCount();

        if (userCount == 0) {
            // Nếu chưa có user nào → tài khoản đầu tiên là Admin
            spnRole.setVisibility(View.GONE);
            Toast.makeText(this, "Hệ thống chưa có tài khoản, bạn sẽ được tạo làm Admin!", Toast.LENGTH_LONG).show();
        }

        // Quay lại màn hình login
        tvBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Nút đăng ký
        btnRegister.setOnClickListener(v -> {
            String fullname = edtFullname.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            String role;
            if (userCount == 0) {
                // Tài khoản đầu tiên luôn là Admin
                role = "Admin";
            } else {
                // Nếu đã có user, lấy loại từ spinner
                role = spnRole.getSelectedItem().toString();
            }

            boolean success = dbHelper.insertUser(username, password, fullname, role);

            if (success) {
                Toast.makeText(this, "Tạo tài khoản thành công (" + role + ")", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
