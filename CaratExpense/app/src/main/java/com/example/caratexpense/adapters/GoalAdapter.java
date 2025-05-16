package com.example.caratexpense.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.R;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.models.Goal;
import com.example.caratexpense.utils.IconUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {
    private List<Goal> goals;
    private OnGoalClickListener listener;
    private Executor executor = Executors.newSingleThreadExecutor();

    public interface OnGoalClickListener {
        void onGoalClick(Goal goal);
        void onGoalLongClick(Goal goal);
    }

    public GoalAdapter(List<Goal> goals, OnGoalClickListener listener) {
        this.goals = goals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goals.get(position);
        holder.bind(goal);
    }

    @Override
    public int getItemCount() {
        return goals.size();
    }

    class GoalViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategoryIcon;
        private TextView tvTitle, tvDeadline, tvNote, tvAmount, tvCompletionPercentage;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDeadline = itemView.findViewById(R.id.tv_deadline);
            tvNote = itemView.findViewById(R.id.tv_note);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvCompletionPercentage = itemView.findViewById(R.id.tv_completion_percentage);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onGoalClick(goals.get(position));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onGoalLongClick(goals.get(position));
                    return true;
                }
                return false;
            });
        }

        public void bind(Goal goal) {
            Context context = itemView.getContext();

            if (goal.getCategory() != null) {
                ivCategoryIcon.setImageResource(IconUtils.getIconResourceId(
                        context,
                        goal.getCategory().getIconName()
                ));
            } else {
                ivCategoryIcon.setImageResource(R.drawable.ic_default);
            }

            tvTitle.setText(goal.getTitle());

            if (goal.getDeadline() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvDeadline.setText(sdf.format(goal.getDeadline()));
            } else {
                tvDeadline.setText("Không có hạn");
            }

            tvNote.setText(goal.getNote());

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvAmount.setText(formatter.format(goal.getTargetAmount()));

            tvCompletionPercentage.setText("Đang tính...");
            tvCompletionPercentage.setTextColor(ContextCompat.getColor(context, R.color.gray_text));

            calculateCompletionPercentage(goal, tvCompletionPercentage, context);
        }

        private void calculateCompletionPercentage(Goal goal, TextView tvCompletionPercentage, Context context) {
            executor.execute(() -> {
                try {
                    AppDatabase db = AppDatabase.getInstance(context);
                    double totalIncome = db.transactionDao().getTotalIncome();
                    double totalExpense = db.transactionDao().getTotalExpense();

                    double remainingAmount = totalIncome - totalExpense;
                    double targetAmount = goal.getTargetAmount();
                    double completionPercentage;

                    if (remainingAmount >= targetAmount) {
                        completionPercentage = 100.0;
                    } else {
                        completionPercentage = (remainingAmount / targetAmount) * 100.0;
                        completionPercentage = Math.max(0, completionPercentage);
                    }

                    String formattedPercentage = String.format(Locale.getDefault(), "%.1f%%", completionPercentage);

                    int colorResId;
                    if (completionPercentage >= 100) {
                        colorResId = R.color.green;
                    } else if (completionPercentage >= 50) {
                        colorResId = R.color.blue;
                    } else {
                        colorResId = R.color.orange;
                    }

                    int finalColorResId = colorResId;
                    itemView.post(() -> {
                        tvCompletionPercentage.setText(formattedPercentage);
                        tvCompletionPercentage.setTextColor(ContextCompat.getColor(context, finalColorResId));
                    });

                } catch (Exception e) {
                    itemView.post(() -> {
                        tvCompletionPercentage.setText("--");
                        tvCompletionPercentage.setTextColor(ContextCompat.getColor(context, R.color.gray_text));
                    });
                }
            });
        }
    }
}
