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

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.iot.sample.constants.DeviceConstants;
import org.wso2.iot.sample.models.ItemModel;
import org.wso2.iot.sample.mqtt.transport.TransportHandlerException;
import org.wso2.iot.sample.services.DeviceManagementService;
import org.wso2.iot.sample.util.dto.Operation;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = CheckoutActivity.class.getSimpleName();

    private ItemModel[] itemModels;
    private String posId;
    private DeviceManagementService deviceManagementService;
    private boolean isBound = false;
    private boolean isPending = false;
    private ProgressDialog dialog;
    private TextView txtOrderDetails;
    private Operation checkoutOperation;

    private BroadcastReceiver operationsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isPending = false;
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            checkoutOperation = (Operation) intent.getSerializableExtra(DeviceConstants.CHECKOUT_OPERATION);
            txtOrderDetails.setText("Bill Total: " + checkoutOperation.getPayload());
        }
    };

    private ServiceConnection deviceManagementServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            DeviceManagementService.DeviceManagementBinder binder = (DeviceManagementService.DeviceManagementBinder) iBinder;
            deviceManagementService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        itemModels = (ItemModel[]) getIntent().getSerializableExtra(DeviceConstants.SELECTED_ITEMS);
        posId = getIntent().getStringExtra(DeviceConstants.POS_ID);

        txtOrderDetails = findViewById(R.id.txtOrderDetails);

        dialog = new ProgressDialog(this);
        isPending = true;
        Intent intent = new Intent(this, DeviceManagementService.class);
        bindService(intent, deviceManagementServiceConnection, Context.BIND_AUTO_CREATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCheckoutRequest();
            }
        }, 2000);

        Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOperationResponse("Confirmed");
                Toast.makeText(CheckoutActivity.this, "Order confirmed!", Toast.LENGTH_LONG).show();
            }
        });

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOperationResponse("Cancelled");
                Toast.makeText(CheckoutActivity.this, "Order Canceled!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendOperationResponse(String response) {
        if (isBound) {
            try {
                deviceManagementService.publishOperationResponse(checkoutOperation.getId(), response);
            } catch (TransportHandlerException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            Log.w(TAG, "Service is not bound.");
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeviceConstants.OPERATION_BROADCAST_ACTION);
        registerReceiver(operationsReceiver, filter);
        if (isPending) {
            dialog.setMessage("Checking out bill, please wait.");
            dialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(operationsReceiver);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(deviceManagementServiceConnection);
        }
    }

    private void sendCheckoutRequest() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pos_id", posId);
            JSONArray jsonArray = new JSONArray();
            for (ItemModel itemModel : itemModels) {
                if (itemModel.isChecked()) {
                    jsonArray.put(itemModel.getName());
                }
            }
            jsonObject.put("items", jsonArray);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        if (isBound) {
            try {
                String payload = jsonObject.toString();
                deviceManagementService.publishMessage(payload);
            } catch (TransportHandlerException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        } else {
            Log.w(TAG, "Service is not bound.");
        }
    }

}
