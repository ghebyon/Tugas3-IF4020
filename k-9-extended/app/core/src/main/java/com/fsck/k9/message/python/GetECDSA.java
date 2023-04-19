package com.fsck.k9.message.python;


import android.content.Context;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.sax.SAXResult;

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

    public List<String> generateKeyPair() {
        List<String> output = new ArrayList<>();
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(mContext));
        }

        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("main");
        PyObject result = pyObject.callAttr("generateKeyPair");

        List<PyObject> keyPair = result.asList();

        output.add(keyPair.get(0).toString());
        output.add(keyPair.get(1).toString());
        output.add(keyPair.get(2).toString());

        return output;
    }
}
