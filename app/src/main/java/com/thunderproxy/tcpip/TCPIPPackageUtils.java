package com.thunderproxy.tcpip;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by 6a209 on 16/8/14.
 */

public class TCPIPPackageUtils {

    static final byte TCP_PROTOCOL_FLAG = 0x06;
    static final byte UDP_PROTOCOL_FLAG = 0x11;



    private static class SingletonHolder {
        private static final TCPIPPackageUtils INSTANCE = new TCPIPPackageUtils();
    }

    private TCPIPPackageUtils() {
    }

    public static final TCPIPPackageUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static class SourceIPPackageInfo {
        public byte[] mIp;
        public byte[] mPort;
    }


    /**
     * change the ip package dest ip && change the header checksum
     * change the tcp dest port && change the tcp checksum
     * the old dest ip is in [16-19] ip package
     * @param ipPackage ip package
     * @param destIp dest ip address
     * @param destPort dest tcp port
     * @return old dest ip address
     */
    public byte[] redirectPackage(byte[] ipPackage, byte[] destIp, byte[] destPort){
        if (null == ipPackage || null == destIp) {
            return null;
        }
        if (ipPackage.length < 20) {
            return null;
        }

        byte[] oldIp = new byte[4];
        oldIp[0] = ipPackage[16];
        ipPackage[16] = destIp[0];

        oldIp[1] = ipPackage[17];
        ipPackage[17] = destIp[1];

        oldIp[2] = ipPackage[18];
        ipPackage[18] = destIp[2];

        oldIp[3] = ipPackage[19];
        ipPackage[19] = destIp[3];

        // change ip checksum
        int headerLength = (ipPackage[0] & 0x0f) * 4;
        byte[] ipHeaderPackage = new byte[headerLength];
        System.arraycopy(ipPackage, 0, ipHeaderPackage, 0, headerLength);
        ipHeaderPackage[10] = 0x00;
        ipHeaderPackage[11] = 0x00;

        byte[] checksum = calculateChecksum(ipHeaderPackage);
        ipPackage[10] = checksum[0];
        ipPackage[11] = checksum[1];


        byte protocol = ipPackage[9];
        boolean isTcp = protocol == TCP_PROTOCOL_FLAG;
        if (isTcp) {
            int tcpPackageLength = ipHeaderPackage.length - headerLength;
            byte[] tcpPackage = new  byte[tcpPackageLength + 12];
            System.arraycopy(ipPackage, 0, tcpPackage, 12, tcpPackageLength);

            // source ip
            tcpPackage[0] = ipPackage[12];
            tcpPackage[1] = ipPackage[13];
            tcpPackage[2] = ipPackage[14];
            tcpPackage[3] = ipPackage[15];

            // destination ip
            tcpPackage[4] = ipPackage[16];
            tcpPackage[5] = ipPackage[17];
            tcpPackage[6] = ipPackage[18];
            tcpPackage[7] = ipPackage[19];

            // zeros
            tcpPackage[8] = 0x00;

            // protocol
            tcpPackage[9] = TCP_PROTOCOL_FLAG;

            // tcp length
            tcpPackage[10] = (byte) (tcpPackageLength & 0xff);
            tcpPackage[11] = (byte) ((tcpPackageLength >> 8) & 0xff);

            tcpPackage[14] = destPort[0];
            tcpPackage[15] = destPort[1];

            // old checksum
            tcpPackage[28] = 0x00;
            tcpPackage[29] = 0x00;

            byte[] tcpChecksum = calculateChecksum(tcpPackage);
            tcpPackage[28] = tcpChecksum[0];
            tcpPackage[29] = tcpChecksum[1];
         }


        return oldIp;
    }



    public byte[] calculateChecksum(byte[] sourcePackage) {

//        Log.d("old check sum", Integer.toHexString(ipHeaderPackage[10] & 0xff) + Integer.toHexString(ipHeaderPackage[11] & 0xff));

        // +1 if length is odd
        short[] shorts = new short[(sourcePackage.length + 1) / 2];
        ByteBuffer.wrap(sourcePackage).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        long checksum = 0;
        for(short item : shorts) {
            checksum += ((item >> 8) & 0xff) * 256 + (item & 0xff);
        }

        checksum = (checksum >> 16) + (checksum & 0xffff);
        checksum += (checksum >> 16);
        short newSum = (short) (~checksum);
        byte[] checksumByte = new byte[2];
        checksumByte[0] = (byte) (newSum & 0xff);
        checksumByte[1] = (byte) ((newSum >> 8) & 0xff);

//        Log.d("new check sum", Integer.toHexString((newSum >> 8) & 0xff) + Integer.toHexString());
        return checksumByte;
    }

    public static void main(String[] argu){

        byte  p = (byte) 0x88;
        int protocol = p;
        System.out.println(protocol);
    }

}
