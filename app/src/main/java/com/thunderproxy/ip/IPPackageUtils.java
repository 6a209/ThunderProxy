package com.thunderproxy.ip;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by 6a209 on 16/8/14.
 */

public class IPPackageUtils {



    private static class SingletonHolder {
        private static final IPPackageUtils INSTANCE = new IPPackageUtils();
    }

    private IPPackageUtils() {
    }

    public static final IPPackageUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * change the ip package dest ip && change the header checksum
     * the old dest ip is in [16-19] ip package
     * @param ipPackage ip package
     * @param destIp dest ip address
     * @return old dest ip address
     */
    public byte[] changeDestIp(byte[] ipPackage, byte[] destIp){
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
        byte[] checksum = getChecksum(ipPackage);
        ipPackage[10] = checksum[0];
        ipPackage[11] = checksum[1];
        return oldIp;
    }

    public byte[] getChecksum(byte[] ipPackage) {
        int headerLength = (ipPackage[0] & 0x0f) * 4;

        byte[] ipHeaderPackage = new byte[headerLength];
        System.arraycopy(ipPackage, 0, ipHeaderPackage, 0, headerLength);

        Log.d("old check sum", Integer.toHexString(ipHeaderPackage[10] & 0xff) + Integer.toHexString(ipHeaderPackage[11] & 0xff));
        ipHeaderPackage[10] = 0x00;
        ipHeaderPackage[11] = 0x00;
        short[] shorts = new short[ipHeaderPackage.length / 2];
        ByteBuffer.wrap(ipHeaderPackage).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

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

        Log.d("new check sum", Integer.toHexString((newSum >> 8) & 0xff) + Integer.toHexString());
        return checksumByte;
    }


}
