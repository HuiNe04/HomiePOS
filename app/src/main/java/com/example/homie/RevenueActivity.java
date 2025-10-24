package com.example.homie;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import java.util.ArrayList;

public class RevenueActivity extends AppCompatActivity {

    TextView tvTotalImport, tvTotalExport, tvProfit;
    BarChart barChartRevenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);

        tvTotalImport = findViewById(R.id.tvTotalImport);
        tvTotalExport = findViewById(R.id.tvTotalExport);
        tvProfit = findViewById(R.id.tvProfit);
        barChartRevenue = findViewById(R.id.barChartRevenue);

        // ⚙️ Back gesture (chuẩn AndroidX)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // 🔹 Dữ liệu giả lập (sau này Khanh sẽ thay bằng dữ liệu thật từ SQLite)
        double totalImport = 2000000;  // tổng nhập
        double totalExport = 3500000;  // tổng xuất
        double profit = totalExport - totalImport;

        // 🔸 Hiển thị text
        tvTotalImport.setText("Tổng Nhập: " + formatCurrency(totalImport));
        tvTotalExport.setText("Tổng Xuất: " + formatCurrency(totalExport));
        tvProfit.setText("Lợi nhuận: " + formatCurrency(profit));

        // 🔸 Hiển thị biểu đồ cột
        setupBarChart(totalImport, totalExport);
    }

    private String formatCurrency(double value) {
        return String.format("%,.0f₫", value);
    }

    private void setupBarChart(double importValue, double exportValue) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) importValue));
        entries.add(new BarEntry(1, (float) exportValue));

        BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
        dataSet.setColors(new int[]{Color.parseColor("#6D4C41"), Color.parseColor("#A1887F")});
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(14f);

        BarData data = new BarData(dataSet);
        barChartRevenue.setData(data);

        // Ẩn đường lưới, căn chỉnh
        XAxis xAxis = barChartRevenue.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.LTGRAY);
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getAxisLabel(float value, com.github.mikephil.charting.components.AxisBase axis) {
                return value == 0 ? "Nhập" : "Xuất";
            }
        });


        barChartRevenue.getAxisLeft().setTextColor(Color.LTGRAY);
        barChartRevenue.getAxisRight().setEnabled(false);
        barChartRevenue.getLegend().setEnabled(false);
        barChartRevenue.getDescription().setEnabled(false);

        barChartRevenue.animateY(1000);
        barChartRevenue.invalidate();
    }
}
