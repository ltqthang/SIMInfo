package com.alpha.siminfo.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.alpha.siminfo.R;

/**
 * Created by I Love Coding on 1/3/2015.
 */
public class CircleButton extends ImageView {
    private final int colorDelta = 25;
    private final float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
    private final float textHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

    private float centerX;
    private float centerY;
    private float smallRadius;
    private float animRadius;
    private float lastRadius;
    private ObjectAnimator anim;

    private Paint paint;
    private Paint paintAnimation;
    private int defColor;
    private int highlyColor;

    private String text;
    private Paint textPaint;


    public CircleButton(Context context) {
        super(context);
        init(context, null);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        setFocusable(true);
        setClickable(true);
        setScaleType(ScaleType.CENTER_INSIDE);

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,  R.styleable.CircleButton);
            try {
                defColor = a.getColor(R.styleable.CircleButton_background_color, Color.GREEN);
                text = a.getString(R.styleable.CircleButton_text);
                highlyColor = highlyColor(defColor, colorDelta);
                paint.setColor(defColor);
            } finally {
                a.recycle();
            }
        }
        paintAnimation = new Paint();
        paintAnimation.setStyle(Paint.Style.FILL_AND_STROKE);
        paintAnimation.setAntiAlias(true);
        paintAnimation.setColor(highlyColor(defColor, 100));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        lastRadius = Math.min(w, h) / 2;
        smallRadius = lastRadius * 0.9f;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            anim = ObjectAnimator.ofFloat(this, "animRadius", smallRadius, lastRadius);
            anim.setDuration(100);
        }
    }

    /**
     * Sets the pressed state for this view.
     *
     * @param pressed Pass true to set the View's internal state to "pressed", or false to reverts
     * @see #isClickable()
     * @see #setClickable(boolean)
     */
    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        paint.setColor(pressed ? highlyColor : defColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (pressed) {
                anim.setFloatValues(animRadius, lastRadius);
            } else {
                anim.setFloatValues(lastRadius, smallRadius);
            }
            anim.start();
        }

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, animRadius, paintAnimation);
        canvas.drawCircle(centerX, centerY, smallRadius, paint);
        if (text != null) {
            float textWidth = textPaint.measureText(text);
            if (textWidth >= centerX * 2 * 0.9) {
                int dividePos = text.indexOf(" ", Math.min(7, text.length() - 1));
                canvas.drawText(text, 0, dividePos , centerX, centerY + textHeight / 2 - textHeight, textPaint);
                canvas.drawText(text, dividePos + 1, text.length(), centerX, centerY + textHeight / 2 + textHeight, textPaint);
            } else {
                canvas.drawText(text, centerX, centerY + textHeight / 2, textPaint);

            }

        }
        super.onDraw(canvas);

    }

    public int highlyColor(int color, int colorDelta) {
        int newR = Color.red(color) + colorDelta;
        int newG = Color.green(color) + colorDelta;
        int newB = Color.blue(color) + colorDelta;
        return Color.argb(255, Math.max(0, Math.min(255, newR)),
                Math.max(0, Math.min(255, newG)),
                Math.max(0, Math.min(255, newB)));
    }

    public float getAnimRadius() {
        return animRadius;
    }

    public void setAnimRadius(float animRadius) {
        this.animRadius = animRadius;
        invalidate();
    }

    public float getLastRadius() {
        return lastRadius;
    }

    public void setLastRadius(float lastRadius) {
        this.lastRadius = lastRadius;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }
}
