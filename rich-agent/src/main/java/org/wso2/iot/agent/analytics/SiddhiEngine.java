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

package org.wso2.iot.agent.analytics;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;

public class SiddhiEngine {

    private static final Log log = LogFactory.getLog(SiddhiEngine.class);

    private String executionPlan;
    private SiddhiManager siddhiManager;
    private SiddhiAppRuntime siddhiAppRuntime;
    private boolean isRunning = false;

    public SiddhiEngine(String executionPlan) {
        this.executionPlan = executionPlan;
        init();
    }

    private void init() {
        // Creating Siddhi Manager
        siddhiManager = new SiddhiManager();
        //Generating runtime
        siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(executionPlan);

        //Starting event processing
        siddhiAppRuntime.start();
        isRunning = true;
        log.info("Siddhi engine started.");
    }

    public void addQueryCallback(String queryName, QueryCallback queryCallback) {
        //Adding callback to retrieve output events from query
        siddhiAppRuntime.addCallback(queryName, queryCallback);
    }

    public InputHandler getInputHandler(String streamId) {
        //Retrieving InputHandler to push events into Siddhi
        return siddhiAppRuntime.getInputHandler(streamId);
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
