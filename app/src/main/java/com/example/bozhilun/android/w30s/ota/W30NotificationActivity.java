package com.example.bozhilun.android.w30s.ota;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/4/20 15:56
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
//            final Intent intent = new Intent(this, NewW30sFirmwareUpgrade.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtras(getIntent().getExtras()); // copy all extras
//            startActivity(intent);
            // Start the app before finishing
            final Intent parentIntent = new Intent(this, NewW30sFirmwareUpgrade.class);
            parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final Intent startAppIntent = new Intent(this, NewW30sFirmwareUpgrade.class);
            startAppIntent.putExtras(getIntent().getExtras());
            startActivities(new Intent[] { parentIntent, startAppIntent });

        }
        // Now finish, which will drop you to the activity at which you were at the top of the task stack
        finish();
    }
}
