package com.example.caratexpense.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.adapters.PaymentReminderAdapter;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.PaymentReminderDao;
import com.example.caratexpense.models.PaymentReminder;
import com.example.caratexpense.utils.PreferenceManager;

import java.util.List;

public class BaseFragment extends Fragment {
    protected PreferenceManager preferenceManager;
    protected PaymentReminderDao paymentReminderDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(requireContext());
        paymentReminderDao = AppDatabase.getInstance(requireContext()).paymentReminderDao();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup notification badge and logo click
        setupNotifications(view);

        // Setup menu icon click
        setupMenuIcon(view);
    }

    protected void setupMenuIcon(View view) {
        ImageView ivMenu = view.findViewById(R.id.iv_menu);
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> {
                // Sử dụng MainActivity để mở drawer
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openDrawer();
                }
            });
        }
    }

    protected void setupNotifications(View view) {
        // Find logo and badge views
        ImageView ivLogo = view.findViewById(R.id.iv_logo);
        TextView tvBadge = view.findViewById(R.id.tv_notification_badge);

        if (ivLogo != null) {
            // Set click listener for logo
            ivLogo.setOnClickListener(v -> {
                showNotificationsDialog();
            });

            // Update badge count
            updateNotificationBadge(tvBadge);
        }
    }

    protected void updateNotificationBadge() {
        View view = getView();
        if (view != null) {
            TextView tvBadge = view.findViewById(R.id.tv_notification_badge);
            updateNotificationBadge(tvBadge);
        }
    }

    protected void updateNotificationBadge(TextView tvBadge) {
        if (tvBadge == null) return;

        new Thread(() -> {
            int unreadCount = paymentReminderDao.getUnreadCount();
            requireActivity().runOnUiThread(() -> {
                if (unreadCount > 0) {
                    // Show badge with count
                    tvBadge.setVisibility(View.VISIBLE);
                    tvBadge.setText(String.valueOf(unreadCount));
                } else {
                    // Hide badge
                    tvBadge.setVisibility(View.GONE);
                }
            });
        }).start();
    }

    protected void showNotificationsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_notifications, null);
        builder.setView(dialogView);

        RecyclerView rvNotifications = dialogView.findViewById(R.id.rv_notifications);
        TextView tvChangeName = dialogView.findViewById(R.id.tv_change_name);

        // Set up notifications list
        new Thread(() -> {
            List<PaymentReminder> reminders = paymentReminderDao.getAllPaymentReminders();
            requireActivity().runOnUiThread(() -> {
                rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
                rvNotifications.setAdapter(new PaymentReminderAdapter.NotificationAdapter(reminders));
            });
        }).start();

        android.app.AlertDialog dialog = builder.create();

        tvChangeName.setOnClickListener(v -> {
            dialog.dismiss();
            showChangeNameDialog();
        });

        dialog.setOnDismissListener(dialog1 -> {
            // Mark all notifications as read
            new Thread(() -> {
                List<PaymentReminder> reminders = paymentReminderDao.getAllPaymentReminders();
                for (PaymentReminder reminder : reminders) {
                    if (!reminder.isRead()) {
                        reminder.setRead(true);
                        paymentReminderDao.update(reminder);
                    }
                }

                // Update notification badge in all visible fragments
                requireActivity().runOnUiThread(this::updateNotificationBadge);
            }).start();
        });

        dialog.show();
    }

    protected void showChangeNameDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_name, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.et_name);
        Button btnSave = dialogView.findViewById(R.id.btn_save);

        etName.setText(preferenceManager.getUserName());

        android.app.AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            if (!newName.isEmpty()) {
                preferenceManager.setUserName(newName);

                // Update user name in current fragment
                View view = getView();
                if (view != null) {
                    TextView tvUserName = view.findViewById(R.id.tv_user_name);
                    if (tvUserName != null) {
                        tvUserName.setText(newName);
                    }
                }

                dialog.dismiss();
                showSuccessDialog("Đổi tên thành công");
            }
        });

        dialog.show();
    }

    protected void showSuccessDialog(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_success, null);
        builder.setView(dialogView);

        TextView tvMessage = dialogView.findViewById(R.id.tv_message);
        Button btnOk = dialogView.findViewById(R.id.btn_ok);

        tvMessage.setText(message);

        android.app.AlertDialog dialog = builder.create();

        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    protected void showError(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    public void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void showLoading(boolean isLoading) {
        View view = getView();
        if (view != null) {
            View loadingView = view.findViewById(R.id.loading_view); // Thêm loading_view nếu cần
            if (loadingView != null) {
                loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        }
    }

    public void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}
