package com.thunderproxy.proxy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 6a209 on 16/1/31.
 */
public class ResSocketReadAction extends SocketReadAction{

    SocketChannel mSocketChannel;
    Response mResponse;


    public ResSocketReadAction(SocketChannel channel){
        mSocketChannel = channel;
    }


    @Override
    public void onAction(SelectionKey selectionKey, Selector selector, ByteBuffer byteBuffer) {
        int count = read(selectionKey, byteBuffer);
        if(count < 0){
            mResponse.setBody(mBodyTemp.toString());
            try {
                selectionKey.channel().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if(null == mResponse){
            mResponse = new Response();
        }

        if(STEP.STATUS == mCurrentStep){
            String[] status = parseStatus(byteBuffer);
            if(status.length != 3){
                return;
            }
            mResponse.setHttpVersion(status[0]);
            mResponse.setStatusCode(status[1]);
            mResponse.setShortDesc(status[2].replace("\r\n", ""));
            mCurrentStep = STEP.HEAD;
        }

        if(STEP.HEAD == mCurrentStep){
            if(byteBuffer.remaining() <= 0){
               return;
            }
            System.out.println("******* head ************");
            Map<String, String> headers = new HashMap<String, String>();
            boolean isOver = parseHeader(byteBuffer, headers);
            mResponse.addHeader(headers);
            mResponse.setContentLength(mResponse.getHeader().get("Content-Length"));
            System.out.println("******* head ************" + mResponse.getHeader().size());
            if(isOver){
                mCurrentStep = STEP.BODY;
                try {
                    mSocketChannel.write(ByteBuffer.wrap(mResponse.format2Byte()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(STEP.BODY == mCurrentStep){

            int size = byteBuffer.remaining();
            if(size > 0) {
                if (null == mBodyTemp) {
                    mBodyTemp = new StringBuilder();
                }
                byte[] bytes = new byte[size];
                byteBuffer.get(bytes);
                mBodyTemp.append(new String(bytes));
                System.out.println("******* body ************");
                System.out.println(mBodyTemp.toString());
                System.out.println("---------------------");
                try {
                    mSocketChannel.write(ByteBuffer.wrap(bytes));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (count < byteBuffer.capacity()) {
                }
            } else if(size <= 0 && null == mBodyTemp){
                mCurrentStep = STEP.OVER;
            }

            if(null != mBodyTemp && mBodyTemp.length() == mResponse.getContentLength()){
                mCurrentStep = STEP.OVER;
            }

        }


        if(STEP.OVER == mCurrentStep){
            System.out.print(mBodyTemp);
            ProxyServer.OnResponseListener listener = ProxyServer.instance().getOnResponseListener();
            if(null != listener){
                listener.onResponseFinish(mResponse);
            }
        }
    }
}
