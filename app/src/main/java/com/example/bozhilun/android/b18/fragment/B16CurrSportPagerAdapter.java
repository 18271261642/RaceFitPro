package com.example.bozhilun.android.b18.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.modle.B16CadenceBean;
import com.example.bozhilun.android.b18.modle.B16CadenceResultBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Admin
 * Date 2020/1/7
 */
public class B16CurrSportPagerAdapter extends PagerAdapter {
    private static final String TAG = "B16CurrSportPagerAdapte";

    private List<B16CadenceResultBean> list;

    private Context context;

    private int flagCode;

    private Gson gson = new Gson();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);
    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm",Locale.CHINA);
    DecimalFormat decimalFormat = new DecimalFormat("#.###");


    public B16CurrSportPagerAdapter(List<B16CadenceResultBean> list, Context context,int code) {
        this.list = list;
        this.context = context;
        this.flagCode = code;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_b16_curr_sport,container,false);

        TextView noTv = new TextView(context);
        noTv.setText(context.getResources().getString(R.string.nodata));
        noTv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        noTv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        noTv.setTextSize(18f);
        noTv.setTextColor(Color.parseColor("#FFFF923C"));
       if(flagCode == 0){   //有数据时
           container.addView(view);
       }else{
           View emptyView = noTv;
           container.addView(emptyView);
           return emptyView;
       }

        TextView startTv = view.findViewById(R.id.itemB16CurrSportTimeTv);
        //下标
        TextView positionTv = view.findViewById(R.id.itemCurrSportTv);
        //消耗的卡路里，大于120的步频
        TextView kcalTv = view.findViewById(R.id.itemB16CurrSportKcalValueTv);
        //转换为燃烧的脂肪
        TextView fatTv = view.findViewById(R.id.itemB16CurrSportKgValueTv);
        TextView fat2Tv = view.findViewById(R.id.itemB16CurrFatTv);
        //30天消耗
        TextView dayFor30Fat = view.findViewById(R.id.itemB16CurrSportCountValue);

        positionTv.setText("Period "+(position+1)+"");
        B16CadenceResultBean resultBean = list.get(position);
        //运动时长 ，开始和结束
        String startTime = resultBean.getParamsDateStr();
        //只要时 分
        try {
            long tmpTime = sdf.parse(startTime).getTime();
            //开始时间
            String resultStartTime = sdf2.format(new Date(tmpTime));
            //间隔
            int sportDuration = resultBean.getSportDuration();
            //个数
            int sportCount = resultBean.getSportCount();
            //总的时间
            int allSportCount = sportCount * sportDuration;

            long tmpTime2 = tmpTime + allSportCount * 1000;
            //结束时间
            String resultEndTime = sdf2.format(new Date(tmpTime2));
            //运动时间
            startTv.setText(resultStartTime+"~"+resultEndTime);


            //消耗卡路里
            List<B16CadenceBean.DetailDataBean> detailDataBeanList = gson.fromJson(resultBean.getSportDetailStr(), new TypeToken<List<B16CadenceBean.DetailDataBean>>() {
            }.getType());

            //计算步频，要大于120步频的数据

            float tmpLevelTime = Float.valueOf(sportDuration) / 60;

            //总的卡路里
            int allCountKcal = 0;

            for(B16CadenceBean.DetailDataBean bb : detailDataBeanList){

                //步频
                float stepGa = bb.getStep() / tmpLevelTime;
                if(stepGa >= 120){
                    allCountKcal += bb.getCalorie();
                }

            }
                        //总的卡路里
            kcalTv.setText(allCountKcal+"");

            //计算燃烧的脂肪
            float currFatV = allCountKcal * 1000 / 7700;
            fatTv.setText(decimalFormat.format(currFatV/1000)+"");
            fat2Tv.setText(decimalFormat.format(currFatV/1000)+"克");
            dayFor30Fat.setText(decimalFormat.format(currFatV * 30 /1000 ) +"");



        } catch (ParseException e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {

        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
