package com.hebehan.someexample.gesturelock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hebe on 2017/3/1.
 */

public class GestureLockView extends View {


    public GestureLockView(Context context) {
        super(context,null);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public GestureLockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    enum Mode{
        STATUS_NO_FINGER,STATUS_FINGER_ON,STATUS_FINGER_UP;
    }

    private Mode currentMode = Mode.STATUS_NO_FINGER;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private int mStorkeWidth = 2;

    private int centerX;
    private int centerY;
    private Paint mPaint;

    private float mArrowRate = 0.333f;
    private int mArrowDegree = -1;
    private Path mArrowPath;

    private float mInnerCircleRadiusRate = 0.3f;
    private int colorNoFingerInner;
    private int colorNoFingerOuter;
    private int colorFingerOn;
    private int colorFingerUp;

    public GestureLockView(Context context,int colorNoFingerInner,int colorNoFingerOuter,int colorFingerOn,int colorFingerUp){
        super(context);
        this.colorFingerOn = colorFingerOn;
        this.colorFingerUp = colorFingerUp;
        this.colorNoFingerInner = colorNoFingerInner;
        this.colorNoFingerOuter = colorNoFingerOuter;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrowPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mWidth = mWidth<mHeight?mWidth:mHeight;
        mRadius = centerX = centerY = mWidth/2;
        mRadius -= mStorkeWidth/2;

        float mArrowLength = mWidth / 2 * mArrowRate;
        mArrowPath.moveTo(mWidth/2,mStorkeWidth+2);
        mArrowPath.lineTo(mWidth/2-mArrowLength,mStorkeWidth+2+mArrowLength);
        mArrowPath.lineTo(mWidth/2+mArrowLength,mStorkeWidth+2+mArrowLength);
        mArrowPath.close();
        mArrowPath.setFillType(Path.FillType.WINDING);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (currentMode){
            case STATUS_FINGER_ON:
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(colorFingerOn);
                mPaint.setStrokeWidth(2);
                canvas.drawCircle(centerX,centerY,mRadius,mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(centerX,centerY,mRadius*mInnerCircleRadiusRate,mPaint);
                break;
            case STATUS_FINGER_UP:
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(colorFingerUp);
                mPaint.setStrokeWidth(2);
                canvas.drawCircle(centerX,centerY,mRadius,mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(centerX,centerY,mRadius*mInnerCircleRadiusRate,mPaint);
                break;
            case STATUS_NO_FINGER:
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(colorNoFingerOuter);
                canvas.drawCircle(centerX,centerY,mRadius,mPaint);
                mPaint.setColor(colorNoFingerInner);
                canvas.drawCircle(centerX,centerY,mRadius*mInnerCircleRadiusRate,mPaint);
                drawArrow(canvas);
                break;
        }

    }

    private void drawArrow(Canvas canvas){
        if (mArrowDegree != -1){
            mPaint.setStyle(Paint.Style.FILL);
            canvas.save();
            canvas.rotate(mArrowDegree,centerX,centerY);
            canvas.drawPath(mArrowPath,mPaint);
            canvas.restore();
        }
    }

    public void setCurrentMode(Mode mode){
        this.currentMode = mode;
        invalidate();
    }

    public void setColorFingerUp(int colorFingerUp) {
        this.colorFingerUp = colorFingerUp;
    }

    public void setmArrowDegree(int mArrowDegree) {
        this.mArrowDegree = mArrowDegree;
    }

    public int getmArrowDegree() {
        return mArrowDegree;
    }
}
