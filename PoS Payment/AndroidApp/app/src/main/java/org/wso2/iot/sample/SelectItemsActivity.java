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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.iot.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.wso2.iot.sample.adaptors.ItemAdaptor;
import org.wso2.iot.sample.constants.DeviceConstants;
import org.wso2.iot.sample.models.ItemModel;
import org.wso2.iot.sample.services.DeviceManagementService;
import org.wso2.iot.sample.util.LocalRegistry;

public class SelectItemsActivity extends AppCompatActivity {

    private static final String TAG = SelectItemsActivity.class.getSimpleName();
    private static final int QR_REQUEST_CODE = 1001;
    private ItemModel[] itemModels;
    private EditText editTextPoSId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_items);

        Intent serviceIntent = new Intent(this, DeviceManagementService.class);
        startService(serviceIntent);

        final ImageButton btnUnregister = findViewById(R.id.btn_disconnect);
        btnUnregister.setVisibility(View.VISIBLE);
        btnUnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregister();
            }
        });

        ListView listView = findViewById(R.id.listItems);
        itemModels = new ItemModel[5];
        itemModels[0] = new ItemModel("pizza", false);
        itemModels[1] = new ItemModel("burger", false);
        itemModels[2] = new ItemModel("olives", false);
        itemModels[3] = new ItemModel("orange", false);
        itemModels[4] = new ItemModel("tomato", false);
        ItemAdaptor adapter = new ItemAdaptor(this, itemModels);
        listView.setAdapter(adapter);

        editTextPoSId = findViewById(R.id.editTextPoSId);

        final Button btnCheckout = findViewById(R.id.btnCheckout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(editTextPoSId.getText())) {
                    Intent activity = new Intent(getApplicationContext(), CheckoutActivity.class);
                    activity.putExtra(DeviceConstants.SELECTED_ITEMS, itemModels);
                    activity.putExtra(DeviceConstants.POS_ID, editTextPoSId.getText().toString());
                    startActivity(activity);
                } else {
                    Toast.makeText(SelectItemsActivity.this, "Please provide PoS Device Id before checkout", Toast.LENGTH_LONG).show();
                    editTextPoSId.requestFocus();
                }
            }
        });

        final Button btnScanQR = findViewById(R.id.btnScanQR);
        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SelectItemsActivity.this, ScanPoSQRActivity.class);
                startActivityForResult(i, QR_REQUEST_CODE);
            }
        });
    }

    private void unregister() {
        Log.i(TAG, "Unregistering agent");
        if (!LocalRegistry.isExist(getApplicationContext())) {
            Intent activity = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(activity);
            finish();
            return;
        }
        LocalRegistry.removeUsername(getApplicationContext());
        LocalRegistry.removeDeviceId(getApplicationContext());
        LocalRegistry.removeServerURL(getApplicationContext());
        LocalRegistry.removeAccessToken(getApplicationContext());
        LocalRegistry.removeRefreshToken(getApplicationContext());
        LocalRegistry.removeMqttEndpoint(getApplicationContext());
        LocalRegistry.setExist(false);

        //Stop current running background services.
        Intent myService = new Intent(this, DeviceManagementService.class);
        stopService(myService);

        Intent registerActivity = new Intent(getApplicationContext(), RegisterActivity.class);
        registerActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerActivity);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == QR_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra(DeviceConstants.SCANNED_QR);
                editTextPoSId.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "QR Scan canceled. Please try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

}
