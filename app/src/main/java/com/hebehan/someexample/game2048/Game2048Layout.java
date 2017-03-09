package com.hebehan.someexample.game2048;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.widget.RelativeLayout;

/**
 * Created by Hebe on 2017/3/9.
 */

public class Game2048Layout extends RelativeLayout {

    private int mColumn = 4;
    private Game2048Item[] items;
    private int mMargin = 10;
    private int padding;
    private GestureDetector mGestureDetector;
    private boolean isMergeHappen;
    private boolean isMoveHappen;
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
        mGestureDetector = new GestureDetector(context,)
    }
}
