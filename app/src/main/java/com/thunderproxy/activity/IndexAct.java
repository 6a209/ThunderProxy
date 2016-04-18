package com.thunderproxy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.thunderproxy.R;
import com.thunderproxy.proxy.ProxyServer;
import com.thunderproxy.proxy.Request;
import com.thunderproxy.proxy.Response;
import com.thunderproxy.rx.ProxyRxHelper;

import rx.functions.Action1;

public class IndexAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        TextView tv = (TextView)findViewById(R.id.hello);
        ProxyServer.instance().start();

        ProxyRxHelper.instance().registerRequestObservable().subscribe(new Action1<Request>() {
            @Override
            public void call(Request request) {
                Log.d("test", "rx request");
            }
        });

        ProxyRxHelper.instance().registerResponseObservable().subscribe(new Action1<Response>() {
            @Override
            public void call(Response response) {
                Log.d("test", "rx response");
            }
        });
    }
}
