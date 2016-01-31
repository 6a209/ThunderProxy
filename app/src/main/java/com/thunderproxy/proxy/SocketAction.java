package com.thunderproxy.proxy;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * Created by 6a209 on 16/1/30.
 */
public interface SocketAction {
    void onAction(SelectionKey selectionKey, Selector selector, ByteBuffer byteBuffer);
}
