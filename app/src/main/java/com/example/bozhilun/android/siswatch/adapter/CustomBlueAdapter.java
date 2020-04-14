package com.example.bozhilun.android.siswatch.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.bean.CustomBlueDevice;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/31.
 */

/**
 * 搜索页面适配器
 */
public class CustomBlueAdapter extends RecyclerView.Adapter<CustomBlueAdapter.CustomBlueViewHolder> {

    private List<CustomBlueDevice> customBlueDeviceList;
    private Context mContext;
    public OnSearchOnBindClickListener onBindClickListener;

    public void setOnBindClickListener(OnSearchOnBindClickListener onBindClickListener) {
        this.onBindClickListener = onBindClickListener;
    }

    public CustomBlueAdapter(List<CustomBlueDevice> customBlueDeviceList, Context mContext) {
        this.customBlueDeviceList = customBlueDeviceList;
        this.mContext = mContext;
    }

    @Override
    public CustomBlueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_bluedevice, null);
        return new CustomBlueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomBlueViewHolder holder, int position) {
        BluetoothDevice bluetoothDevice = customBlueDeviceList.get(position).getBluetoothDevice();
        if (bluetoothDevice != null) {
            //蓝牙名称
            holder.bleNameTv.setText(customBlueDeviceList.get(position).getBluetoothDevice().getName());
            //mac地址
            holder.bleMacTv.setText(customBlueDeviceList.get(position).getBluetoothDevice().getAddress());
            //信号
            holder.bleRiisTv.setText("" + customBlueDeviceList.get(position).getRssi() + "");
            //展示图片
            String bleName = customBlueDeviceList.get(position).getBluetoothDevice().getName();


            //绑定按钮
            holder.circularProgressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onBindClickListener != null) {
                        int position = holder.getLayoutPosition();
                        onBindClickListener.doBindOperator(position);
                    }
                }
            });


            Set<String> set = new HashSet<>(Arrays.asList(WatchUtils.TJ_FilterNamas));
            if(WatchUtils.isEmpty(bleName))
                return;
            if(bleName.contains("H9")){     //H9
                holder.img.setImageResource(R.mipmap.seach_h9);
                return;
            }

            if(customBlueDeviceList.get(position).getCompanyId() == 160 || bleName.contains("H8") || bleName.contains("bozlun")){   //H8
                holder.img.setImageResource(R.mipmap.h8_search);
                return;
            }

            if(bleName.contains("W30")){    //W30
                holder.img.setImageResource(R.mipmap.w30_searchlist_icon);
                return;
            }
            if(bleName.contains("W31")){    //W31
                holder.img.setImageResource(R.mipmap.ic_w31_search);
                return;
            }
            if(bleName.contains("W37")){    //W37
                holder.img.setImageResource(R.mipmap.ic_w37_search);
                return;
            }
            if(bleName.contains("B30")){
                holder.img.setImageResource(R.mipmap.ic_b30_search);
                return;
            }
            if(bleName.contains("Ringmii")){
                holder.img.setImageResource(R.mipmap.hx_search);
                return;
            }
            if(bleName.length() > 2 && (bleName.substring(0, 3).equals("B36"))){    //B36
                holder.img.setImageResource(R.mipmap.ic_b36_search);
                return;
            }
            if(bleName.length()>3 && bleName.substring(0,4).equals("B36M")){   //B36M
                holder.img.setImageResource(R.mipmap.ic_b36m_search);
                return;
            }
            if (bleName.length() >= 3 && bleName.substring(0, 3).equals("B31")) {  //B31
                holder.img.setImageResource(R.mipmap.ic_b31_search);
                return;
            }
            if (bleName.length() >= 4 && bleName.substring(0, 4).equals("B31S")) {  //B31
                holder.img.setImageResource(R.mipmap.ic_b31_search);
                return;
            }
            if(bleName.contains("500S")){   //500S
                holder.img.setImageResource(R.mipmap.ic_seach_500s);
                return;
            }
            if(bleName.contains("B18") || bleName.contains("B16")){ //B50(B18)
                holder.img.setImageResource(R.mipmap.icon_b16_search);
                return;
            }

            if(bleName.contains("X")){
                holder.img.setImageResource(R.mipmap.icon_xw_search);
                return;
            }

            if(bleName.equals("SWatch")){
                holder.img.setImageResource(R.mipmap.icon_s_watch_search);
                return;
            }

            if(bleName.contains("E Watch")){    //EWatch
                holder.img.setImageResource(R.mipmap.icon_e_watch_search);
                return;
            }

            if (set.contains(bleName)) {    //B25
                if (bleName.length() > 1 && !bleName.equals("F6")) {
                    if (bleName.length() >= 3 && bleName.substring(0, 3).equals("B25")) {
                        holder.img.setImageResource(R.mipmap.ic_b52_seach);
                    } else if (bleName.length() >= 4 && bleName.substring(0, 4).equals("B15P")) {
                        holder.img.setImageResource(R.mipmap.ic_b15p_seach);
                    } else {
                        if (bleName.substring(0, 1).equals("B")) {
                            holder.img.setImageResource(R.mipmap.ic_series_b);
                        } else if (bleName.substring(0, 1).equals("L")) {
                            holder.img.setImageResource(R.mipmap.ic_series_l);
                        } else if (bleName.substring(0, 1).equals("F")) {
                            holder.img.setImageResource(R.mipmap.ic_series_f);
                        } else if (bleName.substring(0, 1).equals("M")) {
                            holder.img.setImageResource(R.mipmap.ic_series_m);
                        } else if (bleName.substring(0, 1).equals("W")) {
                            holder.img.setImageResource(R.mipmap.ic_series_w);
                        }
                    }
                } else {
                    holder.img.setImageResource(R.mipmap.img_f6);
                }

            }



        }

    }

    @Override
    public int getItemCount() {
        return customBlueDeviceList.size();
    }

    class CustomBlueViewHolder extends RecyclerView.ViewHolder {

        TextView bleNameTv, bleMacTv, bleRiisTv;
        ImageView img;  //显示手表或者手环图片
        Button circularProgressButton;

        public CustomBlueViewHolder(View itemView) {
            super(itemView);
            bleNameTv = (TextView) itemView.findViewById(R.id.blue_name_tv);
            bleMacTv = (TextView) itemView.findViewById(R.id.snmac_tv);
            bleRiisTv = (TextView) itemView.findViewById(R.id.rssi_tv);
            img = (ImageView) itemView.findViewById(R.id.img_logo);
            circularProgressButton = itemView.findViewById(R.id.bind_btn);
        }
    }

    public interface OnSearchOnBindClickListener {
        void doBindOperator(int position);
    }
}
