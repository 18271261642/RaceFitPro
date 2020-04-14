package com.example.bozhilun.android.util;


import java.util.HashMap;

import cn.sharesdk.framework.Platform;

public interface LoginListenter {
    void OnLoginListenter(Platform platform, HashMap<String, Object> hashMap);

    void onError(Platform platform, int i, Throwable throwable);

    void onCancel(Platform platform, int i);
}
