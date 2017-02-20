/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.iot.alertme.plugin.impl.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.Feature;
import org.wso2.carbon.device.mgt.common.FeatureManager;

import java.util.List;

/**
 * Device type specific feature management server
 */
public class DeviceTypeFeatureManager implements FeatureManager {

    private static List<Feature> features = new ArrayList<>();
    private static final String METHOD = "method";
    private static final String URI = "uri";
    private static final String CONTENT_TYPE = "contentType";
    private static final String PATH_PARAMS = "pathParams";
    private static final String QUERY_PARAMS = "queryParams";
    private static final String FORM_PARAMS = "formParams";

    public DeviceTypeFeatureManager () {
        //Feature for get alerts
        Feature getAlertsFeature = new Feature();
        getAlertsFeature.setCode("getalerts");
        getAlertsFeature.setName("Receive alerts for a SenseMe device");
        getAlertsFeature.setDescription("Map AlertMe device with a SenseMe device");

        Map<String, Object> apiParams = new HashMap<>();
        apiParams.put(METHOD, "POST");
        apiParams.put(URI, "/alertme/device/{deviceId}/getalerts");
        List<String> pathParams = new ArrayList<>();
        List<String> queryParams = new ArrayList<>();
        List<String> formParams = new ArrayList<>();
        pathParams.add("deviceId");
        apiParams.put(PATH_PARAMS, pathParams);
        queryParams.add("senseMeId");
        apiParams.put(QUERY_PARAMS, queryParams);
        apiParams.put(FORM_PARAMS, formParams);
        List<Feature.MetadataEntry> metadataEntries = new ArrayList<>();
        Feature.MetadataEntry metadataEntry = new Feature.MetadataEntry();
        metadataEntry.setId(-1);
        metadataEntry.setValue(apiParams);
        metadataEntries.add(metadataEntry);
        getAlertsFeature.setMetadataEntries(metadataEntries);
        features.add(getAlertsFeature);

        //Feature for set properties
        Feature setPropertiesFeature = new Feature();
        setPropertiesFeature.setCode("setproperties");
        setPropertiesFeature.setName("Set properties for AlertMe device");
        setPropertiesFeature.setDescription("Set SenseMe range and duration of the notification");

        apiParams = new HashMap<>();
        apiParams.put(METHOD, "POST");
        apiParams.put(URI, "/alertme/device/{deviceId}/setproperties");
        pathParams = new ArrayList<>();
        queryParams = new ArrayList<>();
        formParams = new ArrayList<>();
        pathParams.add("deviceId");
        apiParams.put(PATH_PARAMS, pathParams);
        queryParams.add("range");
        queryParams.add("duration");
        apiParams.put(QUERY_PARAMS, queryParams);
        apiParams.put(FORM_PARAMS, formParams);
        metadataEntries = new ArrayList<>();
        metadataEntry = new Feature.MetadataEntry();
        metadataEntry.setId(-1);
        metadataEntry.setValue(apiParams);
        metadataEntries.add(metadataEntry);
        setPropertiesFeature.setMetadataEntries(metadataEntries);
        features.add(setPropertiesFeature);

        //Feature for alert
        Feature alertFeature = new Feature();
        alertFeature.setCode("alert");
        alertFeature.setName("Test device");
        alertFeature.setDescription("Send test alert to AlertMe device");

        apiParams = new HashMap<>();
        apiParams.put(METHOD, "POST");
        apiParams.put(URI, "/alertme/device/{deviceId}/alert");
        pathParams = new ArrayList<>();
        queryParams = new ArrayList<>();
        formParams = new ArrayList<>();
        pathParams.add("deviceId");
        apiParams.put(PATH_PARAMS, pathParams);
        queryParams.add("alerttype");
        queryParams.add("duration");
        apiParams.put(QUERY_PARAMS, queryParams);
        apiParams.put(FORM_PARAMS, formParams);
        metadataEntries = new ArrayList<>();
        metadataEntry = new Feature.MetadataEntry();
        metadataEntry.setId(-1);
        metadataEntry.setValue(apiParams);
        metadataEntries.add(metadataEntry);
        alertFeature.setMetadataEntries(metadataEntries);
        features.add(alertFeature);
    }

	@Override
	public boolean addFeature(Feature feature) throws DeviceManagementException {
		return false;
	}

	@Override
	public boolean addFeatures(List<Feature> features) throws DeviceManagementException {
		return false;
	}

	@Override
	public Feature getFeature(String code) throws DeviceManagementException {
        if (code == null) {
            return null;
        }
        for (Feature feature : features){
            if (code.equals(feature.getCode())){
                return feature;
            }
        }
        return null;
	}

	@Override
	public List<Feature> getFeatures() throws DeviceManagementException {
        return features;
	}

	@Override
	public boolean removeFeature(String name) throws DeviceManagementException {
		return false;
	}

	@Override
	public boolean addSupportedFeaturesToDB() throws DeviceManagementException {
		return false;
	}
}
