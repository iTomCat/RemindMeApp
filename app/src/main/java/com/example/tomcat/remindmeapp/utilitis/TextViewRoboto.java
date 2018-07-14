package com.example.tomcat.remindmeapp.utilitis;

import android.content.Context;
import android.util.AttributeSet;
import com.example.tomcat.remindmeapp.MainActivity;

/**
 * TextView with Roboto Font
 */

public class TextViewRoboto extends android.support.v7.widget.AppCompatTextView {

    public TextViewRoboto(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewRoboto(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewRoboto(Context context) {
        super(context);
        init();
    }

    public void init() {
        //setTextSize(28);
        setTypeface(MainActivity.robotoFont);
        //setTextColor(MainActivity.mainColor);
    }
}
