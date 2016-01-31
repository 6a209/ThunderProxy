package com.thunderproxy.proxy;

/**
 * Created by 6a209 on 16/1/28.
 */
public interface ProxyListener {
    void onRequest();
    void onResponse();
}
