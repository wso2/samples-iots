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

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.androidtv.agent.MessageActivity;
import org.wso2.androidtv.agent.R;
import org.wso2.androidtv.agent.VideoActivity;
import org.wso2.androidtv.agent.constants.TVConstants;
import org.wso2.androidtv.agent.mqtt.AndroidTVMQTTHandler;
import org.wso2.androidtv.agent.mqtt.MessageReceivedCallback;
import org.wso2.androidtv.agent.cache.CacheEntry;
import org.wso2.androidtv.agent.mqtt.transport.TransportHandlerException;
import org.wso2.androidtv.agent.util.AndroidTVUtils;
import org.wso2.androidtv.agent.util.LocalRegistry;
import org.wso2.siddhi.core.event.Event;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DeviceManagementService extends Service {

    private static final String TAG = UsbService.class.getSimpleName();

    private AndroidTVMQTTHandler androidTVMQTTHandler;
    private UsbService usbService;
    private SiddhiService siddhiService;
    private UsbServiceHandler usbServiceHandler;
    private SiddhiServiceHandler siddhiServiceHandler;
    private boolean hasPendingConfigDownload = false;
    private long downloadId = -1;
    private CacheManagementService cacheManagementService;

    private static volatile boolean waitFlag = false;
    private static volatile boolean isSyncPaused = false;
    private static volatile boolean isSyncStopped = false;
    private static volatile boolean isInCriticalPath = false;
    private static volatile String serialOfCurrentEdgeDevice = "";
    private static volatile String incomingMessage = "";
    private static volatile boolean isCacheEnabled = false;

    private DownloadManager downloadManager;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            usbService = ((UsbService.UsbBinder) iBinder).getService();
            usbService.setHandler(usbServiceHandler);
            isInCriticalPath = false;
            isSyncStopped = false;
            new Thread(syncScheduler).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isSyncStopped = true;
            usbService = null;
        }
    };

    private final ServiceConnection siddhiConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            siddhiService = ((SiddhiService.SiddhiBinder) iBinder).getService();
            siddhiService.setHandler(siddhiServiceHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            siddhiService = null;
        }
    };

    private Runnable syncScheduler = new Runnable() {
        @Override
        public void run() {
            while (!isSyncStopped) {
                try {
                    for (String serial : LocalRegistry.getEdgeDevices(getApplicationContext())) {
                        while ((isInCriticalPath || isSyncPaused) && !isSyncStopped) {
                            Thread.sleep(1000);
                        }
                        if (isSyncStopped) {
                            break;
                        }
                        isInCriticalPath = true;
                        Thread.sleep(1000);
                        serialOfCurrentEdgeDevice = serial;
                        String serialH = serial.substring(0, serial.length() - 8);
                        String serialL = serial.substring(serial.length() - 8);
                        sendConfigLine("+++");
                        sendConfigLine("ATDH" + serialH + "\r");
                        sendConfigLine("ATDL" + serialL + "\r");
                        sendConfigLine("ATCN\r");
                        Thread.sleep(1000);
                        usbService.write("D\r".getBytes());
                        Thread.sleep(5000);
                        isInCriticalPath = false;
                        Thread.sleep(1000);
                        if (isSyncStopped) {
                            break;
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        }
    };

    private BroadcastReceiver configDownloadReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            long currentId = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
            if (downloadId == currentId) {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(currentId);
                Cursor c = downloadManager.query(q);
                if (c.moveToFirst()) {
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        unregisterReceiver(configDownloadReceiver);
                        hasPendingConfigDownload = false;
                        String downloadFileLocalUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        if (downloadFileLocalUri != null) {
                            File mFile = new File(Uri.parse(downloadFileLocalUri).getPath());
                            String downloadFilePath = mFile.getAbsolutePath();
                            installConfigurations(downloadFilePath);
                        }
                    }
                }
                c.close();
            }
        }
    };

    private void installConfigurations(final String fileName) {
        Runnable installConfigThread = new Runnable() {
            @Override
            public void run() {
                if (usbService != null) {
                    isSyncPaused = true;
                    while (isInCriticalPath) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    isInCriticalPath = true;
                    try {
                        sendConfigLine("+++");
                        File fXmlFile = new File(fileName);
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(fXmlFile);
                        doc.getDocumentElement().normalize();

                        NodeList nList = doc.getElementsByTagName("setting");
                        for (int i = 0; i < nList.getLength(); i++) {
                            Node nNode = nList.item(i);
                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElement = (Element) nNode;
                                String atCommand = "AT" + eElement.getAttribute("command") + eElement.getTextContent() + "\r";
                                sendConfigLine(atCommand);
                            }
                        }
                        sendConfigLine("ATWR\r");
                        sendConfigLine("ATAC\r");
                    } catch (Exception e) {
                        usbService.write("ATNR0\r".getBytes());
                        Log.e(TAG, e.getMessage(), e);
                    }
                    Log.i(TAG, "Configs updated");
                    waitFlag = true;
                    usbService.write("ATCN\r".getBytes());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    isInCriticalPath = false;
                    isSyncPaused = true;
                }
            }
        };
        new Thread(installConfigThread).start();
    }

    private void sendConfigLine(String line) throws InterruptedException {
        waitFlag = true;
        int count = 0;
        usbService.write(line.getBytes());
        while (waitFlag) {
            Thread.sleep(100);
            if (count++ > 10) {
                usbService.write("+++".getBytes());
                break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        androidTVMQTTHandler = new AndroidTVMQTTHandler(this, new MessageReceivedCallback() {
            @Override
            public void onMessageReceived(JSONObject message) throws JSONException {
                performAction(message.getString("action"), message.getString("payload"));
            }
        });
        androidTVMQTTHandler.connect();

        usbServiceHandler = new UsbServiceHandler(this);
        // Start UsbService(if it was not started before) and Bind it
        startService(UsbService.class, usbConnection, null);

        siddhiServiceHandler = new SiddhiServiceHandler(this);
        Bundle extras = new Bundle();
        String executionPlan = "@Plan:name('edgeAnalytics') " +
                "define stream edgeDeviceEventStream " +
                "(ac int, window int, light int, temperature float, humidity float, keycard int); " +
                "@info(name = 'alertQuery') " +
                "from edgeDeviceEventStream[(1 == ac or 1 == window or 1 == light) and 0 == keycard] " +
                "select ac, window, light insert into alertOutputStream; " +
                "@info(name = 'temperatureQuery') " +
                "from every te1=edgeDeviceEventStream, te2=edgeDeviceEventStream[te1.temperature != temperature ] " +
                "select te2.temperature insert into temperatureOutputStream; " +
                "@info(name = 'humidityQuery') " +
                "from every he1=edgeDeviceEventStream, he2=edgeDeviceEventStream[he1.humidity != humidity ] " +
                "select he2.humidity insert into humidityOutputStream; " +
                "@info(name = 'acQuery') " +
                "from every ae1=edgeDeviceEventStream, ae2=edgeDeviceEventStream[ae1.ac != ac ] " +
                "select ae2.ac insert into acOutputStream; " +
                "@info(name = 'windowQuery') " +
                "from every we1=edgeDeviceEventStream, we2=edgeDeviceEventStream[we1.window != window ] " +
                "select we2.window insert into windowOutputStream; " +
                "@info(name = 'keycardQuery') " +
                "from every ke1=edgeDeviceEventStream, ke2=edgeDeviceEventStream[ke1.keycard != keycard ] " +
                "select ke2.keycard insert into keycardOutputStream;";
        extras.putString(TVConstants.EXECUTION_PLAN_EXTRA, executionPlan);
        startService(SiddhiService.class, siddhiConnection, extras);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        runCacheManagementService();
    }

    @Override
    public void onDestroy() {
        unbindService(usbConnection);
        unbindService(siddhiConnection);
        if (androidTVMQTTHandler != null && androidTVMQTTHandler.isConnected()) {
            androidTVMQTTHandler.disconnect();
        }
        androidTVMQTTHandler = null;
        if (hasPendingConfigDownload) {
            unregisterReceiver(configDownloadReceiver);
            hasPendingConfigDownload = false;
        }
    }

    private void performAction(String action, String payload) {
        switch (action) {
            case "video":
                startActivity(VideoActivity.class, payload);
                break;
            case "message":
                startActivity(MessageActivity.class, payload);
                break;
            case "config-url":
                configureXBee(payload);
                break;
            case "xbee-add":
                LocalRegistry.addEdgeDevice(getApplicationContext(), payload);
                serialOfCurrentEdgeDevice = payload;
                break;
            case "xbee-remove":
                LocalRegistry.removeEdgeDevice(getApplicationContext(), payload);
                break;
            case "xbee-command":
                sendCommandToEdgeDevice(payload);
                break;
            case "edgeQuery":
                Bundle extras = new Bundle();
                extras.putString(TVConstants.EXECUTION_PLAN_EXTRA, payload);
                startService(SiddhiService.class, siddhiConnection, extras);
                break;
        }
    }

    private void sendCommandToEdgeDevice(final String payload) {
        if (usbService != null) {
            Runnable sendCommandThread = new Runnable() {
                @Override
                public void run() {
                    try {
                        isSyncPaused = true;
                        while (isInCriticalPath) {
                            Thread.sleep(1000);
                        }
                        isInCriticalPath = true;
                        Thread.sleep(1000);
                        JSONObject commandJSON = new JSONObject(payload);
                        String serial = commandJSON.getString("serial");
                        String command = commandJSON.getString("command") + "\r";
                        String serialH = serial.substring(0, serial.length() - 8);
                        String serialL = serial.substring(serial.length() - 8);
                        sendConfigLine("+++");
                        sendConfigLine("ATDH" + serialH + "\r");
                        sendConfigLine("ATDL" + serialL + "\r");
                        sendConfigLine("ATCN\r");
                        Thread.sleep(1000);
                        usbService.write(command.getBytes());
                        Thread.sleep(5000);
                    } catch (JSONException | InterruptedException e) {
                        Log.e(TAG, e.getClass().getSimpleName(), e);
                    } finally {
                        isInCriticalPath = false;
                        isSyncPaused = false;
                    }
                }
            };
            new Thread(sendCommandThread).start();
        }
    }

    private void startActivity(Class<?> cls, String extra) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(TVConstants.MESSAGE, extra);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private synchronized void receivedXBeeData(String message) {
        incomingMessage += message;
        if (incomingMessage.endsWith("\r")) {
            message = incomingMessage;
            incomingMessage = "";
            processXBeeMessage(message.replace("\r", ""));
        }
    }

    private void processXBeeMessage(String message) {
        if (waitFlag && "OK".equals(message)) {
            waitFlag = false;
        } else {
            Log.i(TAG, "Message> " + message);
            try {
                JSONObject incomingMsg = new JSONObject(message);
                switch (incomingMsg.getString("a")) {
                    case "LON":
                        sendATResponse("Light ON");
                        break;
                    case "LOFF":
                        sendATResponse("Light OFF");
                        break;
                    case "DOPEN":
                        sendATResponse("Door lock opened");
                        break;
                    case "DCLOSE":
                        sendATResponse("Door lock closed");
                        break;
                    case "CIN":
                        sendATResponse("Access card inserted");
                        break;
                    case "CO":
                        sendATResponse("Access card removed");
                        break;
                    case "DATA":
                        JSONObject payload = incomingMsg.getJSONObject("p");
                        float temp = payload.getInt("t");
                        float humidity = payload.getInt("h");
                        int ac = payload.getInt("a");
                        int window = payload.getInt("w");
                        int light = payload.getInt("l");
                        int keyCard = payload.getInt("k");
                        siddhiService.getInputHandler().send(new Object[]{ac, window, light, temp, humidity, keyCard});
                        break;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Incomplete incoming message. Ignored", e);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getClass().getSimpleName(), e);
            }
        }
    }

    private void sendATResponse(String message) {
        try {
            JSONObject jsonEvent = new JSONObject();
            JSONObject jsonMetaData = new JSONObject();
            jsonMetaData.put("owner", LocalRegistry.getUsername(getApplicationContext()));
            jsonMetaData.put("deviceId", getDeviceId());
            jsonMetaData.put("deviceType", TVConstants.DEVICE_TYPE);
            jsonMetaData.put("time", Calendar.getInstance().getTime().getTime());
            jsonEvent.put("metaData", jsonMetaData);

            JSONObject payload = new JSONObject();
            payload.put("serial", serialOfCurrentEdgeDevice);
            payload.put("at_response", message);
            jsonEvent.put("payloadData", payload);

            JSONObject wrapper = new JSONObject();
            wrapper.put("event", jsonEvent);
            if (androidTVMQTTHandler.isConnected()) {
                androidTVMQTTHandler.publishDeviceData(wrapper.toString());
            } else {
                cacheManagementService.addCacheEntry(androidTVMQTTHandler.getDefaultPublishTopic(),
                        wrapper.toString());
                isCacheEnabled = true;
            }
        } catch (TransportHandlerException | JSONException e) {
            Log.e(TAG, e.getClass().getSimpleName(), e);
        }
    }

    private void publishStats(String topic, String key, float value) {
        try {
            JSONObject jsonEvent = new JSONObject();
            JSONObject jsonMetaData = new JSONObject();
            jsonMetaData.put("owner", LocalRegistry.getUsername(getApplicationContext()));
            jsonMetaData.put("deviceId", getDeviceId());
            jsonMetaData.put("deviceType", TVConstants.DEVICE_TYPE);
            jsonMetaData.put("time", Calendar.getInstance().getTime().getTime());
            jsonEvent.put("metaData", jsonMetaData);

            JSONObject payload = new JSONObject();
            payload.put(key, value);
            jsonEvent.put("payloadData", payload);

            JSONObject wrapper = new JSONObject();
            wrapper.put("event", jsonEvent);
            if (androidTVMQTTHandler.isConnected()) {
                androidTVMQTTHandler.publishDeviceData(wrapper.toString(), topic);
            } else {
                cacheManagementService.addCacheEntry(topic, wrapper.toString());
                isCacheEnabled = true;
            }
        } catch (TransportHandlerException | JSONException e) {
            Log.e(TAG, e.getClass().getSimpleName(), e);
        }
    }

    public void displayAlert(boolean ac, boolean window, boolean light){
        String alertMsg = "Please ";
        if (window) {
            alertMsg += "close the window ";
        }
        if (ac || light) {
            if (window) {
                alertMsg += "and ";
            }
            alertMsg += "turn off ";
            if (ac) {
                alertMsg += "AC ";
            }
            if (ac && light) {
                alertMsg += "and ";
            }
            if (light) {
                alertMsg += "Light ";
            }
        }
        alertMsg += "before leaving the room.";
        startActivity(MessageActivity.class, alertMsg);
    }

    private String getDeviceId() {
        return AndroidTVUtils.generateDeviceId(getBaseContext(), getContentResolver());
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        Intent startService = new Intent(this, service);
        if (extras != null && !extras.isEmpty()) {
            Set<String> keys = extras.keySet();
            for (String key : keys) {
                String extra = extras.getString(key);
                startService.putExtra(key, extra);
            }
        }
        startService(startService);
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void configureXBee(String configUrl) {
        try {
            configUrl = URLDecoder.decode(configUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(configUrl));
        request.setTitle(getResources().getString(R.string.app_name));
        request.setDescription("Downloading XBee configurations");
        request.setDestinationUri(null);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        // get download service and enqueue file
        downloadId = downloadManager.enqueue(request);
        registerReceiver(configDownloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    // This handler will be passed to UsbService. Data received from serial port is displayed through this handler
    private static class UsbServiceHandler extends Handler {
        private final WeakReference<DeviceManagementService> mService;

        UsbServiceHandler(DeviceManagementService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mService.get().receivedXBeeData(data);
                    break;
            }
        }
    }

    // This handler will be passed to SiddhiService. Data received from SiddhiQuery is displayed through this handler
    private static class SiddhiServiceHandler extends Handler {
        private final WeakReference<DeviceManagementService> mService;
        private static  String publishTopic;

        SiddhiServiceHandler(DeviceManagementService service) {
            mService = new WeakReference<>(service);
            publishTopic = LocalRegistry.getTenantDomain(mService.get())+ "/" + TVConstants.DEVICE_TYPE + "/" +
                    LocalRegistry.getDeviceId(mService.get());
        }

        @Override
        public void handleMessage(Message msg) {
            Event data = (Event) msg.obj;
            Log.d(TAG, "Edge data received: " + data.toString());
            switch (msg.what) {
                case SiddhiService.MESSAGE_FROM_SIDDHI_SERVICE_ALERT_QUERY:
                    mService.get().displayAlert((Integer) data.getData(0) == 1, (Integer) data.getData(1) == 1, (Integer) data.getData(2) == 1);
                    break;
                case SiddhiService.MESSAGE_FROM_SIDDHI_SERVICE_TEMPERATURE_QUERY:
                    mService.get().publishStats(publishTopic + "/TEMP", "TEMP", (Float) data.getData(0));
                    break;
                case SiddhiService.MESSAGE_FROM_SIDDHI_SERVICE_HUMIDITY_QUERY:
                    mService.get().publishStats(publishTopic + "/HUMIDITY", "HUMIDITY", (Float) data.getData(0));
                    break;
                case SiddhiService.MESSAGE_FROM_SIDDHI_SERVICE_AC_QUERY:
                    mService.get().publishStats(publishTopic + "/AC", "AC", (Integer) data.getData(0));
                    break;
                case SiddhiService.MESSAGE_FROM_SIDDHI_SERVICE_WINDOW_QUERY:
                    mService.get().publishStats(publishTopic + "/WINDOW", "WINDOW", (Integer) data.getData(0));
                    break;
                case SiddhiService.MESSAGE_FROM_SIDDHI_SERVICE_KEYCARD_QUERY:
                    mService.get().publishStats(publishTopic + "/DOOR", "DOOR", (Integer) data.getData(0));
                    break;
            }
        }
    }

    private void runCacheManagementService() {
        cacheManagementService = new CacheManagementService(getApplicationContext());
        final long threadWaitingTime = 10000; //10 seconds
        final int[] waitingFactor = {1};

        Thread cacheThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isCacheEnabled) {
                        if (androidTVMQTTHandler.isConnected()) {
                            waitingFactor[0] = 1;
                            try {
                                publishCacheData();
                            } catch (TransportHandlerException e) {
                                Log.e("CacheManagementService", "Unable to publish cached data", e);
                            }
                        } else {
                            Log.d("CacheManagerService", "Unable to connect to the MQTT server, " +
                                    "hence thread will wait for " + (threadWaitingTime * (waitingFactor[0]))/1000 + "seconds");
                            try {
                                Thread.sleep(threadWaitingTime * (waitingFactor[0]++));
                            } catch (InterruptedException e) {
                                Log.e("CacheManagementService", "Error occurred while checking connection", e);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(threadWaitingTime);
                        } catch (InterruptedException e) {
                            Log.e("CacheManagementService", "Error occurred while checking cache", e);
                        }
                    }
                }
            }
        });
        cacheThread.start();
    }

    private void publishCacheData() throws TransportHandlerException {
        List<CacheEntry> cacheEntries = cacheManagementService.getCacheEntries();
        for (CacheEntry entry : cacheEntries) {
            if (androidTVMQTTHandler.isConnected()) {
                androidTVMQTTHandler.publishDeviceData(entry.getMessage(), entry.getTopic());
                cacheManagementService.removeCacheEntry(entry.getId());
            }else {
                break;
            }
        }
        cacheEntries = cacheManagementService.getCacheEntries();
        if (cacheEntries.size() == 0) {
            isCacheEnabled = false;
        }
    }
}
