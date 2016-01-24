package com.thunderproxy.proxy;

import android.net.Uri;

import java.nio.channels.SocketChannel;
import java.util.Map;

/**
 * Created by 6a209 on 16/1/24.
 *
 * a abstract http section
 */
public class HTTPSection {

    private String mUrl;
    private METHOD mMethod;
    private Map<String, String> mHeader;
    private Map<String, String> mParams;
    private String mQueryString;
    private String mRemoteIp;
    private String mRemoteHostName;
    private SocketChannel mSocketChannel;
    private String mHttpVersion;
    private String mResponse;

    public HTTPSection(){
    }


    public enum METHOD {
        GET,
        POST,
        PUT,
        DELETE,
        HEAD,
        OPTIONS,
        TRACE,
        CONNECT,
        PATCH
    }
}
