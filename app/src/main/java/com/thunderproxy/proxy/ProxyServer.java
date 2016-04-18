package com.thunderproxy.proxy;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 6a209 on 16/1/23.
 *
 *  a proxy server
 */
public class ProxyServer {

    private static final String TAG = "ProxyServer";
    private static final int DEFAULT_PORT = 8888;
    private static final int BUFFER_SIZE = 8192;

    private static ProxyServer sProxyServer;
    private int mPort;
    private Selector mSelector;
    private ServerSocketChannel mSereverSocketChannel;
    private ByteBuffer mSocketBuffer;


    public interface OnRequestListener{
        void onRequestFinish(Request request);
    }

    public interface OnResponseListener{
        void onResponseFinish(Response response);
    }

    OnRequestListener mOnRequestListener;
    OnResponseListener mOnResponseListener;

    public static ProxyServer instance(){
        if(null == sProxyServer){
            synchronized (ProxyServer.class){
                if(null == sProxyServer){
                    sProxyServer = new ProxyServer();
                }
            }
        }
        return sProxyServer;
    }

    private ProxyServer(){
        mPort = DEFAULT_PORT;
        mSocketBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    public void setOnRequestListener(OnRequestListener listener){
        mOnRequestListener = listener;
    }

    public void setOnResponseListener(OnResponseListener listener){
        mOnResponseListener = listener;
    }

    public OnRequestListener getOnRequestListener(){
        return mOnRequestListener;
    }

    public OnResponseListener getOnResponseListener(){
        return mOnResponseListener;
    }


    public int getPort(){
        return mPort;
    }

    public void setPort(int port){
        mPort = port;
    }

    public boolean start(){
        try {
            mSelector = Selector.open();
            mSereverSocketChannel = ServerSocketChannel.open();
            mSereverSocketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try{
            mSereverSocketChannel.socket().bind(new InetSocketAddress(mPort));
        }catch (IOException e){
//            Log.d(TAG, "proxy server port err");
            return false;
        }
        try {
            mSereverSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                while(true){
                    if(null == mSereverSocketChannel || null == mSelector){
                        break;
                    }

                    try {
                        mSelector.select();
                        Set<SelectionKey> selectionKeys =  mSelector.selectedKeys();
                        Iterator it = selectionKeys.iterator();
                        while(it.hasNext()){
                            SelectionKey key = (SelectionKey)it.next();
                            it.remove();
                            handleKey(key);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.setDaemon(false);
        thread.setName("BigThunderPorxy");
        thread.start();
        return true;
    }

    public void stop(){

    }


    private void handleKey(SelectionKey key){
        if(!key.isValid()){
            return;
        }

        Object socketAction = key.attachment();
        if(null == socketAction){
            socketAction = new SocketAcceptAction();
        }
        if(socketAction instanceof SocketAction){
            ((SocketAction) socketAction).onAction(key, mSelector, mSocketBuffer);
        }
    }

    public static void main(String[] argu){
        System.out.println("*********main************");
        ProxyServer.instance().start();
    }
}
