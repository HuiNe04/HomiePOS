package com.example.homie;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.UUID;

public class ProductActivity extends AppCompatActivity {

    RecyclerView recyclerProducts;
    FloatingActionButton fabAddProduct;
    ProductAdapter productAdapter;
    ArrayList<String> productList;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        recyclerProducts = findViewById(R.id.recyclerProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);

        dbHelper = new DatabaseHelper(this);

        try {
            dbHelper.createDatabase(); // đảm bảo DB đã copy từ assets
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi tạo DB: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        productList = dbHelper.getAllProducts();

        productAdapter = new ProductAdapter(productList);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerProducts.setAdapter(productAdapter);

        fabAddProduct.setOnClickListener(v -> showAddProductDialog());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    // 🟢 Dialog thêm sản phẩm
    private void showAddProductDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_add_product, null);

        EditText edtName = view.findViewById(R.id.edtProductName);
        EditText edtPrice = view.findViewById(R.id.edtProductPrice);
        EditText edtStock = view.findViewById(R.id.edtProductStock); // 🆕 thêm dòng này

        new AlertDialog.Builder(this)
                .setTitle("Thêm sản phẩm mới")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = edtName.getText().toString().trim();
                    String priceStr = edtPrice.getText().toString().trim();
                    String stockStr = edtStock.getText().toString().trim(); // 🆕 lấy số lượng

                    if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        int stock = Integer.parseInt(stockStr); //  chuyển stock sang int
                        String idProduct = "P" + System.currentTimeMillis();

                        boolean success = dbHelper.insertProduct(idProduct, name, price, stock); //  truyền thêm stock
                        if (success) {
                            Toast.makeText(this, "✅ Đã thêm: " + name, Toast.LENGTH_SHORT).show();
                            productList.clear();
                            productList.addAll(dbHelper.getAllProducts());
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "❌ Lỗi khi thêm sản phẩm!", Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Giá hoặc số lượng không hợp lệ!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
