package org.wso2.carbon.device.mgt.androidtv.agent;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        videoView.start();
    }

}
