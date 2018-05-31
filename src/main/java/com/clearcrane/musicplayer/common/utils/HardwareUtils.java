package com.clearcrane.musicplayer.common.utils;

import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * Created by jjy on 2018/5/26.
 */

public class HardwareUtils {
    public static String getMacAddress() {
        String strMacAddr;
        byte[] b;
        try {
            NetworkInterface NIC = NetworkInterface.getByName("eth0");
            b = NIC.getHardwareAddress();
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < b.length; i++) {
                if (i != 0) {
                    buffer.append(':');
                }
                String str = Integer.toHexString(b[i] & 0xFF);
                buffer.append(str.length() == 1 ? 0 + str : str);
            }
            strMacAddr = buffer.toString().toUpperCase();
        } catch (SocketException e) {
            e.printStackTrace();
            return "0.0.0.0";
        }

        return strMacAddr;
    }

}
