package com.rhino.customseekbar.view;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;

import com.rhino.customseekbar.R;

/**
 * <p>This is custom SeekBar with anim, support HORIZONTAL and VERTICAL.</p>
 *Follow this example:
 *
 * <pre class="prettyprint">
 * &lt;?xml version="1.0" encoding="utf-8"?&gt</br>
 * &lt;RelativeLayout
 *      xmlns:android="http://schemas.android.com/apk/res/android"
 *      xmlns:app="http://schemas.android.com/apk/res-auto"
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"&gt
 *
 *      &lt;com.rhino.customseekbar.view.CustomSeekBar
 *          android:id="@+id/CustomSeekBarSection1"
 *          android:layout_width="match_parent"
 *          android:layout_height="50dp"
 *          android:layout_margin="8dp"
 *          android:background="#1A000000"
 *          app:csb_background_color="#FFAAAAAA"
 *          app:csb_progress_height="2dp"
 *          app:csb_progress_color="#FF008888"
 *          app:csb_section_enable="false"
 *          app:csb_section_radius="5dp"
 *          app:csb_thumb_radius="12dp"
 *          app:csb_thumb_scale_enable="true"/&gt
 *
 *&lt;/RelativeLayout&gt
 *</pre>
 * @since Created by LuoLin on 2017/2/10.
 **/
public class CustomSeekBar extends View {

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final int THUMB_ANIM_CANCEL_SPACE = 1000;
    private static final int DEFAULT_PROGRESS_BACKGROUND_COLOR = 0xFFDDDDDD;
    private static final int DEFAULT_PROGRESS_COLOR = 0xFF28AAE5;
    private static final int DEFAULT_SECTION_POINT_RADIUS = 6;
    private static final boolean DEFAULT_OVERSPREAD_ENABLE = false;
    private static final boolean DEFAULT_THUMB_SCALE_ENABLE = false;
    private static final int DEFAULT_THUMB_RADIUS = 18;
    private static final int DEFAULT_MIN_PROGRESS = 0;
    private static final int DEFAULT_MAX_PROGRESS = 100;
    private static final int DEFAULT_PROGRESS_CORNER = 2;
    private static final int DEFAULT_PROGRESS_WIDTH = 2;
    private static final boolean DEFAULT_SECTION_POINT_ENABLE = false;
    private int mOrientation = HORIZONTAL;
    private int mProgressBackgroundColor = DEFAULT_PROGRESS_BACKGROUND_COLOR;
    private int mProgressColor = DEFAULT_PROGRESS_COLOR;
    private int mSectionPointRadius = DEFAULT_SECTION_POINT_RADIUS;
    private boolean mSectionPointEnable = DEFAULT_SECTION_POINT_ENABLE;
    private int mThumbRadius = DEFAULT_THUMB_RADIUS;
    private int mMinProgress = DEFAULT_MIN_PROGRESS;
    private int mMaxProgress = DEFAULT_MAX_PROGRESS;
    private int mProgressCorner = DEFAULT_PROGRESS_CORNER;
    private int mProgressWidth = DEFAULT_PROGRESS_WIDTH;
    private boolean mOverspreadEnable = DEFAULT_OVERSPREAD_ENABLE;

    private int mProgressLength;
    private int mThumbTouchOffset;
    private int mCurrProgress;
    private int mLastProgress;

    private int mViewHeight;
    private int mViewWidth;
    private GradientDrawable mProgressBgDrawable;
    private GradientDrawable mProgressDrawable;
    private Paint mSectionPointPaint;
    private Paint mThumbPaint;
    private Rect mProgressBackgroundRect;
    private Rect mProgressRect;
    private Rect mSectionPointRect;
    private Rect mThumbDestRect;
    private Drawable mThumbDrawable = null;

    private boolean mIsClickOnThumb = false;
    private boolean mIsClickOnProgress = false;
    private boolean mIsMovedOut = false;
    private boolean mIsFromUser = false;
    private boolean mIsThumbEnable = true;
    private OnProgressChangedListener mOnProgressListener;

    private boolean mThumbScaleEnable = DEFAULT_THUMB_SCALE_ENABLE;
    private ValueAnimator mValueAnimator;
    private float mMaxThumbScale = 1.4f;
    private float mThumbScale = 1.0f;

    private ValueAnimator mToDestValueAnimator;


    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar);
            mProgressWidth = typedArray.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_progress_height,
                    dip2px(context, DEFAULT_PROGRESS_WIDTH));
            mProgressCorner = typedArray.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_progress_corner,
                    dip2px(context, DEFAULT_PROGRESS_CORNER));
            mThumbRadius = typedArray.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_thumb_radius,
                    dip2px(context, DEFAULT_THUMB_RADIUS));
            mThumbDrawable = typedArray.getDrawable(R.styleable.CustomSeekBar_csb_thumb_point);
            mSectionPointRadius = typedArray.getDimensionPixelSize(R.styleable.CustomSeekBar_csb_section_radius,
                    dip2px(context, DEFAULT_SECTION_POINT_RADIUS));
            mSectionPointEnable = typedArray.getBoolean(R.styleable.CustomSeekBar_csb_section_enable,
                    DEFAULT_SECTION_POINT_ENABLE);
            mProgressBackgroundColor = typedArray.getColor(R.styleable.CustomSeekBar_csb_background_color,
                    DEFAULT_PROGRESS_BACKGROUND_COLOR);
            mProgressColor = typedArray.getColor(R.styleable.CustomSeekBar_csb_progress_color,
                    DEFAULT_PROGRESS_COLOR);
            mMinProgress = typedArray.getInt(R.styleable.CustomSeekBar_csb_min_value,
                    DEFAULT_MIN_PROGRESS);
            mMaxProgress = typedArray.getInt(R.styleable.CustomSeekBar_csb_max_value,
                    DEFAULT_MAX_PROGRESS);
            mOverspreadEnable = typedArray.getBoolean(R.styleable.CustomSeekBar_csb_overspread_enable,
                    DEFAULT_OVERSPREAD_ENABLE);
            mThumbScaleEnable = typedArray.getBoolean(R.styleable.CustomSeekBar_csb_thumb_scale_enable,
                    DEFAULT_THUMB_SCALE_ENABLE);
            mOrientation = typedArray.getInt(R.styleable.CustomSeekBar_csb_orientation, HORIZONTAL);

            typedArray.recycle();
        }

        mSectionPointPaint = new Paint();
        mSectionPointPaint.setStyle(Paint.Style.FILL);
        mSectionPointPaint.setColor(mProgressColor);
        mSectionPointPaint.setAntiAlias(true);

        mThumbPaint = new Paint();
        mThumbPaint.setStyle(Paint.Style.FILL);
        mThumbPaint.setAntiAlias(true);

        mProgressBgDrawable = new GradientDrawable();
        mProgressBgDrawable.setShape(GradientDrawable.RECTANGLE);
        mProgressBgDrawable.setColor(mProgressBackgroundColor);

        mProgressDrawable = new GradientDrawable();
        mProgressDrawable.setShape(GradientDrawable.RECTANGLE);
        mProgressDrawable.setColor(mProgressColor);

        mProgressBackgroundRect = new Rect();
        mProgressRect = new Rect();
        mSectionPointRect = new Rect();
        mThumbDestRect = new Rect();

        mCurrProgress = mMinProgress;
    }

    /**
     * Do something init.
     *
     * @param width  width
     * @param height height
     */
    private void initViewSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        if (HORIZONTAL == mOrientation) {
            if (mThumbScaleEnable) {
                mProgressLength = (int) (width - 2 * mThumbRadius * mMaxThumbScale);
            } else {
                mProgressLength = width - 2 * mThumbRadius;
            }
            mProgressBackgroundRect.top = -mProgressWidth;
            mProgressBackgroundRect.bottom = -mProgressBackgroundRect.top;
            mProgressBackgroundRect.left = mOverspreadEnable ? -width / 2 : -mProgressLength / 2;
            mProgressBackgroundRect.right = mOverspreadEnable ? width / 2 : mProgressLength / 2;

            mProgressRect.top = -mProgressWidth;
            mProgressRect.bottom = -mProgressRect.top;
            mProgressRect.left = mOverspreadEnable ? -width / 2 : -mProgressLength / 2;
            mProgressRect.right = -mProgressLength / 2;

            mThumbDestRect.top = -mThumbRadius;
            mThumbDestRect.bottom = mThumbRadius;
            mThumbDestRect.left = -mProgressLength / 2 - mThumbRadius;
            mThumbDestRect.right = -mProgressLength / 2 + mThumbRadius;
        } else {
            if (mThumbScaleEnable) {
                mProgressLength = (int) (height - 2 * mThumbRadius * mMaxThumbScale);
            } else {
                mProgressLength = height - 2 * mThumbRadius;
            }
            mProgressBackgroundRect.top = mOverspreadEnable ? -height / 2 : -mProgressLength / 2;
            mProgressBackgroundRect.bottom = mOverspreadEnable ? height / 2 : mProgressLength / 2;
            mProgressBackgroundRect.left = -mProgressWidth;
            mProgressBackgroundRect.right = mProgressWidth;

            mProgressRect.top = mProgressLength / 2;
            mProgressRect.bottom = mOverspreadEnable ? height / 2 : mProgressLength / 2;
            mProgressRect.left = -mProgressWidth;
            mProgressRect.right = mProgressWidth;

            mThumbDestRect.top = -mProgressLength / 2 - mThumbRadius;
            mThumbDestRect.bottom = -mProgressLength / 2 + mThumbRadius;
            mThumbDestRect.left = -mThumbRadius;
            mThumbDestRect.right = mThumbRadius;
        }

        mSectionPointRect.top = -mSectionPointRadius;
        mSectionPointRect.bottom = mSectionPointRadius;
        mSectionPointRect.left = -mSectionPointRadius;
        mSectionPointRect.right = mSectionPointRadius;

        mThumbTouchOffset = mThumbRadius;

        setThumbDrawable(mThumbDrawable);
        setProgress(mCurrProgress);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            mViewWidth = widthSize;
        } else {
            mViewWidth = getWidth();
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            mViewHeight = heightSize;
        } else {
            mViewHeight = getHeight();
        }
        initViewSize(mViewWidth, mViewHeight);
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - mViewWidth / 2;
        float y = event.getY() - mViewHeight / 2;
        ViewParent parent = getParent();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mIsThumbEnable) {
                    return super.onTouchEvent(event);
                }
                if (clickOnThumb(x, y)) {
                    dealThumbAnim(true);
                    mIsClickOnThumb = true;
                    mIsClickOnProgress = true;
                    if (null != parent) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                } else if (clickOnProgress(x, y)) {
                    dealThumbAnim(true);
                    mIsClickOnProgress = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsClickOnThumb) {
                    moveToPoint(HORIZONTAL == mOrientation ? x : y);
                    onProgressChanged(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                dealThumbAnim(false);
                mIsClickOnThumb = false;
                if (mIsClickOnProgress) {
                    mIsClickOnProgress = false;
                    moveToPoint(HORIZONTAL == mOrientation ? x : y);
                    onProgressChanged(true);
                }
                if (null != parent) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.translate(mViewWidth / 2, mViewHeight / 2);

        drawProgressBackground(canvas);
        drawProgress(canvas);
        if (mSectionPointEnable) {
            drawProgressSectionPoint(canvas);
        }
        drawThumb(canvas);

        canvas.restore();
    }

    /**
     * Draw the progress background.
     *
     * @param canvas Canvas
     */
    private void drawProgressBackground(Canvas canvas) {
        canvas.save();
        mProgressBgDrawable.setBounds(mProgressBackgroundRect);
        mProgressBgDrawable.setCornerRadius(mProgressCorner);
        mProgressBgDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * Draw the progress.
     *
     * @param canvas Canvas
     */
    private void drawProgress(Canvas canvas) {
        canvas.save();
        mProgressDrawable.setBounds(mProgressRect);
        mProgressDrawable.setCornerRadius(mProgressCorner);
        mProgressDrawable.draw(canvas);
        canvas.restore();
    }

    /**
     * Draw the progress section.
     *
     * @param canvas Canvas
     */
    private void drawProgressSectionPoint(Canvas canvas) {
        canvas.save();
        for (int i = mMinProgress; i <= mMaxProgress; i++) {
            float coord = progress2Coord(i);
            if (HORIZONTAL == mOrientation) {
                mSectionPointRect.left = (int) (coord - mSectionPointRadius);
                mSectionPointRect.right = (int) (coord + mSectionPointRadius);
                if (coord <= mProgressRect.right) {
                    mSectionPointPaint.setColor(mProgressColor);
                } else {
                    mSectionPointPaint.setColor(mProgressBackgroundColor);
                }
            } else {
                mSectionPointRect.top = (int) (coord - mSectionPointRadius);
                mSectionPointRect.bottom = (int) (coord + mSectionPointRadius);
                if (coord >= mProgressRect.top) {
                    mSectionPointPaint.setColor(mProgressColor);
                } else {
                    mSectionPointPaint.setColor(mProgressBackgroundColor);
                }
            }
            canvas.drawCircle(mSectionPointRect.centerX(),
                    mSectionPointRect.centerY(), mSectionPointRect.width() / 2,
                    mSectionPointPaint);
        }
        canvas.restore();
    }

    /**
     * Draw the progress Thumb.
     *
     * @param canvas Canvas
     */
    private void drawThumb(Canvas canvas) {
        canvas.save();
        if (null != mThumbDrawable) {
            mThumbDrawable.setBounds(mThumbDestRect);
            mThumbDrawable.draw(canvas);
        } else {
            mThumbPaint.setColor(mProgressColor);
            canvas.drawCircle(mThumbDestRect.centerX(), mThumbDestRect.centerY(),
                    mThumbDestRect.width() * mThumbScale / 2, mThumbPaint);
        }
        canvas.restore();
    }

    /**
     * Whether click on thumb.
     *
     * @param x x
     * @param y y
     * @return true or false
     */
    private boolean clickOnThumb(float x, float y) {
        return mThumbDestRect.left < mThumbDestRect.right
                && mThumbDestRect.top < mThumbDestRect.bottom
                && x >= mThumbDestRect.left - mThumbTouchOffset
                && x <= mThumbDestRect.right + mThumbTouchOffset
                && y >= mThumbDestRect.top - mThumbTouchOffset
                && y <= mThumbDestRect.bottom + mThumbTouchOffset;
    }

    /**
     * Whether click on progress.
     *
     * @param x x
     * @param y y
     * @return true or false
     */
    private boolean clickOnProgress(float x, float y) {
        return mProgressBackgroundRect.left < mProgressBackgroundRect.right
                && mProgressBackgroundRect.top < mProgressBackgroundRect.bottom
                && x >= mProgressBackgroundRect.left - mThumbTouchOffset
                && x <= mProgressBackgroundRect.right + mThumbTouchOffset
                && y >= mProgressBackgroundRect.top - mThumbTouchOffset
                && y <= mProgressBackgroundRect.bottom + mThumbTouchOffset;
    }

    /**
     * Deal the listener of progress changed.
     *
     * @param isFinished whether changing is finished
     */
    private void onProgressChanged(boolean isFinished) {
        if (!mIsMovedOut) {
            if (HORIZONTAL == mOrientation) {
                mCurrProgress = coord2Progress(mThumbDestRect.centerX());
            } else {
                mCurrProgress = coord2Progress(mThumbDestRect.centerY());
            }
        }

        if (mLastProgress != mCurrProgress || isFinished) {
            if (null != mOnProgressListener) {
                mIsFromUser = true;
                mOnProgressListener.onChanged(this, true, isFinished);
                mIsFromUser = false;
            }
            mLastProgress = mCurrProgress;
        }
        if (isFinished) { // adjust thumb position when finished.
            float coord = progress2Coord(mCurrProgress);
            moveToPoint(coord);
        }
        mIsMovedOut = false;
    }

    /**
     * Move the thumb position.
     *
     * @param coord the x or y coordinate of thumb
     */
    private void moveToPoint(float coord) {
        float halfLength = mProgressLength / 2;
        if (coord > halfLength) {
            coord = halfLength;
            mIsMovedOut = true;
            mCurrProgress = HORIZONTAL == mOrientation ? mMaxProgress : mMinProgress;
        } else if (coord < -halfLength) {
            coord = -halfLength;
            mIsMovedOut = true;
            mCurrProgress = HORIZONTAL == mOrientation ? mMinProgress : mMaxProgress;
        }

        if (HORIZONTAL == mOrientation) {
            mThumbDestRect.left = (int) (coord - mThumbRadius);
            mThumbDestRect.right = (int) (coord + mThumbRadius);
            mProgressRect.right = (int) coord;
        } else {
            mThumbDestRect.top = (int) (coord - mThumbRadius);
            mThumbDestRect.bottom = (int) (coord + mThumbRadius);
            mProgressRect.top = (int) coord;
        }
        invalidate();
    }

    /**
     * Return x or y coordinate by progress value.
     *
     * @param progress progress value
     * @return x or y coordinate
     */
    private float progress2Coord(int progress) {
        if (HORIZONTAL == mOrientation) {
            return (float) mProgressLength * (progress - mMinProgress)
                    / (mMaxProgress - mMinProgress) - mProgressLength / 2f;
        } else {
            return mProgressLength / 2 - (float) mProgressLength * (progress - mMinProgress)
                    / (mMaxProgress - mMinProgress);
        }
    }

    /**
     * Return progress value by x or y coordinate.
     *
     * @param coord x or y coordinate
     * @return progress value
     */
    private int coord2Progress(int coord) {
        if (coord > mProgressLength / 2) {
            return HORIZONTAL == mOrientation ? mMaxProgress : mMinProgress;
        } else if (coord < -mProgressLength / 2) {
            return HORIZONTAL == mOrientation ? mMinProgress : mMaxProgress;
        } else {
            if (HORIZONTAL == mOrientation) {
                return Math.round((coord + mProgressLength / 2f)
                        * (mMaxProgress - mMinProgress) / mProgressLength)
                        + mMinProgress;
            } else {
                return Math.round((mProgressLength / 2f - coord)
                        * (mMaxProgress - mMinProgress) / mProgressLength)
                        + mMinProgress;
            }
        }
    }

    /**
     * Change thumb scale.
     *
     * @param enlarge enlarge
     */
    private void changeThumbScale(boolean enlarge) {
        if (Build.VERSION.SDK_INT >= 12) {
            float start = mThumbScale;
            float stop = enlarge ? mMaxThumbScale : 1f;
            if (null == mValueAnimator) {
                mValueAnimator = new ValueAnimator();
                mValueAnimator.setDuration(200);
                mValueAnimator.setInterpolator(new DecelerateInterpolator());
                mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mThumbScale = (Float) animation.getAnimatedValue();
                        invalidate();
                    }
                });
            } else {
                mValueAnimator.cancel();
            }
            mValueAnimator.setFloatValues(start, stop);
            mValueAnimator.start();
        } else {
            mThumbScale = enlarge ? mMaxThumbScale : 1f;
        }
    }

    /**
     * Deal the thumb scale.
     *
     * @param enlarge enlarge
     */
    private void dealThumbAnim(boolean enlarge) {
        if (!mThumbScaleEnable) {
            return;
        }
        if (enlarge) {
            removeCallbacks(thumbAnimRunnable);
            changeThumbScale(true);
        } else {
            removeCallbacks(thumbAnimRunnable);
            postDelayed(thumbAnimRunnable, THUMB_ANIM_CANCEL_SPACE);
        }
    }

    /**
     * The runnable for thumb anim.
     */
    private Runnable thumbAnimRunnable = new Runnable() {
        @Override
        public void run() {
            changeThumbScale(false);
        }
    };

    /**
     * Change thumb to dest progress.
     *
     * @param anim     true show anim, false not show anim
     * @param progress progress
     */
    private void toDestProgress(boolean anim, int progress) {
        if (anim && Build.VERSION.SDK_INT >= 12) {
            float startCoord = checkCoord(progress2Coord(mCurrProgress));
            float stopCoord = checkCoord(progress2Coord(progress));
            mCurrProgress = progress;
            if (null == mToDestValueAnimator) {
                mToDestValueAnimator = new ValueAnimator();
                mToDestValueAnimator.setDuration(400);
                mToDestValueAnimator.setInterpolator(new DecelerateInterpolator());
                mToDestValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float coord = (Float) animation.getAnimatedValue();
                        moveToPoint(coord);
                    }
                });
            } else {
                mToDestValueAnimator.cancel();
            }
            mToDestValueAnimator.setFloatValues(startCoord, stopCoord);
            mToDestValueAnimator.start();
        } else {
            mCurrProgress = progress;
            float stopCoord = checkCoord(progress2Coord(progress));
            moveToPoint(stopCoord);
        }
    }

    /**
     * Check the coordinate
     *
     * @param coord coordinate
     * @return the new coordinate
     */
    private float checkCoord(float coord) {
        float halfLength = mProgressLength / 2;
        if (coord > halfLength) {
            return halfLength;
        } else if (coord < -halfLength) {
            return -halfLength;
        }
        return coord;
    }

    /**
     * Whether changed by user.
     *
     * @return true by user, false not by user
     */
    public boolean isFromUser() {
        return mIsFromUser;
    }

    /**
     * Whether change finished.
     *
     * @return true finished, false not finish
     */
    public boolean isFinished() {
        return !mIsClickOnProgress && !mIsClickOnThumb;
    }

    /**
     * Get last progress.
     *
     * @return last progress
     */
    public int getLastProgress() {
        return mLastProgress;
    }

    /**
     * Get current progress.
     *
     * @return the current progress
     */
    public int getProgress() {
        return mCurrProgress;
    }

    /**
     * Set progress.
     *
     * @param progress progress
     * @param anim     true show anim, false not show anim
     * @param fromUser true by user, false not by user
     */
    public void setProgress(int progress, boolean anim, boolean fromUser) {
        if (!mIsThumbEnable || !isFinished()) {
            return; // can not be changed when touching
        }
        if (progress <= mMinProgress) {
            progress = mMinProgress;
        } else if (progress >= mMaxProgress) {
            progress = mMaxProgress;
        }

        toDestProgress(anim, progress);
        if (null != mOnProgressListener) {
            if (mLastProgress != mCurrProgress) {
                mIsFromUser = fromUser;
                mOnProgressListener.onChanged(this, mIsFromUser, true);
                mIsFromUser = false;
            }
            mLastProgress = mCurrProgress;
        }
    }

    /**
     * Set progress.
     *
     * @param progress progress
     */
    public void setProgress(int progress) {
        setProgress(progress, false, false);
    }

    /**
     * Set the min progress.
     *
     * @param minProgress the min progress
     */
    public void setMinProgress(int minProgress) {
        this.mMinProgress = minProgress;
        if (mCurrProgress < mMinProgress) {
            this.mCurrProgress = mMinProgress;
        }
    }

    /**
     * Set the max progress.
     *
     * @param maxProgress the max progress
     */
    public void setMaxProgress(int maxProgress) {
        this.mMaxProgress = maxProgress;
    }

    /**
     * Set the color of progress background.
     *
     * @param color color
     */
    public void setProgressBackgroundColor(@ColorInt int color) {
        this.mProgressBackgroundColor = color;
        mProgressBgDrawable.setColor(mProgressBackgroundColor);
    }

    /**
     * Set the color of progress.
     *
     * @param color color
     */
    public void setProgressColor(@ColorInt int color) {
        this.mProgressColor = color;
        mProgressDrawable.setColor(mProgressColor);
    }

    /**
     * Set the thumb drawable.
     *
     * @param drawable Drawable
     */
    public void setThumbDrawable(Drawable drawable) {
        if (null == drawable) {
            return;
        }
        mThumbDrawable = drawable;
    }

    /**
     * Set the section point enable.
     *
     * @param enable true show section
     */
    public void setSectionEnable(boolean enable) {
        this.mSectionPointEnable = enable;
    }

    /**
     * Set thumb change enable.
     *
     * @param enable true can change
     */
    public void setThumbEnable(boolean enable) {
        this.mIsThumbEnable = enable;
    }

    /**
     * Register a callback to be invoked when the progress changes.
     *
     * @param listener the callback to call on progress change
     */
    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        mOnProgressListener = listener;
    }

    public interface OnProgressChangedListener {
        void onChanged(CustomSeekBar seekBar, boolean fromUser,
                       boolean isFinished);
    }

    private int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
