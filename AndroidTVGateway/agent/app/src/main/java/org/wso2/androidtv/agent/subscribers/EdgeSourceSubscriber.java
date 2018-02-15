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

package org.wso2.androidtv.agent.subscribers;


import org.wso2.siddhi.core.stream.input.source.SourceEventListener;

/**
 * Edge Source subscriber will receive data before the TextEdge source.
 * This class is there to provide a mechanism to check the receiving data
 * before a source receives that data.
 */

public class EdgeSourceSubscriber {

    private SourceEventListener sourceEventListener;
    private String id;


    public EdgeSourceSubscriber(SourceEventListener sourceEventListener, String id){
        this.sourceEventListener = sourceEventListener;
        this.id = id;
    }

    public void recieveEvent(String message, String[] strings){
        sourceEventListener.onEvent(message,strings);
    }
}
