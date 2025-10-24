package com.example.homie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {

    private ArrayList<Invoice> invoiceList;

    public InvoiceAdapter(ArrayList<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);

        holder.tvInvoiceName.setText(invoice.invoiceName);
        holder.tvInvoiceInfo.setText("Loại: " + invoice.type + " | Ngày: " + invoice.date);
        holder.tvInvoiceTotal.setText("Tổng: " + String.format("%,.0f₫", invoice.total)
                + " | VAT: " + invoice.vatPercent + "%");
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    // 🧱 ViewHolder ánh xạ các TextView trong item_invoice.xml
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceName, tvInvoiceInfo, tvInvoiceTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceName = itemView.findViewById(R.id.tvInvoiceName);
            tvInvoiceInfo = itemView.findViewById(R.id.tvInvoiceInfo);
            tvInvoiceTotal = itemView.findViewById(R.id.tvInvoiceTotal);
        }
    }

    // 🧩 Cập nhật danh sách sau khi thêm/sửa/xóa
    public void updateList(ArrayList<Invoice> newList) {
        this.invoiceList = newList;
        notifyDataSetChanged();
    }

    // 🗑️ Xóa một hóa đơn
    public void removeAt(int position) {
        invoiceList.remove(position);
        notifyItemRemoved(position);
    }
}
