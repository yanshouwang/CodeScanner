package dev.yanshouwang.codescanner.views;

import android.animation.Animator;
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
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class ScannerView extends BaseScannerView {
    private static final String TAG = ScannerView.class.getSimpleName();

    private Path mMesh; // 网格
    private RectF mBorder; // 作图边界
    private int mMeshMinorCount;
    private Paint mBorderFillPaint;
    private int[] mBorderFillColors;
    private Paint mMeshStrokePaint;
    private int[] mMeshStrokeColors;
    private float[] mPositions;
    private float mXCursor;
    private float mYCursor;
    private ValueAnimator mCursorAnimator;
    private boolean mIsAutoReverse;
    private int mCurrentCount;

    //region 构造
    public ScannerView(Context context) {
        super(context);
        init();
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ScannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    //endregion

    private void init() {
        this.mMesh = new Path();
        this.mBorder = new RectF();
        this.mMeshMinorCount = 20;
        this.mBorderFillColors = new int[]{Color.TRANSPARENT, Color.argb(100, 255, 0, 0)};
        this.mMeshStrokeColors = new int[]{Color.TRANSPARENT, Color.argb(255, 255, 0, 0)};
        this.mPositions = new float[]{0.5F, 1F};
        this.mBorderFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBorderFillPaint.setStyle(Paint.Style.FILL);
        this.mMeshStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mMeshStrokePaint.setStyle(Paint.Style.STROKE);
        this.mMeshStrokePaint.setStrokeWidth(1F);
        this.mIsAutoReverse = false;
        TimeInterpolator interpolator = new LinearInterpolator();
        this.mCursorAnimator = new ValueAnimator();
        this.mCursorAnimator.setDuration(3000L);
        int repeatMode = this.mIsAutoReverse ? ValueAnimator.REVERSE : ValueAnimator.RESTART;
        this.mCursorAnimator.setRepeatMode(repeatMode);
        this.mCursorAnimator.setInterpolator(interpolator);
        this.mCursorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        this.mCursorAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mCurrentCount = 0;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                mCurrentCount++;
            }
        });
        this.mCursorAnimator.addUpdateListener(animation -> {
            mYCursor = (float) animation.getAnimatedValue();
            invalidate();
        });
    }

    private void updatePaints(float left, float top, float right, float bottom) {
        float width = right - left;
        float height = bottom - top;
        // 根据宽高计算着色器尺寸
        float x0, y0, x1, y1;
        x0 = 0;
        y0 = top;
        x1 = 0;
        y1 = bottom;
        // TODO: 此处未实现复用
        Shader borderShader = new LinearGradient(x0, y0, x1, y1, this.mBorderFillColors, this.mPositions, Shader.TileMode.CLAMP);
        this.mBorderFillPaint.setShader(borderShader);
        Shader meshShader = new LinearGradient(x0, y0, x1, y1, this.mMeshStrokeColors, this.mPositions, Shader.TileMode.CLAMP);
        this.mMeshStrokePaint.setShader(meshShader);
    }

    private void updateShapes(float left, float top, float right, float bottom) {
        if (left == right || top == bottom || this.mMeshMinorCount <= 0) {
            return;
        }
        // 更新 Border
        this.mBorder.left = left;
        this.mBorder.top = top;
        this.mBorder.right = right;
        this.mBorder.bottom = bottom;
        // 更新 Mesh
        this.mMesh.reset();
        float width = Math.abs(right - left);
        float height = Math.abs(bottom - top);
        // 根据宽高计算网格尺寸
        float offset = width < height
                ? width / this.mMeshMinorCount
                : height / this.mMeshMinorCount;
        // Columns 列, 从右向左画线, 保证可见边缘均有线
        float x = right;
        while (x >= 0) {
            this.mMesh.moveTo(x, top);
            this.mMesh.lineTo(x, bottom);
            x -= offset;
        }
        // Rows 行, 自下而上画线, 保证可见边缘均有线
        float y = bottom;
        while (y >= 0) {
            this.mMesh.moveTo(left, y);
            this.mMesh.lineTo(right, y);
            y -= offset;
        }
    }

    private void updateAnimation(float left, float top, float right, float bottom) {
        // 重置动画状态
        this.mXCursor = 0;
        this.mYCursor = 0;
        this.mCursorAnimator.cancel();
        float height = bottom - top;
        float from = top - height;
        float to = top + height;
        mCursorAnimator.setFloatValues(from, to);
        mCursorAnimator.start();
    }

    @Override
    protected void coreLayout(float left, float top, float right, float bottom) {
        updatePaints(left, top, right, bottom);
        updateShapes(left, top, right, bottom);
        updateAnimation(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //region 1. 画当前网格
        float dx1 = this.mXCursor;
        float dy1 = this.mYCursor;
        // 临时剪裁并平移画布
        canvas.save();
        canvas.clipRect(this.mBorder);
        canvas.translate(dx1, dy1);
        // 画渐变填充
        canvas.drawRect(this.mBorder, this.mBorderFillPaint);
        // 画扫描线
        canvas.drawPath(this.mMesh, this.mMeshStrokePaint);
        // 恢复画布
        canvas.restore();
        //endregion
        //region 2. 若上方有空余, 补画网格
        float height = this.mBorder.height();
        float dy2 = dy1 - height;
        if (dy1 > this.mBorder.top && dy1 < this.mBorder.bottom) {
            // 临时剪裁并平移画布
            canvas.save();
            canvas.clipRect(this.mBorder);
            canvas.translate(dx1, dy2);
            // 画渐变填充
            canvas.drawRect(this.mBorder, this.mBorderFillPaint);
            // 画扫描线
            canvas.drawPath(this.mMesh, this.mMeshStrokePaint);
            // 恢复画布
            canvas.restore();
        }
        //endregion
        //region 3. 若非第一次循环且下方有空余, 补画网格
        float dy3 = dy1 + height;
        if (mCurrentCount > 0 && dy3 > this.mBorder.top && dy3 < this.mBorder.bottom) {
            // 临时剪裁并平移画布
            canvas.save();
            canvas.clipRect(this.mBorder);
            canvas.translate(dx1, dy3);
            // 画渐变填充
            canvas.drawRect(this.mBorder, this.mBorderFillPaint);
            // 画扫描线
            canvas.drawPath(this.mMesh, this.mMeshStrokePaint);
            // 恢复画布
            canvas.restore();
        }
        //endregion
    }
}
