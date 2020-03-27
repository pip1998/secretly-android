/*
 * Copyright (C) 2020  chaoyongzhang
 * This file is part of the secretly-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package cn.zcytech.dapp.secretly;

import android.util.Log;

import com.zcytech.secretly.lib.mobile.Envelope;
import com.zcytech.secretly.lib.mobile.Mobile;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class EnvelopeUnitTest {
    private static byte[] senderPrv = hexStrToByteArray("2643eb22fec8c3d59b7f571eef9308202126d620b37f71f8a3345dc314d26a6d");
    private static byte[] senderPub = hexStrToByteArray("044977cdeda277fa4dbe743c304f5aed7e09ab5f7b91f2c75d96ecb534ac6432d27dfad256869c165d5bc67595921e0c9e426fbf64581bc22ce518360307b9c8f2");
    private static byte[] receiverPrv = hexStrToByteArray("5c114104e312d671c9737ef842eccb51c802eb052f400e8e6690a8388a8b0c0e");
    private static byte[] receiverPub = hexStrToByteArray("047b0a693721e8ea9a3b7d6e783fe09070ce7e73b68b85659590f7f23f83a2c87c0fae60a455cda4855199eda6d7167e15c831eed8be0fa897aa1180196dd8e8bd");

    private static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null){
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static byte[] hexStrToByteArray(String str)
    {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    @Test
    public void envelopeTransport() {
        // create an envelope
        byte[] content = "test".getBytes();
        Envelope e = null;
        try {
            e = Mobile.newEnvelope(content, receiverPub);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assertNotNull(e);

        // marshal it (when send)
        byte[] raw = null;
        try {
            raw = e.encodeToRLPBytes(senderPrv);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assertNotNull(raw);

        // unmarshal it (when receive)
        Envelope re = null;
        try {
            re = Mobile.decodeFromRLPBytes(raw);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assertNotNull(re);

        // get sender
        byte[] reSender = null;
        try {
            reSender = re.sender();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assertNotNull(reSender);
        assertEquals(byteArrayToHexStr(senderPub), byteArrayToHexStr(reSender));

        // get plain content
        byte[] plain = null;
        try {
            plain = re.decrypt(receiverPrv);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        assertNotNull(plain);
        assertEquals(new String(plain), new String(content));
        Log.d("Envelope", String.format("envelopeTransport: plain: %s", new String(plain)));
    }
}
