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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.adapters.TransactionAdapter;
import com.example.caratexpense.database.AppDatabase;
import com.example.caratexpense.database.dao.TransactionDao;
import com.example.caratexpense.models.Transaction;
import com.example.caratexpense.utils.SwipeToDeleteCallback;
import com.example.caratexpense.models.TransactionWithCategory;

import java.util.ArrayList;
import java.util.List;

public class IncomeExpenseFragment extends BaseFragment {
    private TextView tvUserName;
    private ImageView ivMenu, ivBack;
    private RecyclerView rvTransactions;
    private Button btnAdd;
    private EditText etSearch;
    private TransactionAdapter adapter;
    private List<TransactionWithCategory> transactionList;
    private List<TransactionWithCategory> filteredTransactionList;

    private TransactionDao transactionDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_expense, container, false);

        transactionDao = AppDatabase.getInstance(requireContext()).transactionDao();

        tvUserName = view.findViewById(R.id.tv_user_name);
        ivMenu = view.findViewById(R.id.iv_menu);
        ivBack = view.findViewById(R.id.iv_back);
        rvTransactions = view.findViewById(R.id.rv_transactions);
        btnAdd = view.findViewById(R.id.btn_add);
        etSearch = view.findViewById(R.id.et_search);

        tvUserName.setText(preferenceManager.getUserName());

        ivBack.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
        });

        setupRecyclerView();
        setupSearch();

        btnAdd.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new AddTransactionFragment());
        });

        loadTransactions();

        return view;
    }

    private void setupRecyclerView() {
        transactionList = new ArrayList<>();
        filteredTransactionList = new ArrayList<>();
        adapter = new TransactionAdapter(filteredTransactionList, transactionWithCategory -> {
            Transaction t = transactionWithCategory.getTransaction();
            t.setCategory(transactionWithCategory.getCategory());

            Bundle bundle = new Bundle();
            bundle.putSerializable("transaction", t);

            AddTransactionFragment fragment = new AddTransactionFragment();
            fragment.setArguments(bundle);

            ((MainActivity) requireActivity()).loadFragment(fragment);
        });


        rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvTransactions.setAdapter(adapter);

        SwipeToDeleteCallback swipeHandler = new SwipeToDeleteCallback(requireContext(), position -> {
            TransactionWithCategory twc = filteredTransactionList.get(position);
            showEditDeleteDialog(twc.getTransaction());

        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(rvTransactions);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterTransactions(s.toString());
            }
        });
    }

    private void filterTransactions(String query) {
        filteredTransactionList.clear();

        if (query.isEmpty()) {
            filteredTransactionList.addAll(transactionList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (TransactionWithCategory twc : transactionList) {
                String categoryName = twc.getCategory().getName().toLowerCase();
                String note = twc.getTransaction().getNote().toLowerCase();
                if (categoryName.contains(lowerCaseQuery) || note.contains(lowerCaseQuery)) {
                    filteredTransactionList.add(twc);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }


    private void showEditDeleteDialog(Transaction transaction) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_delete, null);
        builder.setView(dialogView);

        Button btnEdit = dialogView.findViewById(R.id.btn_edit);
        Button btnDelete = dialogView.findViewById(R.id.btn_delete);

        android.app.AlertDialog dialog = builder.create();

        btnEdit.setOnClickListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putSerializable("transaction", transaction);
            AddTransactionFragment fragment = new AddTransactionFragment();
            fragment.setArguments(bundle);
            ((MainActivity) requireActivity()).loadFragment(fragment);
        });

        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            new Thread(() -> {
                transactionDao.delete(transaction);
                requireActivity().runOnUiThread(() -> {
                    loadTransactions();
                    showSuccessDialog("Xóa thành công");
                });
            }).start();
        });

        dialog.show();
    }

    private void loadTransactions() {
        new Thread(() -> {
            List<TransactionWithCategory> transactionsWithCategory = transactionDao.getAllTransactionsWithCategory();

            requireActivity().runOnUiThread(() -> {
                transactionList.clear();
                transactionList.addAll(transactionsWithCategory);
                filteredTransactionList.clear();
                filteredTransactionList.addAll(transactionsWithCategory);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        loadTransactions();
    }
}
