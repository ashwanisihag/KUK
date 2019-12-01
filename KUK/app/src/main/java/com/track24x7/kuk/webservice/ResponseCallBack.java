package com.track24x7.kuk.webservice;


import com.track24x7.kuk.pojo.ResponsePOJO;

/**
 * Programmed by Sihag.
 */

public interface ResponseCallBack<T> {
    public void onGetMsg(ResponsePOJO<T> responsePOJO);
}
