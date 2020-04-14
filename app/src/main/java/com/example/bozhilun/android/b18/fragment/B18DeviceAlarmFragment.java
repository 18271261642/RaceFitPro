package com.example.bozhilun.android.b18.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b18.B18BleConnManager;
import com.example.bozhilun.android.b18.modle.B18AlarmBean;
import com.example.bozhilun.android.b18.modle.B18AlarmDbManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import static android.widget.AdapterView.*;

/**
 * B18闹钟
 * Created by Admin
 * Date 2019/11/14
 */
public class B18DeviceAlarmFragment extends LazyFragment implements OnItemClickListener,OnAlarmToggCheckListener {

    private static final String TAG = "B18DeviceAlarmFragment";

    View view;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    Unbinder unbinder;


    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;


    private List<B18AlarmBean> alarmBeanList;

   // private B18AlarmAdapter b18AlarmAdapter;


    private B18LvAdapter b18LvAdapter;

    @BindView(R.id.b18AlarmListView)
    ListView b18AlarmListView;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_b18_alarm_layout, container, false);
        unbinder = ButterKnife.bind(this, view);


        initViews();

        return view;
    }

    private void initData() {
        alarmBeanList.clear();
        if(B18AlarmDbManager.getB18AlarmDbManager().findAllAlarm() == null){
            B18AlarmDbManager.getB18AlarmDbManager().createB18AlarmDb();
        }
        List<B18AlarmBean> tmpList = B18AlarmDbManager.getB18AlarmDbManager().findAllAlarm();
        alarmBeanList.addAll(tmpList);
        b18LvAdapter.notifyDataSetChanged();
        setAllDeviceAlarmd(alarmBeanList);

    }

    private void setAllDeviceAlarmd(List<B18AlarmBean> alarmBeanList) {
        B18BleConnManager.getB18BleConnManager().setAllAlarm(alarmBeanList);
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.alarm_clock));

        alarmBeanList = new ArrayList<>();
        b18LvAdapter = new B18LvAdapter(alarmBeanList);
        b18AlarmListView.setAdapter(b18LvAdapter);
        b18AlarmListView.setOnItemClickListener(this);
        b18LvAdapter.setOnAlarmToggCheckListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
        Log.e(TAG,"-----onResume------");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    //,R.id.addB18AlarmBtn
    @OnClick({R.id.commentB30BackImg})
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        switch (v.getId()){
            case R.id.commentB30BackImg:
                getFragmentManager().popBackStack();
                break;
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fragmentManager = getFragmentManager();
        if (fragmentManager == null)
            return;
        B18AlarmDbManager.getB18AlarmDbManager().setB18AlarmBean(alarmBeanList.get(position));
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.b18DeviceFragment, new UpdateB18AlarmFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onPostionCheck(int position, boolean isCheck) {
        if(alarmBeanList.isEmpty())
            return;
        setUpdateAlamr(alarmBeanList.get(position),isCheck);

    }

    private void setUpdateAlamr(B18AlarmBean b18AlarmBean,boolean checked){
       // showLoadingDialog("Loading...");
       b18AlarmBean.setOpen(checked);
       boolean isUpdate = B18AlarmDbManager.getB18AlarmDbManager().updateForIdAlarm(b18AlarmBean);
      if(isUpdate) initData();

    }


    private class B18LvAdapter extends BaseAdapter{

        private List<B18AlarmBean> list;
        private LayoutInflater layoutInflater;

        private OnAlarmToggCheckListener onAlarmToggCheckListener;

        public void setOnAlarmToggCheckListener(OnAlarmToggCheckListener onAlarmToggCheckListener) {
            this.onAlarmToggCheckListener = onAlarmToggCheckListener;
        }

        public B18LvAdapter(List<B18AlarmBean> list) {
            this.list = list;
            layoutInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_b18_alarm_layout,parent,false);
                holder.timeTv = convertView.findViewById(R.id.itemB18AlarmTimeTv);
                holder.weekTv = convertView.findViewById(R.id.itemB18AlarmWeeksTv);
                holder.toggleButton = convertView.findViewById(R.id.itemB18AlarmSwitchToggle);
                holder.typeImg = convertView.findViewById(R.id.itemB18AlarmImg);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            int hour = list.get(position).getHour();
            int mine = list.get(position).getMinute();

            holder.timeTv.setText((hour <= 9 ? 0+""+hour : hour) +":"+(mine<=9?0+""+mine : mine));
            holder.weekTv.setText(list.get(position).showAlarmWeeks(getActivity()));
            holder.toggleButton.setChecked(list.get(position).isOpen());
            ImageView imageView = holder.typeImg;
            switch (position){
                case 0:
                    imageView.setImageResource(R.mipmap.icon_alarm_1);
                    break;
                case 1:
                    imageView.setImageResource(R.mipmap.icon_alarm_2);
                    break;
                case 2:
                    imageView.setImageResource(R.mipmap.icon_alarm_3);
                    break;
                case 3:
                    imageView.setImageResource(R.mipmap.icon_alarm_4);
                    break;
                case 4:
                    imageView.setImageResource(R.mipmap.icon_alarm_5);
                    break;
                case 5:
                    imageView.setImageResource(R.mipmap.icon_alarm_6);
                    break;
            }



            holder.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!buttonView.isPressed())
                        return;
                    if(onAlarmToggCheckListener != null){
                        onAlarmToggCheckListener.onPostionCheck(position,isChecked);
                    }
                }
            });



            return convertView;
        }

        class ViewHolder{
            TextView timeTv,weekTv;
            ToggleButton toggleButton;
            ImageView typeImg;
        }


    }

}
