package com.github.gongw.sms;

/**
 * Listener to receive call backs when catch a sms message
 * Created by gongw on 2018/11/1.
 */

public interface ReceiveSmsMessageListener {

    void onReceive(String smsSender, String smsBody);

}
