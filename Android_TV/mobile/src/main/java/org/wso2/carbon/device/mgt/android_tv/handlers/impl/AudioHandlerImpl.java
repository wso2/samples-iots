package org.wso2.carbon.device.mgt.android_tv.handlers.impl;

import android.content.Context;
import android.media.AudioManager;

import org.wso2.carbon.device.mgt.android_tv.handlers.volume.AudioHandler;


public class AudioHandlerImpl implements AudioHandler {

    private Context ctx;

    public AudioHandlerImpl(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean mute() {
        AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        audioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        audioManager.setStreamMute(AudioManager.STREAM_RING, true);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        return true;
    }

    @Override
    public boolean unmute() {
        AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        audioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
        audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        audioManager.setStreamMute(AudioManager.STREAM_RING, false);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        return true;
    }
}

