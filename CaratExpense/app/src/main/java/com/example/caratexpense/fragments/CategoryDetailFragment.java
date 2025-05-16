package com.example.caratexpense.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.adapters.TransactionAdapter;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.TransactionDao;
import com.example.caratexpense.models.Category;
import com.example.caratexpense.models.CategoryReport;
import com.example.caratexpense.models.Transaction;
import com.example.caratexpense.utils.DateTimeUtils;
import com.example.caratexpense.utils.IconUtils;
import com.example.caratexpense.models.TransactionWithCategory;




import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CategoryDetailFragment extends BaseFragment {
    private TextView tvCategoryName, tvAmount, tvPeriod, tvNoTransactions;
    private ImageView ivBack, ivCategoryIcon;
    private RecyclerView rvTransactions;
    private View loadingView;

    private TransactionAdapter adapter;
    private List<TransactionWithCategory> transactions = new ArrayList<>();


    private CategoryReport categoryReport;
    private int viewType;
    private long periodStart;
    private boolean isExpense;

    private TransactionDao transactionDao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private static final int VIEW_TYPE_DAILY = 0;
    private static final int VIEW_TYPE_MONTHLY = 1;
    private static final int VIEW_TYPE_YEARLY = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_detail, container, false);

        // Get data from arguments
        Bundle args = getArguments();
        if (args != null) {
            categoryReport = (CategoryReport) args.getSerializable("categoryReport");
            viewType = args.getInt("viewType", VIEW_TYPE_MONTHLY);
            periodStart = args.getLong("periodStart", System.currentTimeMillis());
            isExpense = args.getBoolean("isExpense", true);
        }

        // Initialize database
        transactionDao = AppDatabase.getInstance(requireContext()).transactionDao();

        // Initialize views
        initializeViews(view);

        // Setup UI
        setupRecyclerView();
        setupListeners();

        // Load data
        if (categoryReport != null) {
            updateCategoryInfo();
            loadTransactions();
        } else {
            showError("Không thể tải thông tin danh mục");
            ((MainActivity) requireActivity()).loadFragment(new ReportsFragment());
        }

        return view;
    }

    private void initializeViews(View view) {
        tvCategoryName = view.findViewById(R.id.tv_category_name);
        tvAmount = view.findViewById(R.id.tv_amount);
        tvPeriod = view.findViewById(R.id.tv_period);
        tvNoTransactions = view.findViewById(R.id.tv_no_transactions);
        ivBack = view.findViewById(R.id.iv_back);
        ivCategoryIcon = view.findViewById(R.id.iv_category_icon);
        rvTransactions = view.findViewById(R.id.rv_transactions);
        loadingView = view.findViewById(R.id.loading_view);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(transactions, transaction -> {
            // Handle transaction click if needed
        });

        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new ReportsFragment());
        });
    }

    private void updateCategoryInfo() {
        Category category = categoryReport.getCategory();

        // Set category name
        tvCategoryName.setText(category.getName());

        // Set category icon
        int iconResId = IconUtils.getIconResourceId(requireContext(), category.getIconName());
        ivCategoryIcon.setImageResource(iconResId);

        // Set amount
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvAmount.setText(formatter.format(categoryReport.getAmount()) + " đ");

        // Set period text
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(periodStart);

        SimpleDateFormat sdf;
        switch (viewType) {
            case VIEW_TYPE_DAILY:
                sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvPeriod.setText(sdf.format(calendar.getTime()));
                break;

            case VIEW_TYPE_MONTHLY:
                sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                tvPeriod.setText(sdf.format(calendar.getTime()));
                break;

            case VIEW_TYPE_YEARLY:
                sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
                tvPeriod.setText(sdf.format(calendar.getTime()));
                break;
        }
    }

    private void loadTransactions() {
        showLoading(true);

        executor.execute(() -> {
            try {
                // Calculate date range
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();

                startDate.setTimeInMillis(periodStart);
                endDate.setTimeInMillis(periodStart);

                configureTimeRange(startDate, endDate);

                // Format dates for query
                String startDateStr = DateTimeUtils.formatDateTime(startDate.getTime());
                String endDateStr = DateTimeUtils.formatDateTime(endDate.getTime());

                // Get transactions for this category in the date range
                List<TransactionWithCategory> categoryTransactions = transactionDao.getTransactionsWithCategoryByPeriodAndCategory(
                        startDateStr,
                        endDateStr,
                        categoryReport.getCategory().getId()
                );

                // Filter by income/expense if needed
                List<TransactionWithCategory> filteredTransactions = new ArrayList<>();
                for (TransactionWithCategory item : categoryTransactions) {
                    if (item.getTransaction().isIncome() == !isExpense) {
                        filteredTransactions.add(item);
                    }
                }

                // Update UI on main thread
                handler.post(() -> {
                    transactions.clear();
                    transactions.addAll(filteredTransactions);
                    adapter.notifyDataSetChanged();

                    // Show message if no transactions
                    if (transactions.isEmpty()) {
                        tvNoTransactions.setVisibility(View.VISIBLE);
                        rvTransactions.setVisibility(View.GONE);
                    } else {
                        tvNoTransactions.setVisibility(View.GONE);
                        rvTransactions.setVisibility(View.VISIBLE);
                    }

                    showLoading(false);
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    showLoading(false);
                    showError("Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
                });
            }
        });
    }

    private void configureTimeRange(Calendar startDate, Calendar endDate) {
        switch (viewType) {
            case VIEW_TYPE_DAILY:
                // Set time range for daily view (full day)
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);

                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;

            case VIEW_TYPE_MONTHLY:
                // Set time range for monthly view (full month)
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);

                endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;

            case VIEW_TYPE_YEARLY:
                // Set time range for yearly view (full year)
                startDate.set(Calendar.MONTH, Calendar.JANUARY);
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);

                endDate.set(Calendar.MONTH, Calendar.DECEMBER);
                endDate.set(Calendar.DAY_OF_MONTH, 31);
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;
        }
    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean isLoading) {
        if (loadingView != null) {
            loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }
}
