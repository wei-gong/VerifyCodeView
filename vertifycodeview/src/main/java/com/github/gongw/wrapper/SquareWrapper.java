package com.github.gongw.wrapper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * A square Wrapper for VerifyCodeView
 * Created by gongw on 2018/10/19.
 */

public class SquareWrapper implements VerifyCodeWrapper {

    @Override
    public boolean isCovered() {
        //the square and verify code will display together
        return false;
    }

    @Override
    public void drawWrapper(Canvas canvas, Paint paint, RectF rectF, RectF textRectF) {
        //create a square rect
        RectF square = rectF;
        if(rectF.width() > rectF.height()){
            float left = rectF.left + (rectF.width() - rectF.height()) / 2;
            float right = rectF.right -  (rectF.width() - rectF.height()) / 2;
            square = new RectF(left, rectF.top, right, rectF.bottom);
        }else if(rectF.width() < rectF.height()){
            float top = rectF.top + (rectF.height() - rectF.width()) / 2;
            float bottom = rectF.bottom - (rectF.height() - rectF.width()) / 2;
            square = new RectF(rectF.left, top, rectF.right, bottom);
        }
        canvas.drawRect(square, paint);
    }
}
