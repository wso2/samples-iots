package org.wso2.androidtv.agent.siddhiSources;

import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.stream.input.source.Source;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.StreamDefinition;


/**
 * Created by gathikaratnayaka on 8/2/17.
 */

public abstract class AbstractEdgeSource extends Source {

    protected String CONTEXT="context";
    protected SourceEventListener sourceEventListener;
    protected StreamDefinition streamDefinition;
    protected OptionHolder optionHolder;
    protected String context;







    @Override
    public void init(SourceEventListener sourceEventListener, OptionHolder optionHolder, String[] strings, ConfigReader configReader, SiddhiAppContext siddhiAppContext) {
        this.sourceEventListener=sourceEventListener;
        context=optionHolder.validateAndGetStaticValue(CONTEXT,
                siddhiAppContext.getName()+"/"+sourceEventListener.getStreamDefinition().getId());
        streamDefinition=StreamDefinition.id(context);
        streamDefinition.getAttributeList().addAll(sourceEventListener.getStreamDefinition().getAttributeList());
        this.optionHolder=optionHolder;
    }



}
