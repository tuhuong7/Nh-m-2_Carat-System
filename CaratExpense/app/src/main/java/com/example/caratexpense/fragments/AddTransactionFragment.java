package com.example.caratexpense.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.adapters.CategoryAdapter;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.CategoryDao;
import com.example.caratexpense.database.dao.TransactionDao;
import com.example.caratexpense.models.Category;
import com.example.caratexpense.models.Transaction;
import com.example.caratexpense.utils.CurrencyInputFilter;
import com.example.caratexpense.utils.DateTimeUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

public class AddTransactionFragment extends BaseFragment {
    private TextView tvUserName, tvExpense, tvIncome, tvDateTime;
    private ImageView ivMenu, ivBack, ivSettings;
    private EditText etAmount, etNote;
    private Button btnSubmit;
    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private CategoryDao categoryDao;
    private TransactionDao transactionDao;
    private boolean isExpense = true;
    private Category selectedCategory = null;
    private Transaction editingTransaction = null;
    private Calendar selectedDateTime = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        categoryDao = AppDatabase.getInstance(requireContext()).categoryDao();
        transactionDao = AppDatabase.getInstance(requireContext()).transactionDao();

        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivBack = view.findViewById(R.id.iv_back);
        ivSettings = view.findViewById(R.id.iv_settings);
        tvExpense = view.findViewById(R.id.tv_expense);
        tvIncome = view.findViewById(R.id.tv_income);
        tvDateTime = view.findViewById(R.id.tv_date_time);
        etAmount = view.findViewById(R.id.et_amount);
        etNote = view.findViewById(R.id.et_note);
        btnSubmit = view.findViewById(R.id.btn_submit);
        rvCategories = view.findViewById(R.id.rv_categories);

        tvUserName.setText(preferenceManager.getUserName());

        // Setup menu click to open drawer
        ivMenu.setOnClickListener(v -> {
            DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);

            }
        });

        // Check if we're editing an existing transaction
        if (getArguments() != null && getArguments().containsKey("transaction")) {
            editingTransaction = (Transaction) getArguments().getSerializable("transaction");
            setupEditMode();
        }

        setupTabSelection();
        setupDateTimePicker();
        setupAmountInput();
        setupCategoryRecyclerView();

        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new IncomeExpenseFragment());
        });

        btnSubmit.setOnClickListener(v -> {
            saveTransaction();
        });

        // Add new category button
        ImageView ivAddCategory = view.findViewById(R.id.iv_add_category);
        ivAddCategory.setOnClickListener(v -> {
            AddCategoryFragment fragment = new AddCategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isExpense", isExpense);
            fragment.setArguments(bundle);
            ((MainActivity) requireActivity()).loadFragment(fragment);
        });

        ivSettings.setOnClickListener(v -> {
            EditCategoryFragment fragment = new EditCategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("isExpense", isExpense);
            fragment.setArguments(bundle);
            ((MainActivity) requireActivity()).loadFragment(fragment);
        });

        loadCategories();

        return view;
    }

    // Các phương thức khác giữ nguyên
    // ...

    private void setupEditMode() {
        // Set values from the transaction being edited
        isExpense = !editingTransaction.isIncome();
        selectedCategory = editingTransaction.getCategory();

        // Parse date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = sdf.parse(editingTransaction.getDateTime());
            selectedDateTime.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Set amount without formatting
        String amountStr = String.valueOf(editingTransaction.getAmount());
        etAmount.setText(amountStr);

        // Set note
        etNote.setText(editingTransaction.getNote());

        // Update UI
        updateDateTimeDisplay();
        btnSubmit.setText("CẬP NHẬT");
    }

    private void setupTabSelection() {
        // Default to expense tab
        updateTabSelection();

        tvExpense.setOnClickListener(v -> {
            isExpense = true;
            updateTabSelection();
            loadCategories();
        });

        tvIncome.setOnClickListener(v -> {
            isExpense = false;
            updateTabSelection();
            loadCategories();
        });
    }

    private void updateTabSelection() {
        if (isExpense) {
            tvExpense.setTextColor(getResources().getColor(R.color.orange));
            tvIncome.setTextColor(getResources().getColor(R.color.gray));
            btnSubmit.setText(editingTransaction != null ? "CẬP NHẬT" : "NHẬP KHOẢN CHI");
        } else {
            tvExpense.setTextColor(getResources().getColor(R.color.gray));
            tvIncome.setTextColor(getResources().getColor(R.color.orange));
            btnSubmit.setText(editingTransaction != null ? "CẬP NHẬT" : "NHẬP KHOẢN THU");
        }
    }

    private void setupDateTimePicker() {
        updateDateTimeDisplay();

        tvDateTime.setOnClickListener(v -> {
            showDateTimePicker();
        });
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        tvDateTime.setText(sdf.format(selectedDateTime.getTime()));
    }

    private void showDateTimePicker() {
        // Date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // After date is selected, show time picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            requireContext(),
                            (view1, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                updateDateTimeDisplay();
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupAmountInput() {
        etAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    etAmount.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", ""); // Chỉ giữ số
                    try {
                        if (!cleanString.isEmpty()) {
                            double parsed = Double.parseDouble(cleanString);
                            String formatted = NumberFormat.getInstance(new Locale("vi", "VN")).format(parsed);
                            current = formatted;
                            etAmount.setText(formatted);
                            etAmount.setSelection(formatted.length());
                        } else {
                            current = "";
                            etAmount.setText("");
                        }
                    } catch (NumberFormatException e) {
                        // Nếu số quá lớn hoặc có lỗi -> giữ nguyên current
                    }

                    etAmount.addTextChangedListener(this);
                }
            }
        });
    }


    private void setupCategoryRecyclerView() {
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, category -> {
            selectedCategory = category;
            adapter.setSelectedCategory(category);
        });

        rvCategories.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        rvCategories.setAdapter(adapter);
    }

    private void loadCategories() {
        new Thread(() -> {
            List<Category> categories;
            if (isExpense) {
                categories = categoryDao.getExpenseCategories();
            } else {
                categories = categoryDao.getIncomeCategories();
            }

            requireActivity().runOnUiThread(() -> {
                categoryList.clear();
                categoryList.addAll(categories);
                adapter.notifyDataSetChanged();

                // If editing, select the category
                if (editingTransaction != null) {
                    for (Category category : categoryList) {
                        if (category.getId() == editingTransaction.getCategoryId()) {
                            selectedCategory = category;
                            adapter.setSelectedCategory(category);
                            break;
                        }
                    }
                }
            });
        }).start();
    }

    private void saveTransaction() {
        String amountStr = etAmount.getText().toString().replaceAll("[^\\d]", "");
        String note = etNote.getText().toString().trim();

        if (amountStr.isEmpty()) {
            showError("Vui lòng nhập số tiền");
            return;
        }

        if (selectedCategory == null) {
            showError("Vui lòng chọn danh mục");
            return;
        }

        double amount = Double.parseDouble(amountStr);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateTime = sdf.format(selectedDateTime.getTime());

        final Transaction transaction;

        if (editingTransaction != null) {
            // Update existing transaction
            transaction = editingTransaction;
            transaction.setAmount(amount);
            transaction.setNote(note);
            transaction.setDateTime(dateTime);
            transaction.setCategoryId(selectedCategory.getId());
            transaction.setCategory(selectedCategory);
            transaction.setIncome(!isExpense);
        } else {
            // Create new transaction
            transaction = new Transaction(
                    0, // ID will be auto-generated
                    amount,
                    note,
                    dateTime,
                    selectedCategory.getId(),
                    selectedCategory,
                    !isExpense
            );
        }

        new Thread(() -> {
            if (editingTransaction != null) {
                transactionDao.update(transaction);
            } else {
                transactionDao.insert(transaction);
            }

            requireActivity().runOnUiThread(() -> {
                showSuccessDialog(editingTransaction != null ? "Sửa thành công" : "Thêm thu chi thành công");
                ((MainActivity) requireActivity()).loadFragment(new IncomeExpenseFragment());
            });
        }).start();
    }
}
