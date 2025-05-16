package com.example.caratexpense.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.adapters.CategoryReportAdapter;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.CategoryDao;
import com.example.caratexpense.database.dao.TransactionDao;
import com.example.caratexpense.models.Category;
import com.example.caratexpense.models.CategoryReport;
import com.example.caratexpense.models.Transaction;
import com.example.caratexpense.models.TransactionWithCategory;
import com.example.caratexpense.utils.CategoryColorManager;
import com.example.caratexpense.utils.DateTimeUtils;
import com.example.caratexpense.views.PieChartView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ReportsFragment extends BaseFragment {
    // UI Components
    private TextView tvUserName, tvPeriod, tvExpense, tvIncome, tvBalance, tvNoCategoryData;
    private ImageView ivMenu, ivBack, ivPrevPeriod, ivNextPeriod;
    private Button btnDaily, btnMonthly, btnYearly, btnExpense, btnIncome;
    private RecyclerView rvCategories;
    private PieChartView pieChartView;
    private View loadingView;
    private CardView cardExpense, cardIncome, cardBalance;

    // Data
    private CategoryReportAdapter adapter;
    private List<CategoryReport> categoryReports = new ArrayList<>();

    // Database
    private TransactionDao transactionDao;
    private CategoryDao categoryDao;
    private CategoryColorManager colorManager;

    // Background processing
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    // Constants for view types
    private static final int VIEW_TYPE_DAILY = 0;
    private static final int VIEW_TYPE_MONTHLY = 1;
    private static final int VIEW_TYPE_YEARLY = 2;

    // Current state
    private int currentViewType = VIEW_TYPE_DAILY; // Default to daily view
    private boolean isExpenseView = true; // Default to expense view
    private Calendar currentPeriod = Calendar.getInstance(); // Default to current date

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        // Initialize database access
        AppDatabase db = AppDatabase.getInstance(requireContext());
        transactionDao = db.transactionDao();
        categoryDao = db.categoryDao();
        colorManager = CategoryColorManager.getInstance(requireContext());

        // Initialize UI components
        initializeViews(view);

        // Setup UI
        setupRecyclerView();
        setupListeners();
        setupButtonEffects();

        // Update UI state
        updateViewTypeUI();
        updatePeriodDisplay();

        // Load initial data
        loadData();

        return view;
    }

    private void initializeViews(View view) {
        // Header
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivBack = view.findViewById(R.id.iv_back);

        // Filter buttons
        btnDaily = view.findViewById(R.id.btn_daily);
        btnMonthly = view.findViewById(R.id.btn_monthly);
        btnYearly = view.findViewById(R.id.btn_yearly);

        // Period navigation
        tvPeriod = view.findViewById(R.id.tv_period);
        ivPrevPeriod = view.findViewById(R.id.iv_prev_period);
        ivNextPeriod = view.findViewById(R.id.iv_next_period);

        // Summary cards
        tvExpense = view.findViewById(R.id.tv_expense);
        tvIncome = view.findViewById(R.id.tv_income);
        tvBalance = view.findViewById(R.id.tv_balance);
        cardExpense = view.findViewById(R.id.card_expense);
        cardIncome = view.findViewById(R.id.card_income);
        cardBalance = view.findViewById(R.id.card_balance);

        // Tab buttons
        btnExpense = view.findViewById(R.id.btn_expense_tab);
        btnIncome = view.findViewById(R.id.btn_income_tab);

        // Chart and list
        pieChartView = view.findViewById(R.id.pie_chart);
        rvCategories = view.findViewById(R.id.rv_categories);
        tvNoCategoryData = view.findViewById(R.id.tv_no_category_data);

        // Loading view
        loadingView = view.findViewById(R.id.loading_view);

        // Set user name
        tvUserName.setText(preferenceManager.getUserName());
    }

    private void setupRecyclerView() {
        adapter = new CategoryReportAdapter(categoryReports, categoryReport -> {
            // Navigate to category detail when a category is clicked
            navigateToCategoryDetail(categoryReport);
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCategories.setAdapter(adapter);
    }

    private void navigateToCategoryDetail(CategoryReport categoryReport) {
        // Create bundle with necessary data
        Bundle bundle = new Bundle();
        bundle.putLong("categoryId", categoryReport.getCategory().getId());
        bundle.putInt("viewType", currentViewType);
        bundle.putLong("periodStart", getStartDate().getTimeInMillis());
        bundle.putLong("periodEnd", getEndDate().getTimeInMillis());
        bundle.putBoolean("isExpense", isExpenseView);

        // Navigate to category detail fragment
        CategoryDetailFragment fragment = new CategoryDetailFragment();
        fragment.setArguments(bundle);

        ((MainActivity) requireActivity()).loadFragment(fragment);
    }

    private void setupListeners() {
        // Back button
        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
        });

        // Menu button
        ivMenu.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openDrawer();
        });

        // Filter buttons
        btnDaily.setOnClickListener(v -> {
            if (currentViewType != VIEW_TYPE_DAILY) {
                currentViewType = VIEW_TYPE_DAILY;
                updateViewTypeUI();
                updatePeriodDisplay();
                loadData();
            }
        });

        btnMonthly.setOnClickListener(v -> {
            if (currentViewType != VIEW_TYPE_MONTHLY) {
                currentViewType = VIEW_TYPE_MONTHLY;
                updateViewTypeUI();
                updatePeriodDisplay();
                loadData();
            }
        });

        btnYearly.setOnClickListener(v -> {
            if (currentViewType != VIEW_TYPE_YEARLY) {
                currentViewType = VIEW_TYPE_YEARLY;
                updateViewTypeUI();
                updatePeriodDisplay();
                loadData();
            }
        });

        // Period navigation
        ivPrevPeriod.setOnClickListener(v -> {
            navigatePeriod(-1);
        });

        ivNextPeriod.setOnClickListener(v -> {
            navigatePeriod(1);
        });

        // Period selection
        tvPeriod.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        // Tab buttons
        btnExpense.setOnClickListener(v -> {
            if (!isExpenseView) {
                isExpenseView = true;
                updateTabUI();
                loadData();
            }
        });

        btnIncome.setOnClickListener(v -> {
            if (isExpenseView) {
                isExpenseView = false;
                updateTabUI();
                loadData();
            }
        });

        // Card clicks
        cardExpense.setOnClickListener(v -> {
            isExpenseView = true;
            updateTabUI();
            loadData();
        });

        cardIncome.setOnClickListener(v -> {
            isExpenseView = false;
            updateTabUI();
            loadData();
        });
    }

    private void setupButtonEffects() {
        // Add ripple effects or other visual feedback for buttons
    }

    private void updateViewTypeUI() {
        // Reset all buttons to default state
        btnDaily.setBackgroundResource(R.drawable.button_background_white_rounded);
        btnDaily.setTextColor(getResources().getColor(R.color.text_primary));

        btnMonthly.setBackgroundResource(R.drawable.button_background_white_rounded);
        btnMonthly.setTextColor(getResources().getColor(R.color.text_primary));

        btnYearly.setBackgroundResource(R.drawable.button_background_white_rounded);
        btnYearly.setTextColor(getResources().getColor(R.color.text_primary));

        // Highlight the selected button
        switch (currentViewType) {
            case VIEW_TYPE_DAILY:
                btnDaily.setBackgroundResource(R.drawable.button_background_green_rounded);
                btnDaily.setTextColor(Color.WHITE);
                break;
            case VIEW_TYPE_MONTHLY:
                btnMonthly.setBackgroundResource(R.drawable.button_background_green_rounded);
                btnMonthly.setTextColor(Color.WHITE);
                break;
            case VIEW_TYPE_YEARLY:
                btnYearly.setBackgroundResource(R.drawable.button_background_green_rounded);
                btnYearly.setTextColor(Color.WHITE);
                break;
        }
    }

    private void updateTabUI() {
        if (isExpenseView) {
            btnExpense.setBackgroundResource(R.drawable.button_background_green_rounded);
            btnExpense.setTextColor(Color.WHITE);
            btnIncome.setBackgroundResource(R.drawable.button_background_white_rounded);
            btnIncome.setTextColor(getResources().getColor(R.color.text_primary));
        } else {
            btnIncome.setBackgroundResource(R.drawable.button_background_green_rounded);
            btnIncome.setTextColor(Color.WHITE);
            btnExpense.setBackgroundResource(R.drawable.button_background_white_rounded);
            btnExpense.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    private void updatePeriodDisplay() {
        SimpleDateFormat sdf;

        switch (currentViewType) {
            case VIEW_TYPE_DAILY:
                // Format: DD/MM/YYYY
                sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvPeriod.setText(sdf.format(currentPeriod.getTime()));
                break;

            case VIEW_TYPE_MONTHLY:
                // Format: MM/YYYY
                sdf = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
                tvPeriod.setText(sdf.format(currentPeriod.getTime()));
                break;

            case VIEW_TYPE_YEARLY:
                // Format: YYYY
                sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
                tvPeriod.setText(sdf.format(currentPeriod.getTime()));
                break;
        }
    }

    private void navigatePeriod(int direction) {
        switch (currentViewType) {
            case VIEW_TYPE_DAILY:
                currentPeriod.add(Calendar.DAY_OF_MONTH, direction);
                break;

            case VIEW_TYPE_MONTHLY:
                currentPeriod.add(Calendar.MONTH, direction);
                break;

            case VIEW_TYPE_YEARLY:
                currentPeriod.add(Calendar.YEAR, direction);
                break;
        }

        updatePeriodDisplay();
        loadData();
    }

    private void showDatePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView;

        switch (currentViewType) {
            case VIEW_TYPE_DAILY:
                // Show date picker for daily view
                dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_date_picker, null);
                CalendarView calendarView = dialogView.findViewById(R.id.calendar_view);

                // Set current date
                calendarView.setDate(currentPeriod.getTimeInMillis());

                // Create a temporary calendar to store selection
                final Calendar selectedDate = (Calendar) currentPeriod.clone();

                calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                });

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                // Set button actions
                dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
                dialogView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                    currentPeriod = selectedDate;
                    updatePeriodDisplay();
                    loadData();
                    dialog.dismiss();
                });

                dialog.show();
                break;

            case VIEW_TYPE_MONTHLY:
                // Show month-year picker for monthly view
                dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_month_year_picker, null);
                NumberPicker monthPicker = dialogView.findViewById(R.id.month_picker);
                NumberPicker yearPicker = dialogView.findViewById(R.id.year_picker);

                // Setup month picker
                String[] months = new String[]{"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                        "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
                monthPicker.setMinValue(0);
                monthPicker.setMaxValue(11);
                monthPicker.setDisplayedValues(months);
                monthPicker.setValue(currentPeriod.get(Calendar.MONTH));

                // Setup year picker
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                yearPicker.setMinValue(currentYear - 10);
                yearPicker.setMaxValue(currentYear + 10);
                yearPicker.setValue(currentPeriod.get(Calendar.YEAR));

                builder.setView(dialogView);
                AlertDialog monthDialog = builder.create();

                // Set button actions
                dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> monthDialog.dismiss());
                dialogView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                    currentPeriod.set(Calendar.YEAR, yearPicker.getValue());
                    currentPeriod.set(Calendar.MONTH, monthPicker.getValue());
                    updatePeriodDisplay();
                    loadData();
                    monthDialog.dismiss();
                });

                monthDialog.show();
                break;

            case VIEW_TYPE_YEARLY:
                // Show year picker for yearly view
                dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_year_picker, null);
                NumberPicker yearPickerOnly = dialogView.findViewById(R.id.year_picker);

                // Setup year picker
                int currentYearOnly = Calendar.getInstance().get(Calendar.YEAR);
                yearPickerOnly.setMinValue(currentYearOnly - 10);
                yearPickerOnly.setMaxValue(currentYearOnly + 10);
                yearPickerOnly.setValue(currentPeriod.get(Calendar.YEAR));

                builder.setView(dialogView);
                AlertDialog yearDialog = builder.create();

                // Set button actions
                dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> yearDialog.dismiss());
                dialogView.findViewById(R.id.btn_ok).setOnClickListener(v -> {
                    currentPeriod.set(Calendar.YEAR, yearPickerOnly.getValue());
                    updatePeriodDisplay();
                    loadData();
                    yearDialog.dismiss();
                });

                yearDialog.show();
                break;
        }
    }

    private Calendar getStartDate() {
        Calendar startDate = (Calendar) currentPeriod.clone();

        switch (currentViewType) {
            case VIEW_TYPE_DAILY:
                // Start of the selected day
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                break;

            case VIEW_TYPE_MONTHLY:
                // First day of the selected month
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                break;

            case VIEW_TYPE_YEARLY:
                // First day of the selected year
                startDate.set(Calendar.MONTH, Calendar.JANUARY);
                startDate.set(Calendar.DAY_OF_MONTH, 1);
                startDate.set(Calendar.HOUR_OF_DAY, 0);
                startDate.set(Calendar.MINUTE, 0);
                startDate.set(Calendar.SECOND, 0);
                startDate.set(Calendar.MILLISECOND, 0);
                break;
        }

        return startDate;
    }

    private Calendar getEndDate() {
        Calendar endDate = (Calendar) currentPeriod.clone();

        switch (currentViewType) {
            case VIEW_TYPE_DAILY:
                // End of the selected day
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;

            case VIEW_TYPE_MONTHLY:
                // Last day of the selected month
                endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;

            case VIEW_TYPE_YEARLY:
                // Last day of the selected year
                endDate.set(Calendar.MONTH, Calendar.DECEMBER);
                endDate.set(Calendar.DAY_OF_MONTH, 31);
                endDate.set(Calendar.HOUR_OF_DAY, 23);
                endDate.set(Calendar.MINUTE, 59);
                endDate.set(Calendar.SECOND, 59);
                endDate.set(Calendar.MILLISECOND, 999);
                break;
        }

        return endDate;
    }


    private void loadData() {
        showLoading(true);

        executor.execute(() -> {
            try {
                Calendar startDate = getStartDate(); // Lấy ngày bắt đầu
                Calendar endDate = getEndDate();     // Lấy ngày kết thúc

                // Lấy giao dịch trong khoảng thời gian đã chọn
                List<TransactionWithCategory> transactionsWithCategory = transactionDao.getTransactionsWithCategoryByPeriod(
                        DateTimeUtils.formatDateTime(startDate.getTime()),
                        DateTimeUtils.formatDateTime(endDate.getTime())
                );

                // Chuyển đổi TransactionWithCategory thành Transaction và xử lý dữ liệu
                List<Transaction> transactions = new ArrayList<>();
                for (TransactionWithCategory transactionWithCategory : transactionsWithCategory) {
                    transactions.add(transactionWithCategory.getTransaction());
                }

                // Xử lý các giao dịch
                processTransactions(transactions);

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    showLoading(false);
                    Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }





    private void processTransactions(List<Transaction> transactions) {
        // Tiến hành xử lý các giao dịch như trước
        double totalExpense = 0;
        double totalIncome = 0;

        Map<Long, CategoryReport> expenseReports = new HashMap<>();
        Map<Long, CategoryReport> incomeReports = new HashMap<>();

        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                totalIncome += transaction.getAmount();
                CategoryReport report = incomeReports.get(transaction.getCategoryId());
                if (report == null) {
                    Category category = categoryDao.getCategoryById(transaction.getCategoryId());
                    if (category != null) {
                        report = new CategoryReport(category, 0);
                        incomeReports.put(transaction.getCategoryId(), report);
                    }
                }
                if (report != null) {
                    report.addAmount(transaction.getAmount());
                }
            } else {
                totalExpense += transaction.getAmount();
                CategoryReport report = expenseReports.get(transaction.getCategoryId());
                if (report == null) {
                    Category category = categoryDao.getCategoryById(transaction.getCategoryId());
                    if (category != null) {
                        report = new CategoryReport(category, 0);
                        expenseReports.put(transaction.getCategoryId(), report);
                    }
                }
                if (report != null) {
                    report.addAmount(transaction.getAmount());
                }
            }
        }

        List<CategoryReport> sortedExpenseReports = new ArrayList<>(expenseReports.values());
        List<CategoryReport> sortedIncomeReports = new ArrayList<>(incomeReports.values());

        Collections.sort(sortedExpenseReports, (a, b) -> Double.compare(b.getAmount(), a.getAmount()));
        Collections.sort(sortedIncomeReports, (a, b) -> Double.compare(b.getAmount(), a.getAmount()));

        calculatePercentages(sortedExpenseReports, totalExpense);
        calculatePercentages(sortedIncomeReports, totalIncome);

        final double finalTotalExpense = totalExpense;
        final double finalTotalIncome = totalIncome;
        final List<CategoryReport> finalExpenseReports = sortedExpenseReports;
        final List<CategoryReport> finalIncomeReports = sortedIncomeReports;

        handler.post(() -> {
            updateUI(finalTotalExpense, finalTotalIncome, finalExpenseReports, finalIncomeReports);
        });
    }



    private void calculatePercentages(List<CategoryReport> reports, double total) {
        if (total > 0) {
            for (CategoryReport report : reports) {
                report.setPercentage((report.getAmount() / total) * 100);
            }
        }
    }

    private void updateUI(double totalExpense, double totalIncome,
                          List<CategoryReport> expenseReports, List<CategoryReport> incomeReports) {
        try {
            // Format currency
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

            // Update summary cards
            tvExpense.setText("-" + formatter.format(totalExpense) + "đ");
            tvIncome.setText("+" + formatter.format(totalIncome) + "đ");
            tvBalance.setText("+" + formatter.format(totalIncome - totalExpense) + "đ");

            // Update category list based on current view (expense or income)
            categoryReports.clear();

            if (isExpenseView) {
                categoryReports.addAll(expenseReports);
                updatePieChart(expenseReports, totalExpense);
            } else {
                categoryReports.addAll(incomeReports);
                updatePieChart(incomeReports, totalIncome);
            }

            adapter.notifyDataSetChanged();

            // Show message if no data
            if (categoryReports.isEmpty()) {
                tvNoCategoryData.setVisibility(View.VISIBLE);
                rvCategories.setVisibility(View.GONE);
            } else {
                tvNoCategoryData.setVisibility(View.GONE);
                rvCategories.setVisibility(View.VISIBLE);
            }

            showLoading(false);
        } catch (Exception e) {
            e.printStackTrace();
            showLoading(false);
            Toast.makeText(requireContext(), "Lỗi khi cập nhật giao diện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePieChart(List<CategoryReport> reports, double total) {
        // Create pie chart data
        List<PieChartView.PieSlice> slices = new ArrayList<>();

        // Define colors for pie chart
        int[] colors = {
                Color.parseColor("#FFA726"), // Orange
                Color.parseColor("#66BB6A"), // Green
                Color.parseColor("#42A5F5"), // Blue
                Color.parseColor("#EC407A"), // Pink
                Color.parseColor("#AB47BC"), // Purple
                Color.parseColor("#26A69A"), // Teal
                Color.parseColor("#FFC107"), // Amber
                Color.parseColor("#5C6BC0"), // Indigo
                Color.parseColor("#EF5350"), // Red
                Color.parseColor("#7E57C2")  // Deep Purple
        };

        for (int i = 0; i < reports.size(); i++) {
            CategoryReport report = reports.get(i);
            int colorIndex = i % colors.length;

            slices.add(new PieChartView.PieSlice(
                    report.getPercentage(),
                    colors[colorIndex],
                    report.getCategory(),
                    report.getAmount()
            ));
        }

        // Update pie chart
        pieChartView.setSlices(slices);
    }

    @Override
    public void showLoading(boolean show) {
        if (loadingView != null) {
            loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload data when returning to this fragment
        loadData();
    }
}
