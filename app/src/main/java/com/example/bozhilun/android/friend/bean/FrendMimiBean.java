package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class FrendMimiBean {
    /**
     * eisList : [{"exhibition":1,"friendId":"8c4c511a45374bb595e6fdf30bb878b7","addTime":"2018-12-10 08:51:26","updateTime":null,"id":611,"setType":3,"userId":"9875e695a02e44c2b4370e5a9b920263"},{"exhibition":1,"friendId":"8c4c511a45374bb595e6fdf30bb878b7","addTime":"2018-12-10 08:51:26","updateTime":null,"id":612,"setType":1,"userId":"9875e695a02e44c2b4370e5a9b920263"},{"exhibition":1,"friendId":"8c4c511a45374bb595e6fdf30bb878b7","addTime":"2018-12-10 08:51:26","updateTime":null,"id":613,"setType":4,"userId":"9875e695a02e44c2b4370e5a9b920263"},{"exhibition":0,"friendId":"8c4c511a45374bb595e6fdf30bb878b7","addTime":"2018-12-10 08:51:26","updateTime":null,"id":614,"setType":5,"userId":"9875e695a02e44c2b4370e5a9b920263"},{"exhibition":1,"friendId":"8c4c511a45374bb595e6fdf30bb878b7","addTime":"2018-12-10 08:51:26","updateTime":null,"id":615,"setType":2,"userId":"9875e695a02e44c2b4370e5a9b920263"}]
     * resultCode : 001
     */

    private String resultCode;
    private List<EisListBean> eisList;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<EisListBean> getEisList() {
        return eisList;
    }

    public void setEisList(List<EisListBean> eisList) {
        this.eisList = eisList;
    }

    public static class EisListBean {
        /**
         * exhibition : 1
         * friendId : 8c4c511a45374bb595e6fdf30bb878b7
         * addTime : 2018-12-10 08:51:26
         * updateTime : null
         * id : 611
         * setType : 3
         * userId : 9875e695a02e44c2b4370e5a9b920263
         */

        private int exhibition;
        private String friendId;
        private String addTime;
        private Object updateTime;
        private int id;
        private int setType;
        private String userId;

        public int getExhibition() {
            return exhibition;
        }

        public void setExhibition(int exhibition) {
            this.exhibition = exhibition;
        }

        public String getFriendId() {
            return friendId;
        }

        public void setFriendId(String friendId) {
            this.friendId = friendId;
        }

        public String getAddTime() {
            return addTime;
        }

        public void setAddTime(String addTime) {
            this.addTime = addTime;
        }

        public Object getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Object updateTime) {
            this.updateTime = updateTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSetType() {
            return setType;
        }

        public void setSetType(int setType) {
            this.setType = setType;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
