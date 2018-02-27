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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

public class SiddhiEngine {

    private static final Log log = LogFactory.getLog(SiddhiEngine.class);

    private MQTTHandler mqttHandler;
    private InputHandler inputHandler;
    private String executionPlan;
    private SiddhiManager siddhiManager;
    private SiddhiAppRuntime siddhiAppRuntime;
    private boolean isRunning = false;

    public SiddhiEngine(MQTTHandler mqttHandler, String executionPlan) {
        this.mqttHandler = mqttHandler;
        this.executionPlan = executionPlan;
        init();
    }

    private void init() {
        // Creating Siddhi Manager
        siddhiManager = new SiddhiManager();
        //Generating runtime
        siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(executionPlan);

        //Adding callback to retrieve output events from query
        siddhiAppRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timestamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timestamp, inEvents, removeEvents);
                mqttHandler.publishMessage(Application.getInstance().getTenantDomain() + "/" +
                                           Application.getInstance().getDeviceType() + "/" +
                                           Application.getInstance().getDeviceType() + "/events",
                                           "{\"engine_status\":\"idle\",\"fuel_level\":10,\"speed\":0,\"load\":0,\"moisture_level\":20,\"illumination\":50}");
            }
        });

        //Retrieving InputHandler to push events into Siddhi
        inputHandler = siddhiAppRuntime.getInputHandler("agentEventStream");

        //Starting event processing
        siddhiAppRuntime.start();
        isRunning = true;
        log.info("Siddhi engine started.");
    }

    public void publishEvent(Object[] event) {
        try {
            inputHandler.send(event);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void shutdown() {
        //Shutting down the runtime
        siddhiAppRuntime.shutdown();

        //Shutting down Siddhi
        siddhiManager.shutdown();
        isRunning = false;
        log.info("Siddhi engine stopped.");
    }

    public synchronized void updateExecutionPlan(String executionPlan) {
        this.executionPlan = executionPlan;
        if (isRunning) {
            shutdown();
        }
        init();
    }
}
