/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.homeautomation.androidtv.plugin.impl;

import java.util.HashMap;
import java.util.Map;



import org.homeautomation.androidtv.plugin.constants.DeviceTypeConstants;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.ProvisioningConfig;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceManager;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManager;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.push.notification.PushNotificationConfig;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagementService;
import org.wso2.carbon.device.mgt.common.OperationMonitoringTaskConfig;
import org.wso2.carbon.device.mgt.common.policy.mgt.PolicyMonitoringManager;
import org.wso2.carbon.device.mgt.common.OperationMonitoringTaskConfig;
import java.util.List;

public class DeviceTypeManagerService implements DeviceManagementService {
    private DeviceManager deviceManager;
    private OperationMonitoringTaskConfig operationMonitoringTaskConfig;

    @Override
    public String getType() {
        return DeviceTypeConstants.DEVICE_TYPE;
    }

    @Override
    public void init() throws DeviceManagementException {
        this.deviceManager = new DeviceTypeManager();
        this.operationMonitoringTaskConfig = new OperationMonitoringTaskConfig();
    }

    @Override
    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

    @Override
    public ApplicationManager getApplicationManager() {
        return null;
    }

    @Override
    public ProvisioningConfig getProvisioningConfig() {
        return new ProvisioningConfig(DeviceTypeConstants.DEVICE_TYPE_PROVIDER_DOMAIN, false);
    }

    @Override
    public OperationMonitoringTaskConfig getOperationMonitoringConfig() {
        return operationMonitoringTaskConfig;
    }

    @Override
    public PushNotificationConfig getPushNotificationConfig() {
        // this needs to be retrieved from a config file.
        Map<String, String> properties = new HashMap<>();
        properties.put("mqttAdapterName", "androidtv_mqtt");
        properties.put("username", "admin");
        properties.put("password", "admin");
        properties.put("qos", "0");
        properties.put("clearSession", "true");
        properties.put("scopes", "");
        return new PushNotificationConfig("MQTT", properties);
    }

    @Override
    public PolicyMonitoringManager getPolicyMonitoringManager() {
        return null;
    }

}
