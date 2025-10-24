package com.example.homie;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    RecyclerView recyclerInvoices;
    FloatingActionButton fabAddInvoice;
    InvoiceAdapter invoiceAdapter;
    ArrayList<Invoice> invoiceList;
    DatabaseHelper dbHelper;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        recyclerInvoices = findViewById(R.id.recyclerInvoices);
        fabAddInvoice = findViewById(R.id.fabAddInvoice);
        dbHelper = new DatabaseHelper(this);

        currentUserId = getIntent().getStringExtra("id_user");
        Log.d("DEBUG_USER", "InvoiceActivity - currentUserId = " + currentUserId);

        loadInvoices();

        fabAddInvoice.setOnClickListener(v -> showAddInvoiceDialog());

        recyclerInvoices.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                (view, position) -> showOptions(invoiceList.get(position))
        ));

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    // 🔹 Đọc danh sách hóa đơn
    private void loadInvoices() {
        invoiceList = new ArrayList<>();
        try {
            SQLiteDatabase db = dbHelper.openDatabase();
            Cursor c = db.rawQuery("SELECT * FROM INVOICE WHERE ID_USER=? ORDER BY DATE DESC", new String[]{currentUserId});
            Log.d("DEBUG_LOAD", "UserID = " + currentUserId + " | Count = " + c.getCount());

            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndexOrThrow("INVOICE_NAME"));
                Log.d("DEBUG_ROW", "Invoice loaded: " + name);

                invoiceList.add(new Invoice(
                        c.getString(c.getColumnIndexOrThrow("ID_INVOICE")),
                        name,
                        c.getString(c.getColumnIndexOrThrow("TYPE")),
                        c.getString(c.getColumnIndexOrThrow("DATE")),
                        c.getDouble(c.getColumnIndexOrThrow("VAT_PERCENT")),
                        c.getDouble(c.getColumnIndexOrThrow("TOTAL"))
                ));
            }
            c.close();
        } catch (Exception e) {
            Log.e("DEBUG_LOAD", "Error loading invoices: " + e.getMessage());
            Toast.makeText(this, "Lỗi đọc dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        invoiceAdapter = new InvoiceAdapter(invoiceList);
        recyclerInvoices.setLayoutManager(new LinearLayoutManager(this));
        recyclerInvoices.setAdapter(invoiceAdapter);
    }

    // 🔹 Hiển thị dialog thêm hóa đơn
    private void showAddInvoiceDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_invoice, null);
        EditText edtName = view.findViewById(R.id.edtInvoiceName);
        EditText edtVat = view.findViewById(R.id.edtVat);
        EditText edtTotal = view.findViewById(R.id.edtTotalAmount);
        RadioGroup rgType = view.findViewById(R.id.rgInvoiceType);

        new AlertDialog.Builder(this)
                .setTitle("Thêm hóa đơn mới")
                .setView(view)
                .setPositiveButton("Lưu", (d, w) -> {
                    String name = edtName.getText().toString().trim();
                    String vatStr = edtVat.getText().toString().trim();
                    String totalStr = edtTotal.getText().toString().trim();
                    int selectedType = rgType.getCheckedRadioButtonId();
                    RadioButton rbType = view.findViewById(selectedType);

                    if (name.isEmpty() || vatStr.isEmpty() || totalStr.isEmpty() || rbType == null) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String type = rbType.getText().toString();
                    double vatPercent = Double.parseDouble(vatStr);
                    double total = Double.parseDouble(totalStr);
                    double vat = total * vatPercent / 100;
                    double subtotal = total - vat;

                    String id = "INV_" + System.currentTimeMillis();
                    String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

                    SQLiteDatabase db = dbHelper.openDatabase();
                    ContentValues values = new ContentValues();
                    values.put("ID_INVOICE", id);
                    values.put("ID_USER", currentUserId);
                    values.put("INVOICE_NAME", name);
                    values.put("DATE", date);
                    values.put("TYPE", type);
                    values.put("SUBTOTAL", subtotal);
                    values.put("VAT_PERCENT", vatPercent);
                    values.put("VAT", vat);
                    values.put("TOTAL", total);

                    long result = db.insert("INVOICE", null, values);
                    Log.d("DEBUG_INSERT", "Insert result = " + result + ", UserID = " + currentUserId);

                    if (result == -1) {
                        Toast.makeText(this, "❌ Thêm hóa đơn thất bại!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "✅ Đã thêm hóa đơn!", Toast.LENGTH_SHORT).show();
                        loadInvoices();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // 🔹 Menu chỉnh sửa / xóa
    private void showOptions(Invoice invoice) {
        String[] options = {"✏️ Chỉnh sửa", "🗑️ Xóa"};
        new AlertDialog.Builder(this)
                .setTitle(invoice.invoiceName)
                .setItems(options, (d, i) -> {
                    if (i == 0) editInvoice(invoice);
                    else deleteInvoice(invoice);
                })
                .show();
    }

    // 🔹 Chỉnh sửa hóa đơn
    private void editInvoice(Invoice invoice) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_invoice, null);
        EditText edtName = view.findViewById(R.id.edtInvoiceName);
        EditText edtVat = view.findViewById(R.id.edtVat);
        EditText edtTotal = view.findViewById(R.id.edtTotalAmount);
        RadioGroup rgType = view.findViewById(R.id.rgInvoiceType);

        edtName.setText(invoice.invoiceName);
        edtVat.setText(String.valueOf(invoice.vatPercent));
        edtTotal.setText(String.valueOf(invoice.total));
        if (invoice.type.equalsIgnoreCase("Nhập")) rgType.check(R.id.rbNhap);
        else rgType.check(R.id.rbXuat);

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa hóa đơn")
                .setView(view)
                .setPositiveButton("Lưu", (d, w) -> {
                    String name = edtName.getText().toString();
                    String vatStr = edtVat.getText().toString();
                    String totalStr = edtTotal.getText().toString();
                    int selected = rgType.getCheckedRadioButtonId();
                    RadioButton rbType = view.findViewById(selected);

                    if (name.isEmpty() || vatStr.isEmpty() || totalStr.isEmpty() || rbType == null) {
                        Toast.makeText(this, "Vui lòng nhập đủ dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String type = rbType.getText().toString();
                    double vatPercent = Double.parseDouble(vatStr);
                    double total = Double.parseDouble(totalStr);
                    double vat = total * vatPercent / 100;
                    double subtotal = total - vat;

                    SQLiteDatabase db = dbHelper.openDatabase();
                    ContentValues values = new ContentValues();
                    values.put("INVOICE_NAME", name);
                    values.put("TYPE", type);
                    values.put("VAT_PERCENT", vatPercent);
                    values.put("VAT", vat);
                    values.put("SUBTOTAL", subtotal);
                    values.put("TOTAL", total);

                    db.update("INVOICE", values, "ID_INVOICE=?", new String[]{invoice.idInvoice});
                    Toast.makeText(this, "Đã cập nhật hóa đơn!", Toast.LENGTH_SHORT).show();
                    loadInvoices();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // 🔹 Xóa hóa đơn
    private void deleteInvoice(Invoice invoice) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa hóa đơn")
                .setMessage("Bạn có chắc muốn xóa \"" + invoice.invoiceName + "\"?")
                .setPositiveButton("Xóa", (d, w) -> {
                    SQLiteDatabase db = dbHelper.openDatabase();
                    db.delete("INVOICE", "ID_INVOICE=?", new String[]{invoice.idInvoice});
                    Toast.makeText(this, "Đã xóa hóa đơn", Toast.LENGTH_SHORT).show();
                    loadInvoices();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
