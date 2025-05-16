package com.example.caratexpense.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.R;
import com.example.caratexpense.models.CategoryReport;
import com.example.caratexpense.utils.IconUtils;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CategoryReportAdapter extends RecyclerView.Adapter<CategoryReportAdapter.ViewHolder> {

    private final List<CategoryReport> categoryReports;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryReport categoryReport);
    }

    public CategoryReportAdapter(List<CategoryReport> categoryReports, OnCategoryClickListener listener) {
        this.categoryReports = categoryReports;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryReport report = categoryReports.get(position);
        holder.bind(report, listener);
    }

    @Override
    public int getItemCount() {
        return categoryReports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategoryIcon;
        private final TextView tvCategoryName;
        private final TextView tvAmount;
        private final TextView tvPercentage;
        private final View colorIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
            colorIndicator = itemView.findViewById(R.id.color_indicator);
        }

        public void bind(CategoryReport report, OnCategoryClickListener listener) {
            // Set category name
            tvCategoryName.setText(report.getCategory().getName());

            // Set amount
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvAmount.setText(formatter.format(report.getAmount()) + "đ");

            // Set percentage
            tvPercentage.setText(String.format("%.1f%%", report.getPercentage()));

            // Set icon
            String iconName = report.getCategory().getIcon();
            if (iconName != null && !iconName.isEmpty()) {
                int iconResId = IconUtils.getIconResourceId(itemView.getContext(), iconName); // Use context here
                if (iconResId != 0) {
                    ivCategoryIcon.setImageResource(iconResId);
                }
            }

            // Set color indicator with null check for color
            String categoryColor = report.getCategory().getColor();
            if (categoryColor != null && !categoryColor.isEmpty()) {
                try {
                    colorIndicator.setBackgroundColor(Color.parseColor(categoryColor));
                } catch (IllegalArgumentException e) {
                    // In case of an invalid color, use a default color
                    colorIndicator.setBackgroundColor(Color.parseColor("#000000")); // Default to black
                }
            } else {
                // If no color specified, set a default color
                colorIndicator.setBackgroundColor(Color.parseColor("#000000")); // Default to black
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(report);
                }
            });
        }
    }
}
