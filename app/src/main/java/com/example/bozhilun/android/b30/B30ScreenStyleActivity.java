package com.example.bozhilun.android.b30;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.bean.SkinColorBean;
import com.example.bozhilun.android.b30.view.OnDeviceStyleSelectListener;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IScreenStyleListener;
import com.veepoo.protocol.model.datas.ScreenStyleData;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2018/8/14.
 */

/**
 * 主题界面风格设置
 */
public class B30ScreenStyleActivity extends WatchBaseActivity implements OnDeviceStyleSelectListener{

    private static final String TAG = "B30ScreenStyleActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.deviceStyleListView)
    ListView deviceStyleListView;


    private List<SkinColorBean> resultList;
    private DeviceStyleAdapter deviceStyleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_themestyle);
        ButterKnife.bind(this);

        initViews();


        readStyleData();
    }

    private void readStyleData() {

        final int styleCount = (int) SharedPreferencesUtils.getParam(B30ScreenStyleActivity.this, Commont.SP_DEVICE_STYLE_COUNT, 0);

        MyApp.getInstance().getVpOperateManager().readScreenStyle(iBleWriteResponse, new IScreenStyleListener() {
            @Override
            public void onScreenStyleDataChange(ScreenStyleData screenStyleData) {
                resultList.clear();
                Log.e(TAG, "--------screenStyleData=" + screenStyleData.toString());
                for (int i = 0; i < styleCount; i++) {
                    SkinColorBean deviceStyleBean = new SkinColorBean();
                    deviceStyleBean.setImgId(i);
                    deviceStyleBean.setChecked(i == screenStyleData.getscreenStyle());
                    resultList.add(deviceStyleBean);
                }

                deviceStyleAdapter.notifyDataSetChanged();

            }
        });
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.string_devices_ui));

        resultList = new ArrayList<>();
        deviceStyleAdapter = new DeviceStyleAdapter(resultList);
        deviceStyleListView.setAdapter(deviceStyleAdapter);
        deviceStyleAdapter.setOnDeviceStyleSelectListener(this);
        commentB30BackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void settingStyle(int styleId) {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getInstance().getVpOperateManager().settingScreenStyle(iBleWriteResponse, new IScreenStyleListener() {
                @Override
                public void onScreenStyleDataChange(ScreenStyleData screenStyleData) {

                }
            }, styleId);
        }
    }



    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {
            Log.d("---------", i + "");
        }
    };


    //主题选择
    @Override
    public void onItemStyleSelect(int position) {
        settingStyle(position);
    }


    private class DeviceStyleAdapter extends BaseAdapter {

        LayoutInflater layoutInflater;
        private List<SkinColorBean> list;

        private int selectId = -1;

        private OnDeviceStyleSelectListener onDeviceStyleSelectListener;

        public void setOnDeviceStyleSelectListener(OnDeviceStyleSelectListener onDeviceStyleSelectListener) {
            this.onDeviceStyleSelectListener = onDeviceStyleSelectListener;
        }

        public DeviceStyleAdapter(List<SkinColorBean> list) {
            this.list = list;
            layoutInflater = LayoutInflater.from(B30ScreenStyleActivity.this);
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
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_device_style_layout, parent, false);
                holder.tv = convertView.findViewById(R.id.itemStyleTv);
                holder.checkBox = convertView.findViewById(R.id.itemStyleCheckBox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(position == 0 ? getResources().getString(R.string.string_default_style) : getResources().getString(R.string.style_position) + position);

            final CheckBox checkBox = holder.checkBox;
            checkBox.setChecked(list.get(position).isChecked());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!buttonView.isPressed())
                        return;
                    if (onDeviceStyleSelectListener != null)
                        onDeviceStyleSelectListener.onItemStyleSelect(position);
                    if (checkBox.isChecked()) {
                        selectId = position;
                        list.get(position).setChecked(true);
                    } else {
                        selectId = -1;
                    }

                    for (int i = 0; i < list.size(); i++) {
                        if (selectId != i) {
                            list.get(i).setChecked(false);
                        }
                    }

                    notifyDataSetChanged();
                }
            });


            return convertView;
        }


        class ViewHolder {
            TextView tv;
            CheckBox checkBox;
        }


    }


}
