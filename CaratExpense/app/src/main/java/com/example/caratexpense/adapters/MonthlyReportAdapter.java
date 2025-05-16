package com.example.caratexpense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.R;
import com.example.caratexpense.models.MonthlyReport;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MonthlyReportAdapter extends RecyclerView.Adapter<MonthlyReportAdapter.MonthlyReportViewHolder> {
    private List<MonthlyReport> monthlyReports;
    private OnMonthlyReportClickListener listener;
    
    public interface OnMonthlyReportClickListener {
        void onMonthlyReportClick(MonthlyReport monthlyReport);
    }
    
    public MonthlyReportAdapter(List<MonthlyReport> monthlyReports, OnMonthlyReportClickListener listener) {
        this.monthlyReports = monthlyReports;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public MonthlyReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monthly_report, parent, false);
        return new MonthlyReportViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MonthlyReportViewHolder holder, int position) {
        MonthlyReport monthlyReport = monthlyReports.get(position);
        holder.bind(monthlyReport);
    }
    
    @Override
    public int getItemCount() {
        return monthlyReports.size();
    }
    
    class MonthlyReportViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMonthName, tvAmount;
        private ImageView ivArrow;
        
        public MonthlyReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonthName = itemView.findViewById(R.id.tv_month_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            ivArrow = itemView.findViewById(R.id.iv_arrow);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onMonthlyReportClick(monthlyReports.get(position));
                }
            });
        }
        
        public void bind(MonthlyReport monthlyReport) {
            // Set month name
            tvMonthName.setText(monthlyReport.getMonthName());
            
            // Set amount
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvAmount.setText(formatter.format(monthlyReport.getAmount()) + "đ");
        }
    }
}
