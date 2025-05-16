package com.example.caratexpense.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.PaymentReminderDao;
import com.example.caratexpense.models.PaymentReminder;
import com.example.caratexpense.utils.NotificationHelper;
import com.example.caratexpense.utils.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddPaymentReminderFragment extends BaseFragment {
    private TextView tvUserName, tvReminderDate;
    private ImageView ivMenu, ivBack;
    private EditText etContent;
    private Spinner spinnerCycle;
    private Button btnSubmit;
    
    
    private PaymentReminder editingReminder = null;
    private Calendar selectedDate = Calendar.getInstance();
    private String selectedCycle = PaymentReminder.CYCLE_ONCE;
    
    private final String[] cycleOptions = {
            PaymentReminder.CYCLE_ONCE,
            PaymentReminder.CYCLE_DAILY,
            PaymentReminder.CYCLE_MONTHLY
    };
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_payment_reminder, container, false);
        
        
        
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivBack = view.findViewById(R.id.iv_back);
        tvReminderDate = view.findViewById(R.id.tv_reminder_date);
        etContent = view.findViewById(R.id.et_content);
        spinnerCycle = view.findViewById(R.id.spinner_cycle);
        btnSubmit = view.findViewById(R.id.btn_submit);
        
        tvUserName.setText(preferenceManager.getUserName());
        
        // Check if we're editing an existing reminder
        if (getArguments() != null && getArguments().containsKey("paymentReminder")) {
            editingReminder = (PaymentReminder) getArguments().getSerializable("paymentReminder");
            setupEditMode();
        }
        
        setupDatePicker();
        setupCycleSpinner();
        
        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new PaymentsFragment());
        });
        
        btnSubmit.setOnClickListener(v -> {
            savePaymentReminder();
        });
        
        return view;
    }
    
    private void setupEditMode() {
        // Set values from the reminder being edited
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(editingReminder.getReminderDate());
            if (date != null) {
                selectedDate.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        etContent.setText(editingReminder.getContent());
        selectedCycle = editingReminder.getCycle();
        
        // Update UI
        updateDateDisplay();
        btnSubmit.setText("HOÀN THÀNH");
    }
    
    private void setupDatePicker() {
        updateDateDisplay();
        
        tvReminderDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateDisplay();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }
    
    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvReminderDate.setText(sdf.format(selectedDate.getTime()));
    }
    
    private void setupCycleSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                cycleOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCycle.setAdapter(adapter);
        
        // Set default selection
        for (int i = 0; i < cycleOptions.length; i++) {
            if (cycleOptions[i].equals(selectedCycle)) {
                spinnerCycle.setSelection(i);
                break;
            }
        }
        
        spinnerCycle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCycle = cycleOptions[position];
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    
    private void savePaymentReminder() {
        String content = etContent.getText().toString().trim();
        
        if (content.isEmpty()) {
            showError("Vui lòng nhập nội dung nhắc nhở");
            return;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String reminderDate = sdf.format(selectedDate.getTime());
        
        final PaymentReminder reminder;
        
        if (editingReminder != null) {
            // Update existing reminder
            reminder = editingReminder;
            reminder.setContent(content);
            reminder.setReminderDate(reminderDate);
            reminder.setCycle(selectedCycle);
            reminder.setRead(false); // Mark as unread when updated
        } else {
            // Create new reminder
            reminder = new PaymentReminder(
                    0, // ID will be auto-generated
                    content,
                    reminderDate,
                    selectedCycle,
                    false // Not read yet
            );
        }
        
        new Thread(() -> {
            long id;
            if (editingReminder != null) {
                paymentReminderDao.update(reminder);
                id = reminder.getId();
            } else {
                id = paymentReminderDao.insert(reminder);
                reminder.setId(id);
            }
            
            // Schedule notification
            scheduleNotification(reminder);
            
            requireActivity().runOnUiThread(() -> {
                showSuccessDialog(editingReminder != null ? "Cập nhật thành công" : "Thêm nhắc nhở thành công");
            });
        }).start();
    }
    
    private void scheduleNotification(PaymentReminder reminder) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date reminderDate = sdf.parse(reminder.getReminderDate());
            
            if (reminderDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(reminderDate);
                calendar.set(Calendar.HOUR_OF_DAY, 8); // Set notification time to 8:00 AM
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                
                // Cancel any existing notification for this reminder
                NotificationHelper.cancelNotification(requireContext(), (int) reminder.getId());
                
                // Schedule new notification
                NotificationHelper.scheduleNotification(
                        requireContext(),
                        (int) reminder.getId(),
                        reminder.getContent(),
                        calendar.getTimeInMillis(),
                        reminder.getCycle()
                );
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void showError(String message) {
        super.showError(message);
    }

    @Override
    protected void showSuccessDialog(String message) {
        super.showSuccessDialog(message);
    }

}
