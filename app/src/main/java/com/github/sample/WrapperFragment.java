package com.github.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.github.gongw.VerifyCodeView;

/**
 * Created by gongw on 2018/11/2.
 */

public class WrapperFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wrapper, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        VerifyCodeView underLine = view.findViewById(R.id.vc_underLine);
        VerifyCodeView centerLine = view.findViewById(R.id.vc_centerLine);
        VerifyCodeView square = view.findViewById(R.id.vc_square);
        VerifyCodeView circle = view.findViewById(R.id.vc_circle);
        VerifyCodeView.OnAllFilledListener listener = new VerifyCodeView.OnAllFilledListener() {
            @Override
            public void onAllFilled(String text) {
                Toast.makeText(getContext(), "filled by "+text, Toast.LENGTH_SHORT).show();
            }
        };
        underLine.setOnAllFilledListener(listener);
        centerLine.setOnAllFilledListener(listener);
        square.setOnAllFilledListener(listener);
        circle.setOnAllFilledListener(listener);
    }
}
