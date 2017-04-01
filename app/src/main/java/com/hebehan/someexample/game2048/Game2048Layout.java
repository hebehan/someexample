package com.hebehan.someexample.game2048;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Hebe on 2017/3/9.
 */

public class Game2048Layout extends RelativeLayout {

    private int mColumn = 4;
    private Game2048Item[] items;
    private int mMargin = 10;
    private int padding=10;
    private GestureDetector mGestureDetector;
    private boolean isMergeHappen = true;
    private boolean isMoveHappen = true;
    private int mScore;

    public Game2048Layout(Context context) {
        this(context,null);
    }

    public Game2048Layout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Game2048Layout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMargin = 10;
        mGestureDetector = new GestureDetector(context,new MyGestor());
    }

    private boolean once;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int length = Math.min(getMeasuredHeight(),getMeasuredWidth());
        int childWidth = (length -padding*2-mMargin*(mColumn-1))/mColumn;
        if (!once){
            if (items == null){
                items = new Game2048Item[mColumn*mColumn];
            }
            for (int i = 0; i < items.length; i++) {
                Game2048Item item = new Game2048Item(getContext());
                items[i] = item;
                item.setId(i+1);
                RelativeLayout.LayoutParams lp = new LayoutParams(childWidth,childWidth);
                if ((i+1)%mColumn!= 0){
                    lp.rightMargin = mMargin;
                }
                if (i%mColumn != 0){
                    lp.addRule(RelativeLayout.RIGHT_OF,items[i-1].getId());
                }
                if ((i+1)>mColumn){
                    lp.topMargin = mMargin;
                    lp.addRule(RelativeLayout.BELOW,items[i-mColumn].getId());
                }
                addView(item,lp);
            }
            generateNum();
        }
        once = true;
        setMeasuredDimension(length,length);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    public class MyGestor extends GestureDetector.SimpleOnGestureListener{

        final int FLING_MIN_DIS = 50;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x = e2.getX()-e1.getX();
            float y = e2.getY()-e1.getY();
            if (x>FLING_MIN_DIS&&Math.abs(velocityX)>Math.abs(velocityY)){
                action(ACTIONDIRECT.RIGHT);
                Log.e("onFling","RIGHT");
            }else if (x<-FLING_MIN_DIS&&Math.abs(velocityX)>Math.abs(velocityY)){
                action(ACTIONDIRECT.LEFT);
                Log.e("onFling","LEFT");
            }else if (y>FLING_MIN_DIS&&Math.abs(velocityX)<Math.abs(velocityY)){
                action(ACTIONDIRECT.DOWN);
                Log.e("onFling","DOWN");
            }else if (y<-FLING_MIN_DIS&&Math.abs(velocityX)<Math.abs(velocityY)){
                action(ACTIONDIRECT.UP);
                Log.e("onFling","UP");
            }
            return true;
        }
    }

    public enum ACTIONDIRECT{
        RIGHT,LEFT,DOWN,UP;
    }

    public void action(ACTIONDIRECT action){
        for (int i = 0; i < mColumn; i++) {
            List<Game2048Item> row = new ArrayList<>();
            for (int j = 0; j < mColumn; j++) {
                int index = getIndexByAction(action,i,j);
                Game2048Item item = items[index];
                if (item.getmNumber() != 0){
                    row.add(item);
                }
            }
            for (int j = 0; j < mColumn&&j<row.size(); j++) {
                int index = getIndexByAction(action,i,j);
                Game2048Item item = items[index];
                if (item.getmNumber() != row.get(j).getmNumber()){
                    Log.e("for2","move");
                    isMoveHappen = true;
                }
            }
            mergeItem(row);
            for (int j = 0; j < mColumn; j++) {
                int index = getIndexByAction(action,i,j);
                if (row.size()>j){
                    items[index].setmNumber(row.get(j).getmNumber());
                }else {
                    items[index].setmNumber(0);
                }
            }
        }
        generateNum();
    }

    private int getIndexByAction(ACTIONDIRECT action,int i,int j){
        int index = -1;
        switch (action){
            case LEFT:
                index = i*mColumn+j;
                break;
            case RIGHT:
                index = i*mColumn+mColumn-j-1;
                break;
            case UP:
                index = i+j*mColumn;
                break;
            case DOWN:
                index = i+(mColumn-1-j)*mColumn;
                break;
        }
        return index;
    }

    private void mergeItem(List<Game2048Item> row){
        if (row.size() < 2){
            return;
        }
        Log.e("mergeItem","mergeItem");
        for (int i = 0; i < row.size()-1; i++) {
            Game2048Item item1 = row.get(i);
            Game2048Item item2 = row.get(i+1);
            if (item1.getmNumber() == item2.getmNumber()){
                isMergeHappen = true;
                int val = item1.getmNumber()+item2.getmNumber();
                item1.setmNumber(val);

                mScore +=val;
                for (int j = i+1; j < row.size()-1; j++) {
                    row.get(j).setmNumber(row.get(j+1).getmNumber());
                }
                row.get(row.size()-1).setmNumber(0);
                return;
            }
        }
    }

    private void generateNum(){
        if (checkOver()){
            Log.e("layout","游戏结束");
            return;
        }
        if (!isFull()){
            if (isMoveHappen || isMergeHappen){
                Log.e("generateNum","生成数字");
                Random random = new Random();
                int next = random.nextInt(mColumn*mColumn);
                Game2048Item item = items[next];
                while (item.getmNumber() != 0){
                    next = random.nextInt(mColumn*mColumn);
                    item = items[next];
                }
                item.setmNumber(Math.random()>0.75?4:2);
                isMergeHappen = isMoveHappen = false;
            }
        }
    }

    private boolean checkOver(){
        if (!isFull()){
            return false;
        }
        for (int i = 0; i < mColumn; i++) {
            for (int j = 0; j < mColumn; j++) {
                int index = i*mColumn+j;
                Game2048Item item = items[index];
                if ((index+1)%mColumn != 0){
                    Game2048Item itemRight = items[index+1];
                    if (item.getmNumber() == itemRight.getmNumber()){
                        return false;
                    }
                }
                if ((index+mColumn)<mColumn*mColumn){
                    Game2048Item itemBottom = items[index+mColumn];
                    if (itemBottom.getmNumber() == item.getmNumber()){
                        return false;
                    }
                }
                if (index%mColumn != 0){
                    Game2048Item itemLeft = items[index-1];
                    if (itemLeft.getmNumber() == item.getmNumber()){
                        return false;
                    }
                }
                if (index+1>mColumn){
                    Game2048Item itemTop = items[index - mColumn];
                    if (itemTop.getmNumber() == item.getmNumber()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean isFull(){
        for (int i = 0; i < items.length; i++) {
            if (items[i].getmNumber() == 0){
                return false;
            }
        }
        return true;
    }
}
