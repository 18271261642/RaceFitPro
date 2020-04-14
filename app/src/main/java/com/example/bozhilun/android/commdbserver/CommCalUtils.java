package com.example.bozhilun.android.commdbserver;

import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin
 * Date 2019/3/8
 */
public class CommCalUtils {


    /**
     * 计算平均心率，最大心率等
     * @param hourRateData
     * @return
     */
    public static Integer[] calHeartData(List<HalfHourRateData> hourRateData){
        int maxHeart = 0;
        int mieHeart = 0;
        int avgHeart = 0;
        List<Integer> calList = new ArrayList<>();
        int countHeart = 0;
        for(HalfHourRateData halfHourRateData : hourRateData){
            calList.add(halfHourRateData.getRateValue());
            countHeart += halfHourRateData.getRateValue();
        }
        //最大心率
        maxHeart = Collections.max(calList);
        //最小心率
        mieHeart = Collections.min(calList);
        //平均
        avgHeart = countHeart/hourRateData.size();
        return new Integer[]{maxHeart,mieHeart,avgHeart};
    }


    /**
     * 计算血压高压和低压和平均高压平均低压
     * @param bpData
     * @return
     */
    public static Integer[]  calBloodData(List<HalfHourBpData> bpData){
        List<Integer> highValueList = new ArrayList<>();
        List<Integer> lowValueList = new ArrayList<>();

        int maxBlod = 0;
        int minBlod = 0;
        int avgMaxBlod = 0;
        int avgMinBlod = 0;

        int maxSum = 0;
        int minSum = 0;

        for(HalfHourBpData halfHourBpData : bpData){
            highValueList.add(halfHourBpData.getHighValue());
            lowValueList.add(halfHourBpData.getLowValue());
            maxSum += halfHourBpData.getHighValue();
            minSum += halfHourBpData.getLowValue();

        }
        maxBlod = Collections.max(highValueList);
        minBlod = Collections.min(lowValueList);
        avgMaxBlod = maxSum / bpData.size();
        avgMinBlod = minSum / bpData.size();
        return new Integer[]{maxBlod,minBlod,avgMaxBlod,avgMinBlod};

    }

    /**
     * 用于保存数据的map，365天
     * @param startDay 开始日期
     * @param endDay 结束日期
     * @return
     */
    public static Map<String,String> countStepMap(String startDay,String endDay){
        //上一天的日期
        String yesDay = WatchUtils.obtainFormatDate(0);
        Map<String,String> resultMap = new HashMap<>();
        for(int i = 0;i<365;i++){
            yesDay = WatchUtils.obtainAroundDate(yesDay,true,0);
            resultMap.put(yesDay,"0");
        }
        return resultMap;
    }


    public static Map<String,String> heartMap(String startDay,String endDay){
        //上一天的日期
        String yesDay = WatchUtils.obtainFormatDate(0);
        Map<String,String> resultMap = new HashMap<>();
        for(int i = 0;i<364;i++){
            yesDay = WatchUtils.obtainAroundDate(yesDay,true,0);
            resultMap.put(yesDay,"0");
        }
        return resultMap;
    }

    /**
     * 用于保存和计算睡眠的map
     * @param startDay 开始日期
     * @param endDay 结束日期
     * @return
     */
    public static Map<String,String> sleepMap(String startDay,String endDay){
        //上一天的日期
        String yesDay = WatchUtils.obtainFormatDate(0);
        Map<String,String> resultMap = new HashMap<>();
        for(int i = 0;i<364;i++){
            yesDay = WatchUtils.obtainAroundDate(yesDay,true,0);
            resultMap.put(yesDay,"0");
        }
        return resultMap;
    }


    /**
     * 用于保存计算一年血压的map
     * @param startDay 开始日期
     * @param endDay 结束日期
     * @return
     */
    public static Map<String,String> bloodMap(String startDay,String endDay){
        //上一天的日期
        String yesDay = WatchUtils.obtainFormatDate(0);
        Map<String,String> resultMap = new HashMap<>();
        for(int i = 0;i<364;i++){
            yesDay = WatchUtils.obtainAroundDate(yesDay,true,0);
            resultMap.put(yesDay,"0-0");
        }
        return resultMap;
    }


   // public static Map<String,String>


}
