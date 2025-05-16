package com.example.caratexpense.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.caratexpense.R;

public class StockDetailFragment extends Fragment {

    private TextView tvSymbol, tvName, tvPrice, tvExchange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_detail, container, false);

        // Khởi tạo các TextView
        tvSymbol = view.findViewById(R.id.tv_symbol);
        tvName = view.findViewById(R.id.tv_name);
        tvPrice = view.findViewById(R.id.tv_price);
        tvExchange = view.findViewById(R.id.tv_exchange);

        // Lấy dữ liệu từ arguments (được truyền từ StockAdapter)
        if (getArguments() != null) {
            String symbol = getArguments().getString("symbol");
            String name = getArguments().getString("name");
            String price = getArguments().getString("price");
            String exchange = getArguments().getString("exchange");

            // Set data vào các TextView
            tvSymbol.setText(symbol);
            tvName.setText(name);
            tvPrice.setText(price);
            tvExchange.setText(exchange);
        }

        return view;
    }
}
