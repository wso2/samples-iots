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

package org.wso2.iot.senseme.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.analytics.api.AnalyticsDataAPI;
import org.wso2.carbon.analytics.api.AnalyticsDataAPIUtil;
import org.wso2.carbon.analytics.dataservice.commons.AnalyticsDataResponse;
import org.wso2.carbon.analytics.dataservice.commons.SearchResultEntry;
import org.wso2.carbon.analytics.dataservice.commons.SortByField;
import org.wso2.carbon.analytics.datasource.commons.Record;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.apimgt.application.extension.APIManagementProviderService;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.authorization.DeviceAccessAuthorizationService;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupAlreadyExistException;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupManagementException;
import org.wso2.carbon.device.mgt.common.group.mgt.RoleDoesNotExistException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.core.service.GroupManagementProviderService;
import org.wso2.carbon.identity.jwt.client.extension.service.JWTClientManagerService;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.Permission;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.iot.senseme.api.constants.DeviceTypeConstants;
import org.wso2.iot.senseme.api.dto.SensorRecord;
import org.wso2.iot.senseme.api.exception.DeviceTypeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides utility functions used by REST-API.
 */
public class APIUtil {

    private static Log log = LogFactory.getLog(APIUtil.class);

    private APIUtil(){
    }

    public static String getAuthenticatedUser() {
        PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        String username = threadLocalCarbonContext.getUsername();
        String tenantDomain = threadLocalCarbonContext.getTenantDomain();
        if (username.endsWith(tenantDomain)) {
            return username.substring(0, username.lastIndexOf('@'));
        }
        return username;
    }

    public static DeviceManagementProviderService getDeviceManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceManagementProviderService deviceManagementProviderService =
                (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        if (deviceManagementProviderService == null) {
            String msg = "Device Management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceManagementProviderService;
    }

    public static GroupManagementProviderService getGroupManagementProviderService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        GroupManagementProviderService groupManagementProviderService =
                (GroupManagementProviderService) ctx.getOSGiService(GroupManagementProviderService.class, null);
        if (groupManagementProviderService == null) {
            String msg = "Device Management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return groupManagementProviderService;
    }

    public static APIManagementProviderService getAPIManagementProviderService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        APIManagementProviderService apiManagementProviderService =
                (APIManagementProviderService) ctx.getOSGiService(APIManagementProviderService.class, null);
        if (apiManagementProviderService == null) {
            String msg = "API management provider service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return apiManagementProviderService;
    }

    public static JWTClientManagerService getJWTClientManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        JWTClientManagerService jwtClientManagerService =
                (JWTClientManagerService) ctx.getOSGiService(JWTClientManagerService.class, null);
        if (jwtClientManagerService == null) {
            String msg = "JWT Client manager service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return jwtClientManagerService;
    }

    public static String getTenantDomainOftheUser() {
        PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return threadLocalCarbonContext.getTenantDomain();
    }

    public static DeviceAccessAuthorizationService getDeviceAccessAuthorizationService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceAccessAuthorizationService deviceAccessAuthorizationService =
                (DeviceAccessAuthorizationService) ctx.getOSGiService(DeviceAccessAuthorizationService.class, null);
        if (deviceAccessAuthorizationService == null) {
            String msg = "Device Authorization service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceAccessAuthorizationService;
    }

    public static List<SensorRecord> getAllEventsForDevice(String tableName, String query,
                                                           List<SortByField> sortByFields) throws AnalyticsException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        AnalyticsDataAPI analyticsDataAPI = getAnalyticsDataAPI();
        int eventCount = analyticsDataAPI.searchCount(tenantId, tableName, query);
        if (eventCount == 0) {
            return new ArrayList<>();
        }
        List<SearchResultEntry> resultEntries = analyticsDataAPI.search(tenantId, tableName, query, 0, eventCount,
                                                                        sortByFields);
        List<String> recordIds = getRecordIds(resultEntries);
        AnalyticsDataResponse response = analyticsDataAPI.get(tenantId, tableName, 1, null, recordIds);
        Map<String, SensorRecord> sensorData = createSensorData(AnalyticsDataAPIUtil.listRecords(
                analyticsDataAPI, response));
        return getSortedSensorData(sensorData, resultEntries);
    }

    private static List<SensorRecord> getSortedSensorData(Map<String, SensorRecord> sensorDatas,
                                                          List<SearchResultEntry> searchResults) {
        List<SensorRecord> sortedRecords = new ArrayList<>();
        for (SearchResultEntry searchResultEntry : searchResults) {
            sortedRecords.add(sensorDatas.get(searchResultEntry.getId()));
        }
        return sortedRecords;
    }

    private static List<String> getRecordIds(List<SearchResultEntry> searchResults) {
        List<String> ids = new ArrayList<>();
        for (SearchResultEntry searchResult : searchResults) {
            ids.add(searchResult.getId());
        }
        return ids;
    }

    private static Map<String, SensorRecord> createSensorData(List<Record> records) {
        Map<String, SensorRecord> sensorData = new HashMap<>();
        for (Record record : records) {
            SensorRecord sensorDataRecord = createSensorData(record);
            sensorData.put(sensorDataRecord.getId(), sensorDataRecord);
        }
        return sensorData;
    }

    private static SensorRecord createSensorData(Record record) {
        SensorRecord recordBean = new SensorRecord();
        recordBean.setId(record.getId());
        recordBean.setValues(record.getValues());
        return recordBean;
    }

    private static AnalyticsDataAPI getAnalyticsDataAPI() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        AnalyticsDataAPI analyticsDataAPI =
                (AnalyticsDataAPI) ctx.getOSGiService(AnalyticsDataAPI.class, null);
        if (analyticsDataAPI == null) {
            String msg = "Analytics api service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return analyticsDataAPI;
    }

    public static String getAuthenticatedUserTenantDomain() {
        PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return threadLocalCarbonContext.getTenantDomain();
    }

    public static UserStoreManager getUserStoreManager() throws UserStoreException {
        RealmService realmService;
        UserStoreManager userStoreManager;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
        if (realmService == null) {
            String msg = "Realm service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        int tenantId = ctx.getTenantId();
        userStoreManager = realmService.getTenantUserRealm(tenantId).getUserStoreManager();
        return userStoreManager;
    }

    public static AuthorizationManager getAuthorizationManager() {
        RealmService realmService;
        AuthorizationManager authorizationManager;
        try {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
            if (realmService == null) {
                String msg = "Realm service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            int tenantId = ctx.getTenantId();
            authorizationManager = realmService.getTenantUserRealm(tenantId).getAuthorizationManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user store manager";
            log.error(msg, e);
            throw new IllegalStateException(msg);
        }
        return authorizationManager;
    }

    /**
     * Create user role for building and floors.
     *
     * @param role : Role that need to be created
     * @throws UserStoreException User Store Exception
     */
    public static void addRolesForBuildingsAndFloors(String role) throws UserStoreException {
        Permission realTimeAnalytics = new Permission(DeviceTypeConstants.REALTIME_ANALYTICS_PERMISSION,
                                                      CarbonConstants.UI_PERMISSION_ACTION);

        UserStoreManager userStoreManager = APIUtil.getUserStoreManager();
        if (userStoreManager != null) {
            if (!userStoreManager.isExistingRole(role)) {
                userStoreManager.addRole(role, null, new Permission[] { realTimeAnalytics });
            }
        } else {
            log.error("User Store Manager cannot found.");
        }
    }

    /**
     * Create device groups for building and floor and assign the given list of devices.
     *
     * @param groupName:  The name of the group
     * @param role        : The role associated with the group
     * @param description : The description for the group
     * @throws DeviceTypeException Device Type Exception
     */
    public static void createAndAddGroups(String groupName, String role, String description) throws DeviceTypeException {
        try {
            DeviceGroup buildingFloorGroup;
            GroupManagementProviderService groupManagementProviderService = APIUtil.getGroupManagementProviderService();

            if (groupManagementProviderService.getGroup(groupName) != null) {
                return;
            }

            buildingFloorGroup = new DeviceGroup();
            buildingFloorGroup.setOwner(PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername());
            buildingFloorGroup.setName(groupName);
            buildingFloorGroup.setDescription(description);
            groupManagementProviderService.createGroup(buildingFloorGroup, role,
                                                       new String[] { DeviceTypeConstants.REALTIME_ANALYTICS_PERMISSION });
            buildingFloorGroup = groupManagementProviderService.getGroup(groupName);
            groupManagementProviderService
                    .manageGroupSharing(buildingFloorGroup.getGroupId(), new ArrayList<>(Arrays.asList(role)));
        } catch (GroupManagementException e) {
            throw new DeviceTypeException("Error occurred while creting group with the name " + groupName, e);
        } catch (GroupAlreadyExistException e) {
            throw new DeviceTypeException("A group with the name " + groupName + " already exists.", e);
        } catch (RoleDoesNotExistException e) {
            throw new DeviceTypeException("A role with the name " + role + " does not exist", e);
        }
    }
}
