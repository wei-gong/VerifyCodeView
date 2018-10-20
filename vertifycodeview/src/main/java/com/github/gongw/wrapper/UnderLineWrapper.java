package com.github.gongw.wrapper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * A under line Wrapper for VerifyCodeView
 * Created by gongw on 2018/10/19.
 */

public class UnderLineWrapper implements VerifyCodeWrapper {

    @Override
    public boolean isCovered() {
        //the under line and verify code will display together
        return false;
    }

    @Override
    public void drawWrapper(Canvas canvas, Paint paint, RectF rectF, RectF textRectF) {
        //make under line width always twice of text width
        canvas.drawLine(textRectF.left - textRectF.width()/2, rectF.bottom, textRectF.right + textRectF.width() / 2, rectF.bottom, paint);
    }

}
