package com.github.gongw;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * A view designed for inputting verification code.
 * Created by gongw on 2018/10/18.
 */

public class VerifyCodeView extends View {
    /**
     * length of text
     */
    private int vcTextLen = 4;
    /**
     * text builder
     */
    private StringBuilder vcTextBuilder;
    /**
     * text color,default color is CYAN
     */
    private int vcTextColor = Color.CYAN;
    /**
     * text size,default size is 18
     */
    private float vcTextSize = 15;
    /**
     * text font,use Typeface.DEFAULT as default
     */
    private Typeface vcTextFont = Typeface.DEFAULT;
    /**
     * paint to draw verification code text
     */
    private Paint vcTextPaint;
    /**
     * width of this view
     */
    private int mWidth;
    /**
     * height of this view
     */
    private int mHeight;
    /**
     * width of screen
     */
    private int screenWidth;
    /**
     * height of screen
     */
    private int screenHeight;
    /**
     * point array to draw text
     */
    private PointF[] vcTextCenterPoints;

    public VerifyCodeView(Context context) {
        super(context);
        init(context, null);
    }

    public VerifyCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerifyCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VerifyCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * init attributes, paint, etc
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs){
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeView);
            vcTextColor = typedArray.getColor(R.styleable.VerifyCodeView_vcTextColor, vcTextColor);
            vcTextSize = typedArray.getDimension(R.styleable.VerifyCodeView_vcTextSize, vcTextSize);
            vcTextLen = typedArray.getInt(R.styleable.VerifyCodeView_vcTextLen, vcTextLen);
            if(vcTextLen < 2){
                throw new IllegalArgumentException("The Text Length should more than 1");
            }
            String fontPath = typedArray.getString(R.styleable.VerifyCodeView_vcTextFont);
            if(!TextUtils.isEmpty(fontPath)){
                vcTextFont = Typeface.createFromAsset(context.getAssets(), fontPath);
            }
            typedArray.recycle();
        }
        vcTextBuilder = new StringBuilder(vcTextLen);
        vcTextBuilder.append("1234");
        vcTextCenterPoints = new PointF[vcTextLen];
        //init text paint
        vcTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vcTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        vcTextPaint.setColor(vcTextColor);
        vcTextPaint.setTextSize(vcTextSize);
        vcTextPaint.setTypeface(vcTextFont);
        vcTextPaint.setTextAlign(Paint.Align.CENTER);
        //receive focus while in touch mode
        setFocusableInTouchMode(true);
        //get screen width and height
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            screenWidth = displayMetrics.widthPixels;
            screenHeight = displayMetrics.heightPixels;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode == MeasureSpec.AT_MOST){
            mWidth = screenWidth * 2 / 3;
        }
        if(heightMode == MeasureSpec.AT_MOST){
            mHeight = screenHeight / 5;
        }
        //calculate text points
        calculateTextPoints();
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * calculate every text point
     */
    private void calculateTextPoints(){
        Paint.FontMetricsInt fontMetricsInt = vcTextPaint.getFontMetricsInt();
        float baseLine = mHeight / 2 + (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        for(int i=0;i<vcTextLen;i++){
            vcTextCenterPoints[i] = new PointF(i * mWidth / vcTextLen + mWidth / vcTextLen / 2, baseLine);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int realTextLen = vcTextBuilder.length();
        for(int i=0;i<vcTextLen;i++){
            if(i < realTextLen){
                canvas.drawText(vcTextBuilder.toString(), i, i+1, vcTextCenterPoints[i].x, vcTextCenterPoints[i].y, vcTextPaint);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //show keyboard to enter text
            requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //delete the last text when backspace key pressed
        if(keyCode == KeyEvent.KEYCODE_DEL && vcTextBuilder.length() > 0){
            vcTextBuilder.deleteCharAt(vcTextBuilder.length() - 1);
            invalidate();
        }else if(vcTextBuilder.length() < vcTextLen){
            vcTextBuilder.append(KeyEvent.keyCodeToString(keyCode));
            invalidate();
        }
        //hide keyboard when code is enough
        if(vcTextBuilder.length() >= vcTextLen ){
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * get verify code string
     *
     * @return code
     */
    public String getText() {
        return vcTextBuilder != null ? vcTextBuilder.toString() : "";
    }

    /**
     * set verify code
     * @param code code
     */
    public void setText(String code) {
        if (code == null)
            throw new NullPointerException("Code must not null!");
        if (code.length() > vcTextLen) {
            code = code.substring(0, vcTextLen);
        }
        vcTextBuilder = new StringBuilder();
        vcTextBuilder.append(code);
        invalidate();
    }
}
