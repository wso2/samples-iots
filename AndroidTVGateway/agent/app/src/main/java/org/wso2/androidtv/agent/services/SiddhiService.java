package org.wso2.androidtv.agent.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.wso2.androidtv.agent.constants.TVConstants;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;

public class SiddhiService extends Service {

    private static final String TAG = SiddhiService.class.getSimpleName();

    public static final int MESSAGE_FROM_SIDDHI_SERVICE_QUERY = 1;
    private SiddhiManager siddhiManager;
    private ExecutionPlanRuntime executionPlanRuntime;
    private InputHandler inputHandler;
    private Handler mHandler;
    private IBinder binder = new SiddhiBinder();

    public SiddhiService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service.");
        String executionPlan = intent.getExtras().getString(TVConstants.EXECUTION_PLAN_EXTRA);
        if (siddhiManager != null && executionPlan != null) {
            invokeExecutionPlan(executionPlan);
        }
        return Service.START_STICKY;
    }

    private void invokeExecutionPlan(String executionPlan) {
        if (executionPlanRuntime != null) {
            executionPlanRuntime.shutdown();
            executionPlanRuntime = null;
            Log.d(TAG, "Shutting down existing execution plan");
        }
        executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(executionPlan);
        executionPlanRuntime.start();
        executionPlanRuntime.addCallback("alertQuery", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                Log.d(TAG, "Event arrived on alertQuery");
                if (mHandler != null) {
                    for (Event e : inEvents) {
                        mHandler.obtainMessage(MESSAGE_FROM_SIDDHI_SERVICE_QUERY, e).sendToTarget();
                    }
                }
            }
        });

        //Retrieving InputHandler to push events into Siddhi
        setInputHandler(executionPlanRuntime.getInputHandler("edgeDeviceEventStream"));
        Log.i(TAG, "Starting execution plan.");
    }

    @Override
    public void onCreate() {
        if (siddhiManager == null){
            siddhiManager = new SiddhiManager();
            Log.i(TAG, "Siddhi Service created.");
        }
    }

    @Override
    public void onDestroy() {
        if (siddhiManager != null && executionPlanRuntime != null) {
            executionPlanRuntime.shutdown();
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
}
