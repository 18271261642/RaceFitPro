package com.example.bozhilun.android.b11;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.ble.WriteBackDataListener;

import java.util.Calendar;

/**
 * Created by Admin
 * Date 2020/6/8
 */
public class B11BleOperate {

    private static  volatile B11BleOperate b11BleOperate;


    public static B11BleOperate getB11BleOperate(){
        if(b11BleOperate == null){
            synchronized (B11BleOperate.class){
                if(b11BleOperate == null)
                    b11BleOperate = new B11BleOperate();
            }
        }
        return b11BleOperate;
    }

    private B11BleOperate() {
    }




    //同步时间
    public void syncB11Time(MuiltByteDataListener muiltByteDataListener){
        /**
         * byte1 协议标识 0x9D
         * byte2 校验位 byte3+byte4++byte n;
         * byte3 标志状态
         * byte4 命令ID
         * byte5,byte6 数据长度
         * byte7..byten 数据内容
         */


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int currHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currTime = calendar.get(Calendar.MINUTE);
        int currSecond = calendar.get(Calendar.SECOND);

        //标志位
        String bitStr = "10010110";
        byte signByte = WatchUtils.bitToByte(bitStr);

        //校验位
        byte crcByte = (byte) ((byte) (signByte+0x02+0x07+0x20+0x20+ (byte) month+ (byte) day+ (byte) currHour+ (byte) currTime+ (byte) currSecond) & 0xff);

        byte[] timeByte = new byte[]{(byte) 0x9D,crcByte,signByte,0x02,0x00,0x07,0x20,0x20, (byte) month, (byte) day, (byte) currHour, (byte) currTime, (byte) currSecond};

        setDeviceByteData(timeByte,muiltByteDataListener);
    }



    /**
     * 设置手环系统语言
     * 0x00:  中文
     * 0x01:  英文
     * 0x02:  繁体
     * 0x03:  日文
     * 0x04:  韩文
     * 0x05:  泰文
     * 0x06:  法文
     * 0x07:  德文
     * 0x08：俄文
     * 0x09:  阿拉伯文
     * 0x0A: 意大利文
     * 0x0B: 拉丁文
     * 0x0C: 希腊文
     * 0x0D: 葡萄牙文
     * 0x0E:  土耳其文
     * 0x0F:  荷兰文
     * 0x10:  西里尔文
     * 0x11:  希伯来文
     * 0x12:  西班牙语
     * @param languageType
     */
    public void syncSystemLanguage(int languageType,MuiltByteDataListener muiltByteDataListener){
        //标志位
        String bitStr = "10000010";
        byte signByte = WatchUtils.bitToByte(bitStr);
        byte crcByte = (byte) ((byte) (signByte+0x16+0x01+languageType) & 0xff);
        byte[] languageByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x16,0x01, (byte) languageType};
        setDeviceByteData(languageByte,muiltByteDataListener);
    }


    /**
     * 设置用户数据
     * @param sex   性别 0男 ；1女
     * @param age  年龄
     *  @param stepLong 步长 /cm
     * @param height 身高 / m
     * @param weight 体重 / kg
     */
    public void syncUserInfoToDevice(int sex,int age,int stepLong,int height,int weight,MuiltByteDataListener muiltByteDataListener){
        byte[] weightByte = WatchUtils.chaiFenDataIntTo2Byte(weight);
        byte crcByte = (byte) ((byte) (0x86+0x06+ sex+ age+ stepLong+ height+weight) & 0xff);
        byte[] userByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x00,0x06, (byte) sex, (byte) age, (byte) stepLong, (byte) height,weightByte[0],weightByte[1]};
        setDeviceByteData(userByte,muiltByteDataListener);
    }

    //设置运动目标
    public void syncSportGoal(int stepGoal,MuiltByteDataListener muiltByteDataListener){
        byte[] bt = WatchUtils.intToByteArray(stepGoal);
        byte crcByte = (byte) ((byte) (0x86+0x01+0x04+bt[0]+bt[1]+bt[2]+bt[3]) & 0xff);
        byte[] stepByte = new byte[]{(byte) 0x9d,crcByte , (byte) 0x86,0x01,0x04,bt[0],bt[1],bt[2],bt[3]};
        setDeviceByteData(stepByte,muiltByteDataListener);
    }



    //设置久坐提醒
    /**
     *
     * @param isOpen    是否打开 0 关闭；1打开
     * @param startHour 开始小时
     * @param startMine 开始分钟
     * @param endHour   结束小时
     * @param endMine   结束分钟
     * @param longTime  时长
     */
    public void setLongSitRemind(boolean isOpen,int startHour,int startMine,int endHour,int endMine,int longTime,MuiltByteDataListener muiltByteDataListener){
        byte[] longTimeByte = WatchUtils.chaiFenDataIntTo2Byte(longTime);
        byte crcByte = (byte) ((byte) (0x86+0x04+0x08+(isOpen ? 0x01 : 0x00)+ startHour+ startMine+ endHour+endMine+0x7f+longTimeByte[0]+longTimeByte[1]) & 0xff);

        byte[] longSitByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x04,0x08,
                (byte) (isOpen ? 0x00 : 0x01), (byte) startHour, (byte) startMine, (byte) endHour, (byte) endMine,0x7f,longTimeByte[0],longTimeByte[1]};
        setDeviceByteData(longSitByte,muiltByteDataListener);
    }


    //设置勿扰模式
    /**
     *
     * @param isOpen            是否打开 0关闭；1打开
     * @param startHourTime     开始小时
     * @param startMineTime     开始分钟
     * @param endHourTime       结束小时
     * @param endMineTime       结束分钟
     */
    public void setNoDisturb(boolean isOpen,int startHourTime,int startMineTime,int endHourTime,int endMineTime,MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) (0x86+0x05+0x08+ (isOpen ? 0x01: 0x00)+ startHourTime+ startMineTime+ endHourTime+ endMineTime) & 0xff);
        byte[] noDisturb = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x05,0x08, (byte) (isOpen ? 0x01: 0x00), (byte) startHourTime, (byte) startMineTime, (byte) endHourTime, (byte) endMineTime};
        setDeviceByteData(noDisturb,muiltByteDataListener);
    }


    //设置手势唤醒 转腕亮屏和抬手亮屏
    /**
     *
     * @param turnWrist 是否打开转腕亮屏 0关闭；1开启
     * @param liftHand  是否打开抬手亮屏  0关闭；1开启
     */
    public void setTurnWrist(boolean turnWrist,boolean liftHand,MuiltByteDataListener muiltByteDataListener){
        //标志位
        String bitStr = "11000110";
        byte signByte = WatchUtils.bitToByte(bitStr);

        String resultByteStr = "000000"+(turnWrist ? 1 : 0)+(liftHand?1:0);
        byte tmpByte = WatchUtils.bitToByte(resultByteStr);
        byte crcByte = (byte) ((byte) (signByte+0x08+0x01+tmpByte) & 0xff);
        byte[] handByte = new byte[]{(byte) 0x9d,crcByte, signByte,0x08,0x01,tmpByte};
        setDeviceByteData(handByte,muiltByteDataListener);
    }

    //系统设置
    /**
     *
     * @param type 0x00: 恢复出厂设置
     * 0x01: 关机
     * 0x02: 重启
     */
    public void deviceSet(int type,MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) (0x86+0x09 +0x01+ type) & 0xff);
        byte[] deviceByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x09,0x01, (byte) type};
        setDeviceByteData(deviceByte,muiltByteDataListener);
    }


    //设置心率报警
    /**
     *
     * @param isOpen    是否打开 0关闭；1打开
     * @param heartValue 心率值
     */
    public void setHeartAlert(boolean isOpen,int heartValue,MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) ( 0x86+0x0a+0x02+ (isOpen ? 0x01 : 0x00)+heartValue) & 0xff);
        byte[] heartAlert = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x0a,0x02, (byte) (isOpen ? 0x01 : 0x00), (byte) heartValue};
        setDeviceByteData(heartAlert,muiltByteDataListener);
    }

    /**
     * 全天心率检测
     * @param isOpen    是否打开 0关闭；1打开
     * @param longTime  间隔时长
     */
    public void setAllDayDetectHeart(boolean isOpen,int longTime,MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) (0x86+0x0b+0x02+(isOpen ? 0x01 : 0x00)+longTime) & 0xff);
        byte[] allHeart = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x0b,0x02, (byte) (isOpen ? 0x01 : 0x00), (byte) longTime};
        setDeviceByteData(allHeart,muiltByteDataListener);
    }




    //设置时间显示界面
    public void setScreenTimeShow(MuiltByteDataListener muiltByteDataListener){
        byte crcByte = 0;
        byte[] screenByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x0d,0x02, (byte) 0xff,0x00};
        setDeviceByteData(screenByte,muiltByteDataListener);

    }


    /**
     * 查找手表 手机
     * @param code 0x00 查找手表；0x01查找手机
     */
    public void setFindPhone(int code,MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) (0x86+ 0x80+0x01) & 0xff;
        byte[] findByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86, (byte) 0x80,0x01,0x00};
        setDeviceByteData(findByte,muiltByteDataListener);
    }


    //设置小时制模式 0x00-24小时制;0x01-12小时制
    public void setTimeType(int type,MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) (0x86+0x15+0x01+type) & 0xff);
        byte[] timeType = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x15,0x01, (byte) type};
        setDeviceByteData(timeType,muiltByteDataListener);
    }


    /**
     * 设置公英制
     * @param type 0x00:公制；0x01:英制
     */
    public void setMetric(int type,MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) (0x86+0x13+0x01+type) & 0xff);
        byte[] metricByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x13,0x01, (byte) type};
        setDeviceByteData(metricByte,muiltByteDataListener);
    }




    //请求同步信息
    public void syncData(MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) (0x86+0x29+0x01+0xff)& 0xff);

        byte[] syncByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x29,0x01, (byte) 0xff};

        setDeviceByteData(syncByte,muiltByteDataListener);
    }


    //设置实时数据同步

    /**
     * byte1 实时同步类型（1Byte）
     * 0x00: 关闭实时同步
     * 0x01: 实时同步运动数据
     * 0x02: 实时同步睡眠数据
     * 0x03: 实时同步心率数据
     * 0x04: 实时同步血压数据
     * 0x05: 实时同步血氧数据
     * 0xFF: 实时同步所有数据
     *
     * 设置实时同步类型（1Byte）
     * Bit0: 实时同步运动数据
     * Bit1: 实时同步睡眠数据
     * Bit2: 实时同步心率数据
     * Bit3: 实时同步血压数据
     * Bit4: 实时同步血氧数据
     * 0: 关闭   1: 开启
     */
    public void realTimeData(MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) (0x86+0x20+0x02+ 0xff+ 1f) & 0xff);

        byte[] realTimeByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x20,0x02, (byte) 0xff, (byte) 1f};

        setDeviceByteData(realTimeByte,muiltByteDataListener);
    }


    //开始同步
    public void startSyncData(MuiltByteDataListener muiltByteDataListener){
        byte crcByte = (byte) ((byte) (0x86+0x21+0x02+ 1f) & 0xff);
        byte[] startSyncByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x20,0x02, (byte) 0x00, (byte) 1f};
        setDeviceByteData(startSyncByte,muiltByteDataListener);
    }



    //同步运动数据
    public void getSportData(){
        byte crcByte = 0;

        byte[] sportByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x22,0x21,};



    }




    //推送消息提醒信息
    public void pushMsgNotify(int type,String msgContent,MuiltByteDataListener muiltByteDataListener){
        int msgLength = msgContent.length();
        byte[] msgByte = WatchUtils.chaiFenDataIntTo2Byte(msgLength);

        byte[] msgContentByte = msgContent.getBytes();


        byte crcByte = (byte) ((byte) (0x86 + 0x41 + 0x03 + msgContentByte.length + type + msgByte[0] + msgByte[1] ) & 0xff);

        byte[] notifyByte = new byte[]{(byte) 0x9d,crcByte, (byte) 0x86,0x41, (byte) (0x03+msgContentByte.length), (byte) type,msgByte[0],msgByte[1], (byte) msgContentByte.length};


        setDeviceByteData(notifyByte,muiltByteDataListener);
    }




    public void setDeviceByteData(byte[] data,MuiltByteDataListener muiltByteDataListener){
        if(MyCommandManager.DEVICENAME == null)
            return;
        int length = data.length;
        int copy_size = 0;
        while (length > 0) {
            if (length < 20) {
                byte[] val = new byte[length];
                for (int i = 0; i < length; i++) {
                    val[i] = data[i + copy_size];
                }
                writeByteData(val,muiltByteDataListener);
            } else {
                byte[] val = new byte[20];
                for (int i = 0; i < 20; i++) {
                    val[i] = data[i + copy_size];
                }
                writeByteData(val,muiltByteDataListener);
            }
            copy_size += 20;
            length -= 20;
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeByteData(final byte[] bt, final MuiltByteDataListener muiltByteDataListener){
        MyApp.getInstance().getW37BleOperateManager().writeB11BleDataToDevice(bt, new WriteBackDataListener() {
            @Override
            public void backWriteData(byte[] data) {
                muiltByteDataListener.sendBackByteData(bt,data);
            }
        });
    }

}
