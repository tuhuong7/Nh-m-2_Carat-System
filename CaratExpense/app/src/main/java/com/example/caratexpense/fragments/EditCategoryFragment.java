package com.example.caratexpense.fragments;

import android.os.Bundle;
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
import com.example.caratexpense.models.Category;
import com.example.caratexpense.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class EditCategoryFragment extends BaseFragment {
    private TextView tvUserName, tvExpense, tvIncome;
    private ImageView ivMenu, ivBack;
    private EditText etCategoryName;
    private RecyclerView rvCategories;
    private Button btnComplete, btnDelete;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private CategoryDao categoryDao;
    private boolean isExpense = true;
    private Category selectedCategory = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_category, container, false);

        categoryDao = AppDatabase.getInstance(requireContext()).categoryDao();

        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivBack = view.findViewById(R.id.iv_back);
        tvExpense = view.findViewById(R.id.tv_expense);
        tvIncome = view.findViewById(R.id.tv_income);
        etCategoryName = view.findViewById(R.id.et_category_name);
        rvCategories = view.findViewById(R.id.rv_categories);
        btnComplete = view.findViewById(R.id.btn_complete);
        btnDelete = view.findViewById(R.id.btn_delete);

        tvUserName.setText(preferenceManager.getUserName());

        // Check if we have a default selection
        if (getArguments() != null) {
            isExpense = getArguments().getBoolean("isExpense", true);
        }

        setupTabSelection();
        setupCategoryRecyclerView();

        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new AddTransactionFragment());
        });

        btnComplete.setOnClickListener(v -> {
            updateCategory();
        });

        btnDelete.setOnClickListener(v -> {
            deleteCategory();
        });

        loadCategories();

        return view;
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
            tvExpense.setTextColor(getResources().getColor(R.color.white));
            tvIncome.setTextColor(getResources().getColor(R.color.gray));
        } else {
            tvExpense.setTextColor(getResources().getColor(R.color.white));
            tvIncome.setTextColor(getResources().getColor(R.color.orange));
        }
    }

    private void setupCategoryRecyclerView() {
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, category -> {
            selectedCategory = category;
            adapter.setSelectedCategory(category);
            etCategoryName.setText(category.getName());
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

                // Reset selection
                selectedCategory = null;
                etCategoryName.setText("");
            });
        }).start();
    }

    private void updateCategory() {
        if (selectedCategory == null) {
            showError("Vui lòng chọn danh mục để chỉnh sửa");
            return;
        }

        String categoryName = etCategoryName.getText().toString().trim();

        if (categoryName.isEmpty()) {
            showError("Vui lòng nhập tên danh mục");
            return;
        }

        selectedCategory.setName(categoryName);

        new Thread(() -> {
            categoryDao.update(selectedCategory);

            requireActivity().runOnUiThread(() -> {
                showSuccessDialog("Sửa thành công");
            });
        }).start();
    }

    private void deleteCategory() {
        if (selectedCategory == null) {
            showError("Vui lòng chọn danh mục để xóa");
            return;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setMessage("Bạn có chắc chắn muốn xóa danh mục này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    new Thread(() -> {
                        categoryDao.delete(selectedCategory);

                        requireActivity().runOnUiThread(() -> {
                            loadCategories();
                            showSuccessDialog("Xóa thành công");
                        });
                    }).start();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
