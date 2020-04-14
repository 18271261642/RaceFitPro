package com.example.bozhilun.android.friend.bean;

public class PhoneitemBean {
    /**
     * phone : 12342345345
     * contacts : contacts
     */

    private String phone;
    private String contacts;

    public PhoneitemBean(String phone, String contacts) {
        this.phone = phone;
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
}
