package com.example.homie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class InvoiceDetailAdapter extends RecyclerView.Adapter<InvoiceDetailAdapter.ViewHolder> {

    ArrayList<InvoiceDetailItem> detailList;

    public InvoiceDetailAdapter(ArrayList<InvoiceDetailItem> detailList) {
        this.detailList = detailList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceDetailItem item = detailList.get(position);
        holder.tvProductId.setText("Mã SP: " + item.idProduct);
        holder.tvQuantity.setText("Số lượng: " + item.quantity);
        holder.tvPrice.setText("Giá: " + String.format("%,.0f₫", item.price));
        holder.tvSubtotal.setText("Thành tiền: " + String.format("%,.0f₫", item.subtotal));
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductId, tvQuantity, tvPrice, tvSubtotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
