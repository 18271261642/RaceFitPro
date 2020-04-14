package com.example.bozhilun.android.b30;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.afa.tourism.greendao.gen.B30DevicesSportDao;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.B30DevicesSport;
import com.example.bozhilun.android.bzlmaps.mapdb.LatLonBean;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.adapters.CommonRecyclerAdapter;
import com.example.bozhilun.android.w30s.adapters.MyViewHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DevicesSportHisyory extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout frmBack;
    private ImageView imageDate;
    private RecyclerView commentRunRecyclerView;
    private ImageView imageNoData;
    @SuppressLint("SimpleDateFormat")
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    @SuppressLint("SimpleDateFormat")
    private DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String selectTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpssport_history);
        initViews();
        selectTime = df1.format(new Date());
    }

    protected void initViews() {
        frmBack = findViewById(R.id.frm_back);
        imageDate = findViewById(R.id.image_data);
        commentRunRecyclerView = findViewById(R.id.rec_list);
        LinearLayoutManager linm = new LinearLayoutManager(this);
        linm.setOrientation(LinearLayoutManager.VERTICAL);
        commentRunRecyclerView.setLayoutManager(linm);
        commentRunRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        imageNoData = findViewById(R.id.image_no_data);
        frmBack.setOnClickListener(this);
        imageDate.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        finHisData(df.format(new Date()));
    }

    private List<B30DevicesSport> b30DevicesSports = null;

    private List<LatLonBean> latLonBeanList = null;


    /**
     * 从数据看查询数据
     */
    private void finHisData(String dateTime) {
        String bm = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);//设备mac mylanmac
        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), "userId");//
        if (WatchUtils.isEmpty(userId)) return;
        if ( WatchUtils.isEmpty(bm)) return;

        b30DevicesSports = MyApp.getInstance().getDBManager().getDaoSession().getB30DevicesSportDao()
                .queryBuilder().where(B30DevicesSportDao.Properties.Address.eq(bm)
                        , B30DevicesSportDao.Properties.Date.eq(dateTime)).list();

        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
        if (b30DevicesSports == null || b30DevicesSports.size() <= 0) {
            imageNoData.setVisibility(View.VISIBLE);
            commentRunRecyclerView.setVisibility(View.GONE);
            return;
        }
        imageNoData.setVisibility(View.GONE);
        commentRunRecyclerView.setVisibility(View.VISIBLE);

        MyAdapter myAdapter = new MyAdapter(this, b30DevicesSports, R.layout.devices_sport_item);
        commentRunRecyclerView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }




    /**
     * rec---适配器
     */
    class MyAdapter extends CommonRecyclerAdapter<B30DevicesSport> {

        public MyAdapter(Context context, List<B30DevicesSport> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(MyViewHolder holder, final B30DevicesSport item) {
            holder.setText(R.id.text_kcal,item.getKcals()+getResources().getString(R.string.km_cal));
            holder.setText(R.id.text_heart,item.getAverRate()+"bpm");
            holder.setText(R.id.text_step,item.getStepCount()+getResources().getString(R.string.daily_numberofsteps_default));
            holder.setText(R.id.text_data_time,item.getStartTime());
            holder.setText(R.id.text_times,WatchUtils.secToTime(item.getSportTime()));
        }
    }

    /*
     * 毫秒转化
     */
    public static String formatTime(long ms) {

        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        String strDay = day < 10 ? "0" + day : "" + day; //天
        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
        strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

        return strMinute + ":" + strSecond + ":" + strMilliSecond;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frm_back:
                finish();
                break;
            case R.id.image_data:
                CalendarView calendarView = showPop(view);
                calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                        if (b30DevicesSports != null) b30DevicesSports.clear();
                        if (latLonBeanList != null) latLonBeanList.clear();
                        String m = "";
                        String d = "";
                        if ((i1 + 1) < 10) {
                            m = "0" + (i1 + 1);
                        } else {
                            m = "" + (i1 + 1);
                        }
                        if (i2 < 10) {
                            d = "0" + i2;
                        } else {
                            d = "" + i2;
                        }
                        selectTime = i + "-" + m + "-" + d + " 00:00:00";
                        finHisData(i + "-" + m + "-" + d);
                    }
                });
                break;
        }
    }


    private PopupWindow popupWindow = null;

    /**
     * 设置pop
     *
     * @param view
     * @return
     */
    CalendarView showPop(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentview = inflater.inflate(R.layout.date_time_popupwidow, null);
        contentview.getBackground().setAlpha(150);
        popupWindow = new PopupWindow(contentview,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        popupWindow.setBackgroundDrawable(dw);
//        contentview.setOnKeyListener(this);
        popupWindow.showAtLocation(view, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        CalendarView c = contentview.findViewById(R.id.calend_views);
        long l = dataOne(selectTime);
        if (l != 0) c.setDate(l);
        return c;
    }

    /**
     * 调此方法输入所要转换的时间输入例如（"2014-06-14-16-09-00"）返回时间戳 1539964800
     *
     * @param time
     * @return
     */
    public static long dataOne(String time) {
        long ts = 0;
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(time);
            ts = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ts;
    }

    /**
     * 监听Back键按下事件
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
