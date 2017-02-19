package org.wso2.iot.alertme.plugin.impl.dto;

/**
 * Created by ruwan on 2/16/17.
 */
public class Event {

    MetaData metaData;
    PayloadData payloadData;

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public PayloadData getPayloadData() {
        return payloadData;
    }

    public void setPayloadData(PayloadData payloadData) {
        this.payloadData = payloadData;
    }
}
