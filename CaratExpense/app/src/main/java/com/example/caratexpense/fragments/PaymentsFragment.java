package com.example.caratexpense.fragments;

import android.os.Bundle;
import android.text.Editable;
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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.adapters.PaymentReminderAdapter;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.PaymentReminderDao;
import com.example.caratexpense.models.PaymentReminder;
import com.example.caratexpense.utils.NotificationHelper;
import com.example.caratexpense.utils.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;

public class PaymentsFragment extends BaseFragment {
    private TextView tvUserName;
    private ImageView ivMenu, ivBack, ivLogo;
    private EditText etSearch;
    private RecyclerView rvPayments;
    private Button btnAddReminder;
    private PaymentReminderAdapter adapter;
    private List<PaymentReminder> paymentReminderList;
    private List<PaymentReminder> filteredPaymentReminderList;
    private PaymentReminderDao paymentReminderDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);

        paymentReminderDao = AppDatabase.getInstance(requireContext()).paymentReminderDao();

        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivBack = view.findViewById(R.id.iv_back);
        ivLogo = view.findViewById(R.id.iv_logo);
        etSearch = view.findViewById(R.id.et_search);
        rvPayments = view.findViewById(R.id.rv_payments);
        btnAddReminder = view.findViewById(R.id.btn_add_reminder);

        tvUserName.setText(preferenceManager.getUserName());

        // Setup menu click to open drawer
        ivMenu.setOnClickListener(v -> {
            DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.open();
            }
        });

        setupRecyclerView();
        setupListeners();
        loadPaymentReminders();
        updateNotificationBadge();

        return view;
    }

    private void setupRecyclerView() {
        paymentReminderList = new ArrayList<>();
        filteredPaymentReminderList = new ArrayList<>();
        adapter = new PaymentReminderAdapter(filteredPaymentReminderList, reminder -> {
            // Xử lý khi nhấn vào nhắc nhở thanh toán
            Bundle bundle = new Bundle();
            bundle.putSerializable("paymentReminder", reminder);

            AddPaymentReminderFragment fragment = new AddPaymentReminderFragment();
            fragment.setArguments(bundle);

            ((MainActivity) requireActivity()).loadFragment(fragment);
        });

        // Set fixed height for RecyclerView
        rvPayments.setLayoutManager(new LinearLayoutManager(requireContext()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        rvPayments.setAdapter(adapter);

        // Setup swipe to edit/delete
        SwipeToDeleteCallback swipeHandler = new SwipeToDeleteCallback(requireContext(), position -> {
            PaymentReminder reminder = filteredPaymentReminderList.get(position);

            // Show edit/delete options
            showEditDeleteDialog(reminder);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(rvPayments);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
        });

        btnAddReminder.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new AddPaymentReminderFragment());
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterPaymentReminders(s.toString());
            }
        });
    }

    private void filterPaymentReminders(String query) {
        filteredPaymentReminderList.clear();

        if (query.isEmpty()) {
            filteredPaymentReminderList.addAll(paymentReminderList);
        } else {
            String lowerCaseQuery = query.toLowerCase();

            for (PaymentReminder reminder : paymentReminderList) {
                if (reminder.getContent().toLowerCase().contains(lowerCaseQuery)) {
                    filteredPaymentReminderList.add(reminder);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void loadPaymentReminders() {
        new Thread(() -> {
            List<PaymentReminder> reminders = paymentReminderDao.getAllPaymentReminders();
            requireActivity().runOnUiThread(() -> {
                paymentReminderList.clear();
                paymentReminderList.addAll(reminders);
                filteredPaymentReminderList.clear();
                filteredPaymentReminderList.addAll(reminders);
                adapter.notifyDataSetChanged();
                updateNotificationBadge();
            });
        }).start();
    }

    private void showEditDeleteDialog(PaymentReminder paymentReminder) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_delete, null);
        builder.setView(dialogView);

        Button btnEdit = dialogView.findViewById(R.id.btn_edit);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        android.app.AlertDialog dialog = builder.create();

        btnEdit.setOnClickListener(v -> {
            dialog.dismiss();

            // Navigate to edit screen
            Bundle bundle = new Bundle();
            bundle.putSerializable("paymentReminder", paymentReminder);

            AddPaymentReminderFragment fragment = new AddPaymentReminderFragment();
            fragment.setArguments(bundle);

            ((MainActivity) requireActivity()).loadFragment(fragment);
        });

        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();

            // Delete payment reminder
            new Thread(() -> {
                paymentReminderDao.delete(paymentReminder);

                // Cancel notification for this reminder
                NotificationHelper.cancelNotification(requireContext(), (int) paymentReminder.getId());

                requireActivity().runOnUiThread(() -> {
                    loadPaymentReminders();
                    showSuccessDialog("Xóa thành công");
                });
            }).start();
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPaymentReminders();
    }
}
