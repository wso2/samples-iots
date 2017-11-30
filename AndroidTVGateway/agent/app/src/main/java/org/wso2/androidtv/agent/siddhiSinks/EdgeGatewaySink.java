package org.wso2.androidtv.agent.siddhiSinks;

import android.util.EventLog;
import android.util.Log;

import net.minidev.json.parser.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.androidtv.agent.constants.TVConstants;
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

import java.util.Calendar;
import java.util.Map;

import org.wso2.androidtv.agent.mqtt.AndroidTVMQTTHandler;
import org.wso2.androidtv.agent.services.DeviceManagementService;
import org.wso2.androidtv.agent.services.CacheManagementService;

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

public class EdgeGatewaySink extends Sink {

    private static AndroidTVMQTTHandler androidTVMQTTHandler;

    private Option topicOption;
    private boolean persistOption;
    private String deviceTopic;
    private String specificTopic;
    private String topic;

    @Override
    public Class[] getSupportedInputEventClasses() {
        return new Class[]{String.class, Event.class};
    }

    @Override
    public String[] getSupportedDynamicOptions() {
        return new String[]{MqttConstants.MESSAGE_TOPIC,MqttConstants.PERSIST};

    }

    @Override
    protected void init(StreamDefinition streamDefinition, OptionHolder optionHolder, ConfigReader configReader, SiddhiAppContext siddhiAppContext) {
        this.topicOption = optionHolder.validateAndGetOption(MqttConstants.MESSAGE_TOPIC);
        this.persistOption=Boolean.parseBoolean(optionHolder.validateAndGetStaticValue(MqttConstants.PERSIST,MqttConstants.DEFAULT_PERSIST));
    }

    @Override
    public void publish(Object o, DynamicOptions dynamicOptions) throws ConnectionUnavailableException {

        try {
            specificTopic = topicOption.getValue(dynamicOptions);
            topic=this.deviceTopic+this.specificTopic;

            JSONObject jObject = new JSONObject(o.toString());
            JSONObject event = jObject.getJSONObject("event");
            JSONObject jsonEvent = new JSONObject();    //event will contains metadata and payload
            JSONObject jsonMetaData = new JSONObject(); //this will contain metadata info

            try {
                jsonMetaData.put("owner", LocalRegistry.getOwnerNameSiddhi());
            } catch (JSONException e) {
                Log.e("EdgeGatewaySink","Error while inserting values to JSON object");
            }
            try {
                jsonMetaData.put("deviceId", LocalRegistry.getDeviceIDSiddhi());
            } catch (JSONException e) {
                Log.e("EdgeGatewaySink","Error while inserting values to JSON object");
            }

            try {
                jsonMetaData.put("deviceType", TVConstants.DEVICE_TYPE);
            } catch (JSONException e) {
                Log.e("EdgeGatewaySink","Error while inserting values to JSON object");
            }

            try {
                jsonMetaData.put("time", Calendar.getInstance().getTime().getTime());
            } catch (JSONException e) {
                Log.e("EdgeGatewaySink","Error while inserting values to JSON object");
            }
            try {
                jsonEvent.put("metaData", jsonMetaData);
            } catch (JSONException e) {
                Log.e("EdgeGatewaySink","Error while inserting values to JSON object");
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
                Log.e("EdgeGatewaySink","Error while inserting values to JSON object");
            }

            JSONObject wrapper = new JSONObject();
            wrapper.put("event", jsonEvent);

            if (androidTVMQTTHandler != null) {
                if (androidTVMQTTHandler.isConnected()) {
                    androidTVMQTTHandler.publishDeviceData(wrapper.toString(), topic);
                    this.deviceTopic=androidTVMQTTHandler.getTopicPrefix();
                    Log.i("PublishStats", "Connection is available, published stats");
                } else {
                    if(persistOption) {
                        //events should be persisted if persisting is enabled
                    }
                }
            }else {
                Log.i("EdgeGatewaySink","androidtv mqtt handler not initialized");
            }


        } catch (JSONException e) {
            Log.e("EdgeGatewaySink","JSONException was thrown");
        } catch (TransportHandlerException e) {
            Log.e("EdgeGatewaySink","JSONException was thrown");
        }


    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        //get the access to use the MQTT connection in the edge gateway.
        if(DeviceManagementService.getAndroidTVMQTTHandler()!=null){
            this.androidTVMQTTHandler = DeviceManagementService.getAndroidTVMQTTHandler();
        }else{
            Log.i("TAG","androidTVMQTTHandler is not initialized");
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
