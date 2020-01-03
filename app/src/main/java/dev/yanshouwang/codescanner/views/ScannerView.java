package dev.yanshouwang.codescanner.views;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class ScannerView extends View {
    private static final String TAG = ScannerView.class.getSimpleName();

    private Path mMeshGrid; // 网格
    private RectF mMeshBoard;
    private int mGridColumnCount;
    private int mGridRowCount;
    private Paint mGridFillPaint;
    private int mGridFillColor;
    private Shader mGridFillShader;
    private Paint mGridStrokePaint;
    private int mGridStrokeColor;
    private Shader mGridStrokeShader;
    private int mGridStrokeWidth;
    private float mMeshScale;
    private float mMeshLeft;
    private float mMeshRight;
    private float mMeshTop;
    private float mMeshBottom;
    private float mOffset;
    private ValueAnimator mGridAnimator;

    //region 构造
    public ScannerView(Context context) {
        super(context);
        initialize();
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }
    //endregion

    private void initialize() {
        this.mGridStrokeWidth = 1;
        this.mGridColumnCount = 20;
        this.mGridRowCount = 20;
        this.mOffset = 40F;
        this.mGridFillColor = Color.argb(100, 0, 255, 0);
        this.mGridStrokeColor = Color.GREEN;
        this.mMeshScale = 0.9F;
    }

    private void initGridFillPaint(float x0, float y0, float x1, float y1) {
        this.mGridFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mGridFillPaint.setStyle(Paint.Style.FILL);
        int[] colors = new int[]{Color.TRANSPARENT, this.mGridFillColor};
        float[] positions = new float[]{0.5F, 1F};
        this.mGridFillShader = new LinearGradient(x0, y0, x1, y1, colors, positions, Shader.TileMode.CLAMP);
        this.mGridFillPaint.setShader(this.mGridFillShader);
    }

    private void initGridStrokePaint(float x0, float y0, float x1, float y1) {
        this.mGridStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mGridStrokePaint.setStyle(Paint.Style.STROKE);
        this.mGridStrokePaint.setStrokeWidth(this.mGridStrokeWidth);
        int[] colors = new int[]{Color.TRANSPARENT, this.mGridStrokeColor};
        float[] positions = new float[]{0.5F, 1F};
        this.mGridStrokeShader = new LinearGradient(x0, y0, x1, y1, colors, positions, Shader.TileMode.CLAMP);
        this.mGridStrokePaint.setShader(this.mGridStrokeShader);
    }

    private void initGridPath(float left, float top, float width, float height) {
        this.mMeshGrid = new Path();
        float xUnit = width / this.mGridColumnCount;
        float yUnit = height / this.mGridRowCount;
        // Columns 列
        for (int i = 0; i <= this.mGridColumnCount; i++) {
            float x = left + i * xUnit;
            float bottom = top + height;
            this.mMeshGrid.moveTo(x, top);
            this.mMeshGrid.lineTo(x, bottom);
        }
        // Rows 行
        for (int i = 0; i <= this.mGridRowCount; i++) {
            float y = top + i * yUnit;
            float right = left + width;
            this.mMeshGrid.moveTo(left, y);
            this.mMeshGrid.lineTo(right, y);
        }
    }

    private void initMeshBoard(float left, float top, float width, float height) {
        float right = left + width;
        float bottom = top + height;
        this.mMeshBoard = new RectF(left, top, right, bottom);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int measuredWidth = this.getMeasuredWidth();
        int measuredHeight = this.getMeasuredHeight();
        float meshWidth = measuredWidth * this.mMeshScale;
        float meshHeight = measuredHeight * this.mMeshScale;
        this.mMeshLeft = (measuredWidth - meshWidth) / 2F;
        this.mMeshTop = (measuredHeight - meshHeight) / 2F;
        this.mMeshRight = measuredWidth - this.mMeshLeft;
        this.mMeshBottom = measuredHeight - this.mMeshTop;
        initGridFillPaint(0, this.mMeshTop, 0, this.mMeshBottom);
        initGridStrokePaint(0, this.mMeshTop, 0, this.mMeshBottom);
        initMeshBoard(this.mMeshLeft, this.mMeshTop, meshWidth, meshHeight);
        initGridPath(this.mMeshLeft, this.mMeshTop, meshWidth, meshHeight);
        initAnimation(this.mMeshTop - meshHeight, this.mMeshTop + meshHeight * 0.5F);
    }

    private void initAnimation(float... values) {
        this.mGridAnimator = new ValueAnimator();
        mGridAnimator.setDuration(2000L);
        mGridAnimator.setFloatValues(values);
        mGridAnimator.setRepeatMode(ValueAnimator.RESTART);
        TimeInterpolator interpolator = new LinearInterpolator();
        mGridAnimator.setInterpolator(interpolator);
        mGridAnimator.setRepeatCount(Integer.MAX_VALUE);
        mGridAnimator.addUpdateListener(animation -> {
            mOffset = (float) animation.getAnimatedValue();
            invalidate();
        });
        mGridAnimator.start();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // 画边框
        // 临时剪裁并平移画布
        canvas.save();
        canvas.clipRect(this.mMeshLeft, this.mMeshTop, this.mMeshRight, this.mMeshBottom);
        canvas.translate(0, this.mOffset);
        // 画渐变填充
        canvas.drawRect(this.mMeshBoard, this.mGridFillPaint);
        // 画扫描线
        canvas.drawPath(this.mMeshGrid, this.mGridStrokePaint);
        // 恢复画布
        canvas.restore();
    }
}
