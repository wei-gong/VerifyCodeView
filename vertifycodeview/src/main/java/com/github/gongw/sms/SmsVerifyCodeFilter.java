package com.github.gongw.sms;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains filter conditions and methods to get verify code from a sms message
 * Created by gongw on 2018/11/1.
 */

public class SmsVerifyCodeFilter {
    /**
     * filter condition of sms message body starts with
     */
    private String smsBodyStart;
    /**
     * filter condition of sms message body contains
     */
    private String smsBodyContains;
    /**
     * filter condition of sms message sender starts with
     */
    private String smsSenderStart;
    /**
     * filter condition of sms message sender contains
     */
    private String smsSenderContains;
    /**
     * the count of sms message verify code
     */
    private int verifyCodeCount;

    /**
     * get verify code fits all filter conditions from the sms message
     * @param smsSender sender of sms message
     * @param smsBody body of sms message
     * @return the verify code fits all filter conditions, null if no verify code found
     */
    public String filterVerifyCode(String smsSender, String smsBody){
        if(TextUtils.isEmpty(smsSender) || TextUtils.isEmpty(smsBody)){
            return null;
        }
        if(!checkSmsSender(smsSender) || !checkSmsBody(smsBody)){
            return null;
        }
        String regex = "(\\d{" + verifyCodeCount + "})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(smsBody);
        if(matcher.find()){
            return matcher.group(0);
        }else{
            return null;
        }
    }

    /**
     * check whether the sms message sender fits the sender conditions
     * @param smsSender the sender of sms message
     * @return true if fits, false if not
     */
    public boolean checkSmsSender(String smsSender){
        if(TextUtils.isEmpty(smsSender)){
            return false;
        }
        if(!TextUtils.isEmpty(smsSenderStart) && !smsSender.startsWith(smsSenderStart)){
            return false;
        }
        if(!TextUtils.isEmpty(smsSenderContains) && !smsSender.contains(smsSenderContains)){
            return false;
        }
        return true;
    }

    /**
     * check whether the sms message body fits the body conditions
     * @param smsBody the body of sms message
     * @return true if fits, false if not
     */
    public boolean checkSmsBody(String smsBody){
        if(TextUtils.isEmpty(smsBody)){
            return false;
        }
        if(!TextUtils.isEmpty(smsBodyStart) && !smsBody.startsWith(smsBodyStart)){
            return false;
        }
        if(!TextUtils.isEmpty(smsBodyContains) && !smsBody.contains(smsBodyContains)){
            return false;
        }
        return true;
    }

    public String getSmsBodyStart() {
        return smsBodyStart;
    }

    public void setSmsBodyStart(String smsBodyStart) {
        this.smsBodyStart = smsBodyStart;
    }

    public String getSmsBodyContains() {
        return smsBodyContains;
    }

    public void setSmsBodyContains(String smsBodyContains) {
        this.smsBodyContains = smsBodyContains;
    }

    public String getSmsSenderStart() {
        return smsSenderStart;
    }

    public void setSmsSenderStart(String smsSenderStart) {
        this.smsSenderStart = smsSenderStart;
    }

    public String getSmsSenderContains() {
        return smsSenderContains;
    }

    public void setSmsSenderContains(String smsSenderContains) {
        this.smsSenderContains = smsSenderContains;
    }

    public int getVerifyCodeCount() {
        return verifyCodeCount;
    }

    public void setVerifyCodeCount(int verifyCodeCount) {
        this.verifyCodeCount = verifyCodeCount;
    }
}
