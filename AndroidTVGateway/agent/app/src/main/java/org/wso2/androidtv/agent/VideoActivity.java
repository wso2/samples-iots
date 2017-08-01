/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.androidtv.agent;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.commons.codec.binary.Hex;
import org.wso2.androidtv.agent.constants.TVConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

public class VideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        String url = getIntent().getStringExtra(TVConstants.MESSAGE);

        VideoView videoView = (VideoView) findViewById(R.id.videoView);

        //regex to check whether the URL belongs to YouTube
        String checkYouTubeRegEx = "^(http%3A%2F%2F|https%3A%2F%2F)(youtu\\.be|www\\.youtube\\." +
                "com)(.*)";
        //checks whether the URL belongs to YouTube
        boolean isYouTubeURL = Pattern.matches(checkYouTubeRegEx, url);


        if (isYouTubeURL) {
            //ID given for the video by YouTube
            String youTubeVideoID = url.substring(url.length() - 11);
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" +
                    youTubeVideoID));
            startActivity(appIntent);
        } else {
            try {
                videoView.setVideoURI(Uri.parse(URLDecoder.decode(url, "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                Log.e("VideoActivity", "Unable to parse url", e);
                Toast.makeText(getApplicationContext(), "Unable to play video. " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        videoView.start();
    }

}