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

package org.wso2.androidtv.agent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.wso2.androidtv.agent.services.DeviceManagementService;
import org.wso2.androidtv.agent.util.LocalRegistry;

public class RegisteredActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        Intent serviceIntent = new Intent(this, DeviceManagementService.class);
        startService(serviceIntent);

        final Button unregisterBtn = (Button) findViewById(R.id.button);
        unregisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregister();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean unregister() {
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

        //Stop current running background services.
        Intent myService = new Intent(this, DeviceManagementService.class);
        stopService(myService);

        Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
        registerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerActivity);
        finish();
        return true;
    }

}
