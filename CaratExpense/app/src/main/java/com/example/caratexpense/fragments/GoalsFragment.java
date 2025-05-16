package com.example.caratexpense.fragments;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;  // Đảm bảo sử dụng Button
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.adapters.GoalAdapter;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.GoalDao;
import com.example.caratexpense.database.dao.TransactionDao;
import com.example.caratexpense.models.Goal;
import com.example.caratexpense.utils.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GoalsFragment extends BaseFragment {
    private TextView tvUserName, tvNoGoals;
    private ImageView ivMenu, ivBack;
    private RecyclerView rvGoals;
    private Button btnAddGoal;  // Sử dụng Button thay vì FloatingActionButton
    private View loadingView;

    private GoalAdapter adapter;
    private List<Goal> goals = new ArrayList<>();

    private GoalDao goalDao;
    private TransactionDao transactionDao;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goals, container, false);

        // Initialize database
        goalDao = AppDatabase.getInstance(requireContext()).goalDao();
        transactionDao = AppDatabase.getInstance(requireContext()).transactionDao();

        // Initialize views
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        rvGoals = view.findViewById(R.id.rv_goals);
        ivBack = view.findViewById(R.id.iv_back); // Đảm bảo đã khai báo ivBack để sử dụng

        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
        });
        // Đảm bảo sử dụng đúng Button trong XML
        btnAddGoal = view.findViewById(R.id.btn_add_goal);  // Correct Button ID
        btnAddGoal.setOnClickListener(v -> {
            // Mở AddGoalFragment khi bấm vào nút
            ((MainActivity) requireActivity()).loadFragment(new AddGoalFragment());
        });

        tvNoGoals = view.findViewById(R.id.tv_no_goals);

        // Set user name
        tvUserName.setText(preferenceManager.getUserName());

        // Setup UI
        setupRecyclerView();
        setupListeners();

        // Load data
        loadGoals();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new GoalAdapter(goals, new GoalAdapter.OnGoalClickListener() {
            @Override
            public void onGoalClick(Goal goal) {
                editGoal(goal);
            }

            @Override
            public void onGoalLongClick(Goal goal) {
                showGoalOptionsDialog(goal);
            }
        });

        rvGoals.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvGoals.setAdapter(adapter);

        // Setup swipe to delete/edit
        setupSwipeActions();
    }

    private void setupSwipeActions() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(requireContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final Goal goal = goals.get(position);

                // Chỉ vuốt trái, xử lý sửa + xóa tại đây
                showGoalOptionsDialog(goal);
                adapter.notifyItemChanged(position); // Để khôi phục swipe
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(rvGoals);
    }

    private void setupListeners() {
        ivMenu.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).openDrawer();
        });
    }

    private void loadGoals() {
        showLoading(true);

        executor.execute(() -> {
            try {
                // Get all goals
                List<Goal> allGoals = goalDao.getAllGoals();

                // Get total income and expense for completion calculation
                double totalIncome = transactionDao.getTotalIncome();
                double totalExpense = transactionDao.getTotalExpense();
                double availableAmount = totalIncome - totalExpense;

                // Calculate completion percentage for each goal
                for (Goal goal : allGoals) {
                    double completionPercentage = Math.min(100, (availableAmount / goal.getTargetAmount()) * 100);
                    goal.setCompletionPercentage(Math.max(0, completionPercentage));
                }

                // Update UI on main thread
                handler.post(() -> {
                    goals.clear();
                    goals.addAll(allGoals);
                    adapter.notifyDataSetChanged();

                    // Show message if no goals
                    if (goals.isEmpty()) {
                        tvNoGoals.setVisibility(View.VISIBLE);
                        rvGoals.setVisibility(View.GONE);
                    } else {
                        tvNoGoals.setVisibility(View.GONE);
                        rvGoals.setVisibility(View.VISIBLE);
                    }

                    showLoading(false);
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    showLoading(false);
                    showError("Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
                });
            }
        });
    }

    private void showGoalOptionsDialog(Goal goal) {
        String[] options = {"Chỉnh sửa", "Xóa"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tùy chọn");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Edit goal
                    editGoal(goal);
                    break;
                case 1:
                    // Delete goal
                    showDeleteConfirmationDialog(goal, goals.indexOf(goal));
                    break;
            }
        });

        builder.show();
    }

    private void editGoal(Goal goal) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);

        AddGoalFragment fragment = new AddGoalFragment();
        fragment.setArguments(bundle);

        ((MainActivity) requireActivity()).loadFragment(fragment);
    }

    private void showDeleteConfirmationDialog(Goal goal, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa mục tiêu này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            deleteGoal(goal, position);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss();
            adapter.notifyItemChanged(position);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteGoal(Goal goal, int position) {
        executor.execute(() -> {
            try {
                goalDao.delete(goal);

                handler.post(() -> {
                    goals.remove(position);
                    adapter.notifyItemRemoved(position);

                    // Show message if no goals
                    if (goals.isEmpty()) {
                        tvNoGoals.setVisibility(View.VISIBLE);
                        rvGoals.setVisibility(View.GONE);
                    }

                    Toast.makeText(requireContext(), "Đã xóa mục tiêu", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                handler.post(() -> {
                    adapter.notifyItemChanged(position);
                    showError("Có lỗi xảy ra khi xóa mục tiêu: " + e.getMessage());
                });
            }
        });
    }

    @Override
    public void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean isLoading) {
        if (loadingView != null) {
            loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload data when returning to this fragment
        loadGoals();
    }
}
