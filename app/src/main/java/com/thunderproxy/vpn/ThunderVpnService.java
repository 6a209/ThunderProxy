package com.thunderproxy.vpn;

import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.thunderproxy.tcpip.TCPIPPackageUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created by 6a209 on 16/7/5.
 */

public class ThunderVpnService extends VpnService{

    static VpnService sVpnservice;

    /**
     * 保护socket 直接发掉
     * @param socket
     */
    public static void protectSocket(Socket socket) {
        if (null != sVpnservice) {
           sVpnservice.protect(socket);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        sVpnservice = this;
        startVpn();
        return super.onStartCommand(intent, flag, startId);
    }

    private void startVpn() {
        Builder builder = new Builder();
        builder.addAddress("10.25.1.1", 24)
        .addRoute("1.0.0.0", 8)
        .addRoute("2.0.0.0", 7)
        .addRoute("4.0.0.0", 6)
        .addRoute("8.0.0.0", 7)
        // 10.0.0.0 - 10.255.255.255
        .addRoute("11.0.0.0", 8)
        .addRoute("12.0.0.0", 6)
        .addRoute("16.0.0.0", 4)
        .addRoute("32.0.0.0", 3)
        .addRoute("64.0.0.0", 2)
        .addRoute("139.0.0.0", 8)
        .addRoute("140.0.0.0", 6)
        .addRoute("144.0.0.0", 4)
        .addRoute("160.0.0.0", 5)
        .addRoute("168.0.0.0", 6)
        .addRoute("172.0.0.0", 12)
        // 172.16.0.0 - 172.31.255.255
        .addRoute("172.32.0.0", 11)
        .addRoute("172.64.0.0", 10)
        .addRoute("172.128.0.0", 9)
        .addRoute("173.0.0.0", 8)
        .addRoute("174.0.0.0", 7)
        .addRoute("176.0.0.0", 4)
        .addRoute("192.0.0.0", 9)
        .addRoute("192.128.0.0", 11)
        .addRoute("192.160.0.0", 13)
        // 192.168.0.0 - 192.168.255.255
        .addRoute("192.169.0.0", 16)
        .addRoute("192.170.0.0", 15)
        .addRoute("192.172.0.0", 14)
        .addRoute("192.176.0.0", 12)
        .addRoute("192.192.0.0", 10)
        .addRoute("193.0.0.0", 8)
        .addRoute("194.0.0.0", 7)
        .addRoute("196.0.0.0", 6)
        .addRoute("200.0.0.0", 5)
        .addRoute("208.0.0.0", 4)
        .addRoute("224.0.0.0", 4)
        .addRoute("240.0.0.0",5)
        .addRoute("248.0.0.0",6)
        .addRoute("252.0.0.0",7)
        .addRoute("254.0.0.0",8)
        .addRoute("0.0.0.0", 0)
        .setMtu(1000)
        .addDnsServer("8.8.8.8")
        .setSession("ThunderProxy");

        ParcelFileDescriptor pfd = builder.establish();
        FileInputStream fin = new FileInputStream(pfd.getFileDescriptor());
        FileOutputStream fout = new FileOutputStream(pfd.getFileDescriptor());
        ByteBuffer packet = ByteBuffer.allocate(32767);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final LocalServerSocket fdServerSocket;
                try {
                    fdServerSocket = new LocalServerSocket("thundersocket");
                    while (true) {
                        final LocalSocket fdSocket = fdServerSocket.accept();
                        OutputStream outputStream = fdSocket.getOutputStream();
                        InputStream inputStream = fdSocket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1);
                        String request = reader.readLine();
                        Log.d("ThunderVpnService", request);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        protect(pfd.getFd());



        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LocalSocket localSocket = new LocalSocket();
        LocalSocketAddress localSocketAddress = new LocalSocketAddress("thundersocket");
        try {
            localSocket.connect(localSocketAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
            new Thread(){
                @Override
                public void run(){
                    try {
                        Socket socket = new Socket("127.0.0.1", 8888);
                        socket.getOutputStream().write(">>>> test <<<<<".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();

        while (true) {
            try {
                byte[] byteArray = packet.array();
                int length = fin.read(byteArray);

                if (length > 0) {


//                    TCPIPPackageUtils.getInstance().redirectPackage()
//                    TCPIPPackageUtils.getInstance(
//                    Log.d("length >>", "" + length);
//                    Log.d("hex ====>>>", bytesToHexString(byteArray));
//                    TCPIPPackageUtils.getInstance().changeChecksum(byteArray);
//                    logArray("version", System.arraycopy(byteArray, 0, new byte[4], 0, 4));
//                    Log.d("version", "" + byteArray[0] + byteArray[1] + byteArray[2] + byteArray[3]);
//                    Log.d("iP header length", "" + byteArray[4] + byteArray[5] + byteArray[6] + byteArray[7]);
//                    Log.d("iP package length", "" + byteArray[16] + byteArray[17] + byteArray[18] + byteArray[19]
//                    + byteArray[20] + byteArray[21] + byteArray[22] + byteArray[23] + byteArray[24] + byteArray[25] + byteArray[26] + byteArray[27]
//                    + byteArray[28] + byteArray[29] + byteArray[30] + byteArray[31]);


//                    OutputStream outputStream = localSocket.getOutputStream();
//                    outputStream.write(packet.array());
//                    Log.d("Thunder => ", new String(packet.array()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv+" ");
        }
        return stringBuilder.toString();
    }

    void logArray(String key, byte[] array) {

    }



}
