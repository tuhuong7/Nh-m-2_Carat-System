package com.example.caratexpense.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CategoryColorManager {
    private static final String PREFS_NAME = "category_colors";
    private static CategoryColorManager instance;

    private final SharedPreferences prefs;
    private final Map<Long, Integer> colorCache = new HashMap<>();
    private final int[] defaultColors = {
            Color.parseColor("#FFA726"), // Orange
            Color.parseColor("#66BB6A"), // Green
            Color.parseColor("#42A5F5"), // Blue
            Color.parseColor("#EC407A"), // Pink
            Color.parseColor("#AB47BC"), // Purple
            Color.parseColor("#26A69A"), // Teal
            Color.parseColor("#FFC107"), // Amber
            Color.parseColor("#5C6BC0"), // Indigo
            Color.parseColor("#EF5350"), // Red
            Color.parseColor("#7E57C2")  // Deep Purple
    };

    private CategoryColorManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized CategoryColorManager getInstance(Context context) {
        if (instance == null) {
            instance = new CategoryColorManager(context.getApplicationContext());
        }
        return instance;
    }

    public int getColorForCategory(long categoryId) {
        // Check cache first
        if (colorCache.containsKey(categoryId)) {
            return colorCache.get(categoryId);
        }

        // Check preferences
        String colorStr = prefs.getString("category_" + categoryId, null);
        if (colorStr != null) {
            try {
                int color = Color.parseColor(colorStr);
                colorCache.put(categoryId, color);
                return color;
            } catch (IllegalArgumentException e) {
                // Invalid color string, will generate a new one
            }
        }

        // Generate a new color
        int color = generateColorForCategory(categoryId);
        saveColorForCategory(categoryId, color);
        return color;
    }

    public void setColorForCategory(long categoryId, int color) {
        saveColorForCategory(categoryId, color);
    }

    private void saveColorForCategory(long categoryId, int color) {
        colorCache.put(categoryId, color);
        prefs.edit().putString("category_" + categoryId, String.format("#%06X", (0xFFFFFF & color))).apply();
    }

    private int generateColorForCategory(long categoryId) {
        // Use category ID to select a color from the default colors
        int index = (int) (categoryId % defaultColors.length);

        // Add some variation to avoid identical colors for sequential IDs
        Random random = new Random(categoryId);
        int variation = random.nextInt(30) - 15; // -15 to +15

        int baseColor = defaultColors[index];

        // Apply variation to each RGB component
        int red = Color.red(baseColor) + variation;
        int green = Color.green(baseColor) + variation;
        int blue = Color.blue(baseColor) + variation;

        // Clamp values to valid range
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return Color.rgb(red, green, blue);
    }
}
