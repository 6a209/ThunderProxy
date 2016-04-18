package com.thunderproxy.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 6a209 on 16/1/31.
 *
 * client request
 */
public class ReqSocketReadAction extends SocketReadAction{

    Request mRequest;


    @Override
    public void onAction(SelectionKey selectionKey, Selector selector, ByteBuffer buffer) {

        int count = read(selectionKey, buffer);
        if(count <= 0){
            try {
                selectionKey.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if(null == mRequest) {
            mRequest = new Request();
        }
        mRequest.setSocketChannel((SocketChannel) selectionKey.channel());

        if(STEP.STATUS == mCurrentStep){
            String[] status = parseStatus(buffer);
            if(status.length != 3){
               return;
            }
            mRequest.setMethod(status[0]);
            mRequest.setUrl(status[1]);
            mRequest.setHttpVersion(status[2].replace("\r\n", ""));
            mCurrentStep = STEP.HEAD;
        }

        if(STEP.HEAD == mCurrentStep){
            Map<String, String> headers = new HashMap<String, String>();
            boolean isOver = parseHeader(buffer, headers);
            mRequest.addHeaders(headers);
            if(isOver){
                mCurrentStep = STEP.BODY;
            }
        }

        if(STEP.BODY == mCurrentStep){
            int size = buffer.remaining();
            if(size > 0) {
                if (null == mBodyTemp) {
                    mBodyTemp = new StringBuilder();
                }
                byte[] bytes = new byte[size];
                buffer.get(bytes);
                mBodyTemp.append(new String(bytes));
                if (count < buffer.capacity()) {
                    mCurrentStep = STEP.OVER;
                    if (mRequest.getMethod() == Request.METHOD.GET) {
                        mRequest.setQueryString(mBodyTemp.toString());
                    } else if (mRequest.getMethod() == Request.METHOD.POST) {
                        mRequest.setBody(mBodyTemp.toString());
                    }
                }
            }else{
                mCurrentStep = STEP.OVER;
            }
        }

        if(STEP.OVER == mCurrentStep){
            connectHost(selector, mRequest);
            ProxyServer.OnRequestListener listener = ProxyServer.instance().getOnRequestListener();
            if(null != listener){
                listener.onRequestFinish(mRequest);
            }
        }
    }


    void connectHost(Selector selector, Request request){
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            System.out.print("________________");
            System.out.print(InetAddress.getByName(request.getHost()));
            System.out.print("________________");

            SocketAddress address = new InetSocketAddress(InetAddress.getByName(request.getHost()), request.getPort());
            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_CONNECT);
            selectionKey.attach(new SocketConnectAction(request));
            socketChannel.connect(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
