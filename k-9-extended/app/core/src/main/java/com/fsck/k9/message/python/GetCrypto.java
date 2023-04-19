package com.fsck.k9.message.python;

import android.content.Context;
import android.util.Log;

import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;

public class GetCrypto {
    private Context mContext;

    public GetCrypto(Context context) {
        mContext = context;
    }

    public String runPythonScript(String message, String key) {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(mContext));
        }

        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("main");
        PyObject result = pyObject.callAttr("encrypt", message, key );

        String output = result.toString();

        return output;
    }
}
