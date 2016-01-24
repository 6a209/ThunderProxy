package com.thunderproxy.proxy;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
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
                            handleKey(key);
                            it.remove();
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

        if(key.isAcceptable()){
            try {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                SelectionKey readSectionKey = socketChannel.register(mSelector, SelectionKey.OP_READ);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(key.isConnectable()){

        }else if(key.isReadable()){
            mSocketBuffer.clear();
            SocketChannel socketChannel = (SocketChannel)key.channel();
            try {
                int count = socketChannel.read(mSocketBuffer);
                mSocketBuffer.flip();


                String line = readLine(mSocketBuffer);
                while (line != null && line.length() > 0){
                    System.out.println("********************");
                    System.out.println(line);
                    line = readLine(mSocketBuffer);
                }
//                if(count > 0){
//                    byte[] bytes = new byte[count];
//                    mSocketBuffer.get(bytes);
//                    System.out.println(new String(bytes, Charset.forName("UTF-8")));
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    String readLine(ByteBuffer byteBuffer){
        StringBuilder sb = new StringBuilder();
        while(byteBuffer.remaining() > 0){
            byte ch = byteBuffer.get();
            if(-1 == ch || '\n' == ch){
                break;
            }
            if(ch != '\r'){
                sb.append((char)ch);
            }
        }
        return sb.toString();
    }

    public static void main(String[] argu){
        System.out.println("*********main************");
        ProxyServer.instance().start();
    }
}
