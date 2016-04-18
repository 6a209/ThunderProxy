package com.thunderproxy.rx;

import android.util.Log;

import com.thunderproxy.proxy.ProxyServer;
import com.thunderproxy.proxy.Request;
import com.thunderproxy.proxy.Response;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by 6a209 on 16/3/20.
 */
public class ProxyRxHelper {
    private static final String TAG = "ProxyRxHelper";


    private static ProxyRxHelper sProxyRxHelper = new ProxyRxHelper();
    private Observable<Request> mRequestObservable;
    private Observable<Response> mResponseObservable;

    ProxyServer.OnRequestListener mOnRequestListener;
    ProxyServer.OnResponseListener mOnResponseListener;





    private ProxyRxHelper(){
//        mRequestObservable = Observable.create();
//        mResponseObservable = new Observable<Response>();
    }


    public static ProxyRxHelper instance(){
        if(null == sProxyRxHelper){
            sProxyRxHelper = new ProxyRxHelper();
        }
        return sProxyRxHelper;
    }

    public Observable registerRequestObservable(){

        return Observable.create(new Observable.OnSubscribe<Request>() {
            @Override
            public void call(final Subscriber<? super Request> subscriber) {
                mOnRequestListener = new ProxyServer.OnRequestListener() {

                    @Override
                    public void onRequestFinish(Request request) {
                        Log.d(TAG, "onRequestFinish");
                        if(!subscriber.isUnsubscribed()){
                            subscriber.onNext(request);
                        }
                    }
                };
                ProxyServer.instance().setOnRequestListener(mOnRequestListener);
            }
        });
    }

    public Observable registerResponseObservable(){
        return Observable.create(new Observable.OnSubscribe<Response>(){
            @Override
            public void call(final Subscriber<? super Response> subscriber) {

                mOnResponseListener = new ProxyServer.OnResponseListener() {
                    @Override
                    public void onResponseFinish(Response response) {
                        Log.d(TAG, "onResponseFinish");
                        if(!subscriber.isUnsubscribed()){
                            subscriber.onNext(response);
                        }
                    }
                };
                ProxyServer.instance().setOnResponseListener(mOnResponseListener);
            }
        });
    }
}
