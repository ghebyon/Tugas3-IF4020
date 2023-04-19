package com.fsck.k9.message.python;


import android.content.Context;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.List;

public class GetECDSA {
    private Context mContext;

    public GetECDSA(Context context) {
        mContext = context;
    }

    public String sign(String message, String privateKey) {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(mContext));
        }

        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("main");
        PyObject result = pyObject.callAttr("sign", message, privateKey);

        String output = result.toString();

        return output;
    }
}
