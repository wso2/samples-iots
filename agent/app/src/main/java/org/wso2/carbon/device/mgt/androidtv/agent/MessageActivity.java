package org.wso2.carbon.device.mgt.androidtv.agent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.wso2.carbon.device.mgt.androidtv.agent.constants.TVConstants;

public class MessageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        TextView messageView = (TextView) findViewById(R.id.textView);
        String message = getIntent().getStringExtra(TVConstants.MESSAGE);
        messageView.setText(message);

        Button btnOk = (Button) findViewById(R.id.button);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
