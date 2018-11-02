package com.github.gongw.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

/**
 * Broadcast receiver to receive sms message,when a sms message caught,pass it to ReceiveSmsMessageListener
 * Created by gongw on 2018/10/31.
 */

public class SmsReceiver extends BroadcastReceiver {

    private ReceiveSmsMessageListener listener;

    public void setReceiveSmsMessageListener(ReceiveSmsMessageListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())){
            SmsMessage sms = getSmsMessage(intent);
            if(sms == null){
                return;
            }
            String smsSender = sms.getOriginatingAddress();
            String smsBody = sms.getMessageBody();
            if(listener != null){
                listener.onReceive(smsSender, smsBody);
            }
        }
    }

    /**
     * get sms message info from intent
     * @param intent intent obtains sms message info
     * @return SmsMessage object
     */
    private SmsMessage getSmsMessage(Intent intent) {
        SmsMessage[] messages;
        if (Build.VERSION.SDK_INT >= 19) {
            messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        } else {
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");
            if ((pdus == null) || (pdus.length == 0)) {
                return null;
            }
            messages = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
        }

        if(messages == null || messages.length == 0){
            return null;
        }else{
            return messages[0];
        }
    }

    /**
     * register broadcast receiver to receive sms message
     * @param context the context broadcast receiver running in
     */
    public void register(Context context){
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        context.registerReceiver(this, filter);
    }

    /**
     * unregister this broadcast receiver
     * @param context
     */
    public void unregister(Context context){
        context.unregisterReceiver(this);
    }

}
