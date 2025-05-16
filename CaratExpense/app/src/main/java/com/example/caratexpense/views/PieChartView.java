package com.example.caratexpense.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.caratexpense.models.Category;

import java.util.ArrayList;
import java.util.List;

public class PieChartView extends View {

    private List<PieSlice> slices = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF bounds = new RectF();
    private float centerX, centerY, radius;
    private OnSliceClickListener listener;

    public interface OnSliceClickListener {
        void onSliceClick(int sliceIndex, Category category, double percentage, double amount);
    }

    public static class PieSlice {
        private double percentage;
        private int color;
        private Category category;
        private double amount;

        public PieSlice(double percentage, int color, Category category, double amount) {
            this.percentage = percentage;
            this.color = color;
            this.category = category;
            this.amount = amount;
        }

        public double getPercentage() {
            return percentage;
        }

        public int getColor() {
            return color;
        }

        public Category getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }
    }

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
    }

    public void setSlices(List<PieSlice> slices) {
        this.slices = slices;
        invalidate();
    }

    public void setOnSliceClickListener(OnSliceClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate the bounds of the pie chart
        float padding = 50;
        centerX = w / 2f;
        centerY = h / 2f;
        radius = Math.min(w, h) / 2f - padding;

        bounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (slices.isEmpty()) {
            // Draw empty state
            paint.setColor(Color.LTGRAY);
            canvas.drawCircle(centerX, centerY, radius, paint);

            // Draw center hole
            paint.setColor(Color.WHITE);
            canvas.drawCircle(centerX, centerY, radius * 0.6f, paint);
            return;
        }

        float startAngle = 0;

        for (PieSlice slice : slices) {
            // Draw the slice
            paint.setColor(slice.getColor());
            float sweepAngle = (float) (slice.getPercentage() * 3.6f); // Convert percentage to degrees
            canvas.drawArc(bounds, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }

        // Draw center hole (donut style)
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, radius * 0.6f, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && listener != null) {
            float x = event.getX();
            float y = event.getY();

            // Calculate distance from center
            float dx = x - centerX;
            float dy = y - centerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            // Check if touch is within the pie chart (not in the center hole)
            if (distance <= radius && distance >= radius * 0.6f) {
                // Calculate angle
                double angle = Math.toDegrees(Math.atan2(dy, dx));
                if (angle < 0) {
                    angle += 360;
                }

                // Adjust angle to match the pie chart starting position (top)
                angle = (angle + 90) % 360;

                // Find which slice was touched
                float startAngle = 0;
                for (int i = 0; i < slices.size(); i++) {
                    PieSlice slice = slices.get(i);
                    float sweepAngle = (float) (slice.getPercentage() * 3.6f);

                    if (angle >= startAngle && angle <= startAngle + sweepAngle) {
                        listener.onSliceClick(i, slice.getCategory(), slice.getPercentage(), slice.getAmount());
                        return true;
                    }

                    startAngle += sweepAngle;
                }
            }
        }

        return super.onTouchEvent(event);
    }


}
