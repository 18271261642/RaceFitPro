package com.example.bozhilun.android.commdbserver;

/**
 * Created by Admin
 * Date 2019/3/6
 * 上传和下载缓存的数据，数据库中保存的数据 地址
 */
public class SyncDbUrls {

    //base地址
    static final String SYNC_BASE_URL = "http://47.90.83.197:9070/Watch/";

    //上传总步数
    public static String uploadCountStepUrl(){
        return SYNC_BASE_URL+"sportDay/submit";
    }
    //下载步数
    public static String downloadCountStepUrl(){
        return SYNC_BASE_URL + "sportDay/getList";
    }


    //上传心率
    public static String uploadHeartUrl(){
        return SYNC_BASE_URL + "heartRateDay/submit";
    }

    //下载心率
    public static String downloadHeartUrl(){
        return SYNC_BASE_URL + "heartRateDay/getList";
    }



    //上传睡眠
    public static String uploadSleepUrl(){
        return SYNC_BASE_URL + "sleepDay/submit";
    }

    //下载睡眠
    public static String downloadSleepUrl(){
        return SYNC_BASE_URL + "sleepDay/getList";
    }


    //上传血压
    public static String uploadBloodUrl(){
        return SYNC_BASE_URL + "bloodPressureDay/submit";
    }

    //下载血压
    public static String downloadBloodUrl(){
        return SYNC_BASE_URL + "bloodPressureDay/getList";

    }


    //上传当天汇总的血氧数据
    public static String uploadTodayCountSpo2(){
        return SYNC_BASE_URL + "bloodOxygenDay/submit";
    }

    //下载当天汇总的血氧数据
    public static String downloadTodayCountSpo2(){
        return SYNC_BASE_URL + "bloodOxygenDay/getList";
    }

    //上传当天汇总的HRV数据
    public static String uploadTodayCountHrv(){
        return SYNC_BASE_URL + "hrvDay/submit";
    }




    /*******************************详细数据***********************************/

    //上传步数详细数据
    public static String uploadDetailStepUrl(){
        return SYNC_BASE_URL + "stepNumber/submit";
    }


    //上传心率详细数据
    public static String uploadDetailHeartUrl(){
        return SYNC_BASE_URL + "heartRate/submit";
    }


    //上传睡眠详细数据
    public static String uploadDetailSleepUrl(){
        return SYNC_BASE_URL + "sleepSlot/submit";
    }


    //上传血压详细数据
    public static String uploadDetailBloodUrl(){
        return SYNC_BASE_URL + "bloodPressure/submit";
    }


    //上传血氧详细数据
    public static String uploadBloodOxyUrl(){
        return SYNC_BASE_URL + "bloodOximetry/submit";
    }

    //下载血氧详细数据
    public static String downloadBloodOxyUrl(){
        return SYNC_BASE_URL + "bloodOximetry/getList";
    }

    //上传详细HRV数据
    public static String uploadHrvDetailUrl(){
        return SYNC_BASE_URL + "hrv/submit";
    }

    //下载详细HRV数据
    public static String downloadHrvDetailUrl(){
        return SYNC_BASE_URL + "hrv/getList";
    }

}
