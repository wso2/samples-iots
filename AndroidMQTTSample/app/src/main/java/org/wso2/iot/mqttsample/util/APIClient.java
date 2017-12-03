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

package org.wso2.iot.mqttsample.util;

import android.content.Context;
import android.util.Log;


import org.wso2.iot.mqttsample.constants.DeviceConstants;
import org.wso2.iot.mqttsample.util.dto.RegisterInfo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This Client is used for http communication with the server.
 */
public class APIClient {
    private final static String TAG = APIClient.class.getSimpleName();

    private Context context;

    public APIClient(Context context) {
        this.context = context;
    }

    /**
     * Enroll the device.
     */
    public RegisterInfo register(String username, String password, String deviceId) {
        Map<String, String> response = registerWithTimeWait(username, password, deviceId);
        String responseStatus = response.get("status");
        RegisterInfo registerInfo = new RegisterInfo();
        if (responseStatus.trim().contains(DeviceConstants.Request.REQUEST_SUCCESSFUL)) {
            registerInfo.setMsg("Login Successful");
            registerInfo.setIsRegistered(true);
            return registerInfo;
        } else {
            registerInfo.setMsg("Authentication failed, please check your credentials and try again.");
            registerInfo.setIsRegistered(false);
            return registerInfo;
        }
    }

    private Map<String, String> registerWithTimeWait(String username, String password, String deviceId) {
        try {
            RegistrationAsyncExecutor registrationAsyncExecutor = new RegistrationAsyncExecutor(context);
            String endpoint = LocalRegistry.getServerURL(context);
            registrationAsyncExecutor.execute(username, password, deviceId, endpoint);
            Map<String, String> response = registrationAsyncExecutor.get();
            if (response != null) {
                return response;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Thread Interruption for endpoint " + LocalRegistry.getServerURL(context));
        } catch (ExecutionException e) {
            Log.e(TAG, "Failed to push data to the endpoint " + LocalRegistry.getServerURL(context));
        }
        return null;
    }

    public Map<String, String> renewToken() {
        try {
            TokenRenewAsyncExecutor renewAsyncExecutor = new TokenRenewAsyncExecutor(context);
            String endpoint = LocalRegistry.getServerURL(context);
            renewAsyncExecutor.execute(endpoint);
            Map<String, String> response = renewAsyncExecutor.get();
            if (response != null) {
                return response;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e(TAG, "Thread Interruption for endpoint " + LocalRegistry.getServerURL(context));
        } catch (ExecutionException e) {
            Log.e(TAG, "Failed to push data to the endpoint " + LocalRegistry.getServerURL(context));
        }
        return null;
    }

}
