package org.wso2.carbon.device.mgt.androidtv.agent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.wso2.carbon.device.mgt.androidtv.agent.mqtt.AndroidTVMQTTHandler;
import org.wso2.carbon.device.mgt.androidtv.agent.mqtt.transport.MQTTTransportHandler;
import org.wso2.carbon.device.mgt.androidtv.agent.util.LocalRegistry;

public class RegisteredActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        final Button unregisterBtn = (Button) findViewById(R.id.button);
        unregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregister();
            }
        });
    }

    public boolean unregister() {
        if (!LocalRegistry.isExist(getApplicationContext())) {
            Intent activity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(activity);
        }
        LocalRegistry.setEnrolled(getApplicationContext(), false);
        LocalRegistry.removeUsername(getApplicationContext());
        LocalRegistry.removeDeviceId(getApplicationContext());
        LocalRegistry.removeServerURL(getApplicationContext());
        LocalRegistry.removeAccessToken(getApplicationContext());
        LocalRegistry.removeRefreshToken(getApplicationContext());
        LocalRegistry.removeMqttEndpoint(getApplicationContext());
        LocalRegistry.removeTenantDomain(getApplicationContext());
        LocalRegistry.setExist(false);
        //Stop the current running background services.

        MQTTTransportHandler mqttTransportHandler = AndroidTVMQTTHandler.getInstance(getApplicationContext());

        //Disconnects the mqtt connection.
        if (mqttTransportHandler.isConnected()) {
            mqttTransportHandler.disconnect();
        }

        Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
        registerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerActivity);
        finish();
        return true;
    }
}
