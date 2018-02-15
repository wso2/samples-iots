/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.androidtv.agent.siddhiSinks;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.androidtv.agent.constants.TVConstants;
import org.wso2.androidtv.agent.h2cache.H2Connection;
import org.wso2.androidtv.agent.mqtt.transport.TransportHandlerException;
import org.wso2.androidtv.agent.util.LocalRegistry;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.output.sink.Sink;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.DynamicOptions;
import org.wso2.siddhi.core.util.transport.Option;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.siddhi.core.event.Event;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.wso2.androidtv.agent.mqtt.AndroidTVMQTTHandler;
import org.wso2.androidtv.agent.services.DeviceManagementService;

/**
 * The EdgeGateway Sink is a customized Siddhi Sink.
 * Each instance of this Siddhi Sink will use the default
 * MQTT connection of the Android Edge Computing gateway.
 * EdgeGateway can detect whether the MQTT connection between
 * the Android Edge Computing Gateway and WSO2 IOT server exists.
 * EdgeGateway sink will publish processed data to
 * relevant topics in WSO2 IOT server and also can be used
 * to persist data when the connection to the IOT server is broken.
 */

@Extension(
        name = "edgeGateway",
        namespace = "sink",
        description = "This sink publishes data from edgeGateway to broker of IOT server ",
        parameters = {
                @Parameter(
                name = "topic",
                description = "The topic in the broker to which the events processed by " +
                        "WSO2 SP are published via MQTT. " +
                        "This is a mandatory parameter.",
                type = {DataType.STRING},
                dynamic = true),
                @Parameter(
                        name = "persist",
                        description = "The variable to decide whether " +
                                "the data is going to be persisted" +
                                "if the connection is unavailable." +
                                "Default value is false" ,
                        type = {DataType.BOOL},
                        optional = true,
                        defaultValue = "false",
                        dynamic = true)},
        examples = @Example(description = "TBD", syntax = "TBD")
)

class EdgeGatewaySink extends Sink {

    private AndroidTVMQTTHandler androidTVMQTTHandler;
    private static final String TAG = "PublishStats";
    private static final String TAG_SINK = "EdgeGatewaySink";

    private Option topicOption;
    private boolean persistOption;
    private String deviceTopic;
    private boolean tableCreated = false;
    private boolean hasPersistedData = false;

    @SuppressWarnings("unchecked")
    private final Queue<String> queue = new PriorityQueue();


    @Override
    public Class[] getSupportedInputEventClasses() {
        return new Class[]{String.class, Event.class};
    }

    @Override
    public String[] getSupportedDynamicOptions() {
        return new String[]{MqttConstants.MESSAGE_TOPIC,MqttConstants.PERSIST};

    }

    @Override
    protected void init(StreamDefinition streamDefinition, OptionHolder optionHolder,
                        ConfigReader configReader, SiddhiAppContext siddhiAppContext) {
        this.topicOption = optionHolder.validateAndGetOption(MqttConstants.MESSAGE_TOPIC);
        this.persistOption=Boolean.parseBoolean(optionHolder
                .validateAndGetStaticValue(MqttConstants.PERSIST,MqttConstants.DEFAULT_PERSIST));



    }

    @Override
    public void publish(Object o, DynamicOptions dynamicOptions) throws
            ConnectionUnavailableException {

        try {
            String specificTopic = topicOption.getValue(dynamicOptions);
            String topic = this.deviceTopic + specificTopic;

            JSONObject jObject = new JSONObject(o.toString());
            JSONObject event = jObject.getJSONObject("event");
            JSONObject jsonEvent = new JSONObject();    //event will contains metadata and payload
            JSONObject jsonMetaData = new JSONObject(); //this will contain metadata info

            try {
                jsonMetaData.put("owner", LocalRegistry.getOwnerNameSiddhi());
            } catch (JSONException e) {
                Log.e(TAG_SINK,"Error while inserting values to JSON object",e);
            }
            try {
                jsonMetaData.put("deviceId", LocalRegistry.getDeviceIDSiddhi());
            } catch (JSONException e) {
                Log.e(TAG_SINK,"Error while inserting values to JSON object", e);
            }

            try {
                jsonMetaData.put("deviceType", TVConstants.DEVICE_TYPE);
            } catch (JSONException e) {
                Log.e(TAG_SINK,"Error while inserting values to JSON object", e);
            }

            try {
                jsonMetaData.put("time", Calendar.getInstance().getTime().getTime());
            } catch (JSONException e) {
                Log.e(TAG_SINK,"Error while inserting values to JSON object", e);
            }
            try {
                jsonEvent.put("metaData", jsonMetaData);
            } catch (JSONException e) {
                Log.e(TAG_SINK,"Error while inserting values to JSON object", e);
            }

            JSONObject payload = new JSONObject();

            //as the number of attributes in a payload can vary, a for loop is being used.
            for (int i = 0; i < event.names().length(); i++) {
                try {
                    payload.put(event.names().getString(i), event.get(event.names().getString(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                jsonEvent.put("payloadData", payload);
            } catch (JSONException e) {
                Log.e(TAG_SINK,"Error while inserting values to JSON object", e);
            }

            JSONObject wrapper = new JSONObject();
            wrapper.put("event", jsonEvent);

            if (androidTVMQTTHandler != null) {

                H2Connection H2Conn = new H2Connection();

                if (androidTVMQTTHandler.isConnected()) {

                    if(hasPersistedData){

                        queue.add(wrapper.toString());

                        List<String> persisted_data_list = H2Conn.retrieveData(specificTopic);
                        for (String val : persisted_data_list) {
                            androidTVMQTTHandler.publishDeviceData(val, topic);
                        }

                        hasPersistedData = false;

                        H2Conn.deleteQuery(specificTopic);

                        if (queue.peek()!= null) {
                            for (int i=0; i<= queue.size() ; i++) {
                                String d = queue.poll();
                                androidTVMQTTHandler.publishDeviceData(d, topic);
                            }
                        }
                    }

                    androidTVMQTTHandler.publishDeviceData(wrapper.toString(), topic);
                    this.deviceTopic = androidTVMQTTHandler.getTopicPrefix();
                    Log.i(TAG, "Connection is available, published stats");


                } else {

                    if(persistOption) {
                        String data_to_persist = wrapper.toString();

                        if(!tableCreated){
                            H2Conn.createQuery(specificTopic);
                            tableCreated = true;
                        }

                        try {
                            System.out.println("ThIS IS topic "+ specificTopic);
                            H2Conn.insertQuery(data_to_persist, specificTopic);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        hasPersistedData = true;

                        try {
                            H2Conn.retrieveData(specificTopic);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }else {
                Log.i(TAG_SINK,"androidtv mqtt handler not initialized");
            }

        } catch (JSONException | TransportHandlerException e) {
            Log.e(TAG_SINK,"JSONException was thrown", e);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() throws ConnectionUnavailableException {

        //get the access to use the MQTT connection in the edge gateway.
        if(DeviceManagementService.getAndroidTVMQTTHandler()!=null){
            this.androidTVMQTTHandler = DeviceManagementService.getAndroidTVMQTTHandler();
        }else{
            Log.i(TAG,"androidTVMQTTHandler is not initialized");
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Map<String, Object> currentState() {
        return null;
    }

    @Override
    public void restoreState(Map<String, Object> map) {

    }
}
