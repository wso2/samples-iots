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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.homeautomation.androidtv.api.service;

import org.homeautomation.androidtv.api.dto.EdgeDevice;
import org.homeautomation.androidtv.api.exception.AndroidTVException;

import java.util.List;

/**
 * Android TV management service to expose models.
 */
public interface AndroidTVManagementService {

    /**
     * Add edge device to Android TV gateway.
     *
     * @param edgeDevice to be added.
     * @throws AndroidTVException on errors.
     */
    void addEdgeDevice(EdgeDevice edgeDevice) throws AndroidTVException;

    /**
     * Remove edge device from Android TV gateway.
     *
     * @param serial of the edge device to be removed.
     * @throws AndroidTVException on errors.
     */
    void removeEdgeDevice(String serial) throws AndroidTVException;

    /**
     * Get edge devices connected with android tv gateway.
     *
     * @param gatewayId of the gateway device.
     * @return A list of edge devices.
     * @throws AndroidTVException on errors.
     */
    List<EdgeDevice> getAllEdgeDevices(String gatewayId) throws AndroidTVException;
}
