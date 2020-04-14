package com.example.bozhilun.android.commdbserver;

public interface DowlnListenter {
    /**
     * 本地没数据时，同步服务器，返回数据接口
     *
     * @param type
     */
    void SyscnSucce();
}
