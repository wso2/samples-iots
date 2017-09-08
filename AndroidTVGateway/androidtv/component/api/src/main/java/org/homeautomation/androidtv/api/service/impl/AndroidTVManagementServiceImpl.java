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

package org.homeautomation.androidtv.api.service.impl;

import org.homeautomation.androidtv.api.dao.AndroidTVDAOFactory;
import org.homeautomation.androidtv.api.dto.EdgeDevice;
import org.homeautomation.androidtv.api.exception.AndroidTVDAOException;
import org.homeautomation.androidtv.api.exception.AndroidTVException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.homeautomation.androidtv.api.service.AndroidTVManagementService;

import java.util.List;

/**
 * Implementation of AndroidTVManagementService.
 */
public class AndroidTVManagementServiceImpl implements AndroidTVManagementService {

    private static final Log log = LogFactory.getLog(AndroidTVManagementServiceImpl.class);
    private static final AndroidTVDAOFactory androidTVDAOHandler = new AndroidTVDAOFactory();
    
    public void addEdgeDevice(EdgeDevice edgeDevice) throws AndroidTVException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Adding a new edge device : " + edgeDevice.getEdgeDeviceSerial());
            }
            AndroidTVDAOFactory.beginTransaction();
            androidTVDAOHandler.getDeviceTypeDAO().addEdgeDevice(edgeDevice);
            AndroidTVDAOFactory.commitTransaction();
        } catch (AndroidTVDAOException e) {
            try {
                AndroidTVDAOFactory.rollbackTransaction();
            } catch (AndroidTVDAOException iotDAOEx) {
                log.warn("Error occurred while roll back the edge device adding transaction", iotDAOEx);
            }
            String msg = "Error while adding edge device : " + edgeDevice.getEdgeDeviceName();
            log.error(msg, e);
            throw new AndroidTVException(msg, e);
        }
    }

    public void removeEdgeDevice(String serial) throws AndroidTVException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("removing edge device : " + serial);
            }
            AndroidTVDAOFactory.beginTransaction();
            androidTVDAOHandler.getDeviceTypeDAO().removeEdgeDevice(serial);
            AndroidTVDAOFactory.commitTransaction();
        } catch (AndroidTVDAOException e) {
            try {
                AndroidTVDAOFactory.rollbackTransaction();
            } catch (AndroidTVDAOException iotDAOEx) {
                log.warn("Error occurred while roll back the edge device removing transaction", iotDAOEx);
            }
            String msg = "Error while removing edge device : " + serial;
            log.error(msg, e);
            throw new AndroidTVException(msg, e);
        }
    }

    public List<EdgeDevice> getAllEdgeDevices(String gatewayId) throws AndroidTVException {
        List<EdgeDevice> edgeDevices;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Fetching the details of all edge devices");
            }
            edgeDevices = androidTVDAOHandler.getDeviceTypeDAO().getAllEdgeDevices(gatewayId);
        } catch (AndroidTVDAOException e) {
            String msg = "Error while fetching all androidtv devices.";
            log.error(msg, e);
            throw new AndroidTVException(msg, e);
        }
        return edgeDevices;
    }
}
