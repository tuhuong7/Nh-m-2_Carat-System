package com.example.caratexpense.utils;

import android.content.Context;
import android.text.TextUtils;  // Thêm thư viện TextUtils

import com.example.caratexpense.R;

public class IconUtils {
    public static int getIconResourceId(Context context, String iconName) {
        // Kiểm tra xem iconName có null hoặc rỗng không
        if (TextUtils.isEmpty(iconName)) {
            return R.drawable.ic_default;  // Trả về biểu tượng mặc định nếu iconName null hoặc rỗng
        }

        // Nếu không rỗng, tiếp tục với việc so khớp tên biểu tượng
        switch (iconName) {
            case "ic_shopping":
                return R.drawable.ic_shopping;
            case "ic_food":
                return R.drawable.ic_food;
            case "ic_phone":
                return R.drawable.ic_phone;
            case "ic_entertainment":
                return R.drawable.ic_entertainment;
            case "ic_clothes":
                return R.drawable.ic_clothes;
            case "ic_travel":
                return R.drawable.ic_travel;
            case "ic_health":
                return R.drawable.ic_health;
            case "ic_salary":
                return R.drawable.ic_salary;
            case "ic_allowance":
                return R.drawable.ic_allowance;
            case "ic_investment":
                return R.drawable.ic_investment;
            case "ic_bonus":
                return R.drawable.ic_bonus;
            default:
                return R.drawable.ic_default;  // Trả về biểu tượng mặc định nếu không tìm thấy match
        }
    }
}
