package com.thunderproxy.adapter;

import com.thunderproxy.proxy.Request;
import com.thunderproxy.proxy.Response;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 6a209 on 16/5/3.
 */
public class IndexItemData {

    Request mRequest;
    Response mResponse;

    long mCreateTimestamp;
    String mCreateTime;
    String mElapsedTime;

    public IndexItemData(){
        mCreateTimestamp = System.currentTimeMillis();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        mCreateTime = sdf.format(date);
    }

    public String getCreateTime(){
        return mCreateTime;
    }

    public Request getRequest(){
        return mRequest;
    }

    public void setRequest(Request request){
        mRequest = request;
    }

    public Response getResponse(){
        return mResponse;
    }

    public void setResponse(Response response){
        mElapsedTime = String.valueOf(System.currentTimeMillis() - mCreateTimestamp);
        mResponse = response;
    }

    public String getElapsedTime(){
        return mElapsedTime;
    }

}
