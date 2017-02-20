package org.wso2.iot.alertme.plugin.impl.dto;

/**
 * Created by ruwan on 2/16/17.
 */
public class PayloadData {
    private float ULTRASONIC;
    private float PIR;

    public float getULTRASONIC() {
        return ULTRASONIC;
    }

    public void setULTRASONIC(float ULTRASONIC) {
        this.ULTRASONIC = ULTRASONIC;
    }


    public float getPIR() {
        return PIR;
    }

    public void setPIR(float PIR) {
        this.PIR = PIR;
    }
}
