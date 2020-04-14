package com.example.bozhilun.android.view;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.NewLoginActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

/**
 * Created by Admin
 * Date 2020/1/11
 */
public class ShowPermissDialogView extends WatchBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permiss_layout);

        findViewById(R.id.permissBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtils.setParam(ShowPermissDialogView.this,"is_first",true);
               finish();
              //startActivity(NewLoginActivity.class);
            }
        });


    }
}
