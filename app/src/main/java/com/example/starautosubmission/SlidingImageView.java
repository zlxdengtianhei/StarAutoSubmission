package com.example.starautosubmission;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

public class SlidingImageView extends AppCompatImageView {

    private Bitmap bitmap;
    private Paint paint = new Paint();
    private float translateX = 0f;
    private ValueAnimator animator;

    public SlidingImageView(Context context) {
        super(context);
        init();
    }

    public SlidingImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
    }

    public void loadImage(String url) {
        Glide.with(getContext())
                .asBitmap()
                .load(url)
                .into(new BitmapImageViewTarget(this) {
                    @Override
                    public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        bitmap = resource;
                        startSliding();
                    }
                });
    }

    private void startSliding() {
        if (bitmap == null) return;

        int imageWidth = bitmap.getWidth();
        int viewWidth = getWidth();

        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        animator = ValueAnimator.ofFloat(0, imageWidth - viewWidth);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        animator.addUpdateListener(animation -> {
            translateX = (float) animation.getAnimatedValue();
            invalidate();
        });

        postDelayed(() -> animator.start(), 1000); // Wait for 1 second before starting the animation
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, -translateX, 0, paint);
        }
    }

    public void loadDefaultImage(int resourceId) {
        Glide.with(getContext())
                .asBitmap()
                .load(resourceId)
                .into(new BitmapImageViewTarget(this) {
                    @Override
                    public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                        bitmap = resource;
                        startSliding();
                    }
                });
    }

}
