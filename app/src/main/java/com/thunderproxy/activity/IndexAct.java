package com.thunderproxy.activity;

import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.thunderproxy.R;
import com.thunderproxy.adapter.IndexAdapter;
import com.thunderproxy.adapter.IndexItemData;
import com.thunderproxy.proxy.ProxyServer;
import com.thunderproxy.proxy.Request;
import com.thunderproxy.proxy.Response;
import com.thunderproxy.rx.ProxyRxHelper;
import com.thunderproxy.vpn.ThunderVpnService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RunnableFuture;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class IndexAct extends BaseAct {

    Map<String, IndexItemData> mMapItems = new HashMap<>();
    IndexAdapter mIndexAdapter;
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = VpnService.prepare(IndexAct.this);
                if (null != intent) {
                    startActivityForResult(intent, 1);
                } else {
                    onActivityResult(0, RESULT_OK, null);
                }
            }
        }, 2000);


        ProxyServer.instance().start();
        setContentView(R.layout.activity_index);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mIndexAdapter = new IndexAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mIndexAdapter);

        ProxyRxHelper.instance().registerRequestObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Request>() {
            @Override
            public void call(Request request) {
                IndexItemData itemData = new IndexItemData();
                itemData.setRequest(request);
                mMapItems.put(String.valueOf(request.hashCode()), itemData);
                mIndexAdapter.addData(itemData);
                Log.d("test", "rx request");
            }
        });

        ProxyRxHelper.instance().registerResponseObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Pair<Request, Response>>() {
            @Override
            public void call(Pair<Request, Response> requestResponsePair) {
                String key = String.valueOf(requestResponsePair.first.hashCode());
                IndexItemData itemData = mMapItems.get(key);
                if(null == itemData){
                    return;
                }
                itemData.setResponse(requestResponsePair.second);
                mIndexAdapter.notifyDataSetChanged();
            }
        });


    }

    @Override
    public void onActivityResult(int request, int result, Intent data){
        if (RESULT_OK == result) {
            Intent intent = new Intent(this, ThunderVpnService.class);
            startService(intent);
        }
    }
}
