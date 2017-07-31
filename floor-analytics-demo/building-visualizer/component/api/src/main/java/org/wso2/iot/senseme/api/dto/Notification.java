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

package org.wso2.iot.senseme.api.dto;

import com.google.gson.Gson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * This class represents the information of sending notification operation.
 */
@ApiModel(value = "Notification",
          description = "Details related to notifications passed to device.")
public class Notification implements Serializable {

    public Notification() {

    }

    public Notification(AlertMessage alertMessage) {
        messageText = "Alert from " + alertMessage.getBuildingId() + " building, " + alertMessage.getFloorId() +
                " floor. " + alertMessage.getType() + " value is " + String.format("%.2f", alertMessage.getValue())
                + ". " + alertMessage.getInformation();
        messageTitle = alertMessage.getType();
    }

	private String messageText;

	private String messageTitle;

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}

    /*
	* This method is used to convert operation object to a json format.
	*
	* @return json formatted String.
	*/
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
