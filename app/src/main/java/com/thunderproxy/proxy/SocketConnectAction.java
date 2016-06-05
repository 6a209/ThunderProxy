package com.thunderproxy.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by 6a209 on 16/1/30.
 */
public class SocketConnectAction implements SocketAction{

    Request mRequest;

    public SocketConnectAction(Request request){
       mRequest = request;
    }

    @Override
    public void onAction(SelectionKey key, Selector selector, ByteBuffer byteBuffer) {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if (!socketChannel.finishConnect()) {
                return;
            }
            System.out.print("*****ok***");
            SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            selectionKey.attach(new ResSocketReadAction(mRequest));
            sendRequest(socketChannel, mRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    void sendRequest(SocketChannel socketChannel, Request request){
        ByteBuffer byteBuffer = request.factoryRequest();
        try {
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
