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

package org.homeautomation.firealarm.api.util;

import org.wso2.carbon.user.core.Permission;

/**
 * This hold the constants related to the device type.
 */
public class Constants {

    public static final String DEFAULT_PERMISSION_RESOURCE = "/permission/admin/device-mgt/firealarm/user";
    public static final String DEFAULT_ROLE_NAME = "firealarm_user";
    public static final Permission DEFAULT_PERMISSION[]
            = new Permission[]{new Permission(Constants.DEFAULT_PERMISSION_RESOURCE, "ui.execute")};
}
