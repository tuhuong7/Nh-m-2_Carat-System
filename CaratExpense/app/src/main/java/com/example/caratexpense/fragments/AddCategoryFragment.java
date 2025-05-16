package com.example.caratexpense.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.CategoryDao;
import com.example.caratexpense.models.Category;

public class AddCategoryFragment extends BaseFragment {
    private TextView tvUserName;
    private ImageView ivMenu, ivBack;
    private EditText etCategoryName;
    private RadioButton rbExpense, rbIncome;
    private Button btnAdd;
    private CategoryDao categoryDao;
    private boolean isExpense = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);

        categoryDao = AppDatabase.getInstance(requireContext()).categoryDao();

        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivBack = view.findViewById(R.id.iv_back);
        etCategoryName = view.findViewById(R.id.et_category_name);
        rbExpense = view.findViewById(R.id.rb_expense);
        rbIncome = view.findViewById(R.id.rb_income);
        btnAdd = view.findViewById(R.id.btn_add);

        tvUserName.setText(preferenceManager.getUserName());

        // Check if we have a default selection
        if (getArguments() != null) {
            isExpense = getArguments().getBoolean("isExpense", true);
            if (isExpense) {
                rbExpense.setChecked(true);
            } else {
                rbIncome.setChecked(true);
            }
        }

        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new AddTransactionFragment());
        });

        rbExpense.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isExpense = true;
            }
        });

        rbIncome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isExpense = false;
            }
        });

        btnAdd.setOnClickListener(v -> {
            saveCategory();
        });

        // Set focus and show keyboard when fragment is created
        etCategoryName.requestFocus();

        return view;
    }

    private void saveCategory() {
        String categoryName = etCategoryName.getText().toString().trim();

        if (categoryName.isEmpty()) {
            showError("Vui lòng nhập tên danh mục");
            return;
        }

        // Thêm icon name vào constructor, ví dụ sử dụng "ic_default" cho icon mặc định
        String iconName = "ic_default";  // Bạn có thể thay đổi theo yêu cầu

        Category category = new Category(0, categoryName, isExpense, iconName); // Sửa ở đây

        new Thread(() -> {
            categoryDao.insert(category);

            requireActivity().runOnUiThread(() -> {
                showSuccessDialog("Thêm danh mục thành công");
                ((MainActivity) requireActivity()).loadFragment(new AddTransactionFragment());
            });
        }).start();
    }

}
