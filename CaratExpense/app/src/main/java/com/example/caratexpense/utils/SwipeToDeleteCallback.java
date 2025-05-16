package com.example.caratexpense.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caratexpense.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private final SwipeActionCallback callback;
    private final Drawable icon;
    private final ColorDrawable background;

    public interface SwipeActionCallback {
        void onSwiped(int position);
    }

    // ✅ Constructor với Callback
    public SwipeToDeleteCallback(Context context, SwipeActionCallback callback) {
        super(0, ItemTouchHelper.LEFT);
        this.callback = callback;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_edit);
        background = new ColorDrawable(Color.parseColor("#8A6BC1"));
    }

    // ✅ Constructor KHÔNG callback, để override trực tiếp trong anonymous class
    public SwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT);
        this.callback = null;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_edit);
        background = new ColorDrawable(Color.parseColor("#8A6BC1"));
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (callback != null) {
            int position = viewHolder.getAdapterPosition();
            callback.onSwiped(position);
        }
        // Nếu không dùng callback thì để cho subclass override
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;

        if (c.getClipBounds() != null) {
            int restoreCount = c.save();

            // Vẽ nền tím
            background.setBounds(
                    itemView.getRight() + (int) dX,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom()
            );
            background.draw(c);

            // Vẽ icon
            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
            int iconTop = itemView.getTop() + iconMargin;
            int iconBottom = iconTop + icon.getIntrinsicHeight();
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.draw(c);

            c.restoreToCount(restoreCount);
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
