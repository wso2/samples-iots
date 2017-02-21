/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */
package org.wso2.carbon.device.mgt.android_tv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.wso2.carbon.device.mgt.android_tv.handlers.impl.AudioHandlerImpl;
import org.wso2.carbon.device.mgt.android_tv.mqtt.AndroidSenseMQTTHandler;
import org.wso2.carbon.device.mgt.android_tv.mqtt.transport.MQTTTransportHandler;
import org.wso2.carbon.device.mgt.android_tv.util.LocalRegistry;
import org.wso2.carbon.device.mgt.android_tv.util.SenseClient;
import org.wso2.carbon.device.mgt.android_tv.util.SenseUtils;
import org.wso2.carbon.device.mgt.android_tv.util.dto.RegisterInfo;


/**
 * A login screen that offers to register the device.
 */
public class Register extends Activity {

    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mHostView;
    private View mProgressView;
    private View mLoginFormView;
    private Button deviceRegisterButton;
    private Button muteButton;
    private Button unmuteButton;
    private Handler mUiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LocalRegistry.isExist(getApplicationContext())) {
            Intent intent = new Intent(getApplicationContext(), Registered.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_register);
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mHostView = (EditText) findViewById(R.id.hostname);

        deviceRegisterButton = (Button) findViewById(R.id.device_register_button);
        muteButton = (Button) findViewById(R.id.button2);
        unmuteButton = (Button) findViewById(R.id.button3);


        deviceRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        final AudioHandlerImpl volumeHandler = new AudioHandlerImpl(getApplicationContext());

        muteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                volumeHandler.mute();
            }
        });

        unmuteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                volumeHandler.unmute();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void attemptLogin() {
        showProgress(true);
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String hostname = mHostView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password)) {
            // mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            //cancel = true;
        }
        // Check for a valid username .
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        if (TextUtils.isEmpty(username)) {
            mHostView.setError(getString(R.string.error_field_required));
            focusView = mHostView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Thread myThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    SenseClient client = new SenseClient(getApplicationContext());
                    LocalRegistry.addServerURL(getBaseContext(), hostname);
                    String deviceId = SenseUtils.generateDeviceId(getBaseContext(), getContentResolver());
                    final RegisterInfo registerStatus = client.register(username, password, deviceId, mUiHandler);
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), registerStatus.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    });

                    if (registerStatus.isRegistered()) {
                        LocalRegistry.setEnrolled(getApplicationContext(), true);
                        LocalRegistry.addUsername(getApplicationContext(), username);
                        LocalRegistry.addDeviceId(getApplicationContext(), deviceId);
                        MQTTTransportHandler mqttTransportHandler = AndroidSenseMQTTHandler.getInstance(getApplicationContext());
                        if (!mqttTransportHandler.isConnected()) {
                            mqttTransportHandler.connect();
                        }

                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), Registered.class);
                                startActivity(intent);
                            }
                        });

                    }
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showProgress(false);
                        }
                    });

                }
            });
            myThread.start();

        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            deviceRegisterButton.setVisibility(show? View.VISIBLE : View.GONE);
        }
    }

}

