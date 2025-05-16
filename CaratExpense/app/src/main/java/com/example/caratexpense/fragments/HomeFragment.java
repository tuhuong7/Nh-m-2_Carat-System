package com.example.caratexpense.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.adapters.StockAdapter;
import com.example.caratexpense.api.StockApiClient;
import com.example.caratexpense.api.StockApiService;
import com.example.caratexpense.models.Stock;
import com.example.caratexpense.utils.MockStockGenerator;
import com.example.caratexpense.utils.SampleDataGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment {
    private TextView tvUserName;
    private ImageView ivMenu;
    private RecyclerView rvStocks;
    private ProgressBar progressBar;
    private TextView tvError;
    private StockAdapter stockAdapter;
    private List<Stock> stockList = new ArrayList<>();

    // Đặt thành true để sử dụng API thực, false để sử dụng dữ liệu mẫu
    private static final boolean USE_REAL_API = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Generate sample data
        SampleDataGenerator.generateSampleData(requireContext());

        // Initialize views
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        rvStocks = view.findViewById(R.id.rv_stocks);
        progressBar = view.findViewById(R.id.progress_bar);
        tvError = view.findViewById(R.id.tv_error);

        // Set user name from preferences
        tvUserName.setText(preferenceManager.getUserName());

        // Setup menu button
        ivMenu.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openDrawer();
        });

        // Setup feature buttons
        setupFeatureButtons(view);

        // Setup stocks recycler view
        setupStocksRecyclerView();

        // Load stock data
        loadStockData();

        return view;
    }

    private void setupFeatureButtons(View view) {
        // Income/Expense button
        view.findViewById(R.id.cv_income_expense).setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new IncomeExpenseFragment());
        });

        // Goals button
        view.findViewById(R.id.cv_goals).setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new GoalsFragment());
        });

        // Reports button
        view.findViewById(R.id.cv_reports).setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new ReportsFragment());
        });

        // Payments button
        view.findViewById(R.id.cv_payments).setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new PaymentsFragment());
        });
    }

    private void setupStocksRecyclerView() {
        // Setup adapter with the child fragment manager
        stockAdapter = new StockAdapter(requireContext(), getChildFragmentManager());  // Use getChildFragmentManager() for fragments

        // Setup recycler view
        rvStocks.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvStocks.setAdapter(stockAdapter);
    }

    private void loadStockData() {
        if (USE_REAL_API) {
            // Hiển thị chỉ báo đang tải
            progressBar.setVisibility(View.VISIBLE);
            tvError.setVisibility(View.GONE);

            // Lấy API key từ tài nguyên (nếu đã lưu trong strings.xml)
            String apiKey = getString(R.string.news_api_key);

            // Tạo API service
            StockApiService apiService = StockApiClient.getClient().create(StockApiService.class);

            // Gọi API với API key
            Call<List<Stock>> call = apiService.getStockList(apiKey);
            call.enqueue(new Callback<List<Stock>>() {
                @Override
                public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<Stock> stocks = response.body();
                        // Giới hạn số lượng cổ phiếu hiển thị
                        if (stocks.size() > 20) {
                            stocks = stocks.subList(0, 20);
                        }
                        stockAdapter.setStockList(stocks);
                    } else {
                        showError();
                        loadMockData();  // Sử dụng dữ liệu mock khi có lỗi
                    }
                }

                @Override
                public void onFailure(Call<List<Stock>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    showError();
                    loadMockData();  // Sử dụng dữ liệu mock khi có lỗi
                }
            });
        } else {
            // Nếu USE_REAL_API là false, sử dụng dữ liệu mock
            loadMockData();
        }
    }

    private void loadMockData() {
        // Sử dụng dữ liệu mock khi không thể tải từ API
        stockList = MockStockGenerator.generateMockStocks();
        stockAdapter.setStockList(stockList);
    }

    private void showError() {
        // Hiển thị lỗi khi không thể tải dữ liệu
        tvError.setVisibility(View.VISIBLE);
        tvError.setText(R.string.error_loading_stocks);
        Toast.makeText(requireContext(), R.string.error_loading_stocks, Toast.LENGTH_SHORT).show();
    }
}
