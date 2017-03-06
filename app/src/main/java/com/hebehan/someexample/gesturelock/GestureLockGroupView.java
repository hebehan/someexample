package com.hebehan.someexample.gesturelock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Hebe on 2017/3/2.
 */

public class GestureLockGroupView extends RelativeLayout {


    private GestureLockView[] views;
    private int mCount = 3;
    private int[] mAnswer = new int[]{1,2,5,8};
    private List<Integer> mChooses = new ArrayList<>();
    private Paint mPaint;
    private int lockViewMargin = 30;
    private int lockviewWidth;
    /**
     * GestureLockView无手指触摸的状态下内圆的颜色
     */
    private int mNoFingerInnerCircleColor = 0xFF939090;
    /**
     * GestureLockView无手指触摸的状态下外圆的颜色
     */
    private int mNoFingerOuterCircleColor = 0xFFE0DBDB;
    /**
     * GestureLockView手指触摸的状态下内圆和外圆的颜色
     */
    private int mFingerOnColor = 0xFF378FC9;
    /**
     * GestureLockView手指抬起的状态下内圆和外圆的颜色
     */
    private int mFingerUpColor = 0xFFFF0000;

    private int mFingerUpRightColor = 0xFF00FF00;

    private int mWidth;
    private int mHeight;

    private Path mPath;

    private int mLastPathX;
    private int mLastPathY;

    private Point mEndPoint = new Point();

    private int mMaxTry = 4;



    public GestureLockGroupView(Context context) {
        this(context,null);
    }

    public GestureLockGroupView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GestureLockGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.)
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mHeight = mWidth = mWidth<mHeight?mWidth:mHeight;

        if (views == null){
            views = new GestureLockView[mCount*mCount];
            lockviewWidth = (int)(4*mWidth*1.0f/(5*mCount+1));
            lockViewMargin = (int)(lockviewWidth * 0.25);
            mPaint.setStrokeWidth(lockviewWidth*0.29f);
            for (int i = 0; i < views.length; i++) {
                views[i] = new GestureLockView(getContext(),mNoFingerInnerCircleColor,mNoFingerOuterCircleColor,mFingerOnColor,mFingerUpColor);
                views[i].setId(i+1);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(lockviewWidth,lockviewWidth);
                if (i%mCount != 0){
                    params.addRule(RelativeLayout.RIGHT_OF,views[i-1].getId());
                }
                if (i>mCount-1){
                    params.addRule(BELOW,views[i-mCount].getId());
                }
                int rightMargin = lockViewMargin;
                int bottomMargin = lockViewMargin;
                int leftMargin = 0;
                int topMargin = 0;
                params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
                views[i].setCurrentMode(GestureLockView.Mode.STATUS_NO_FINGER);
                addView(views[i],params);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int)event.getX();
        int y = (int)event.getY();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                mPaint.setColor(mFingerOnColor);
                mPaint.setAlpha(50);
                GestureLockView child = getChildByPos(x,y);
                if (child != null){
                    int cid = child.getId();
                    if (!mChooses.contains(cid)){
                        mChooses.add(cid);
                        child.setCurrentMode(GestureLockView.Mode.STATUS_FINGER_ON);
                        mLastPathX = child.getLeft()/2+child.getRight()/2;
                        mLastPathY = child.getTop()/2+child.getBottom()/2;
                        if (mChooses.size() == 1){
                            mPath.moveTo(mLastPathX,mLastPathY);
                        }else {
                            mPath.lineTo(mLastPathX,mLastPathY);
                        }
                    }
                }
                mEndPoint.x = x;
                mEndPoint.y = y;
                break;
            case MotionEvent.ACTION_UP:
                if (checkAnswer()){
                    mPaint.setColor(mFingerUpRightColor);
                }else {
                    mPaint.setColor(mFingerUpColor);
                }
                mPaint.setAlpha(50);
                this.mMaxTry--;
                if (mChooses.size()>0){
//                    checkAnswer();
                    if (this.mMaxTry <= 0){
                        Log.e("groupview","重试次数超标");
                    }

                }
                mEndPoint.x = mLastPathX;
                mEndPoint.y = mLastPathY;
                changeItemMode();
                for (int i = 0; i+1< mChooses.size(); i++) {
                    int childid = mChooses.get(i);
                    int nextid = mChooses.get(i+1);
                    GestureLockView startview = (GestureLockView) findViewById(childid);
                    GestureLockView endView = (GestureLockView) findViewById(nextid);
                    int dx = endView.getLeft() - startview.getLeft();
                    int dy = endView.getTop() - startview.getTop();

                    int angle = (int)Math.toDegrees(Math.atan2(dy,dx))+90;
                    startview.setmArrowDegree(angle);
                }
                break;
        }
        invalidate();
        return true;
    }

    private void changeItemMode(){
        for (GestureLockView view : views){
            if (mChooses.contains(view.getId())){
                if (checkAnswer()){
                    view.setColorFingerUp(mFingerUpRightColor);
                }else {
                    view.setColorFingerUp(mFingerUpColor);
                }
                view.setCurrentMode(GestureLockView.Mode.STATUS_FINGER_UP);
            }
        }
    }

    private void reset(){
        mChooses.clear();
        mPath.reset();
        for (GestureLockView view : views){
            view.setCurrentMode(GestureLockView.Mode.STATUS_NO_FINGER);
            view.setmArrowDegree(-1);
        }
    }

    private boolean checkAnswer(){
        if (mAnswer.length != mChooses.size()){
            return false;
        }

        for (int i = 0; i < mAnswer.length; i++) {
            Log.e("ans",mChooses.get(i)+"");
            if (mAnswer[i] != mChooses.get(i)){
                return false;
            }
        }
        return true;
    }

    private boolean checkPositionInChild(View child,int x,int y){
        int padding = (int)(lockviewWidth * 0.15);
        if (x>=child.getLeft()+padding&&x<=child.getRight()-padding&&y>=child.getTop()+padding&&y<=child.getBottom()-padding){
            return true;
        }
        return false;
    }

    private GestureLockView getChildByPos(int x,int y){
        for (GestureLockView view : views){
            if (checkPositionInChild(view,x,y)){
                return  view;
            }
        }
        return null;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPaint != null){
            canvas.drawPath(mPath,mPaint);
        }
        if (mChooses.size()>0){
            if (mLastPathX != 0 && mLastPathY != 0){
                canvas.drawLine(mLastPathX,mLastPathY,mEndPoint.x,mEndPoint.y,mPaint);
            }
        }
    }
}
