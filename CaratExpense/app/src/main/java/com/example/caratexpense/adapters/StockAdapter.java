package com.example.caratexpense.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.R;
import com.example.caratexpense.fragments.StockDetailFragment;  // Import StockDetailFragment
import com.example.caratexpense.models.Stock;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    private List<Stock> stockList;
    private Context context;
    private FragmentManager fragmentManager;  // FragmentManager truyền từ Fragment

    public StockAdapter(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.stockList = new ArrayList<>();
        this.fragmentManager = fragmentManager;  // Lưu FragmentManager từ Fragment
    }

    public void setStockList(List<Stock> stockList) {
        this.stockList = stockList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Stock stock = stockList.get(position);

        holder.tvSymbol.setText(stock.getSymbol());
        holder.tvName.setText(stock.getName());
        holder.tvPrice.setText(context.getString(R.string.stock_price, stock.getPrice()));
        holder.tvExchange.setText(context.getString(R.string.stock_exchange, stock.getExchangeShortName()));

        // Khi người dùng click vào item cổ phiếu
        holder.itemView.setOnClickListener(v -> {
            // Tạo một instance của StockDetailFragment và truyền dữ liệu vào
            StockDetailFragment stockDetailFragment = new StockDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("symbol", stock.getSymbol());
            bundle.putString("name", stock.getName());
            bundle.putString("price", stock.getPrice());
            bundle.putString("exchange", stock.getExchangeShortName());
            stockDetailFragment.setArguments(bundle);

            // Thực hiện chuyển fragment vào fragment container
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, stockDetailFragment)  // Replace vào container fragment
                    .addToBackStack(null)  // Thêm vào back stack để người dùng có thể quay lại
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    static class StockViewHolder extends RecyclerView.ViewHolder {
        TextView tvSymbol, tvName, tvPrice, tvExchange;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSymbol = itemView.findViewById(R.id.tv_stock_symbol);
            tvName = itemView.findViewById(R.id.tv_stock_name);
            tvPrice = itemView.findViewById(R.id.tv_stock_price);
            tvExchange = itemView.findViewById(R.id.tv_stock_exchange);
        }
    }
}
