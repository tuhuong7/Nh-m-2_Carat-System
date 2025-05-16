package com.example.caratexpense.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class CurrencyInputFilter implements InputFilter {
    private final int maxDigits;

    public CurrencyInputFilter() {
        this.maxDigits = 10; // Default max digits
    }

    public CurrencyInputFilter(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Only allow digits
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            char c = source.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }

        // Check max length
        String result = dest.toString().substring(0, dstart) + builder.toString() + dest.toString().substring(dend);
        String digitsOnly = result.replaceAll("[^\\d]", "");

        if (digitsOnly.length() > maxDigits) {
            return "";
        }

        return builder.toString();
    }
}
