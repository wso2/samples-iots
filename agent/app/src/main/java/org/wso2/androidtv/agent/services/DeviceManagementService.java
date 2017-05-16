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
import org.wso2.androidtv.agent.mqtt.transport.TransportHandlerException;
import org.wso2.androidtv.agent.util.AndroidTVUtils;
import org.wso2.androidtv.agent.util.LocalRegistry;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DeviceManagementService extends Service {

    private static final String TAG = UsbService.class.getSimpleName();

    private AndroidTVMQTTHandler androidTVMQTTHandler;
    private UsbService usbService;
    private UsbServiceHandler mHandler;
    private boolean hasPendingConfigDownload = false;
    private long downloadId = -1;
    private static volatile boolean waitFlag = false;
    private String incomingMessage = "";

    private DownloadManager downloadManager;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    BroadcastReceiver configDownloadReceiver = new BroadcastReceiver() {
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
                        String fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        installConfigurations(fileName);
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
                if (usbService != null && UsbService.SERVICE_CONNECTED) {
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
                        sendConfigLine("ATCN\r");
                    } catch (Exception e) {
                        usbService.write("ATNR0\r".getBytes());
                        Log.e(TAG, e.getMessage(), e);
                    }
                    Log.i(TAG, "Configs updated");
                    usbService.write("ATND\r".getBytes());
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
        mHandler = new UsbServiceHandler(this);
        // Start UsbService(if it was not started before) and Bind it
        startService(UsbService.class, usbConnection, null);
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public void onDestroy() {
        unbindService(usbConnection);
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
                break;
            case "xbee-remove":
                LocalRegistry.removeEdgeDevice(getApplicationContext(), payload);
                break;
            default:
                if (usbService != null) { // if UsbService was correctly bind, Send data
                    usbService.write(payload.getBytes());
                }
        }
    }

    private void startActivity(Class<?> cls, String extra) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(TVConstants.MESSAGE, extra);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void receivedXBeeData(String message) {
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
                JSONObject jsonEvent = new JSONObject();
                JSONObject jsonMetaData = new JSONObject();
                jsonMetaData.put("owner", LocalRegistry.getUsername(getApplicationContext()));
                jsonMetaData.put("deviceId", getDeviceId());
                jsonMetaData.put("type", TVConstants.DEVICE_TYPE);
                jsonMetaData.put("time", Calendar.getInstance().getTime().getTime());
                jsonEvent.put("metaData", jsonMetaData);

                JSONObject payload = new JSONObject();
                payload.put("serial", "0000000000000000");
                payload.put("at_response", message);
                jsonEvent.put("payloadData", payload);


                JSONObject wrapper = new JSONObject();
                wrapper.put("event", jsonEvent);
                androidTVMQTTHandler.publishDeviceData(wrapper.toString());
            } catch (TransportHandlerException | JSONException e) {
                Log.e(TAG, e.getClass().getSimpleName(), e);
            }
        }
    }

    private String getDeviceId() {
        return AndroidTVUtils.generateDeviceId(getBaseContext(), getContentResolver());
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
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
        request.setShowRunningNotification(true);

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
}
