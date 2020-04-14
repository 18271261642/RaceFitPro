package com.example.bozhilun.android.siswatch.adapter;

/**
 * Created by Administrator on 2017/11/20.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.AmapSportBean;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.bzlmaps.mapdb.SportMaps;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 跑步列表显示适配器
 */
public class OutDoorSportAdapterNew extends RecyclerView.Adapter<OutDoorSportAdapterNew.OutDoorSportViewHolder> {

    private List<AmapSportBean> outDoorSportBeanList;
    private Context mContext;


    private OnOutDoorSportItemClickListener listener;

    public void setListener(OnOutDoorSportItemClickListener listener) {
        this.listener = listener;
    }

    public OutDoorSportAdapterNew(List<AmapSportBean> outDoorSportBeanList, Context mContext) {
        this.outDoorSportBeanList = outDoorSportBeanList;
        this.mContext = mContext;
    }

    @Override
    public OutDoorSportViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sporthistory_list_item, parent, false);
        return new OutDoorSportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OutDoorSportViewHolder holder, int position) {
        //日期
        holder.yearTv.setText((TextUtils.isEmpty(outDoorSportBeanList.get(position).getRtc())?"--":outDoorSportBeanList.get(position).getRtc()));
        //公里
        double distance = Double.valueOf(outDoorSportBeanList.get(position).getCountDisance());
        double tempDistance = distance * 1000;
        //总公里数

        boolean isUnit = (boolean) SharedPreferencesUtils.getParam(MyApp.getContext(), Commont.ISSystem, false);
        int h9_step_util = (int) SharedPreferencesUtils.getParam(MyApp.getInstance(), "H9_UTIT", 0);

        if(!WatchUtils.isEmpty(MyCommandManager.DEVICENAME) &&(MyCommandManager.DEVICENAME.equals("H9") ||
                MyCommandManager.DEVICENAME.equals("W06X"))){
            if (h9_step_util == 0) {
                if (distance > 0) {
                    BigDecimal decimal = new BigDecimal(distance / 1000.0);
                    BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
                    holder.discTv.setText(setScale.doubleValue() +" km");
                    holder.sumdiscTv.setText(setScale.doubleValue() +" km");
                }else {
                    holder.discTv.setText("0 km");
                    holder.sumdiscTv.setText("0 km");
                }
            } else {
                BigDecimal decimal = new BigDecimal(distance * 0.0006214);
                BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_DOWN);
                holder.discTv.setText(setScale.doubleValue()+" Mi");//String.format("%.3f", metric)
                holder.sumdiscTv.setText(setScale.doubleValue()+" Mi");
            }
        }else{
            if (isUnit) {     //公制
                //            holder.discTv.setText(""+Math.round(distance)+"km");
//            holder.sumdiscTv.setText(""+Math.round(distance)+"km");
                holder.discTv.setText(String.format("%.3f", distance)+" km");
                holder.sumdiscTv.setText(String.format("%.3f", distance)+" km");
            } else { //Ft 英里
                //            holder.discTv.setText(""+Math.round(WatchUtils.mul(tempDistance,3.28))+"ft");
//            holder.sumdiscTv.setText(""+Math.round(WatchUtils.mul(tempDistance,3.28))+"ft");
                holder.discTv.setText(WatchUtils.unitToImperial(distance,3)+" mile");//String.format("%.3f", metric)
                holder.sumdiscTv.setText(WatchUtils.unitToImperial(distance,3)+" mile");
            }
        }


        //开始时间
        holder.dateWeekTv.setText(outDoorSportBeanList.get(position).getDetailTime());
        //0 跑步 ；1 骑行
        int outSportType = outDoorSportBeanList.get(position).getSportType();
        if (outSportType == 0) {
            holder.typeImg.setImageResource(R.mipmap.huwaipaohuan);
            holder.outSportTypeTv.setText(mContext.getResources().getString(R.string.outdoor_running));
        } else {
            holder.typeImg.setImageResource(R.mipmap.image_w30s_qixing_run);
            holder.outSportTypeTv.setText(mContext.getResources().getString(R.string.outdoor_cycling));
        }
        //运动时间
        holder.sportDruationTv.setText((TextUtils.isEmpty(outDoorSportBeanList.get(position).getCountTime())?"--":outDoorSportBeanList.get(position).getCountTime()));
        //卡里留
        String calories = outDoorSportBeanList.get(position).getKcal();
        holder.kcalTv.setText(""+(TextUtils.isEmpty(calories)||calories ==null?"0":calories)+"kcal");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    int position = holder.getLayoutPosition();
                    listener.doItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return outDoorSportBeanList.size();
    }

    class OutDoorSportViewHolder extends RecyclerView.ViewHolder {

        TextView yearTv, sumdiscTv, dateWeekTv, outSportTypeTv, discTv, sportDruationTv, kcalTv;
        ImageView typeImg;

        public OutDoorSportViewHolder(View itemView) {
            super(itemView);
            //日期
            yearTv = (TextView) itemView.findViewById(R.id.my_year);
            //总里程
            sumdiscTv = (TextView) itemView.findViewById(R.id.shuji_zonggongli);
            //运动类型展示图片
            typeImg = (ImageView) itemView.findViewById(R.id.my_paobu);
            //星期
            dateWeekTv = (TextView) itemView.findViewById(R.id.ri_xiangqing);
            //运动类型
            outSportTypeTv = (TextView) itemView.findViewById(R.id.qixing_my_huwai);
            //此次运动里程
            discTv = (TextView) itemView.findViewById(R.id.chixugongli_time);
            //持续运动时长
            sportDruationTv = (TextView) itemView.findViewById(R.id.chixu_time);
            //卡路里
            kcalTv = (TextView) itemView.findViewById(R.id.my_kacal);
        }
    }

    public interface OnOutDoorSportItemClickListener {
        void doItemClick(int position);
    }

}
