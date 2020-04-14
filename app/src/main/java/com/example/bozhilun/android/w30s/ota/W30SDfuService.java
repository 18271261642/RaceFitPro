package com.example.bozhilun.android.w30s.ota;

import android.app.Activity;

import vpno.nordicsemi.android.dfu.DfuBaseService;


/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/4/20 15:32
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SDfuService extends DfuBaseService {

    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return W30NotificationActivity.class;
    }
}
