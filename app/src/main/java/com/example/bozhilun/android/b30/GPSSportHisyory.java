package com.example.bozhilun.android.b30;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.AmapSportBean;
import com.example.bozhilun.android.bzlmaps.gaodemaps.AmapHistorySportActivity;
import com.example.bozhilun.android.siswatch.adapter.OutDoorSportAdapterNew;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * GPS运动历史记录
 */
public class GPSSportHisyory extends AppCompatActivity implements View.OnClickListener,
        OutDoorSportAdapterNew.OnOutDoorSportItemClickListener {

    private static final String TAG = "GPSSportHisyory";

    private FrameLayout frmBack;
    private ImageView imageDate;
    private RecyclerView commentRunRecyclerView;
    private ImageView imageNoData;

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private String selectTime;

    //数据集合
    private List<AmapSportBean> resultList;
    //适配器
    private OutDoorSportAdapterNew adapter;


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

        resultList = new ArrayList<>();
        adapter = new OutDoorSportAdapterNew(resultList, GPSSportHisyory.this);
        commentRunRecyclerView.setAdapter(adapter);
        adapter.setListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //从数据库中查找数据
        findHistoryForDB(WatchUtils.getCurrentDate());

    }

    //从数据库中查找数据
    private void findHistoryForDB(String currentDate) {
        if (resultList != null)
            resultList.clear();
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
        String bm = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.BLEMAC);//设备mac mylanmac
        String userId = (String) SharedPreferencesUtils.readObject(MyApp.getContext(), Commont.USER_ID_DATA);//
        if (WatchUtils.isEmpty(userId)) return;
        if (WatchUtils.isEmpty(bm)) return;



        String where = "rtc = ? and userId = ?";
        List<AmapSportBean> sportMapsList = LitePal.where(where,currentDate,userId).find(AmapSportBean.class);


//        DaoSession daoSession = MyApp.getInstance().getDBManager().getDaoSession();
//        if (daoSession == null)
//            return;
//        List<SportMaps> sportMapsList = daoSession.getSportMapsDao()
//                .queryBuilder().where(SportMapsDao.Properties.Mac.eq(bm),
//                        SportMapsDao.Properties.UserId.eq(userId), SportMapsDao.Properties.Rtc.eq(currentDate)).list();
        if (sportMapsList == null || sportMapsList.isEmpty()) {
            imageNoData.setVisibility(View.VISIBLE);
            commentRunRecyclerView.setVisibility(View.GONE);
            return;
        }
        imageNoData.setVisibility(View.GONE);
        commentRunRecyclerView.setVisibility(View.VISIBLE);
        resultList.addAll(sportMapsList);
        adapter.notifyDataSetChanged();

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
                        findHistoryForDB(i + "-" + m + "-" + d);
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
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


    /**
     * item点击
     *
     * @param position
     */
    @Override
    public void doItemClick(int position) {
        if(resultList.size()==0)
            return;
        String indexStr = new Gson().toJson(resultList.get(position));
        Intent intent = new Intent(GPSSportHisyory.this, AmapHistorySportActivity.class);
        intent.putExtra("sport_str", indexStr);
        startActivity(intent);







//
//        Map<String, Object> mapb = new HashMap<>();
//        mapb.put("year", resultList.get(position).getRtc());//日期
//        mapb.put("day", resultList.get(position).getStartTime());//开始日期
//        mapb.put("zonggongli", resultList.get(position).getDistance() + "Km");//总公里
//        if (resultList.get(position).getType() == 0) {
//            mapb.put("qixing", getResources().getString(R.string.outdoor_running));//骑行或者跑步
//            mapb.put("image", R.mipmap.huwaipaohuan);//跑步-骑行
//        } else {
//            mapb.put("qixing", getResources().getString(R.string.outdoor_cycling));//骑行或者跑步
//            mapb.put("image", R.mipmap.qixinghuan);//跑步-骑行
//        }
//        mapb.put("chixugongli", resultList.get(position).getDistance() + "Km");//持续公里数
//        mapb.put("chixutime", resultList.get(position).getTimeLen());//持续时间
//        mapb.put("kclal", resultList.get(position).getCalories() + "Kcal");//卡路里
//        mapb.put("image", resultList.get(position).getImage());
//        mapb.put("temp", resultList.get(position).getTemp());
//        mapb.put("description", resultList.get(position).getDescription());
//        mapb.put("speed", resultList.get(position).getSpeed());
//        Intent intent = new Intent(GPSSportHisyory.this, MapRecordActivity.class);
//        intent.putExtra("mapdata", resultList.get(position).getLatLons().trim());
//        intent.putExtra("mapdata2", new Gson().toJson(mapb));
//        startActivity(intent);
    }


}
