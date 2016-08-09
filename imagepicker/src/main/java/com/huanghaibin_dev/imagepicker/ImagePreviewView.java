package com.huanghaibin_dev.imagepicker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

/**
 * 支持图片预览, 放大,缩小,位置自适应,双击放大缩小
 * Created by thanatosx on 16/5/3.
 */
public class ImagePreviewView extends ImageView {

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mFlatDetector;
    private float scale = 1.f;
    private static final float mMaxScale = 4.f;
    private static final float mMinScale = 0.4f;
    private float translateLeft = 0.f;
    private float translateTop = 0.f;
    private int mBoundWidth = 0;
    private int mBoundHeight = 0;
    private boolean isTranslating = false;
    private boolean isScaling = false;
    private boolean isAutoScale = false;
    private ValueAnimator resetScaleAnimator;
    private ValueAnimator resetXAnimator;
    private ValueAnimator resetYAnimator;
    private ValueAnimator.AnimatorUpdateListener onScaleAnimationUpdate;
    private ValueAnimator.AnimatorUpdateListener onTranslateXAnimationUpdate;
    private ValueAnimator.AnimatorUpdateListener onTranslateYAnimationUpdate;
    
    private OnTouchBorderListener onTouchBorderListener;
    
    public interface OnTouchBorderListener{
        void onBorder(boolean isBorder);
    }
    
    public void setOnTouchBorderListener(OnTouchBorderListener l){
        this.onTouchBorderListener = l;
    }


    public ImagePreviewView(Context context) {
        super(context);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mFlatDetector = new GestureDetector(getContext(), new FlatGestureListener());
    }

    public ImagePreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mFlatDetector = new GestureDetector(getContext(), new FlatGestureListener());
    }

    public ImagePreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        mFlatDetector = new GestureDetector(getContext(), new FlatGestureListener());
    }

    /**
     * 重置伸缩动画的监听器
     * @return
     */
    public ValueAnimator.AnimatorUpdateListener getOnScaleAnimationUpdate() {
        if (onScaleAnimationUpdate != null) return onScaleAnimationUpdate;
        onScaleAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scale = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onScaleAnimationUpdate;
    }

    /**
     * 重置水平动画的监听器
     * @return
     */
    public ValueAnimator.AnimatorUpdateListener getOnTranslateXAnimationUpdate() {
        if (onTranslateXAnimationUpdate != null) return onTranslateXAnimationUpdate;
        onTranslateXAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translateLeft = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onTranslateXAnimationUpdate;
    }

    /**
     * 重置垂直动画的监听器
     * @return
     */
    public ValueAnimator.AnimatorUpdateListener getOnTranslateYAnimationUpdate() {
        if (onTranslateYAnimationUpdate != null) return onTranslateYAnimationUpdate;
        onTranslateYAnimationUpdate = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translateTop = (float) animation.getAnimatedValue();
                invalidate();
            }
        };
        return onTranslateYAnimationUpdate;
    }

    /**
     * 重置伸缩
     *
     * @return the animator control scale value
     */
    private ValueAnimator getResetScaleAnimator() {
        if (resetScaleAnimator != null) {
            resetScaleAnimator.removeAllUpdateListeners();
            return resetScaleAnimator;
        }
        resetScaleAnimator = ValueAnimator.ofFloat();
        resetScaleAnimator.setDuration(150);
        resetScaleAnimator.setInterpolator(new AccelerateInterpolator());
        resetScaleAnimator.setEvaluator(new FloatEvaluator());
        resetScaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        return resetScaleAnimator;
    }

    private void cancelResetScaleAnimation() {
        if (resetScaleAnimator == null || !resetScaleAnimator.isRunning()) return;
        resetScaleAnimator.cancel();
    }

    /**
     * 水平方向的重置动画
     *
     * @return
     */
    private ValueAnimator getResetXAnimator() {
        if (resetXAnimator != null) {
            resetXAnimator.removeAllUpdateListeners();
            return resetXAnimator;
        }
        resetXAnimator = ValueAnimator.ofFloat();
        resetXAnimator.setDuration(150);
        resetXAnimator.setInterpolator(new AccelerateInterpolator());
        resetXAnimator.setEvaluator(new FloatEvaluator());
        return resetXAnimator;
    }

    private void cancelResetFlatXAnimation() {
        if (resetXAnimator == null || !resetXAnimator.isRunning()) return;
        resetXAnimator.cancel();
    }

    /**
     * 垂直方向的重置动画
     *
     * @return
     */
    private ValueAnimator getResetYAnimator() {
        if (resetYAnimator != null) {
            resetYAnimator.removeAllUpdateListeners();
            return resetYAnimator;
        }
        resetYAnimator = ValueAnimator.ofFloat();
        resetYAnimator.setDuration(150);
        resetYAnimator.setInterpolator(new AccelerateInterpolator());
        resetYAnimator.setEvaluator(new FloatEvaluator());
        return resetYAnimator;
    }

    private void cancelResetFlatYAnimation() {
        if (resetYAnimator == null || !resetYAnimator.isRunning()) return;
        resetYAnimator.cancel();
    }

    /**
     * @return 如果是正数, 左边有空隙, 如果是负数, 右边有空隙, 如果是0, 代表两边都没有空隙
     */
    private float getDiffX() {
        final float mScaledWidth = mBoundWidth * scale;
        return translateLeft >= 0
                ? translateLeft
                : getWidth() - translateLeft - mScaledWidth > 0
                ? -(getWidth() - translateLeft - mScaledWidth)
                : 0;
    }

    /**
     * @return 如果是正数, 上面有空隙, 如果是负数, 下面有空隙, 如果是0, 代表两边都没有空隙
     */
    private float getDiffY() {
        final float mScaledHeight = mBoundHeight * scale;
        return translateTop >= 0
                ? translateTop
                : getHeight() - translateTop - mScaledHeight > 0
                ? -(getHeight() - translateTop - mScaledHeight)
                : 0;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        // 清理动画
        if (action == MotionEvent.ACTION_DOWN){
            if (onTouchBorderListener != null){
                onTouchBorderListener.onBorder(scale == 1.0f);
            }
            if (getResetScaleAnimator().isRunning())
                cancelResetScaleAnimation();
            if (getResetXAnimator().isRunning())
                cancelResetFlatXAnimation();
            if (getResetYAnimator().isRunning())
                cancelResetFlatYAnimation();
        }

        final boolean translated = mFlatDetector.onTouchEvent(event);
        final boolean scaled = mScaleDetector.onTouchEvent(event);

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
            if (isAutoScale){
                isAutoScale = false;
            }else{
                if (isTranslating) isTranslating = false;
                if (scale < 1) {
                    ValueAnimator animator = getResetScaleAnimator();
                    animator.setFloatValues(scale, 1.f);
                    animator.addUpdateListener(getOnScaleAnimationUpdate());
                    animator.start();
                }
                final float mScaledWidth = mBoundWidth * scale;
                final float mScaledHeight = mBoundHeight * scale;

                final float mDiffX = getDiffX();
                final float mDiffY = getDiffY();

                // 左右边界重置
                if (mScaledWidth >= getWidth() && mDiffX != 0) {
                    ValueAnimator animator = getResetXAnimator();
                    animator.setFloatValues(translateLeft, translateLeft - mDiffX);
                    animator.addUpdateListener(getOnTranslateXAnimationUpdate());
                    animator.start();
                }

                // 上下边界重置
                if (mScaledHeight >= getHeight() && mDiffY != 0) {
                    ValueAnimator animator = getResetYAnimator();
                    animator.setFloatValues(translateTop, translateTop - mDiffY);
                    animator.addUpdateListener(getOnTranslateYAnimationUpdate());
                    animator.start();
                }

                // 重置到中间位置
                if (mScaledWidth < getWidth() && mScaledHeight >= getHeight() && mDiffX != 0) {
                    ValueAnimator animator = getResetXAnimator();
                    animator.setFloatValues(translateLeft, (getWidth() - mScaledWidth) / 2.f);
                    animator.addUpdateListener(getOnTranslateXAnimationUpdate());
                    animator.start();
                }

                // 重置到中间位置
                if (mScaledHeight < getHeight() && mScaledWidth >= getWidth() && mDiffY != 0) {
                    ValueAnimator animator = getResetYAnimator();
                    animator.setFloatValues(translateTop, (getHeight() - mScaledHeight) / 2.f);
                    animator.addUpdateListener(getOnTranslateYAnimationUpdate());
                    animator.start();
                }

                if (mScaledWidth < getWidth() && mScaledHeight < getHeight()) {
                    resetDefaultState();
                }
            }

        }
        return scaled || translated;
    }

    private void resetDefaultState(){
        final float midWidth = (getWidth() - mBoundWidth) / 2;
        final float midHeight = (getHeight() - mBoundHeight) / 2;
        if (midWidth != translateLeft){
            ValueAnimator mTranslateXAnimator = getResetXAnimator();
            mTranslateXAnimator.setFloatValues(translateLeft, midWidth);
            mTranslateXAnimator.addUpdateListener(getOnTranslateXAnimationUpdate());
            mTranslateXAnimator.start();
        }

        if (midHeight != translateTop){
            ValueAnimator mTranslateYAnimator = getResetYAnimator();
            mTranslateYAnimator.setFloatValues(translateTop, midHeight);
            mTranslateYAnimator.addUpdateListener(getOnTranslateYAnimationUpdate());
            mTranslateYAnimator.start();
        }

    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean change = super.setFrame(l, t, r, b);

        Drawable drawable = getDrawable();
        if (drawable == null) return false;

        int width = getWidth();
        int height = getHeight();

        mBoundWidth = drawable.getBounds().width();
        mBoundHeight = drawable.getBounds().height();

        float scale = Math.max((float) mBoundWidth / width, (float) mBoundHeight / height);

        mBoundHeight /= scale;
        mBoundWidth /= scale;

        drawable.setBounds(0, 0, mBoundWidth, mBoundHeight);

        translateLeft = (width - mBoundWidth) / 2;
        translateTop = (height - mBoundHeight) / 2;

        return change;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        translateLeft = (w - mBoundWidth) / 2;
        translateTop = (h - mBoundHeight) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable mDrawable = getDrawable();
        if (mDrawable == null) return;

        final int mDrawableWidth = mDrawable.getIntrinsicWidth();
        final int mDrawableHeight = mDrawable.getIntrinsicHeight();

        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            return;     // nothing to draw (empty bounds)
        }

        int saveCount = canvas.getSaveCount();
        canvas.save();

        canvas.translate(translateLeft, translateTop);
        canvas.scale(scale, scale);

        // 如果先scale,再translate,那么,真实translate的值是要与scale值相乘的
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        /**
         * factor = detector.getCurrentSpan() / detector.getPreviousSpan()
         * @param detector
         * @return
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            final float mOldScaledWidth = mBoundWidth * scale;
            final float mOldScaledHeight = mBoundHeight * scale;

            if (mOldScaledWidth > getWidth() && getDiffX() != 0 ||
                    (mOldScaledHeight > getHeight() && getDiffY() != 0)) return false;

            isScaling = true;
            float factor = detector.getScaleFactor();
            float value = scale;
            value += (factor - 1) * 2;
            if (value == scale) return true;
            if (value <= mMinScale) return isScaling = false;
            if (value > mMaxScale) return isScaling = false;
            scale = value;
            final float mScaledWidth = mBoundWidth * scale;
            final float mScaledHeight = mBoundHeight * scale;

            // 走了些弯路, 不应该带入translateX计算, 因为二次放大之后计算就不正确了,它应该受scale的制约
            translateLeft = getWidth() / 2.f - (getWidth() / 2.f - translateLeft) * mScaledWidth / mOldScaledWidth;
            translateTop = getHeight() / 2.f - (getHeight() / 2.f - translateTop) * mScaledHeight / mOldScaledHeight;

            final float diffX = getDiffX();
            final float diffY = getDiffY();

            // 考虑宽图, 如果缩小的时候图片左边界到了屏幕左边界,停留在左边界缩小
            if (diffX > 0 && mScaledWidth > getWidth()){
                translateLeft = 0;
            }
            // 右边界问题
            if (diffX < 0 && mScaledWidth > getWidth()){
                translateLeft = getWidth() - mScaledWidth;
            }

            // 考虑到长图,上边界问题
            if (diffY > 0 && mScaledHeight > getHeight()){
                translateTop = 0;
            }

            // 下边界问题
            if (diffY < 0 && mScaledHeight > getHeight()){
                translateTop = getHeight() - mScaledHeight;
            }

            invalidate();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            isScaling = false;
        }
    }

    private class FlatGestureListener extends GestureDetector.SimpleOnGestureListener {
        /**
         * @param e1        horizontal event
         * @param e2        vertical event
         * @param distanceX previous X - current X, toward left , is position
         * @param distanceY previous Y - current Y, toward up, is position
         * @return
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isScaling) return false;
            isTranslating = true;

            final float mScaledWidth = mBoundWidth * scale;
            final float mScaledHeight = mBoundHeight * scale;

            boolean change = false;

            if (mScaledHeight > getHeight()) {
                if (getDiffY() != 0) {
                    final float disY = (float) (Math.acos(Math.abs(getDiffY()) / getHeight() * 6) * distanceY);
                    if (disY == disY) translateTop -= disY; // float 低值溢出变Nan数值
                } else {
                    translateTop -= distanceY * 1.5;
                }
                change = true;
            }

            if (mScaledWidth > getWidth()) {
                if (getDiffX() != 0) {
                    final float disX = (float) (Math.acos(Math.abs(getDiffX()) / getWidth() * 4) * distanceX);
                    if (disX == disX) translateLeft -= disX;
                } else {
                    translateLeft -= distanceX * 1.5;
                }
                change = true;
            }

            if (change) invalidate();
            return change;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isAutoScale = true;
            ValueAnimator mResetScaleAnimator = getResetScaleAnimator();

            if (scale == 1.f){
                mResetScaleAnimator.setFloatValues(1.f, 2.f);

                ValueAnimator mResetXAnimator = getResetXAnimator();
                ValueAnimator mResetYAnimator = getResetYAnimator();
                mResetXAnimator.setFloatValues(translateLeft, (getWidth() - mBoundWidth * 2.f) / 2.f);
                mResetYAnimator.setFloatValues(translateTop, (getHeight() - mBoundHeight * 2.f) / 2.f);
                mResetXAnimator.addUpdateListener(getOnTranslateXAnimationUpdate());
                mResetYAnimator.addUpdateListener(getOnTranslateYAnimationUpdate());
                mResetXAnimator.start();
                mResetYAnimator.start();
            }else{
                mResetScaleAnimator.setFloatValues(scale, 1.f);
                resetDefaultState();
            }

            mResetScaleAnimator.addUpdateListener(getOnScaleAnimationUpdate());
            mResetScaleAnimator.start();
            return true;
        }
    }


}
