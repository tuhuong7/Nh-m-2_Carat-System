package com.example.caratexpense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.R;
import com.example.caratexpense.models.Category;
import com.example.caratexpense.utils.IconUtils;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private OnCategoryClickListener listener;
    private Category selectedCategory = null;
    
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
    
    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }
    
    public void setSelectedCategory(Category category) {
        this.selectedCategory = category;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }
    
    @Override
    public int getItemCount() {
        return categories.size();
    }
    
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView ivCategoryIcon;
        private TextView tvCategoryName;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(categories.get(position));
                }
            });
        }
        
        public void bind(Category category) {
            // Set category icon
            ivCategoryIcon.setImageResource(IconUtils.getIconResourceId(
                    itemView.getContext(), 
                    category.getIconName()
            ));
            
            // Set category name
            tvCategoryName.setText(category.getName());
            
            // Highlight selected category
            if (selectedCategory != null && selectedCategory.getId() == category.getId()) {
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.green));
                ivCategoryIcon.setColorFilter(itemView.getContext().getResources().getColor(R.color.white));
                tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.white));
            } else {
                cardView.setCardBackgroundColor(itemView.getContext().getResources().getColor(R.color.light_gray));
                ivCategoryIcon.clearColorFilter();
                tvCategoryName.setTextColor(itemView.getContext().getResources().getColor(R.color.black));
            }
        }
    }
}
