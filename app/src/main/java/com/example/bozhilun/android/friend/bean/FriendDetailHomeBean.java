package com.example.bozhilun.android.friend.bean;

import java.util.List;

/**
 * 查看好友详细数据，首页面板
 * Created by Admin
 * Date 2019/7/12
 */
public class FriendDetailHomeBean {


    private int code;
    private Object msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * sleepDay : {"rtc":"2018-11-14","deepSleep":99,"shallowSleep":342,"wakeTime":"2018-11-14 07:35","addTime":"2018-11-14 08:15","soberLen":0,"sleepTime":"2018-11-14 23:00","sleepLen":441,"updateTime":"2018-11-14 21:09","id":91592,"deviceCode":"W30_E3D9","userId":"8c4c511a45374bb595e6fdf30bb878b7"}
         * heartRateDay : {"rtc":"2018-12-07","addTime":"2018-12-07 08:52","minHeartRate":"55","updateTime":"2018-12-07 17:47","id":"164701","deviceCode":"E9:84:FA:77:54:60","userId":"5c2b58f0681547a0801d4d4ac8465f82","maxHeartRate":"103","avgHeartRate":"64"}
         * friendInfo : {"phone":"15916947377","nickname":"超级管理员","sex":"F","birthday":"1994-10-10","height":"172","weight":"60","image":"http://47.90.83.197/image/2019/06/22/1561167386104.jpg","userid":"5c2b58f0681547a0801d4d4ac8465f82","equipment":"B30","mac":"D1:70:BB:0D:0D:09","code":null,"count":null,"stepNumber":null,"friendStatus":null,"isThumbs":null,"lastThumbsDay":null,"day":null,"see":null,"hasBloodPressure":null,"todayThumbs":null,"rankNo":null,"exInfoSetList":[{"id":1601,"userid":"e028098595dc49909f908cb8f0b4b2a1","friendid":"5c2b58f0681547a0801d4d4ac8465f82","settype":3,"exhibition":1,"addtime":"2019-07-12 10:30:52","updatetime":null},{"id":1602,"userid":"e028098595dc49909f908cb8f0b4b2a1","friendid":"5c2b58f0681547a0801d4d4ac8465f82","settype":1,"exhibition":1,"addtime":"2019-07-12 10:30:52","updatetime":null}]}
         * sportDay : {"id":1152529,"userid":"5c2b58f0681547a0801d4d4ac8465f82","stepnumber":3722,"date":"2019-07-12","count":1,"reach":0,"distance":2.98,"calorie":null,"addtime":"2019-07-12 08:54:08","devicecode":"D9:CB:97:30:58:69","updatetime":null,"user":null}
         * bloodPressureDay : {"rtc":"2019-01-07","maxDiastolic":"107","avgSystolic":"79","addTime":"2019-01-07 08:40","updateTime":"2019-01-07 11:25","id":"27115","deviceCode":"E7:A7:0F:11:BE:B5","userId":"0d56716e5629475882d4f4bfc7c51420","avgDiastolic":"118","minSystolic":"83"}
         */

        private SleepDayBean sleepDay;
        private HeartRateDayBean heartRateDay;
        private FriendInfoBean friendInfo;
        private SportDayBean sportDay;
        private BloodPressureDayBean bloodPressureDay;
        private BloodOxygenDay bloodOxygenDay;
        private HrvDay hrvDay;

        //女性生理期bean
        private MenstrualCycle menstrualCycle;

        public MenstrualCycle getMenstrualCycle() {
            return menstrualCycle;
        }

        public void setMenstrualCycle(MenstrualCycle menstrualCycle) {
            this.menstrualCycle = menstrualCycle;
        }

        public HrvDay getHrvDay() {
            return hrvDay;
        }

        public void setHrvDay(HrvDay hrvDay) {
            this.hrvDay = hrvDay;
        }

        public SleepDayBean getSleepDay() {
            return sleepDay;
        }

        public void setSleepDay(SleepDayBean sleepDay) {
            this.sleepDay = sleepDay;
        }

        public HeartRateDayBean getHeartRateDay() {
            return heartRateDay;
        }

        public void setHeartRateDay(HeartRateDayBean heartRateDay) {
            this.heartRateDay = heartRateDay;
        }

        public FriendInfoBean getFriendInfo() {
            return friendInfo;
        }

        public void setFriendInfo(FriendInfoBean friendInfo) {
            this.friendInfo = friendInfo;
        }

        public SportDayBean getSportDay() {
            return sportDay;
        }

        public void setSportDay(SportDayBean sportDay) {
            this.sportDay = sportDay;
        }

        public BloodPressureDayBean getBloodPressureDay() {
            return bloodPressureDay;
        }

        public void setBloodPressureDay(BloodPressureDayBean bloodPressureDay) {
            this.bloodPressureDay = bloodPressureDay;
        }

        public BloodOxygenDay getBloodOxygenDay() {
            return bloodOxygenDay;
        }

        public void setBloodOxygenDay(BloodOxygenDay bloodOxygenDay) {
            this.bloodOxygenDay = bloodOxygenDay;
        }

        public static class SleepDayBean {

            /**
             * id : 409352
             * userid : 5c2b58f0681547a0801d4d4ac8465f82
             * devicecode : E9:30:AE:E5:AF:A1
             * rtc : 2019-07-17
             * sleeplen : 523
             * deepsleep : 259
             * shallowsleep : 264
             * soberlen : 0
             * sleeptime : 23:17
             * waketime : 08:00
             * addtime : 2019-07-18 09:41:37
             * updatetime : 2019-07-18 09:41:37
             * wakecount : 0
             */

            private int id;
            private String userid;
            private String devicecode;
            private String rtc;
            private int sleeplen;
            private int deepsleep;
            private int shallowsleep;
            private int soberlen;
            private String sleeptime;
            private String waketime;
            private String addtime;
            private String updatetime;
            private int wakecount;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getDevicecode() {
                return devicecode;
            }

            public void setDevicecode(String devicecode) {
                this.devicecode = devicecode;
            }

            public String getRtc() {
                return rtc;
            }

            public void setRtc(String rtc) {
                this.rtc = rtc;
            }

            public int getSleeplen() {
                return sleeplen;
            }

            public void setSleeplen(int sleeplen) {
                this.sleeplen = sleeplen;
            }

            public int getDeepsleep() {
                return deepsleep;
            }

            public void setDeepsleep(int deepsleep) {
                this.deepsleep = deepsleep;
            }

            public int getShallowsleep() {
                return shallowsleep;
            }

            public void setShallowsleep(int shallowsleep) {
                this.shallowsleep = shallowsleep;
            }

            public int getSoberlen() {
                return soberlen;
            }

            public void setSoberlen(int soberlen) {
                this.soberlen = soberlen;
            }

            public String getSleeptime() {
                return sleeptime;
            }

            public void setSleeptime(String sleeptime) {
                this.sleeptime = sleeptime;
            }

            public String getWaketime() {
                return waketime;
            }

            public void setWaketime(String waketime) {
                this.waketime = waketime;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }

            public int getWakecount() {
                return wakecount;
            }

            public void setWakecount(int wakecount) {
                this.wakecount = wakecount;
            }

            @Override
            public String toString() {
                return "SleepDayBean{" +
                        "id=" + id +
                        ", userid='" + userid + '\'' +
                        ", devicecode='" + devicecode + '\'' +
                        ", rtc='" + rtc + '\'' +
                        ", sleeplen=" + sleeplen +
                        ", deepsleep=" + deepsleep +
                        ", shallowsleep=" + shallowsleep +
                        ", soberlen=" + soberlen +
                        ", sleeptime='" + sleeptime + '\'' +
                        ", waketime='" + waketime + '\'' +
                        ", addtime='" + addtime + '\'' +
                        ", updatetime='" + updatetime + '\'' +
                        ", wakecount=" + wakecount +
                        '}';
            }
        }

        public static class HeartRateDayBean {

            /**
             * id : 1068526
             * userid : 94f3b4bb5cb846ffb3aab0527f34bf6c
             * devicecode : E9:30:AE:E5:AF:A1
             * maxheartrate : 132
             * minheartrate : 0
             * avgheartrate : 70
             * rtc : 2019-07-19
             * addtime : 2019-07-19 08:34:56
             * updatetime : 2019-07-19 08:34:56
             */

            private int id;
            private String userid;
            private String devicecode;
            private int maxheartrate;
            private int minheartrate;
            private int avgheartrate;
            private String rtc;
            private String addtime;
            private String updatetime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getDevicecode() {
                return devicecode;
            }

            public void setDevicecode(String devicecode) {
                this.devicecode = devicecode;
            }

            public int getMaxheartrate() {
                return maxheartrate;
            }

            public void setMaxheartrate(int maxheartrate) {
                this.maxheartrate = maxheartrate;
            }

            public int getMinheartrate() {
                return minheartrate;
            }

            public void setMinheartrate(int minheartrate) {
                this.minheartrate = minheartrate;
            }

            public int getAvgheartrate() {
                return avgheartrate;
            }

            public void setAvgheartrate(int avgheartrate) {
                this.avgheartrate = avgheartrate;
            }

            public String getRtc() {
                return rtc;
            }

            public void setRtc(String rtc) {
                this.rtc = rtc;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }

            @Override
            public String toString() {
                return "HeartRateDayBean{" +
                        "id=" + id +
                        ", userid='" + userid + '\'' +
                        ", devicecode='" + devicecode + '\'' +
                        ", maxheartrate=" + maxheartrate +
                        ", minheartrate=" + minheartrate +
                        ", avgheartrate=" + avgheartrate +
                        ", rtc='" + rtc + '\'' +
                        ", addtime='" + addtime + '\'' +
                        ", updatetime='" + updatetime + '\'' +
                        '}';
            }
        }

        public static class FriendInfoBean {
            /**
             * phone : 15916947377
             * nickname : 超级管理员
             * sex : F
             * birthday : 1994-10-10
             * height : 172
             * weight : 60
             * image : http://47.90.83.197/image/2019/06/22/1561167386104.jpg
             * userid : 5c2b58f0681547a0801d4d4ac8465f82
             * equipment : B30
             * mac : D1:70:BB:0D:0D:09
             * code : null
             * count : null
             * stepNumber : null
             * friendStatus : null
             * isThumbs : null
             * lastThumbsDay : null
             * day : null
             * see : null
             * hasBloodPressure : null
             * todayThumbs : null
             * rankNo : null
             * exInfoSetList : [{"id":1601,"userid":"e028098595dc49909f908cb8f0b4b2a1","friendid":"5c2b58f0681547a0801d4d4ac8465f82","settype":3,"exhibition":1,"addtime":"2019-07-12 10:30:52","updatetime":null},{"id":1602,"userid":"e028098595dc49909f908cb8f0b4b2a1","friendid":"5c2b58f0681547a0801d4d4ac8465f82","settype":1,"exhibition":1,"addtime":"2019-07-12 10:30:52","updatetime":null}]
             */

            private String phone;
            private String nickname;
            private String sex;
            private String birthday;
            private String height;
            private String weight;
            private String image;
            private String userid;
            private String equipment;
            private String mac;
            private Object code;
            private Object count;
            private Object stepNumber;
            private Object friendStatus;
            private Object isThumbs;
            private Object lastThumbsDay;
            private Object day;
            private Object see;
            private Object hasBloodPressure;
            private Object todayThumbs;
            private Object rankNo;
            private List<ExInfoSetListBean> exInfoSetList;


            private int bindWx;
            private int level;
            private int addType;
            private String addRemark;
            private String deviceToken;
            private String deviceId;
            private String deviceType;
            private String language;




            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getSex() {
                return sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getBirthday() {
                return birthday;
            }

            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getWeight() {
                return weight;
            }

            public void setWeight(String weight) {
                this.weight = weight;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getEquipment() {
                return equipment;
            }

            public void setEquipment(String equipment) {
                this.equipment = equipment;
            }

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
            }

            public Object getCode() {
                return code;
            }

            public void setCode(Object code) {
                this.code = code;
            }

            public Object getCount() {
                return count;
            }

            public void setCount(Object count) {
                this.count = count;
            }

            public Object getStepNumber() {
                return stepNumber;
            }

            public void setStepNumber(Object stepNumber) {
                this.stepNumber = stepNumber;
            }

            public Object getFriendStatus() {
                return friendStatus;
            }

            public void setFriendStatus(Object friendStatus) {
                this.friendStatus = friendStatus;
            }

            public Object getIsThumbs() {
                return isThumbs;
            }

            public void setIsThumbs(Object isThumbs) {
                this.isThumbs = isThumbs;
            }

            public Object getLastThumbsDay() {
                return lastThumbsDay;
            }

            public void setLastThumbsDay(Object lastThumbsDay) {
                this.lastThumbsDay = lastThumbsDay;
            }

            public Object getDay() {
                return day;
            }

            public void setDay(Object day) {
                this.day = day;
            }

            public Object getSee() {
                return see;
            }

            public void setSee(Object see) {
                this.see = see;
            }

            public Object getHasBloodPressure() {
                return hasBloodPressure;
            }

            public void setHasBloodPressure(Object hasBloodPressure) {
                this.hasBloodPressure = hasBloodPressure;
            }

            public Object getTodayThumbs() {
                return todayThumbs;
            }

            public void setTodayThumbs(Object todayThumbs) {
                this.todayThumbs = todayThumbs;
            }

            public Object getRankNo() {
                return rankNo;
            }

            public void setRankNo(Object rankNo) {
                this.rankNo = rankNo;
            }

            public List<ExInfoSetListBean> getExInfoSetList() {
                return exInfoSetList;
            }

            public void setExInfoSetList(List<ExInfoSetListBean> exInfoSetList) {
                this.exInfoSetList = exInfoSetList;
            }

            public int getBindWx() {
                return bindWx;
            }

            public void setBindWx(int bindWx) {
                this.bindWx = bindWx;
            }

            public int getLevel() {
                return level;
            }

            public void setLevel(int level) {
                this.level = level;
            }

            public int getAddType() {
                return addType;
            }

            public void setAddType(int addType) {
                this.addType = addType;
            }

            public String getAddRemark() {
                return addRemark;
            }

            public void setAddRemark(String addRemark) {
                this.addRemark = addRemark;
            }

            public String getDeviceToken() {
                return deviceToken;
            }

            public void setDeviceToken(String deviceToken) {
                this.deviceToken = deviceToken;
            }

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public String getDeviceType() {
                return deviceType;
            }

            public void setDeviceType(String deviceType) {
                this.deviceType = deviceType;
            }

            public String getLanguage() {
                return language;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public static class ExInfoSetListBean {
                /**
                 * id : 1601
                 * userid : e028098595dc49909f908cb8f0b4b2a1
                 * friendid : 5c2b58f0681547a0801d4d4ac8465f82
                 * settype : 3
                 * exhibition : 1
                 * addtime : 2019-07-12 10:30:52
                 * updatetime : null
                 */

                private int id;
                private String userid;
                private String friendid;
                private int settype;
                private int exhibition;
                private String addtime;
                private Object updatetime;

                public int getId() {
                    return id;
                }

                public void setId(int id) {
                    this.id = id;
                }

                public String getUserid() {
                    return userid;
                }

                public void setUserid(String userid) {
                    this.userid = userid;
                }

                public String getFriendid() {
                    return friendid;
                }

                public void setFriendid(String friendid) {
                    this.friendid = friendid;
                }

                public int getSettype() {
                    return settype;
                }

                public void setSettype(int settype) {
                    this.settype = settype;
                }

                public int getExhibition() {
                    return exhibition;
                }

                public void setExhibition(int exhibition) {
                    this.exhibition = exhibition;
                }

                public String getAddtime() {
                    return addtime;
                }

                public void setAddtime(String addtime) {
                    this.addtime = addtime;
                }

                public Object getUpdatetime() {
                    return updatetime;
                }

                public void setUpdatetime(Object updatetime) {
                    this.updatetime = updatetime;
                }
            }
        }


        public static class SportDayBean {
            /**
             * id : 1152529
             * userid : 5c2b58f0681547a0801d4d4ac8465f82
             * stepnumber : 3722
             * date : 2019-07-12
             * count : 1
             * reach : 0
             * distance : 2.98
             * calorie : null
             * addtime : 2019-07-12 08:54:08
             * devicecode : D9:CB:97:30:58:69
             * updatetime : null
             * user : null
             */

            private int id;
            private String userid;
            private int stepnumber;
            private String date;
            private int count;
            private int reach;
            private double distance;
            private Object calorie;
            private String addtime;
            private String devicecode;
            private Object updatetime;
            private Object user;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public int getStepnumber() {
                return stepnumber;
            }

            public void setStepnumber(int stepnumber) {
                this.stepnumber = stepnumber;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            public int getReach() {
                return reach;
            }

            public void setReach(int reach) {
                this.reach = reach;
            }

            public double getDistance() {
                return distance;
            }

            public void setDistance(double distance) {
                this.distance = distance;
            }

            public Object getCalorie() {
                return calorie;
            }

            public void setCalorie(Object calorie) {
                this.calorie = calorie;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getDevicecode() {
                return devicecode;
            }

            public void setDevicecode(String devicecode) {
                this.devicecode = devicecode;
            }

            public Object getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(Object updatetime) {
                this.updatetime = updatetime;
            }

            public Object getUser() {
                return user;
            }

            public void setUser(Object user) {
                this.user = user;
            }

            @Override
            public String toString() {
                return "SportDayBean{" +
                        "id=" + id +
                        ", userid='" + userid + '\'' +
                        ", stepnumber=" + stepnumber +
                        ", date='" + date + '\'' +
                        ", count=" + count +
                        ", reach=" + reach +
                        ", distance=" + distance +
                        ", calorie=" + calorie +
                        ", addtime='" + addtime + '\'' +
                        ", devicecode='" + devicecode + '\'' +
                        ", updatetime=" + updatetime +
                        ", user=" + user +
                        '}';
            }
        }

        public static class BloodPressureDayBean {

            /**
             * id : 286591
             * userid : 5c2b58f0681547a0801d4d4ac8465f82
             * devicecode : E9:30:AE:E5:AF:A1
             * maxdiastolic : 80
             * minsystolic : 0
             * avgdiastolic : 120
             * avgsystolic : 80
             * rtc : 2019-07-18
             * addtime : 2019-07-18 09:41:37
             * updatetime : 2019-07-18 09:41:37
             */

            private int id;
            private String userid;
            private String devicecode;
            private int maxdiastolic;
            private int minsystolic;
            private int avgdiastolic;
            private int avgsystolic;
            private String rtc;
            private String addtime;
            private String updatetime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getDevicecode() {
                return devicecode;
            }

            public void setDevicecode(String devicecode) {
                this.devicecode = devicecode;
            }

            public int getMaxdiastolic() {
                return maxdiastolic;
            }

            public void setMaxdiastolic(int maxdiastolic) {
                this.maxdiastolic = maxdiastolic;
            }

            public int getMinsystolic() {
                return minsystolic;
            }

            public void setMinsystolic(int minsystolic) {
                this.minsystolic = minsystolic;
            }

            public int getAvgdiastolic() {
                return avgdiastolic;
            }

            public void setAvgdiastolic(int avgdiastolic) {
                this.avgdiastolic = avgdiastolic;
            }

            public int getAvgsystolic() {
                return avgsystolic;
            }

            public void setAvgsystolic(int avgsystolic) {
                this.avgsystolic = avgsystolic;
            }

            public String getRtc() {
                return rtc;
            }

            public void setRtc(String rtc) {
                this.rtc = rtc;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }

            @Override
            public String toString() {
                return "BloodPressureDayBean{" +
                        "id=" + id +
                        ", userid='" + userid + '\'' +
                        ", devicecode='" + devicecode + '\'' +
                        ", maxdiastolic=" + maxdiastolic +
                        ", minsystolic=" + minsystolic +
                        ", avgdiastolic=" + avgdiastolic +
                        ", avgsystolic=" + avgsystolic +
                        ", rtc='" + rtc + '\'' +
                        ", addtime='" + addtime + '\'' +
                        ", updatetime='" + updatetime + '\'' +
                        '}';
            }
        }

        //血氧
        public static class BloodOxygenDay {

            /**
             * id : 517
             * userid : 5c2b58f0681547a0801d4d4ac8465f82
             * devicecode : E9:37:C4:E7:28:D8
             * avgbloodoxygen : 98
             * addtime : 2019-08-26 09:25:18
             * updatetime : 1
             * rtc : 2019-08-26
             * minbloodoxygen : 96
             * maxbloodoxygen : 99
             */

            private int id;
            private String userid;
            private String devicecode;
            private int avgbloodoxygen;
            private String addtime;
            private String updatetime;
            private String rtc;
            private int minbloodoxygen;
            private int maxbloodoxygen;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getDevicecode() {
                return devicecode;
            }

            public void setDevicecode(String devicecode) {
                this.devicecode = devicecode;
            }

            public int getAvgbloodoxygen() {
                return avgbloodoxygen;
            }

            public void setAvgbloodoxygen(int avgbloodoxygen) {
                this.avgbloodoxygen = avgbloodoxygen;
            }

            public String getAddtime() {
                return addtime;
            }

            public void setAddtime(String addtime) {
                this.addtime = addtime;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }

            public String getRtc() {
                return rtc;
            }

            public void setRtc(String rtc) {
                this.rtc = rtc;
            }

            public int getMinbloodoxygen() {
                return minbloodoxygen;
            }

            public void setMinbloodoxygen(int minbloodoxygen) {
                this.minbloodoxygen = minbloodoxygen;
            }

            public int getMaxbloodoxygen() {
                return maxbloodoxygen;
            }

            public void setMaxbloodoxygen(int maxbloodoxygen) {
                this.maxbloodoxygen = maxbloodoxygen;
            }
        }

        public static class HrvDay {


            /**
             * userId : 5c2b58f0681547a0801d4d4ac8465f82
             * deviceCode : E9:37:C4:E7:28:D8
             * day : 2019-08-26
             * heartSocre : 75
             */

            private String userId;
            private String deviceCode;
            private String day;
            private String heartSocre;

            public String getUserId() {
                return userId;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getDeviceCode() {
                return deviceCode;
            }

            public void setDeviceCode(String deviceCode) {
                this.deviceCode = deviceCode;
            }

            public String getDay() {
                return day;
            }

            public void setDay(String day) {
                this.day = day;
            }

            public String getHeartSocre() {
                return heartSocre;
            }

            public void setHeartSocre(String heartSocre) {
                this.heartSocre = heartSocre;
            }
        }

    }
}