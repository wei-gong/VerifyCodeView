package com.github.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.github.gongw.VerifyCodeView;
import com.github.gongw.sms.SmsVerifyCodeFilter;

/**
 * Created by gongw on 2018/11/2.
 */

public class AutoFilledFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auto_filled, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Switch switcher = view.findViewById(R.id.switch1);
        final VerifyCodeView verifyCodeView = view.findViewById(R.id.verifyCodeView2);
        final SmsVerifyCodeFilter filter = new SmsVerifyCodeFilter();
//        filter.setSmsSenderStart("1096");
//        filter.setSmsSenderContains("5225");
        filter.setSmsBodyStart("验证短信：");
        filter.setSmsBodyContains("验证码");
        filter.setVerifyCodeCount(verifyCodeView.getVcTextCount());
        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    verifyCodeView.startListen(filter);
                }else{
                    verifyCodeView.stopListen();
                }
            }
        });
    }
}
