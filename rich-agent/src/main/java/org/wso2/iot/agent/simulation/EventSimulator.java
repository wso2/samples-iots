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

package org.wso2.iot.agent.simulation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.iot.agent.Application;
import org.wso2.siddhi.core.stream.input.InputHandler;

import javax.swing.*;
import java.util.Random;

public class EventSimulator {

    private static final Log log = LogFactory.getLog(Application.class);
    private final InputHandler inputHandler;
    private AgentUI agentUI;

    public EventSimulator(InputHandler inputHandler) {
        this.inputHandler = inputHandler;

        try {
            // Set System L&F for Device UI
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            log.error(
                    "'UnsupportedLookAndFeelException' error occurred whilst initializing the" +
                    " Agent UI.");
        } catch (ClassNotFoundException e) {
            log.error(
                    "'ClassNotFoundException' error occurred whilst initializing the Agent UI.");
        } catch (InstantiationException e) {
            log.error(
                    "'InstantiationException' error occurred whilst initializing the Agent UI.");
        } catch (IllegalAccessException e) {
            log.error(
                    "'IllegalAccessException' error occurred whilst initializing the Agent UI.");
        }
        java.awt.EventQueue.invokeLater(() -> {
            agentUI = new AgentUI();
            agentUI.setVisible(true);
        });
    }

    public void start(long intervalMillis){
        Runnable simulator = () -> {
            boolean interrupted = false;
            while (!interrupted) {
                try {
                    //EngineTemp double, humidity double, " +
                    //"tractorSpeed double, loadWeight double,
                    // soilMoisture double, illumination double, " +
                    //"fuelUsage double, engineidle bool, raining bool, temperature double
                    inputHandler.send(new Object[]{90.0, 42.1, 10.1, 5.2, 8.3, 56.3, 89.2, true, true, 32.1});
                    log.info("New event emitted.");
                    Thread.sleep(intervalMillis);
                } catch (InterruptedException e) {
                    log.warn("Thread interrupted.", e);
                    interrupted = true;
                    Thread.currentThread().interrupt();
                }
            }
        };
        new Thread(simulator).start();
    }
}
