package com.example.canteen.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FoodWheelView extends View {

    private final List<String> labels = new ArrayList<>();

    private final Paint segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    private final int[] palette = new int[]{
            0xFFFFE0B2, 0xFFFFCC80, 0xFFFFB74D, 0xFFFFA726,
            0xFFFF9800, 0xFFF57C00, 0xFFFFD180, 0xFFFFE082
    };

    private int highlightIndex = -1;
    private boolean spinning = false;
    private ValueAnimator runningAnimator;

    public FoodWheelView(Context context) {
        super(context);
        init();
    }

    public FoodWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FoodWheelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        segmentPaint.setStyle(Paint.Style.FILL);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(dp(1.2f));
        borderPaint.setColor(0xFFFFF3E0);

        textPaint.setColor(0xFF6D3B00);
        textPaint.setTextSize(sp(14));
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setLabels(@NonNull List<String> newLabels) {
        labels.clear();
        if (newLabels.isEmpty()) {
            labels.add("");
        } else {
            labels.addAll(newLabels);
        }

        if (labels.size() > 12) {
            while (labels.size() > 12) {
                labels.remove(labels.size() - 1);
            }
        }

        highlightIndex = -1;
        invalidate();
    }

    public void setHighlightIndex(int index) {
        if (labels.isEmpty()) return;
        highlightIndex = clamp(index, 0, labels.size() - 1);
        invalidate();
    }

    public void spinToIndex(final int targetIndex, @Nullable final Runnable endAction) {
        if (labels.isEmpty()) {
            if (endAction != null) endAction.run();
            return;
        }

        final int safeTarget = clamp(targetIndex, 0, labels.size() - 1);

        if (runningAnimator != null) {
            runningAnimator.cancel();
            runningAnimator = null;
        }

        spinning = true;

        final int totalSteps = labels.size() * 4 + safeTarget;
        runningAnimator = ValueAnimator.ofInt(0, totalSteps);
        runningAnimator.setDuration(2600L + labels.size() * 80L);
        runningAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        runningAnimator.addUpdateListener(animation -> {
            int step = (Integer) animation.getAnimatedValue();
            highlightIndex = step % labels.size();
            invalidate();
        });

        runningAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                spinning = false;
                highlightIndex = safeTarget;
                invalidate();
                if (endAction != null) endAction.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                spinning = false;
            }
        });

        runningAnimator.start();
    }

    public boolean isSpinning() {
        return spinning;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        float cx = w / 2f;
        float cy = h / 2f;

        float radius = Math.min(w, h) * 0.42f;
        RectF rect = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);

        int count = Math.max(labels.size(), 1);
        float sweep = 360f / count;

        // 外圈底色
        //canvas.drawCircle(cx, cy, radius + dp(4), borderPaint.getColor() == 0 ? 0xFFFFF3E0 : 0xFFFFF3E0, segmentPaint);
        // Expected 4 arguments but found 5
        canvas.drawCircle(cx, cy, radius + dp(4), segmentPaint);

        for (int i = 0; i < count; i++) {
            float start = -90f + i * sweep;

            int baseColor = palette[i % palette.length];
            int color = baseColor;

            if (i == highlightIndex) {
                color = 0xFFFF8F00; // 高亮橙
            }

            segmentPaint.setColor(color);
            canvas.drawArc(rect, start, sweep, true, segmentPaint);

            // 扇形分割线
            canvas.drawArc(rect, start, sweep, true, borderPaint);

            String label = i < labels.size() ? labels.get(i) : "";
            if (label != null && label.length() > 0) {
                drawLabel(canvas, cx, cy, radius, start + sweep / 2f, label);
            }
        }

        // 中心轻微压暗，增强层次
        Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setColor(0x22FFFFFF);
        canvas.drawCircle(cx, cy, radius * 0.08f, centerPaint);
    }

    private void drawLabel(Canvas canvas, float cx, float cy, float radius, float angleDeg, @NonNull String label) {
        double rad = Math.toRadians(angleDeg);
        float textRadius = radius * 0.62f;
        float x = cx + (float) Math.cos(rad) * textRadius;
        float y = cy + (float) Math.sin(rad) * textRadius;

        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float baseline = y - (fm.ascent + fm.descent) / 2f;

        canvas.drawText(label, x, baseline, textPaint);
    }

    private int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}