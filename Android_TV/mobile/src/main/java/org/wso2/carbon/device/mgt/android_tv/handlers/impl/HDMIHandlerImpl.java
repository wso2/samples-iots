package org.wso2.carbon.device.mgt.android_tv.handlers.impl;

import android.content.Context;

import org.wso2.carbon.device.mgt.android_tv.handlers.hdmi.HDMIHandler;


public class HDMIHandlerImpl implements HDMIHandler {
    private Context ctx;

    public HDMIHandlerImpl(Context context) {
        this.ctx = context;

    }

    @Override
    public boolean enable() {
        return false;
    }

    @Override
    public boolean disable() {
        return false;
    }
}
