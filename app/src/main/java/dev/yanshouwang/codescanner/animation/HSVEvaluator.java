package dev.yanshouwang.codescanner.animation;

import android.animation.TypeEvaluator;
import android.graphics.Color;

public class HSVEvaluator implements TypeEvaluator<Integer> {
    private int mStartA;
    private int mEndA;
    private int mDifA;
    private int mOutA;
    private float[] mStartHSV;
    private float[] mEndHSV;
    private float mDifH;
    private float mDifS;
    private float mDifV;
    private float[] outHSV;

    public HSVEvaluator() {
        this.mStartA = 0;
        this.mEndA = 0;
        this.mDifA = 0;
        this.mOutA = 0;
        this.mStartHSV = new float[3];
        this.mEndHSV = new float[3];
        this.mDifH = 0F;
        this.mDifS = 0F;
        this.mDifV = 0F;
        this.outHSV = new float[3];
    }

    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        // 将 ARGB 转换为 HSV
        Color.colorToHSV(startValue, this.mStartHSV);
        Color.colorToHSV(endValue, this.mEndHSV);
        // 计算当前动画完成度(fraction)所对应的透明度
        this.mStartA = startValue >> 24;
        this.mEndA = endValue >> 24;
        this.mDifA = this.mEndA - this.mStartA;
        this.mOutA = this.mStartA + (int) (mDifA * fraction);
        // 计算当前动画完成度(fraction)所对应的颜色值
        this.mDifH = this.mEndHSV[0] - this.mStartHSV[0];
        this.mDifS = this.mEndHSV[1] - this.mStartHSV[1];
        this.mDifV = this.mEndHSV[2] - this.mStartHSV[2];
        if (this.mDifH > 180F) {
            this.mDifH -= 360F;
        } else if (this.mDifH < -180F) {
            this.mDifH += 360F;
        }
        this.outHSV[0] = this.mStartHSV[0] + this.mDifH * fraction;
        if (this.outHSV[0] > 360F) {
            this.outHSV[0] -= 360F;
        } else if (this.outHSV[0] < 0F) {
            this.outHSV[0] += 360F;
        }
        this.outHSV[1] = this.mStartHSV[1] + this.mDifS * fraction;
        this.outHSV[2] = this.mStartHSV[2] + this.mDifV * fraction;
        // 将 HSV 转换为 ARGB 并返回
        return Color.HSVToColor(this.mOutA, this.outHSV);
    }
}
