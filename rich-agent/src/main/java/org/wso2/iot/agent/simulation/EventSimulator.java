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

import java.util.Random;

public class EventSimulator {

    private static final Log log = LogFactory.getLog(Application.class);
    private static final String[] engineStates = {"idle", "running", "stopped"};
    private final InputHandler inputHandler;
    private double fuelLevel = 100.0;
    private double speed = 0.0;
    private double load = 0.0;

    public EventSimulator(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void start(long intervalMillis){
        Runnable simulator = () -> {
            boolean interrupted = false;
            while (!interrupted) {
                Random random = new Random();
                String engineState = engineStates[random.nextInt(3)];
                fuelLevel = Math.round((fuelLevel - random.nextDouble()) * 100) / 100.0;
                if ("stopped".equals(engineState)){
                    speed = 0.0;
                    load = 0.0;
                } else {
                    if (speed < 1.0) {
                        speed = 10.0;
                    }
                    if (load < 1.0) {
                        load = 5.0;
                    }
                    speed = (int)(random.nextInt((int)(speed * 110 - speed * 90)) + speed * 90) / 100.0;
                    load = (int)(random.nextInt((int)(load * 110 - load * 90)) + load * 90) / 100.0;
                }
                try {
                    inputHandler.send(new Object[]{engineState, fuelLevel, speed, load});
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
