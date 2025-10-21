package com.example.homie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText edtFullname, edtUsername, edtPassword;
    MaterialButton btnRegister;
    TextView tvBackLogin, tvRoleLabel;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ View
        edtFullname = findViewById(R.id.edtFullname);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackLogin = findViewById(R.id.tvBackLogin);
        tvRoleLabel = findViewById(R.id.tvRoleLabel);

        dbHelper = new DatabaseHelper(this);

        // 🟢 Kiểm tra số lượng user hiện có trong database
        //int userCount = dbHelper.getUserCount();

        //String defaultRole;
        //if (userCount == 0) {
            // ⚙️ Nếu chưa có user nào → người đầu tiên là Admin
            //defaultRole = "Admin";
            //tvRoleLabel.setText("Loại tài khoản: Admin (mặc định)");
        //} else {
            // ⚙️ Nếu đã có user → tất cả các tài khoản mới mặc định là Staff
            //defaultRole = "Staff";
            //tvRoleLabel.setText("Loại tài khoản: Staff (mặc định)");
        //}

        // 🔙 Quay lại màn hình đăng nhập
        tvBackLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // 🔘 Xử lý nút đăng ký
        //String finalDefaultRole = defaultRole;
        //btnRegister.setOnClickListener(v -> {
            //String fullname = edtFullname.getText().toString().trim();
            //String username = edtUsername.getText().toString().trim();
            //String password = edtPassword.getText().toString().trim();

           // if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                //Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
           // }

            // Gán role theo logic
            //String role = finalDefaultRole;

            // success = dbHelper.insertUser(username, password, fullname, role);
           // if (success) {
                //Toast.makeText(this, "Tạo tài khoản thành công (" + role + ")", Toast.LENGTH_SHORT).show();
                //finish();
            //} else {
               // Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
            //}
        //});
    }
}
