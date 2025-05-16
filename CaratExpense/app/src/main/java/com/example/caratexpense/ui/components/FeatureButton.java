package com.example.caratexpense.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.example.caratexpense.R;

public class FeatureButton extends LinearLayout {
    private CardView cardView;
    private ImageView iconView;
    private TextView titleView;
    private OnClickListener clickListener;

    public FeatureButton(Context context) {
        super(context);
        init(context);
    }

    public FeatureButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FeatureButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.component_feature_button, this, true);
        
        cardView = view.findViewById(R.id.card_view);
        iconView = view.findViewById(R.id.iv_icon);
        titleView = view.findViewById(R.id.tv_title);
        
        cardView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick();
            }
        });
    }
    
    public void setTitle(String title) {
        titleView.setText(title);
    }
    
    public void setIcon(int iconResId) {
        iconView.setImageResource(iconResId);
    }
    
    public void setOnFeatureClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }
    
    public interface OnClickListener {
        void onClick();
    }
}
