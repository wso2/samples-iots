/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.androidtv.agent.siddhiSources;

/**
 * This is a customized Siddhi Source for Android Edge Computing Gateway.
 * A customized Siddhi Source has been used in order to meet specific
 * requirements of the Android Edge Computing Gateway.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;


import org.wso2.androidtv.agent.services.DeviceManagementService;
import org.wso2.androidtv.agent.subscribers.EdgeSourceSubscriber;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.stream.input.source.Source;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.OptionHolder;

import java.util.Map;

@Extension(
              name = "textEdge",
              namespace="source",
              description = "Get event streams from edge devices as text",

              parameters = {
                      @Parameter(
                            name = "client.id",
                            description = "ID which should be given to the source. " +
                            "This can be the device ID.",
                            type = {DataType.STRING},
                            dynamic = false)
                      },
              examples = @Example(description = "TBD",syntax = "TBD")
         )

public class TextEdgeSource extends AbstractEdgeSource{

    protected EdgeSourceSubscriber edgeSourceSubscriber;
    private String clientId;


    @Override
    public void init(SourceEventListener sourceEventListener, OptionHolder optionHolder,
                     String[] strings, ConfigReader configReader, SiddhiAppContext
                                 siddhiAppContext) {
        super.init(sourceEventListener,optionHolder,strings,configReader,siddhiAppContext);

        this.clientId = optionHolder.validateAndGetStaticValue(TextEdgeConstants.CLIENT_ID,
                TextEdgeConstants.EMPTY_STRING);

        /*
        sourceEventListner of the source will be passed to the source subscriber of the source.
        When the subscriber receives data it will transmit the data to the source via the source
         event listener.
         */
        this.edgeSourceSubscriber = new EdgeSourceSubscriber(sourceEventListener,this.clientId);

    }


    @Override
    public Class[] getOutputEventClasses() {
        return new Class[]{String.class};
    }


    //The source will be connected to DeviceManagementService.
    @Override
    public void connect(Source.ConnectionCallback connectionCallback) throws
            ConnectionUnavailableException {

            DeviceManagementService.connectToSource(edgeSourceSubscriber);

    }

    @Override
    public void disconnect() {
            DeviceManagementService.disConnectToSource(edgeSourceSubscriber);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void pause() {
            DeviceManagementService.disConnectToSource(edgeSourceSubscriber);
    }

    @Override
    public void resume() {
            DeviceManagementService.connectToSource(edgeSourceSubscriber);

    }

    @Override
    public Map<String, Object> currentState() {
        return null;
    }

    @Override
    public void restoreState(Map<String, Object> map) {

    }








}
