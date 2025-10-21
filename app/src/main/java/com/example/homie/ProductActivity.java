package com.example.homie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerProducts;
    FloatingActionButton fabAddProduct;
    ProductAdapter productAdapter;
    ArrayList<String> productList; // tạm dùng list string, sau này Khanh gắn SQLite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        recyclerProducts = findViewById(R.id.recyclerProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        // Tạo danh sách giả lập ban đầu
        productList = new ArrayList<>();
        productList.add("Cà phê sữa đá");
        productList.add("Trà đào cam sả");
        productList.add("Bánh ngọt socola");

        // Gắn adapter
        productAdapter = new ProductAdapter(productList);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerProducts.setAdapter(productAdapter);

        // 🟢 Nút thêm sản phẩm
        fabAddProduct.setOnClickListener(v -> showAddProductDialog());

        // ⚙️ Back gesture (chuẩn AndroidX)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    // 🧱 Hàm hiển thị dialog thêm sản phẩm
    private void showAddProductDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_add_product, null);

        EditText edtName = view.findViewById(R.id.edtProductName);
        EditText edtPrice = view.findViewById(R.id.edtProductPrice);

        new AlertDialog.Builder(this)
                .setTitle("Thêm sản phẩm mới")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = edtName.getText().toString().trim();
                    String price = edtPrice.getText().toString().trim();

                    if (name.isEmpty() || price.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    } else {
                        productList.add(name + " - " + price + "₫");
                        productAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Đã thêm sản phẩm: " + name, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
