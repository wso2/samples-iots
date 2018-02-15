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

package org.wso2.androidtv.agent.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.wso2.androidtv.agent.MessageActivity;
import org.wso2.androidtv.agent.constants.TVConstants;
import org.wso2.androidtv.agent.siddhiSources.TextEdgeSource;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
//import org.wso2.extension.siddhi.map.text.sourcemapper.TextSourceMapper;
//import org.wso2.siddhi.extension.input.mapper.text.TextSourceMapper;

public class SiddhiService extends Service {

    private static final String TAG = SiddhiService.class.getSimpleName();

    public static final int MESSAGE_FROM_SIDDHI_SERVICE_ALERT_QUERY = 1;
    public static final int MESSAGE_FROM_SIDDHI_SERVICE_TEMPERATURE_QUERY = 2;
    public static final int MESSAGE_FROM_SIDDHI_SERVICE_HUMIDITY_QUERY = 3;
    public static final int MESSAGE_FROM_SIDDHI_SERVICE_WINDOW_QUERY = 4;
    public static final int MESSAGE_FROM_SIDDHI_SERVICE_AC_QUERY = 5;
    public static final int MESSAGE_FROM_SIDDHI_SERVICE_KEYCARD_QUERY = 6;
    private SiddhiManager siddhiManager;
    private SiddhiAppRuntime siddhiAppRuntime;
    private InputHandler inputHandler;
    private Handler mHandler;
    private IBinder binder = new SiddhiBinder();
    private String executionPlan = "";

    public SiddhiService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service.");
        if (intent.hasExtra(TVConstants.EXECUTION_PLAN_EXTRA)) {
            executionPlan = intent.getExtras().getString(TVConstants.EXECUTION_PLAN_EXTRA);
        }
        if (siddhiManager != null && executionPlan != null) {
            invokeExecutionPlan(executionPlan);
        }
        return Service.START_STICKY;
    }

    private void invokeExecutionPlan(String executionPlan) {
        if (siddhiAppRuntime != null) {
            siddhiAppRuntime.shutdown();
            siddhiAppRuntime = null;
            Log.d(TAG, "Shutting down existing execution plan");
        }
        siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(executionPlan);
        siddhiAppRuntime.start();

        //method to show alert messages
        siddhiAppRuntime.addCallback("alertStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                System.out.println("alertEvent :"+events[0].getData(0));
                String alertMsg = events[0].getData(0).toString();
                showAlert(MessageActivity.class, alertMsg);
            }
        });
        Log.i(TAG, "Starting execution plan.");

    }

    @Override
    public void onCreate() {
        if (siddhiManager == null){
            siddhiManager = new SiddhiManager();
            siddhiManager.setExtension("source:textEdge",TextEdgeSource.class);
            Log.i(TAG, "Siddhi Service created.");
        }
    }

    @Override
    public void onDestroy() {
        if (siddhiManager != null && siddhiAppRuntime != null) {
            siddhiAppRuntime.shutdown();
            Log.i(TAG, "Shutting down execution plan.");
        }
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    private void setInputHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    class SiddhiBinder extends Binder {
        SiddhiService getService() {
            return SiddhiService.this;
        }
    }

    private void showAlert(Class<?> cls, String extra) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(TVConstants.MESSAGE, extra);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
