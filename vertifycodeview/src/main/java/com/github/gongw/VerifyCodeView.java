package com.github.gongw;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import com.github.gongw.sms.ReceiveSmsMessageListener;
import com.github.gongw.sms.SmsObserver;
import com.github.gongw.sms.SmsReceiver;
import com.github.gongw.sms.SmsVerifyCodeFilter;
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
     * verification code text size by sp
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
     * divider width by dp between verify code item
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
    private int vcNextWrapperColor = Color.BLACK;
    /**
     * the stroke width of wrapper by dp
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

    public VerifyCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerifyCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
            vcTextSize = typedArray.getDimension(R.styleable.VerifyCodeView_vcTextSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, vcTextSize, context.getResources().getDisplayMetrics()));
            vcDividerWidth = typedArray.getDimension(R.styleable.VerifyCodeView_vcDividerWidth, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, vcDividerWidth, context.getResources().getDisplayMetrics()));
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
            vcWrapperStrokeWidth = typedArray.getDimension(R.styleable.VerifyCodeView_vcWrapperStrokeWidth, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, vcWrapperStrokeWidth, context.getResources().getDisplayMetrics()));
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
        //init and set paint config
        initPaint();
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
        initPaint();
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

    /**
     * init and set paint config
     */
    private void initPaint(){
        //init text paint
        if(vcTextPaint == null){
            vcTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        vcTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        vcTextPaint.setColor(vcTextColor);
        vcTextPaint.setTextSize(vcTextSize);
        vcTextPaint.setTypeface(vcTextFont);
        vcTextPaint.setTextAlign(Paint.Align.CENTER);
        //init wrapper paint
        if(vcWrapperPaint == null){
            vcWrapperPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        vcWrapperPaint.setStyle(Paint.Style.STROKE);
        vcWrapperPaint.setColor(vcWrapperColor);
        vcWrapperPaint.setStrokeWidth(vcWrapperStrokeWidth);
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
            if(onTextChangedListener != null){
                onTextChangedListener.onTextChanged(vcTextBuilder.toString());
            }
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
     * get verify code text color
     * @return verify code text color
     */
    public int getVcTextColor() {
        return vcTextColor;
    }

    /**
     * set verify code text color
     * @param vcTextColor verify code text color
     */
    public void setVcTextColor(int vcTextColor) {
        if(this.vcTextColor == vcTextColor){
            return;
        }
        this.vcTextColor = vcTextColor;
        invalidate();
    }

    /**
     * get verify code text size by sp
     * @return verify code text size by sp
     */
    public float getVcTextSize() {
        return vcTextSize;
    }

    /**
     * set verify code text size by sp
     * @param vcTextSize verify code text size by sp
     */
    public void setVcTextSize(float vcTextSize) {
        if(this.vcTextSize == vcTextSize){
            return;
        }
        this.vcTextSize = vcTextSize;
        invalidate();
    }

    /**
     * get verify code item divider width by dp
     * @return verify code item divider width by dp
     */
    public float getVcDividerWidth() {
        return vcDividerWidth;
    }

    /**
     * set verify code item divider width by dp
     * @param vcDividerWidth verify code item divider width by dp
     */
    public void setVcDividerWidth(float vcDividerWidth) {
        if(this.vcDividerWidth == vcDividerWidth){
            return;
        }
        this.vcDividerWidth = vcDividerWidth;
        invalidate();
    }

    /**
     * get verify code wrapper color
     * @return verify code wrapper color
     */
    public int getVcWrapperColor() {
        return vcWrapperColor;
    }

    /**
     * set verify code wrapper color
     * @param vcWrapperColor verify code wrapper color
     */
    public void setVcWrapperColor(int vcWrapperColor) {
        if(this.vcWrapperColor == vcWrapperColor){
            return;
        }
        this.vcWrapperColor = vcWrapperColor;
        invalidate();
    }

    /**
     * get the next filled verify code wrapper color
     * @return the next filled verify code wrapper color
     */
    public int getVcNextWrapperColor() {
        return vcNextWrapperColor;
    }

    /**
     * set the next filled verify code wrapper color
     * @param vcNextWrapperColor the next filled verify code wrapper color
     */
    public void setVcNextWrapperColor(int vcNextWrapperColor) {
        if(this.vcNextWrapperColor == vcNextWrapperColor){
            return;
        }
        this.vcNextWrapperColor = vcNextWrapperColor;
        invalidate();
    }

    /**
     * get verify code wrapper stroke width by dp
     * @return the verify code wrapper stroke width by dp
     */
    public float getVcWrapperStrokeWidth() {
        return vcWrapperStrokeWidth;
    }

    /**
     * set verify code wrapper stroke width by dp
     * @param vcWrapperStrokeWidth the verify code wrapper stroke width by dp
     */
    public void setVcWrapperStrokeWidth(float vcWrapperStrokeWidth) {
        if(this.vcWrapperStrokeWidth == vcWrapperStrokeWidth){
            return;
        }
        this.vcWrapperStrokeWidth = vcWrapperStrokeWidth;
        invalidate();
    }

    /**
     * get verify code string
     * @return the verify code string
     */
    public String getVcText() {
        return vcTextBuilder != null ? vcTextBuilder.toString() : "";
    }

    /**
     * set verify code text
     * @param code the verify code string
     */
    public void setVcText(String code) {
        if (code == null)
            throw new NullPointerException("Code must not null!");
        if (code.length() > vcTextCount) {
            code = code.substring(0, vcTextCount);
        }
        if(vcTextBuilder.toString().equals(code)){
            return;
        }
        vcTextBuilder = new StringBuilder();
        vcTextBuilder.append(code);
        if(onTextChangedListener != null){
            onTextChangedListener.onTextChanged(vcTextBuilder.toString());
        }
        if(code.length() >= vcTextCount && onAllFilledListener != null){
            onAllFilledListener.onAllFilled(vcTextBuilder.toString());
        }
        invalidate();
    }

    /**
     * set verify code text font
     * @param path relative path from assets directory
     */
    public void setVcTextFont(String path){
        this.vcTextFont = Typeface.createFromAsset(getContext().getAssets(), path);
        invalidate();
    }

    /**
     * set verify code text typeface
     * @param vcTextFont verify code text typeface
     */
    public void setVcTextFont(Typeface vcTextFont) {
        this.vcTextFont = vcTextFont;
        invalidate();
    }

    /**
     * set verify code count
     * @param vcTextCount verify code count
     */
    public void setVcTextCount(int vcTextCount){
        if(this.vcTextCount == vcTextCount){
            return;
        }
        this.vcTextCount = vcTextCount;
        invalidate();
    }

    /**
     * get verify code count
     * @return verify code count
     */
    public int getVcTextCount() {
        return vcTextCount;
    }

    /**
     * clear all verify code text
     */
    public void clearVcText(){
        if(vcTextBuilder.length() == 0){
            return;
        }
        vcTextBuilder.delete(0, vcTextBuilder.length()-1);
        if(onTextChangedListener != null){
            onTextChangedListener.onTextChanged(vcTextBuilder.toString());
        }
        invalidate();
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
     * @param hide hide key board automatically when verify code all filled
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
         * this method is called after verify code item all filled, you can write your verify logic here
         * @param text text after changed
         */
        void onAllFilled(String text);
    }

    /**
     * register a callback to be invoked after verify code text changed
     * @param onTextChangedListener the callback that will run
     */
    public void setOnTextChangedListener(OnTextChangedListener onTextChangedListener){
        this.onTextChangedListener = onTextChangedListener;
    }

    /**
     * register a callback to be invoked after verify code item all filled
     * @param onAllFilledListener the callback that will run
     */
    public void setOnAllFilledListener(OnAllFilledListener onAllFilledListener){
        this.onAllFilledListener = onAllFilledListener;
    }

    /**
     * the broadcast receiver to receive sms message
     */
    private SmsReceiver smsReceiver;
    /**
     * the content observer to observe sms message change
     */
    private SmsObserver smsObserver;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //stop listening sms message when detached
        stopListen();
    }

    /**
     * register a broadcast receiver to receive sms message,
     * read the sms message and filter out the verify code to filled verifycodeview
     * @param filter policy to filter verify code from sms message
     */
    public void startListen(final SmsVerifyCodeFilter filter){
        //check RECEIVE_SMS READ_SMS permissions, if not granted then request
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[] {Manifest.permission.RECEIVE_SMS}, 0);
        }
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) getContext(), new String[] {Manifest.permission.READ_SMS}, 1);
        }
        //get verify code by receiving sms message broadcast
        if(smsReceiver == null){
            smsReceiver = new SmsReceiver();
        }
        smsReceiver.setReceiveSmsMessageListener(new ReceiveSmsMessageListener() {
            @Override
            public void onReceive(String smsSender, String smsBody) {
                String verifyCode = filter.filterVerifyCode(smsSender, smsBody);
                if(verifyCode != null){
                    setVcText(verifyCode);
                }
            }
        });
        smsReceiver.register(getContext());
        //get verify code by observing sms message content
        if(smsObserver == null){
            smsObserver = new SmsObserver(getContext());
        }
        smsObserver.setReceiveSmsMessageListener(new ReceiveSmsMessageListener() {
            @Override
            public void onReceive(String smsSender, String smsBody) {
                String verifyCode = filter.filterVerifyCode(smsSender, smsBody);
                if(verifyCode != null){
                    setVcText(verifyCode);
                }
            }
        });
        smsObserver.register();
    }

    /**
     * unregister the sms message receiver
     */
    public void stopListen(){
        if(smsReceiver != null){
            smsReceiver.unregister(getContext());
            smsReceiver = null;
        }
        if(smsObserver != null){
            smsObserver.unregister();
            smsObserver = null;
        }
    }
}
