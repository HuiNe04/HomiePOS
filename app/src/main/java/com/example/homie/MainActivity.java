package com.example.homie;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    TextView tvWelcome;
    MaterialButton btnProduct, btnInvoice, btnRevenue, btnCreateUser;
    String currentUserId;
    String currentUserName;
    String currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("TEST_APP", "MainActivity started successfully!");
        // ✅ Nhận thông tin từ LoginActivity (nếu có)
        currentUserName = getIntent().getStringExtra("user_name");
        currentUserRole = getIntent().getStringExtra("user_role");
        currentUserId = getIntent().getStringExtra("id_user");

        if (currentUserName == null) currentUserName = "Người dùng";
        if (currentUserRole == null) currentUserRole = "Staff";

        // ✅ Kết nối với database có sẵn (trong assets)
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase();
            db = dbHelper.openDatabase();
            Toast.makeText(this, "✅ Kết nối thành công với HOMI.db", Toast.LENGTH_SHORT).show();

            // 🧠 In ra danh sách bảng để kiểm tra copy thành công
            dbHelper.logAllTables();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "❌ Lỗi kết nối SQLite: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("DB_ERROR", e.getMessage());
        }

        // ✅ Ánh xạ View
        tvWelcome = findViewById(R.id.tvWelcome);
        btnProduct = findViewById(R.id.btnProduct);
        btnInvoice = findViewById(R.id.btnInvoice);
        btnRevenue = findViewById(R.id.btnRevenue);
        btnCreateUser = findViewById(R.id.btnCreateUser);

        // ✅ Hiển thị lời chào
        tvWelcome.setText("Xin chào, " + currentUserName + " (" + currentUserRole + ")");

        // ✅ Phân quyền hiển thị nút “Tạo tài khoản”
        if (currentUserRole.equalsIgnoreCase("Admin")) {
            btnCreateUser.setVisibility(android.view.View.VISIBLE);
        } else {
            btnCreateUser.setVisibility(android.view.View.GONE);
        }

        // ✅ Khi Admin bấm "Tạo tài khoản" → mở RegisterActivity
        btnCreateUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // ✅ Quản lý sản phẩm
        btnProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // ✅ Quản lý hóa đơn
        btnInvoice.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InvoiceActivity.class);
            intent.putExtra("id_user", currentUserId); // 🟢 Gửi ID_USER của người đăng nhập
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // ✅ Doanh thu
        btnRevenue.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RevenueActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // ✅ Hành vi nút Back tương thích Android 13+
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // Đóng Activity hiện tại
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}
