package com.github.gongw.sms;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Telephony;

/**
 * Observer to receive changes to sms message
 * Created by gongw on 2018/11/1.
 */

public class SmsObserver extends ContentObserver {

    private Context context;
    private long lastTime;
    private ReceiveSmsMessageListener listener;

    /**
     * Creates a Sms content observer.
     * @param context The context the observer is running in.
     */
    public SmsObserver(Context context) {
        super(new Handler(Looper.getMainLooper()));
        this.context = context;
    }

    public void setReceiveSmsMessageListener(ReceiveSmsMessageListener listener){
        this.listener = listener;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        //only receive one event within 1 second
        if(System.currentTimeMillis() - lastTime < 1000){
            return;
        }
        lastTime = System.currentTimeMillis();
        //query sms message
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"),
                new String[]{Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE},
                Telephony.Sms.READ + "=?", new String[]{"0"}, Telephony.Sms.Inbox.DEFAULT_SORT_ORDER);

        if(cursor == null){
            return;
        }
        while(cursor.moveToNext()){
            String smsSender = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
            String smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY));
            if(listener != null){
                listener.onReceive(smsSender, smsBody);
                break;
            }
        }

        if(!cursor.isClosed()){
            cursor.close();
        }
    }

    /**
     * register this content observer to observe change of sms message inbox
     */
    public void register(){
        context.getContentResolver().registerContentObserver(Uri.parse("content://sms/inbox"), true, this);
    }

    /**
     * unregister this content observer
     */
    public void unregister(){
        context.getContentResolver().unregisterContentObserver(this);
    }
}
