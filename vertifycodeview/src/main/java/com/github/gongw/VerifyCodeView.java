package com.github.gongw;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import com.github.gongw.wrapper.CenterLineWrapper;
import com.github.gongw.wrapper.CircleWrapper;
import com.github.gongw.wrapper.SquareWrapper;
import com.github.gongw.wrapper.UnderLineWrapper;
import com.github.gongw.wrapper.VerifyCodeWrapper;

/**
 * A view designed for inputting verification code.
 * Created by gongw on 2018/10/18.
 */

public class VerifyCodeView extends View {
    /**
     * verification code text count
     */
    private int vcTextCount = 4;
    /**
     * verification code text builder
     */
    private StringBuilder vcTextBuilder;
    /**
     * verification code text color
     */
    private int vcTextColor = Color.BLACK;
    /**
     * verification code text size
     */
    private float vcTextSize = 36;
    /**
     * verification code text font
     */
    private Typeface vcTextFont = Typeface.DEFAULT;
    /**
     * paint to draw verification code text
     */
    private Paint vcTextPaint;
    /**
     * divider width of every verify code
     */
    private float vcDividerWidth = 6;
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
     * every verification code text position
     */
    private PointF[] vcTextPositions;
    /**
     * the wrapper contains verify code
     */
    private VerifyCodeWrapper vcWrapper;
    /**
     * the color of wrapper contains verify code
     */
    private int vcWrapperColor = Color.BLACK;
    /**
     * the color of wrapper which is the next one to be filled
     */
    private int vcNextWrapperColor = Color.GREEN;
    /**
     * the stroke width of wrapper
     */
    private float vcWrapperStrokeWidth = 1;
    /**
     * paint to draw verify code wrapper
     */
    private Paint vcWrapperPaint;
    /**
     * the value of under line wrapper
     */
    private static final int WRAPPER_UNDER_LINE = 0;
    /**
     * the value of center line wrapper
     */
    private static final int WRAPPER_CENTER_LINE = 1;
    /**
     * the value of square wrapper
     */
    private static final int WRAPPER_SQUARE = 2;
    /**
     * the value of circle wrapper
     */
    private static final int WRAPPER_CIRCLE = 3;
    /**
     * outer boundaries of every verify code
     */
    private RectF[] vcOuterRects;
    /**
     * boundaries of every verify code
     */
    private RectF[] vcTextRects;
    /**
     * hide key board automatically when verify code all filled
     */
    private boolean autoHideKeyboard = true;
    /**
     * after verify code text changed, its method will be called
     */
    private OnTextChangedListener onTextChangedListener;
    /**
     * after verify code item all filled, its method will be called
     */
    private OnAllFilledListener onAllFilledListener;

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
     * @param context The Context the view is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    private void init(Context context, AttributeSet attrs){
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerifyCodeView);
            vcTextCount = typedArray.getInt(R.styleable.VerifyCodeView_vcTextCount, vcTextCount);
            if(vcTextCount < 2){
                throw new IllegalArgumentException("The Text Length should more than 1");
            }
            vcTextColor = typedArray.getColor(R.styleable.VerifyCodeView_vcTextColor, vcTextColor);
            vcTextSize = typedArray.getDimension(R.styleable.VerifyCodeView_vcTextSize, vcTextSize);
            vcDividerWidth = typedArray.getDimension(R.styleable.VerifyCodeView_vcDividerWidth, vcDividerWidth);
            int wrapperValue = typedArray.getInt(R.styleable.VerifyCodeView_vcWrapper, WRAPPER_UNDER_LINE);
            switch (wrapperValue){
                default:
                case WRAPPER_UNDER_LINE:
                    vcWrapper = new UnderLineWrapper();
                    break;
                case WRAPPER_CENTER_LINE:
                    vcWrapper = new CenterLineWrapper();
                    break;
                case WRAPPER_SQUARE:
                    vcWrapper = new SquareWrapper();
                    break;
                case WRAPPER_CIRCLE:
                    vcWrapper = new CircleWrapper();
                    break;
            }
            vcWrapperStrokeWidth = typedArray.getDimension(R.styleable.VerifyCodeView_vcWrapperStrokeWidth, vcWrapperStrokeWidth);
            vcWrapperColor = typedArray.getColor(R.styleable.VerifyCodeView_vcWrapperColor, vcWrapperColor);
            vcNextWrapperColor = typedArray.getColor(R.styleable.VerifyCodeView_vcNextWrapperColor, vcNextWrapperColor);
            String fontPath = typedArray.getString(R.styleable.VerifyCodeView_vcTextFont);
            if(!TextUtils.isEmpty(fontPath)){
                vcTextFont = Typeface.createFromAsset(context.getAssets(), fontPath);
            }
            typedArray.recycle();
        }
        vcTextBuilder = new StringBuilder(vcTextCount);
        vcTextPositions = new PointF[vcTextCount];
        vcOuterRects = new RectF[vcTextCount];
        vcTextRects = new RectF[vcTextCount];
        //init text paint
        vcTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vcTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        vcTextPaint.setColor(vcTextColor);
        vcTextPaint.setTextSize(vcTextSize);
        vcTextPaint.setTypeface(vcTextFont);
        vcTextPaint.setTextAlign(Paint.Align.CENTER);
        //init wrapper paint
        vcWrapperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vcWrapperPaint.setStyle(Paint.Style.STROKE);
        vcWrapperPaint.setColor(vcWrapperColor);
        vcWrapperPaint.setStrokeWidth(vcWrapperStrokeWidth);
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
        //calculate positions and boundaries
        calculatePositions();
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * calculate boundaries and position of every verify code item
     */
    private void calculatePositions(){
        Paint.FontMetricsInt fontMetricsInt = vcTextPaint.getFontMetricsInt();
        float textWidth = vcTextPaint.measureText("8");
        float textHeight = fontMetricsInt.bottom - fontMetricsInt.top;
        float baseLine = mHeight / 2 + textHeight / 2 - fontMetricsInt.bottom;
        float vcItemWidth = (mWidth - (vcTextCount - 1) * vcDividerWidth) / vcTextCount;
        for(int i = 0; i< vcTextCount; i++){
            //calculate verify code text position, keep text at center
            vcTextPositions[i] = new PointF(i * vcItemWidth + i * vcDividerWidth + vcItemWidth / 2, baseLine);
            //calculate verify code item boundary
            vcOuterRects[i] = new RectF(i * vcItemWidth + i * vcDividerWidth, 0, (i + 1) * vcItemWidth + i * vcDividerWidth, mHeight);
            //calculate verify code text boundary
            vcTextRects[i] = new RectF(vcTextPositions[i].x - textWidth / 2, vcTextPositions[i].y + fontMetricsInt.top, vcTextPositions[i].x + textWidth / 2, vcTextPositions[i].y + fontMetricsInt.bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int realTextLen = vcTextBuilder.length();
        for(int i = 0; i< vcTextCount; i++){
            if(i < realTextLen){
                canvas.drawText(vcTextBuilder.toString(), i, i+1, vcTextPositions[i].x, vcTextPositions[i].y, vcTextPaint);
            }
            if(vcWrapper != null){
                vcWrapperPaint.setColor(hasFocus() && i == realTextLen ? vcNextWrapperColor: vcWrapperColor);
                //draw wrapper if it is not covered
                if(!vcWrapper.isCovered() || i >= realTextLen){
                    vcWrapper.drawWrapper(canvas, vcWrapperPaint, vcOuterRects[i], vcTextRects[i]);
                }
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            requestFocus();
            //show keyboard to enter text
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
            }
        }
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        //define keyboard to number keyboard
        BaseInputConnection fic = new BaseInputConnection(this, false) {
            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
        };
        outAttrs.actionLabel = null;
        outAttrs.inputType = InputType.TYPE_CLASS_PHONE;
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NEXT;
        return fic;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //delete the last code when backspace key pressed
        if(keyCode == KeyEvent.KEYCODE_DEL && vcTextBuilder.length() > 0){
            vcTextBuilder.deleteCharAt(vcTextBuilder.length() - 1);
            invalidate();
        }else if(keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9 && vcTextBuilder.length() < vcTextCount){
            //only add number code to builder
            vcTextBuilder.append(event.getDisplayLabel());
            if(onTextChangedListener != null){
                onTextChangedListener.onTextChanged(vcTextBuilder.toString());
            }
            invalidate();
        }
        if(vcTextBuilder.length() >= vcTextCount && autoHideKeyboard){
            if(onAllFilledListener != null){
                onAllFilledListener.onAllFilled(vcTextBuilder.toString());
            }
            clearFocus();
            //hide keyboard when code is enough
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
    public String getVcText() {
        return vcTextBuilder != null ? vcTextBuilder.toString() : "";
    }

    /**
     * set verify code text
     * @param code code
     */
    public void setVcText(String code) {
        if (code == null)
            throw new NullPointerException("Code must not null!");
        if (code.length() > vcTextCount) {
            code = code.substring(0, vcTextCount);
        }
        vcTextBuilder = new StringBuilder();
        vcTextBuilder.append(code);
        invalidate();
    }

    /**
     * set verify code count
     * @param count verify code count
     */
    public void setVcTextCount(int count){
        this.vcTextCount = count;
        invalidate();
    }

    /**
     * clear all verify code text
     */
    public void clearText(){
        if(vcTextBuilder.length() > 0){
            vcTextBuilder.delete(0, vcTextBuilder.length()-1);
        }
    }

    /**
     * set wrapper for VerifyCodeView
     * @param vcWrapper Wrapper for VerifyCodeView
     */
    public void setVcWrapper(VerifyCodeWrapper vcWrapper){
        this.vcWrapper = vcWrapper;
        invalidate();
    }

    /**
     * whether to hide key board automatically when verify code all filled
     * @param hide
     */
    public void setAutoHideKeyboard(boolean hide){
        this.autoHideKeyboard = hide;
    }

    /**
     * after verify code text changed, its method will be called
     */
    public interface OnTextChangedListener{
        /**
         * this method is called after verify code text changed
         * @param text text after changed
         */
        void onTextChanged(String text);
    }

    /**
     * after verify code item all filled, its method will be called
     */
    public interface OnAllFilledListener{
        /**
         * this method is called after verify code item all filled,
         * you can write your verify logic here
         * @param text text after changed
         */
        void onAllFilled(String text);
    }

    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener){
        this.onTextChangedListener = onTextChangedListener;
    }

    public void setOnAllFilledListener(OnAllFilledListener onAllFilledListener){
        this.onAllFilledListener = onAllFilledListener;
    }
}
