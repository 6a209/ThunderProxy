package com.thunderproxy.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by 6a209 on 16/1/30.
 *
 * when socket accpet
 */
public class SocketAcceptAction implements SocketAction{

    @Override
    public void onAction(SelectionKey key, Selector selector, ByteBuffer byteBuffer) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
        SocketChannel socketChannel = null;
        try {
            socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            key = socketChannel.register(selector, SelectionKey.OP_READ);
            key.attach(new ReqSocketReadAction());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
