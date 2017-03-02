package org.wso2.carbon.device.mgt.androidtv.agent;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import org.wso2.carbon.device.mgt.androidtv.agent.constants.TVConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class VideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        String url = getIntent().getStringExtra(TVConstants.MESSAGE);

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        try {
            videoView.setVideoURI(Uri.parse(URLDecoder.decode(url, "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            Log.e("VideoActivity", "Unable to parse url", e);
            Toast.makeText(getApplicationContext(), "Unable to play video. " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        videoView.start();
    }

}
