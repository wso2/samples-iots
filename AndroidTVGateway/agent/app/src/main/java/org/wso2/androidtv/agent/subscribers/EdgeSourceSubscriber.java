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
