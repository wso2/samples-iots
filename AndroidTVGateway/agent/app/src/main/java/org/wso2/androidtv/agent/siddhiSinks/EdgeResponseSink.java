package org.wso2.androidtv.agent.siddhiSinks;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.androidtv.agent.constants.TVConstants;
import org.wso2.androidtv.agent.mqtt.AndroidTVMQTTHandler;
import org.wso2.androidtv.agent.mqtt.transport.TransportHandlerException;
import org.wso2.androidtv.agent.services.DeviceManagementService;
import org.wso2.androidtv.agent.util.LocalRegistry;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.output.sink.Sink;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.DynamicOptions;
import org.wso2.siddhi.core.util.transport.Option;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.StreamDefinition;

import java.util.Calendar;
import java.util.Map;

/**
 * The purpose of EdgeResponse Sink is to .
 */

@Extension(
        name = "edgeResponse",
        namespace = "sink",
        description = "This sink sends information regarding the changes in edge devices to WSO2" +
                "IOT server ",
        parameters = {
                @Parameter(
                        name = "topic",
                        description = "The topic to which via MQTT. " +
                                "This is a mandatory parameter.",
                        type = {DataType.STRING},
                        dynamic = true),
                },
        examples = @Example(description = "TBD", syntax = "TBD")
)

public class EdgeResponseSink extends Sink {
    private static AndroidTVMQTTHandler androidTVMQTTHandler;


    private Option topicOption;
    private String deviceTopic;
    private String specificTopic;
    private String topic;

    @Override
    public Class[] getSupportedInputEventClasses() {
        return new Class[]{String.class, Event.class};
    }

    @Override
    public String[] getSupportedDynamicOptions() {
        return new String[]{MqttConstants.MESSAGE_TOPIC};
    }

    @Override
    protected void init(StreamDefinition streamDefinition, OptionHolder optionHolder, ConfigReader configReader, SiddhiAppContext siddhiAppContext) {
        this.topicOption = optionHolder.validateAndGetOption(MqttConstants.MESSAGE_TOPIC);
    }

    @Override
    public void publish(Object o, DynamicOptions dynamicOptions) throws ConnectionUnavailableException {

        specificTopic = topicOption.getValue(dynamicOptions);
        topic = this.deviceTopic+this.specificTopic;
        try {
            JSONObject jObject = new JSONObject(o.toString());
            JSONObject event = jObject.getJSONObject("event");
            JSONObject jsonEvent = new JSONObject();
            JSONObject jsonMetaData = new JSONObject();

            try {
                jsonMetaData.put("owner", LocalRegistry.getOwnerNameSiddhi());
            } catch (JSONException e) {
                Log.e("EdgeResponseSink","Error while inserting values to JSON object");
            }
            try {
                jsonMetaData.put("deviceId", LocalRegistry.getDeviceIDSiddhi());
            } catch (JSONException e) {
                Log.e("EdgeResponseSink","Error while inserting values to JSON object");
            }

            try {
                jsonMetaData.put("deviceType", TVConstants.DEVICE_TYPE);
            } catch (JSONException e) {
                Log.e("EdgeResponseSink","Error while inserting values to JSON object");
            }

            try {
                jsonMetaData.put("time", Calendar.getInstance().getTime().getTime());
            } catch (JSONException e) {
                Log.e("EdgeResponseSink","Error while inserting values to JSON object");
            }
            try {
                jsonEvent.put("metaData", jsonMetaData);
            } catch (JSONException e) {
                Log.e("EdgeResponseSink","Error while inserting values to JSON object");
            }

            JSONObject payload = new JSONObject();
            String message = event.get(event.names().getString(0)).toString();
            payload.put("serial", DeviceManagementService.getSerialOfCurrentEdgeDevice());
            payload.put("at_response", message);

            try {
                jsonEvent.put("payloadData", payload);
            } catch (JSONException e) {
                Log.e("EdgeResponseSink","Error while inserting values to JSON object");
            }

            JSONObject wrapper = new JSONObject();
            wrapper.put("event", jsonEvent);

            if (androidTVMQTTHandler != null) {
                if (androidTVMQTTHandler.isConnected()) {
                    androidTVMQTTHandler.publishDeviceData(wrapper.toString(),topic);
                } else {
                    //events should be persisted if persisting is enabled
                    Log.i("PublishStats", "Connection not available");
                }
            }else {
                Log.i("EdgeResponseSink","androidtv mqtt handler not initialized");
            }


        } catch (JSONException e) {
            Log.e("EdgeResponseSink","JSONException was thrown");
        } catch (TransportHandlerException e) {
            Log.e("EdgeResponseSink","TransportHandlerException was thrown");
        }
    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        if(DeviceManagementService.getAndroidTVMQTTHandler()!=null){
            this.androidTVMQTTHandler = DeviceManagementService.getAndroidTVMQTTHandler();
            this.deviceTopic=androidTVMQTTHandler.getTopicPrefix();
        }else{
            Log.i("EdgeResponseSink","androidTVMQTTHandler is not initialized");
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
