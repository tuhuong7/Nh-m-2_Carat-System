package com.example.caratexpense.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.caratexpense.MainActivity;
import com.example.caratexpense.R;
import com.example.caratexpense.utils.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OnboardingFragment extends Fragment {
    private EditText etName;
    private FloatingActionButton btnStart;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        preferenceManager = new PreferenceManager(requireContext());

        etName = view.findViewById(R.id.et_name);
        btnStart = view.findViewById(R.id.btn_confirm);  // btn_confirm là FloatingActionButton

        btnStart.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();

            if (name.isEmpty()) {
                showError("Vui lòng nhập tên của bạn");
                return;
            }

            // Lưu tên và chuyển sang màn hình chính
            preferenceManager.setUserName(name);
            preferenceManager.setFirstTimeLaunch(false);

            ((MainActivity) requireActivity()).loadFragment(new HomeFragment());
        });

        return view;
    }

    private void showError(String message) {
        new android.app.AlertDialog.Builder(requireContext())
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
