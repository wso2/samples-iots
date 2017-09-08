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

package org.homeautomation.androidtv.api.dao;

import org.homeautomation.androidtv.api.dto.EdgeDevice;
import org.homeautomation.androidtv.api.exception.AndroidTVDAOException;

import java.util.List;

/**
 * DAO Interface for Android TV database related operations
 */
public interface AndroidTVDAO {

    /**
     * Get all edge devices assigned to the Gateway.
     *
     * @param gatewayId of the gateway device
     * @return A list of edge devices.
     * @throws AndroidTVDAOException on DAO errors.
     */
    List<EdgeDevice> getAllEdgeDevices(String gatewayId) throws AndroidTVDAOException;

    /**
     * Add edge device to gateway.
     *
     * @param edgeDevice to be added.
     * @throws AndroidTVDAOException on DAO errors.
     */
    void addEdgeDevice(EdgeDevice edgeDevice) throws AndroidTVDAOException;

    /**
     * Remove edge device from gateway.
     *
     * @param serial of the edge device to be removed.
     * @throws AndroidTVDAOException on DAO errors.
     */
    void removeEdgeDevice(String serial) throws AndroidTVDAOException;
}
