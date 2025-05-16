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
import com.example.caratexpense.database.dao.GoalDao;
import com.example.caratexpense.models.Category;
import com.example.caratexpense.models.Goal;
import com.example.caratexpense.utils.CurrencyInputFilter;
import com.example.caratexpense.utils.DateTimeUtils;
import com.example.caratexpense.utils.PreferenceManager;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddGoalFragment extends BaseFragment {
    private TextView tvUserName, tvDateTime;
    private ImageView ivMenu, ivBack;
    private EditText etAmount, etNote;
    private Button btnSubmit;
    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private CategoryDao categoryDao;
    private GoalDao goalDao;
    private Category selectedCategory = null;
    private Goal editingGoal = null;
    private Calendar selectedDateTime = Calendar.getInstance();
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_goal, container, false);
        
        categoryDao = AppDatabase.getInstance(requireContext()).categoryDao();
        goalDao = AppDatabase.getInstance(requireContext()).goalDao();
        
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivBack = view.findViewById(R.id.iv_back);
        tvDateTime = view.findViewById(R.id.tv_date_time);
        etAmount = view.findViewById(R.id.et_amount);
        etNote = view.findViewById(R.id.et_note);
        btnSubmit = view.findViewById(R.id.btn_submit);
        rvCategories = view.findViewById(R.id.rv_categories);

        tvUserName.setText(preferenceManager.getUserName());
        
        // Check if we're editing an existing goal
        if (getArguments() != null && getArguments().containsKey("goal")) {
            editingGoal = (Goal) getArguments().getSerializable("goal");
            setupEditMode();
        }
        
        setupDateTimePicker();
        setupAmountInput();
        setupCategoryRecyclerView();
        
        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new GoalsFragment());
        });
        
        btnSubmit.setOnClickListener(v -> {
            saveGoal();
        });
        
        loadCategories();
        
        return view;
    }
    
    private void setupEditMode() {
        // Set values from the goal being edited
        selectedCategory = editingGoal.getCategory();
        
        // Parse date
        if (editingGoal.getDeadline() != null) {
            selectedDateTime.setTime(editingGoal.getDeadline());
        }
        
        // Set amount without formatting
        String amountStr = String.valueOf((long)editingGoal.getTargetAmount());
        etAmount.setText(amountStr);
        
        // Set note
        etNote.setText(editingGoal.getNote());
        
        // Update UI
        updateDateTimeDisplay();
        btnSubmit.setText("HOÀN THÀNH");
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
            List<Category> categories = categoryDao.getAllCategories();
            
            requireActivity().runOnUiThread(() -> {
                categoryList.clear();
                categoryList.addAll(categories);
                adapter.notifyDataSetChanged();
                
                // If editing, select the category
                if (editingGoal != null && editingGoal.getCategory() != null) {
                    for (Category category : categoryList) {
                        if (category.getId() == editingGoal.getCategory().getId()) {
                            selectedCategory = category;
                            adapter.setSelectedCategory(category);
                            break;
                        }
                    }
                }
            });
        }).start();
    }
    
    private void saveGoal() {
        String amountStr = etAmount.getText().toString().replaceAll("[^\\d]", "");
        String note = etNote.getText().toString().trim();
        
        if (amountStr.isEmpty()) {
            showError("Vui lòng nhập số tiền mục tiêu");
            return;
        }
        
        if (selectedCategory == null) {
            showError("Vui lòng chọn danh mục");
            return;
        }
        
        double amount = Double.parseDouble(amountStr);
        Date deadline = selectedDateTime.getTime();
        
        final Goal goal;
        
        if (editingGoal != null) {
            // Update existing goal
            goal = editingGoal;
            goal.setTargetAmount(amount);
            goal.setNote(note);
            goal.setDeadline(deadline);
            goal.setCategoryId(selectedCategory.getId());
            goal.setCategory(selectedCategory);
        } else {
            // Create new goal
            goal = new Goal(
                    0, // ID will be auto-generated
                    selectedCategory.getName(),
                    amount,
                    0.0,
                    deadline,
                    false
            );
            goal.setCategoryId(selectedCategory.getId());
            goal.setCategory(selectedCategory);
            goal.setNote(note);
        }
        
        new Thread(() -> {
            if (editingGoal != null) {
                goalDao.update(goal);
            } else {
                goalDao.insert(goal);
            }
            
            requireActivity().runOnUiThread(() -> {
                showSuccessDialog(editingGoal != null ? "Sửa thành công" : "Thêm mục tiêu thành công");
            });
        }).start();
    }

    @Override
    protected void showError(String message) {
        super.showError(message);  // Đúng vì BaseFragment đã có hàm này
    }

    @Override
    protected void showSuccessDialog(String message) {
        super.showSuccessDialog(message);
    }

}
