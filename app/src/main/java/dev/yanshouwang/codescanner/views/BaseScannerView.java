package dev.yanshouwang.codescanner.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public abstract class BaseScannerView extends View {
    private float mCorePaddingFactor;
    private Path mBorder;
    private Paint mBorderPaint;

    //region 构造
    public BaseScannerView(Context context) {
        super(context);
        this.init();
    }

    public BaseScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public BaseScannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    public BaseScannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }
    //endregion

    private void init() {
        this.mCorePaddingFactor = 0F;
        this.mBorder = new Path();
        this.mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setStrokeWidth(12F);
        this.mBorderPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!changed) {
            return;
        }
        int width = this.getWidth();
        int height = this.getHeight();
        updateBorder(width, height);
        float l = width < height
                ? width * this.mCorePaddingFactor
                : height * this.mCorePaddingFactor;
        float r = width - l;
        float b = height - l;
        this.coreLayout(l, l, r, b);
    }

    private void updateBorder(float width, float height) {
        float length = width < height ? width / 6F : height / 6F;
        // 左上角
        this.mBorder.moveTo(0F, length);
        this.mBorder.lineTo(0F, 0F);
        this.mBorder.lineTo(length, 0F);
        // 右上角
        this.mBorder.moveTo(width - length, 0);
        this.mBorder.lineTo(width, 0F);
        this.mBorder.lineTo(width, length);
        // 右下角
        this.mBorder.moveTo(width, height - length);
        this.mBorder.lineTo(width, height);
        this.mBorder.lineTo(width - length, height);
        // 左下角
        this.mBorder.moveTo(length, height);
        this.mBorder.lineTo(0F, height);
        this.mBorder.lineTo(0F, height - length);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        // 画边框
        canvas.drawPath(mBorder, mBorderPaint);
    }

    protected abstract void coreLayout(float left, float top, float right, float bottom);
}
