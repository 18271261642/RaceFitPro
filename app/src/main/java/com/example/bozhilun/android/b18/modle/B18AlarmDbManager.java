package com.example.bozhilun.android.b18.modle;


import org.litepal.LitePal;
import java.util.List;

/**
 * 闹钟操作，固件不支持闹钟返回
 * Created by Admin
 * Date 2019/11/15
 */
public class B18AlarmDbManager {


    public B18AlarmBean b18AlarmBean;

    public B18AlarmBean getB18AlarmBean() {
        return b18AlarmBean;
    }

    public void setB18AlarmBean(B18AlarmBean b18AlarmBean) {
        this.b18AlarmBean = b18AlarmBean;
    }

    private static B18AlarmDbManager b18AlarmDbManager;

    public B18AlarmDbManager() {
    }


    public static B18AlarmDbManager getB18AlarmDbManager(){
        if(b18AlarmDbManager == null){
            synchronized (B18AlarmDbManager.class){
                if(b18AlarmDbManager == null)
                    b18AlarmDbManager = new B18AlarmDbManager();
            }
        }
        return b18AlarmDbManager;
    }

    //创建数据表
    public void createB18AlarmDb(){
        for(int i = 0;i<6;i++){
            B18AlarmBean b18AlarmBean = new B18AlarmBean();
            b18AlarmBean.setId(i);
            b18AlarmBean.setHour(0);
            b18AlarmBean.setMinute(0);
            b18AlarmBean.setOpen(false);
            b18AlarmBean.setOpenSaturday(false);
            b18AlarmBean.setOpenSunday(false);
            b18AlarmBean.setOpenFriday(false);
            b18AlarmBean.setOpenThursday(false);
            b18AlarmBean.setOpenWednesday(false);
            b18AlarmBean.setOpenTuesday(false);
            b18AlarmBean.setOpenMonday(false);
            b18AlarmBean.save();

        }

    }


    //查询是否存在
    public List<B18AlarmBean> findAllAlarm(){
        List<B18AlarmBean> list = LitePal.findAll(B18AlarmBean.class);
        return list == null || list.isEmpty() ? null : list;
    }


    //根据ID修改数据
    public boolean updateForIdAlarm(B18AlarmBean bd){
        return bd.saveOrUpdate("id = ?",bd.getId()+"");

    }



}
