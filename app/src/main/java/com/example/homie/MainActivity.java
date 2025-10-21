package com.example.homie;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    TextView tvWelcome;
    MaterialButton btnProduct, btnInvoice, btnRevenue, btnCreateUser;

    String currentUserName = "Huy";
    String currentUserRole = "Admin"; // sau này lấy từ DB hoặc Intent LoginActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // kết nối với DataBase
        dbHelper = new DatabaseHelper(this);


        try {
            // copy file Homi vào file thư mục
            dbHelper.createDatabase();

            //mở chuỗi kết nối
            db = dbHelper.openDatabase();
            Toast.makeText(this , "Kết nối thành công với HOMI.db", Toast.LENGTH_LONG).show();

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Lỗi kết nối SQLite: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Ánh xạ View
        tvWelcome = findViewById(R.id.tvWelcome);
        btnProduct = findViewById(R.id.btnProduct);
        btnInvoice = findViewById(R.id.btnInvoice);
        btnRevenue = findViewById(R.id.btnRevenue);
        btnCreateUser = findViewById(R.id.btnCreateUser);

        // Hiển thị lời chào
        tvWelcome.setText("Xin chào, " + currentUserName + " (" + currentUserRole + ")");

        // 🟢 Phân quyền hiển thị nút "Tạo tài khoản"
        if (currentUserRole.equalsIgnoreCase("Admin")) {
            btnCreateUser.setVisibility(View.VISIBLE);
        } else {
            btnCreateUser.setVisibility(View.GONE);
        }

        // 🟢 Khi Admin bấm nút "Tạo tài khoản" → mở RegisterActivity
        btnCreateUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 🟤 Quản lý sản phẩm
        btnProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 🟡 Quản lý hóa đơn
        btnInvoice.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InvoiceActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 🟢 Doanh thu
        btnRevenue.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RevenueActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // ⚙️ Back gesture tương thích Android 13+ (OnBackPressedDispatcher)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Khi người dùng vuốt Back hoặc bấm nút Back
                finish(); // Đóng Activity hiện tại
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}
