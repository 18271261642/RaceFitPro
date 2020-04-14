package com.example.bozhilun.android.b15p.b15pdb;

public class B15PSleepBean {
        private String type;
        private String sleep;
        private int count;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSleep() {
        return sleep;
    }

    public void setSleep(String sleep) {
        this.sleep = sleep;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public B15PSleepBean(String type, String sleep, int count) {
            this.type = type;
            this.sleep = sleep;
            this.count = count;
        }


        @Override
        public String toString() {
            return "B15PSleepBean{" +
                    "type='" + type + '\'' +
                    ", count=" + count +
                    '}';
        }
    }