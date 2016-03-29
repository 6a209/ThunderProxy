package com.thunderproxy.rx;

import com.thunderproxy.proxy.Request;
import com.thunderproxy.proxy.Response;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by 6a209 on 16/3/20.
 */
public class ProxyRxHelper {

    Observable<Request> mRequestObservable;
    Observable<Response> mResponseObservable;

//    Observable.OnSubscribe mRequestOnSubscribe  = new Observable.OnSubscribe<Request> (){
//        @Override
//        public void call(Subscriber<? super Request> subscriber) {
//            subscriber.onNext();
//        }
//    };

    public Observable registerRequestObservable(){
        return mRequestObservable;
    }

    public Observable registerResponseObservable(){
        return mResponseObservable;
    }




}
