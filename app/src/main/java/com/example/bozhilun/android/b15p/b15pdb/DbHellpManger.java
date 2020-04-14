//package com.example.bozhilun.android.b15p.b15pdb;
//
//import com.example.bozhilun.android.b30.bean.B30HalfHourDB;
//
//import org.litepal.LitePal;
//
//import java.util.List;
//
//public class DbHellpManger {
//    private static volatile DbHellpManger instance = null;
//
//    private DbHellpManger() {
//
//    }
//
//    public static DbHellpManger getInstance() {
//        if (instance == null) {
//            synchronized (DbHellpManger.class) {
//                if (instance == null) {
//                    instance = new DbHellpManger();
//                }
//            }
//        }
//        return instance;
//    }
//
//
//    public synchronized boolean saveDataToDB(String mac, String date, String type, String datas) {
//        boolean result = false;
//        B30HalfHourDB db = new B30HalfHourDB();
//        db.setAddress(mac);
//        db.setDate(date);
//        db.setType(type);
//        db.setOriginData(datas);
//        db.setUpload(0);
//        result = db.saveOrUpdate("address=? and date =? and type=?", mac, date, type);
//        return result;
//    }
//
//
//    public synchronized String getDataToDB(String mac, String dates, String type) {
//        B30HalfHourDB result = getOriginData(mac, dates, type);
//        return result == null ? null : result.getOriginData();
//    }
//
//
//    /**
//     * 获取单条数据源
//     *
//     * @param address 手环MAC地址
//     * @param date    日期
//     * @param type    数据类型
//     * @return 数据源Json字符串
//     */
//    private B30HalfHourDB getOriginData(String address, String date, String type) {
//        String where = "address = ? and date = ? and type = ?";
//        List<B30HalfHourDB> resultList = LitePal.where(where, address, date, type).limit(1).find
//                (B30HalfHourDB.class);// 一个类型,同一天只有一条数据
//        return resultList == null || resultList.isEmpty() ? null : resultList.get(0);
//    }
//}
