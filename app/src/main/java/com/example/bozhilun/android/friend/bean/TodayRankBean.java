package com.example.bozhilun.android.friend.bean;

import java.util.List;

public class TodayRankBean {


    private String resultCode;
    private List<RankListBean> rankList;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public List<RankListBean> getRankList() {
        return rankList;
    }

    public void setRankList(List<RankListBean> rankList) {
        this.rankList = rankList;
    }

    public static class RankListBean {
        /**
         * img : http://thirdwx.qlogo.cn/mmopen/vi_32/0icUGw7uyx0ahNsLH6heWTOGVzYDtuHM19AaSichibDTOevpjIrydXWib5o1v6cbW8Yicibiae4qOicTSgVBKgl7QCoPsA/132
         * stepNumber : 21420
         * userName : 天高任鳥飛 ，龙爷！
         */

        private String img;
        private int stepNumber;
        private String userName;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getStepNumber() {
            return stepNumber;
        }

        public void setStepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
