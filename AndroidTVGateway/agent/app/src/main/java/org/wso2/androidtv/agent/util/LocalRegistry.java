/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.androidtv.agent.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.wso2.androidtv.agent.mqtt.transport.MQTTTransportHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

/**
 * This is used to store the values in either in memory or in shared preferences.
 */
public class LocalRegistry {

    private static final String SENSE_SHARED_PREFERENCES = "senseSharedPreferences";
    private static final String USERNAME_KEY = "usernameKey";
    private static final String DEVICE_ID_KEY = "deviceIdKey";
    private static final String SERVER_HOST_KEY = "serverHostKey";
    private static final String ACCESS_TOKEN_KEY = "accessTokenKey";
    private static final String REFRESH_TOKEN_KEY = "refreshTokenKey";
    private static final String CONSUMER_KEY = "consumerKey";
    private static final String CONSUMER_SECRET = "ConsumerSecret";
    private static final String MQTT_ENDPOINT_KEY = "mqttEndpointKey";
    private static final String IS_ENROLLED_KEY = "enrolledKey";
    private static final String TENANT_DOMAIN_KEY = "tenantDomainKey";
    private static final String EDGE_DEVICES_KEY = "edgeDevicesKey";
    private static boolean exists = false;
    private static String username;
    private static String deviceId;
    private static String serverURL;
    private static MQTTTransportHandler mqttTransportHandler;
    private static volatile String accessToken;
    private static volatile String refreshToken;
    private static volatile HashSet<String> edgeDevices = new HashSet<>();
    private static String consumerKey;
    private static String consumerSecret;
    private static String mqttEndpoint;
    private static boolean enrolled;
    private static String tenantDomain;

    public static boolean isExist(Context context) {
        if (!exists) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String username = sharedpreferences.getString(USERNAME_KEY, null);
            String deviceId = sharedpreferences.getString(DEVICE_ID_KEY, null);
            exists = (username != null && !username.isEmpty() && deviceId != null && !deviceId.isEmpty());
        }
        return exists;
    }

    public static void setExist(boolean status) {
        exists = status;
    }


    public static void addUsername(Context context, String username) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(USERNAME_KEY, username);
        editor.apply();
        LocalRegistry.username = username;
    }

    public static String getUsername(Context context) {
        if (LocalRegistry.username == null || username.isEmpty()) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.username = sharedpreferences.getString(USERNAME_KEY, "");
        }
        return LocalRegistry.username;
    }

    public static String getOwnerNameSiddhi(){
        String ownername = "";
        if(LocalRegistry.username!=null){
            ownername=LocalRegistry.username;
        }
        return ownername;
    }

    public static void removeUsername(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.remove(USERNAME_KEY);
        editor.apply();
        LocalRegistry.username = null;
    }

    public static void addDeviceId(Context context, String deviceId) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(DEVICE_ID_KEY, deviceId);
        editor.apply();
        LocalRegistry.deviceId = deviceId;
    }

    public static String getDeviceIDSiddhi(){
        String deviceId="";
        if(LocalRegistry.deviceId!=null){
            deviceId=LocalRegistry.deviceId;
        }
        return deviceId;
    }

    public static void removeDeviceId(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(DEVICE_ID_KEY);
        editor.clear();
        editor.apply();
        LocalRegistry.deviceId = null;
    }

    public static String getDeviceId(Context context) {
        if (LocalRegistry.deviceId == null || LocalRegistry.deviceId.isEmpty()) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.deviceId = sharedpreferences.getString(DEVICE_ID_KEY, "");
        }
        return LocalRegistry.deviceId;
    }

    public static void addServerURL(Context context, String host) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(SERVER_HOST_KEY, host);
        editor.apply();
        LocalRegistry.serverURL = host;
    }

    public static void removeServerURL(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(SERVER_HOST_KEY);
        editor.clear();
        editor.apply();
        LocalRegistry.serverURL = null;
    }

    public static String getServerURL(Context context) {
        if (LocalRegistry.serverURL == null || LocalRegistry.serverURL.isEmpty()) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.serverURL = sharedpreferences.getString(SERVER_HOST_KEY, "");
        }
        return LocalRegistry.serverURL;
    }

    public static synchronized void addAccessToken(Context context, String accessToken) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.apply();
        LocalRegistry.accessToken = accessToken;
    }

    public static synchronized void removeAccessToken(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.clear();
        editor.apply();
        LocalRegistry.accessToken = null;
    }

    public static synchronized String getAccessToken(Context context) {
        if (LocalRegistry.accessToken == null || LocalRegistry.accessToken.isEmpty()) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.accessToken = sharedpreferences.getString(ACCESS_TOKEN_KEY, "");
        }
        return LocalRegistry.accessToken;
    }

    public static synchronized String getAccessTokenSidhhi(){
        String token ="";
        if(LocalRegistry.accessToken != null){
            token = LocalRegistry.accessToken;
        }
        return token;
    }

    public static synchronized void addRefreshToken(Context context, String refreshToken) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(REFRESH_TOKEN_KEY, refreshToken);
        editor.apply();
        LocalRegistry.refreshToken = refreshToken;
    }

    public static synchronized void removeRefreshToken(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(REFRESH_TOKEN_KEY);
        editor.clear();
        editor.apply();
        LocalRegistry.refreshToken = null;
    }

    public static synchronized String getRefreshToken(Context context) {
        if (LocalRegistry.refreshToken == null || LocalRegistry.refreshToken.isEmpty()) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.refreshToken = sharedpreferences.getString(REFRESH_TOKEN_KEY, "");
        }
        return LocalRegistry.refreshToken;
    }

    public static void addConsumerKey(Context context, String consumerKey) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(CONSUMER_KEY, consumerKey);
        editor.apply();
        LocalRegistry.consumerKey = consumerKey;
    }

    public static void removeConsumerKey(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(CONSUMER_KEY);
        editor.clear();
        editor.apply();
        LocalRegistry.consumerKey = null;
    }

    public static String getConsumerKey(Context context) {
        if (LocalRegistry.consumerKey == null || LocalRegistry.consumerKey.isEmpty()) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.consumerKey = sharedpreferences.getString(CONSUMER_KEY, "");
        }
        return LocalRegistry.consumerKey;
    }

    public static void addConsumerSecret(Context context, String consumerSecret) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(CONSUMER_SECRET, consumerSecret);
        editor.apply();
        LocalRegistry.consumerSecret = consumerSecret;
    }

    public static void removeConsumerSecret(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(CONSUMER_SECRET);
        editor.clear();
        editor.apply();
        LocalRegistry.consumerSecret = null;
    }

    public static String getConsumerSecret(Context context) {
        if (LocalRegistry.consumerSecret == null || LocalRegistry.consumerSecret.isEmpty()) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.consumerSecret = sharedpreferences.getString(CONSUMER_SECRET, "");
        }
        return LocalRegistry.consumerSecret;
    }

    public static void addMqttEndpoint(Context context, String endpoint) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(MQTT_ENDPOINT_KEY, endpoint);
        editor.apply();
        LocalRegistry.mqttEndpoint = endpoint;
    }

    public static void removeMqttEndpoint(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(MQTT_ENDPOINT_KEY);
        editor.clear();
        editor.apply();
        LocalRegistry.mqttEndpoint = null;
    }

    public static String getMqttEndpoint(Context context) {
        if (LocalRegistry.mqttEndpoint == null) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.mqttEndpoint = sharedpreferences.getString(MQTT_ENDPOINT_KEY, "");
        }
        return LocalRegistry.mqttEndpoint;
    }

    public static String getMqttEndpointSiddhi(){
        String url= null;
        if(LocalRegistry.mqttEndpoint!=null){
            url=LocalRegistry.mqttEndpoint;
        }
        return  url;
    }

    public static void setEnrolled(Context context, boolean enrolled) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(IS_ENROLLED_KEY, enrolled);
        editor.apply();
        LocalRegistry.enrolled = enrolled;
    }

    public static boolean isEnrolled(Context context) {
        if (!LocalRegistry.enrolled) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            return LocalRegistry.enrolled = sharedpreferences.getBoolean(IS_ENROLLED_KEY, false);
        }
        return LocalRegistry.enrolled;
    }

    public static void addTenantDomain(Context context, String tenantDomain) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(TENANT_DOMAIN_KEY, tenantDomain);
        editor.apply();
        LocalRegistry.tenantDomain = tenantDomain;
    }

    public static void removeTenantDomain(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(TENANT_DOMAIN_KEY);
        editor.clear();
        editor.apply();
        LocalRegistry.tenantDomain = null;
    }

    public static String getTenantDomain(Context context) {
        if (LocalRegistry.tenantDomain == null) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            LocalRegistry.tenantDomain = sharedpreferences.getString(TENANT_DOMAIN_KEY, "");
        }
        return LocalRegistry.tenantDomain;
    }

    public static String getServerHost(Context context) {

        URL url = null;
        String urlString = getServerURL(context);
        try {
            url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            Log.e("Host ", "Invalid urlString :" + urlString);
            return null;
        }
    }

    public static synchronized void addEdgeDevice(Context context, String serial) {
        LocalRegistry.edgeDevices.add(serial);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(LocalRegistry.edgeDevices);
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(EDGE_DEVICES_KEY, json);
        editor.apply();
    }

    public static synchronized void removeEdgeDevice(Context context, String serial) {
        if (LocalRegistry.edgeDevices.isEmpty()) {
            return;
        }
        LocalRegistry.edgeDevices.remove(serial);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(LocalRegistry.edgeDevices);
        SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(EDGE_DEVICES_KEY, json);
        editor.apply();
    }

    public static synchronized HashSet<String> getEdgeDevices(Context context) throws IOException {
        if (LocalRegistry.edgeDevices.isEmpty()) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(SENSE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            String edgeDevicesJsonString = sharedpreferences.getString(EDGE_DEVICES_KEY, null);
            if (edgeDevicesJsonString != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<HashSet<String>>(){}.getType();
                LocalRegistry.edgeDevices = gson.fromJson(edgeDevicesJsonString, type);
            }
        }
        return LocalRegistry.edgeDevices;
    }
}
