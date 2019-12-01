package com.track24x7.kuk.webservice;


import com.track24x7.kuk.pojo.ResponseListPOJO;

/**
 * Programmed by Sihag.
 */

public interface ResponseListCallback<T> {
    public void onGetMsg(ResponseListPOJO<T> responseListPOJO);
}
