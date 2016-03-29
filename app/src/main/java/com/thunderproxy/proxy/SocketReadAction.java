package com.thunderproxy.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 6a209 on 16/1/30.
 */
public abstract class SocketReadAction implements SocketAction{

    StringBuilder mBodyTemp;
    enum STEP{
        STATUS,
        HEAD,
        BODY,
        OVER
    }
    STEP mCurrentStep = STEP.STATUS;


    int read(SelectionKey selectionKey, ByteBuffer byteBuffer){
        byteBuffer.clear();
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        int count = 0;
        try {
            count = socketChannel.read(byteBuffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuffer.flip();
        return count;
    }


    String[] parseStatus(ByteBuffer byteBuffer){
        String status = readLine(byteBuffer);
        String[] arr = status.split(" ");
        return arr;
    }

    /**
     *
     * @param byteBuffer
     * @param header
     * @return true
     */
    boolean parseHeader(ByteBuffer byteBuffer, Map<String, String> header){
        String line;
        while((line = readLine(byteBuffer)).length() > 0){
            String[] array = line.split(": ");
            if(array.length == 2){
                header.put(array[0], array[1].replace("\r\n", ""));
            }else{
                if(line.equals("\r\n")) {
                    // header is over
                    return true;
                }
            }
        }
        return false;
    }

    String readLine(ByteBuffer byteBuffer){
        StringBuilder sb = new StringBuilder();
        byte lastByte = 0x00;
        while(byteBuffer.remaining() > 0){
            byte ch = byteBuffer.get();
            sb.append((char)ch);
            if(('\r' == lastByte && '\n' == ch) || -1 == ch){
                break;
            }
            lastByte = ch;

        }
        return sb.toString();
    }



}
