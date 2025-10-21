package com.example.homie;

import android.app.AlertDialog;
import android.os.Bundle;
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
import java.util.ArrayList;

public class InvoiceActivity extends AppCompatActivity {

    RecyclerView recyclerInvoices;
    FloatingActionButton fabAddInvoice;
    InvoiceAdapter invoiceAdapter;
    ArrayList<String> invoiceList; // tạm thời là String, sau Khanh nối DB thật

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        recyclerInvoices = findViewById(R.id.recyclerInvoices);
        fabAddInvoice = findViewById(R.id.fabAddInvoice);

        // Dữ liệu mẫu ban đầu
        invoiceList = new ArrayList<>();
        invoiceList.add("Hóa đơn Nhập #1 - VAT 10% - Tổng 1.200.000₫");
        invoiceList.add("Hóa đơn Xuất #2 - VAT 8% - Tổng 900.000₫");

        invoiceAdapter = new InvoiceAdapter(invoiceList);
        recyclerInvoices.setLayoutManager(new LinearLayoutManager(this));
        recyclerInvoices.setAdapter(invoiceAdapter);

        // 🟢 Nút thêm hóa đơn
        fabAddInvoice.setOnClickListener(v -> showAddInvoiceDialog());

        // ⚙️ Back gesture (chuẩn AndroidX)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    // 📦 Hàm hiển thị dialog thêm hóa đơn
    private void showAddInvoiceDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_add_invoice, null);

        RadioGroup rgType = view.findViewById(R.id.rgInvoiceType);
        EditText edtVat = view.findViewById(R.id.edtVat);
        EditText edtTotal = view.findViewById(R.id.edtTotalAmount);

        new AlertDialog.Builder(this)
                .setTitle("Tạo hóa đơn mới")
                .setView(view)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    int selectedTypeId = rgType.getCheckedRadioButtonId();
                    RadioButton rbType = view.findViewById(selectedTypeId);

                    if (rbType == null) {
                        Toast.makeText(this, "Vui lòng chọn loại hóa đơn", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String type = rbType.getText().toString();
                    String vat = edtVat.getText().toString().trim();
                    String total = edtTotal.getText().toString().trim();

                    if (vat.isEmpty() || total.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập VAT và tổng tiền", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String newInvoice = "Hóa đơn " + type + " - VAT " + vat + "% - Tổng " + total + "₫";
                    invoiceList.add(newInvoice);
                    invoiceAdapter.notifyDataSetChanged();

                    Toast.makeText(this, "Đã thêm " + type, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
