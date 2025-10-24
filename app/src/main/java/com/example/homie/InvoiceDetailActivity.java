package com.example.homie;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class InvoiceDetailActivity extends AppCompatActivity {

    RecyclerView recyclerDetails;
    FloatingActionButton fabAddDetail;
    InvoiceDetailAdapter detailAdapter;
    ArrayList<InvoiceDetailItem> detailList;
    DatabaseHelper dbHelper;
    String invoiceId, invoiceType, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        recyclerDetails = findViewById(R.id.recyclerDetails);
        fabAddDetail = findViewById(R.id.fabAddDetail);

        dbHelper = new DatabaseHelper(this);
        invoiceId = getIntent().getStringExtra("id_invoice");
        invoiceType = getIntent().getStringExtra("type");
        currentUserId = getIntent().getStringExtra("id_user");

        loadInvoiceDetails();

        fabAddDetail.setOnClickListener(v -> showAddDetailDialog());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private void loadInvoiceDetails() {
        detailList = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.openDatabase();
            Cursor c = db.rawQuery("SELECT * FROM INVOICE_DETAIL WHERE ID_INVOICE=?", new String[]{invoiceId});
            while (c.moveToNext()) {
                detailList.add(new InvoiceDetailItem(
                        c.getString(c.getColumnIndexOrThrow("ID_PRODUCT")),
                        c.getInt(c.getColumnIndexOrThrow("QUANTITY")),
                        c.getDouble(c.getColumnIndexOrThrow("PRICE")),
                        c.getDouble(c.getColumnIndexOrThrow("SUBTOTAL"))
                ));
            }
            c.close();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi đọc chi tiết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        detailAdapter = new InvoiceDetailAdapter(detailList);
        recyclerDetails.setLayoutManager(new LinearLayoutManager(this));
        recyclerDetails.setAdapter(detailAdapter);
    }

    private void showAddDetailDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_detail, null);
        Spinner spProduct = view.findViewById(R.id.spProduct);
        EditText edtQuantity = view.findViewById(R.id.edtQuantity);
        EditText edtPrice = view.findViewById(R.id.edtPrice);

        ArrayList<String> productIds = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.openDatabase();
            Cursor cursor = db.rawQuery("SELECT ID_PRODUCT, NAME FROM PRODUCT", null);
            while (cursor.moveToNext()) {
                productIds.add(cursor.getString(0) + " - " + cursor.getString(1));
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Không tải được danh sách sản phẩm", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, productIds);
        spProduct.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle("Thêm chi tiết hóa đơn")
                .setView(view)
                .setPositiveButton("Lưu", (d, w) -> {
                    if (spProduct.getSelectedItem() == null) {
                        Toast.makeText(this, "Chưa có sản phẩm", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String selected = spProduct.getSelectedItem().toString();
                    String idProduct = selected.split(" - ")[0];
                    String qtyStr = edtQuantity.getText().toString();
                    String priceStr = edtPrice.getText().toString();

                    if (qtyStr.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(this, "Nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int quantity = Integer.parseInt(qtyStr);
                    double price = Double.parseDouble(priceStr);
                    double subtotal = quantity * price;

                    try {
                        SQLiteDatabase db = dbHelper.openDatabase();
                        db.execSQL("INSERT INTO INVOICE_DETAIL (ID_INVOICE, ID_PRODUCT, ID_USER, QUANTITY, PRICE, SUBTOTAL) VALUES (?, ?, ?, ?, ?, ?)",
                                new Object[]{invoiceId, idProduct, currentUserId, quantity, price, subtotal});

                        // Cập nhật tồn kho
                        updateProductStock(idProduct, quantity, invoiceType);

                        Toast.makeText(this, "✅ Đã thêm chi tiết", Toast.LENGTH_SHORT).show();
                        loadInvoiceDetails();
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi thêm chi tiết: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateProductStock(String idProduct, int qty, String type) {
        SQLiteDatabase db = dbHelper.openDatabase();
        try {
            Cursor c = db.rawQuery("SELECT STOCK FROM PRODUCT WHERE ID_PRODUCT=?", new String[]{idProduct});
            if (c.moveToFirst()) {
                int currentStock = c.getInt(0);
                int newStock = (type.equalsIgnoreCase("Nhập")) ? currentStock + qty : currentStock - qty;
                db.execSQL("UPDATE PRODUCT SET STOCK=? WHERE ID_PRODUCT=?", new Object[]{newStock, idProduct});
                Log.d("STOCK_UPDATE", "ID_PRODUCT " + idProduct + " → " + newStock);
            }
            c.close();
        } catch (Exception e) {
            Log.e("STOCK_ERROR", e.getMessage());
        }
    }
}
