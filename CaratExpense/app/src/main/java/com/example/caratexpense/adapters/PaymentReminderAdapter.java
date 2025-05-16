package com.example.caratexpense.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.R;
import com.example.caratexpense.models.PaymentReminder;

import java.util.List;

public class PaymentReminderAdapter extends RecyclerView.Adapter<PaymentReminderAdapter.PaymentReminderViewHolder> {
    private List<PaymentReminder> paymentReminders;
    private PaymentReminderActionListener listener;

    public interface PaymentReminderActionListener {
        void onPaymentReminderClick(PaymentReminder paymentReminder);
    }

    public PaymentReminderAdapter(List<PaymentReminder> paymentReminders) {
        this.paymentReminders = paymentReminders;
    }

    public PaymentReminderAdapter(List<PaymentReminder> paymentReminders, PaymentReminderActionListener listener) {
        this.paymentReminders = paymentReminders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_reminder, parent, false);
        return new PaymentReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentReminderViewHolder holder, int position) {
        PaymentReminder paymentReminder = paymentReminders.get(position);
        holder.bind(paymentReminder);
    }

    @Override
    public int getItemCount() {
        return paymentReminders.size();
    }

    class PaymentReminderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvContent, tvReminderDate, tvCycle;

        public PaymentReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvReminderDate = itemView.findViewById(R.id.tv_reminder_date);
            tvCycle = itemView.findViewById(R.id.tv_cycle);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onPaymentReminderClick(paymentReminders.get(position));
                }
            });
        }

        public void bind(PaymentReminder paymentReminder) {
            tvContent.setText(paymentReminder.getContent());
            tvReminderDate.setText(paymentReminder.getReminderDate());
            tvCycle.setText(paymentReminder.getCycle());
        }
    }

    public static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
        private List<PaymentReminder> paymentReminders;

        public NotificationAdapter(List<PaymentReminder> paymentReminders) {
            this.paymentReminders = paymentReminders;
        }

        @NonNull
        @Override
        public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
            PaymentReminder paymentReminder = paymentReminders.get(position);
            holder.bind(paymentReminder);
        }

        @Override
        public int getItemCount() {
            return paymentReminders.size();
        }

        static class NotificationViewHolder extends RecyclerView.ViewHolder {
            private TextView tvContent, tvReminderDate;
            private ImageView ivIcon;

            public NotificationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvContent = itemView.findViewById(R.id.tv_content);
                tvReminderDate = itemView.findViewById(R.id.tv_reminder_date);
                ivIcon = itemView.findViewById(R.id.iv_icon);
            }

            public void bind(PaymentReminder paymentReminder) {
                tvContent.setText(paymentReminder.getContent());
                tvReminderDate.setText(paymentReminder.getReminderDate());
                ivIcon.setImageResource(R.drawable.ic_notification);
            }
        }
    }
}
