package com.electricimp.blinkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

final class BlinkupPacket {
    private static final int[] crc16Table;
    private ArrayList<Integer> bitStream = new ArrayList();
    private char crc;
    private boolean triLevel = false;

    static {
        int[] iArr = new int[256];
        iArr[1] = 49345;
        iArr[2] = 49537;
        iArr[3] = 320;
        iArr[4] = 49921;
        iArr[5] = 960;
        iArr[6] = 640;
        iArr[7] = 49729;
        iArr[8] = 50689;
        iArr[9] = 1728;
        iArr[10] = 1920;
        iArr[11] = 51009;
        iArr[12] = 1280;
        iArr[13] = 50625;
        iArr[14] = 50305;
        iArr[15] = 1088;
        iArr[16] = 52225;
        iArr[17] = 3264;
        iArr[18] = 3456;
        iArr[19] = 52545;
        iArr[20] = 3840;
        iArr[21] = 53185;
        iArr[22] = 52865;
        iArr[23] = 3648;
        iArr[24] = 2560;
        iArr[25] = 51905;
        iArr[26] = 52097;
        iArr[27] = 2880;
        iArr[28] = 51457;
        iArr[29] = 2496;
        iArr[30] = 2176;
        iArr[31] = 51265;
        iArr[32] = 55297;
        iArr[33] = 6336;
        iArr[34] = 6528;
        iArr[35] = 55617;
        iArr[36] = 6912;
        iArr[37] = 56257;
        iArr[38] = 55937;
        iArr[39] = 6720;
        iArr[40] = 7680;
        iArr[41] = 57025;
        iArr[42] = 57217;
        iArr[43] = 8000;
        iArr[44] = 56577;
        iArr[45] = 7616;
        iArr[46] = 7296;
        iArr[47] = 56385;
        iArr[48] = 5120;
        iArr[49] = 54465;
        iArr[50] = 54657;
        iArr[51] = 5440;
        iArr[52] = 55041;
        iArr[53] = 6080;
        iArr[54] = 5760;
        iArr[55] = 54849;
        iArr[56] = 53761;
        iArr[57] = 4800;
        iArr[58] = 4992;
        iArr[59] = 54081;
        iArr[60] = 4352;
        iArr[61] = 53697;
        iArr[62] = 53377;
        iArr[63] = 4160;
        iArr[64] = 61441;
        iArr[65] = 12480;
        iArr[66] = 12672;
        iArr[67] = 61761;
        iArr[68] = 13056;
        iArr[69] = 62401;
        iArr[70] = 62081;
        iArr[71] = 12864;
        iArr[72] = 13824;
        iArr[73] = 63169;
        iArr[74] = 63361;
        iArr[75] = 14144;
        iArr[76] = 62721;
        iArr[77] = 13760;
        iArr[78] = 13440;
        iArr[79] = 62529;
        iArr[80] = 15360;
        iArr[81] = 64705;
        iArr[82] = 64897;
        iArr[83] = 15680;
        iArr[84] = 65281;
        iArr[85] = 16320;
        iArr[86] = 16000;
        iArr[87] = 65089;
        iArr[88] = 64001;
        iArr[89] = 15040;
        iArr[90] = 15232;
        iArr[91] = 64321;
        iArr[92] = 14592;
        iArr[93] = 63937;
        iArr[94] = 63617;
        iArr[95] = 14400;
        iArr[96] = 10240;
        iArr[97] = 59585;
        iArr[98] = 59777;
        iArr[99] = 10560;
        iArr[100] = 60161;
        iArr[101] = 11200;
        iArr[102] = 10880;
        iArr[103] = 59969;
        iArr[104] = 60929;
        iArr[105] = 11968;
        iArr[106] = 12160;
        iArr[107] = 61249;
        iArr[108] = 11520;
        iArr[109] = 60865;
        iArr[110] = 60545;
        iArr[111] = 11328;
        iArr[112] = 58369;
        iArr[113] = 9408;
        iArr[114] = 9600;
        iArr[115] = 58689;
        iArr[116] = 9984;
        iArr[117] = 59329;
        iArr[118] = 59009;
        iArr[119] = 9792;
        iArr[120] = 8704;
        iArr[121] = 58049;
        iArr[122] = 58241;
        iArr[123] = 9024;
        iArr[124] = 57601;
        iArr[125] = 8640;
        iArr[126] = 8320;
        iArr[127] = 57409;
        iArr[128] = 40961;
        iArr[129] = 24768;
        iArr[130] = 24960;
        iArr[131] = 41281;
        iArr[132] = 25344;
        iArr[133] = 41921;
        iArr[134] = 41601;
        iArr[135] = 25152;
        iArr[136] = 26112;
        iArr[137] = 42689;
        iArr[138] = 42881;
        iArr[139] = 26432;
        iArr[140] = 42241;
        iArr[141] = 26048;
        iArr[142] = 25728;
        iArr[143] = 42049;
        iArr[144] = 27648;
        iArr[145] = 44225;
        iArr[146] = 44417;
        iArr[147] = 27968;
        iArr[148] = 44801;
        iArr[149] = 28608;
        iArr[150] = 28288;
        iArr[151] = 44609;
        iArr[152] = 43521;
        iArr[153] = 27328;
        iArr[154] = 27520;
        iArr[155] = 43841;
        iArr[156] = 26880;
        iArr[157] = 43457;
        iArr[158] = 43137;
        iArr[159] = 26688;
        iArr[160] = 30720;
        iArr[161] = 47297;
        iArr[162] = 47489;
        iArr[163] = 31040;
        iArr[164] = 47873;
        iArr[165] = 31680;
        iArr[166] = 31360;
        iArr[167] = 47681;
        iArr[168] = 48641;
        iArr[169] = 32448;
        iArr[170] = 32640;
        iArr[171] = 48961;
        iArr[172] = 32000;
        iArr[173] = 48577;
        iArr[174] = 48257;
        iArr[175] = 31808;
        iArr[176] = 46081;
        iArr[177] = 29888;
        iArr[178] = 30080;
        iArr[179] = 46401;
        iArr[180] = 30464;
        iArr[181] = 47041;
        iArr[182] = 46721;
        iArr[183] = 30272;
        iArr[184] = 29184;
        iArr[185] = 45761;
        iArr[186] = 45953;
        iArr[187] = 29504;
        iArr[188] = 45313;
        iArr[189] = 29120;
        iArr[190] = 28800;
        iArr[191] = 45121;
        iArr[192] = 20480;
        iArr[193] = 37057;
        iArr[194] = 37249;
        iArr[195] = 20800;
        iArr[196] = 37633;
        iArr[197] = 21440;
        iArr[198] = 21120;
        iArr[199] = 37441;
        iArr[Callback.DEFAULT_DRAG_ANIMATION_DURATION] = 38401;
        iArr[201] = 22208;
        iArr[202] = 22400;
        iArr[203] = 38721;
        iArr[204] = 21760;
        iArr[205] = 38337;
        iArr[206] = 38017;
        iArr[207] = 21568;
        iArr[208] = 39937;
        iArr[209] = 23744;
        iArr[210] = 23936;
        iArr[211] = 40257;
        iArr[212] = 24320;
        iArr[213] = 40897;
        iArr[214] = 40577;
        iArr[215] = 24128;
        iArr[216] = 23040;
        iArr[217] = 39617;
        iArr[218] = 39809;
        iArr[219] = 23360;
        iArr[220] = 39169;
        iArr[221] = 22976;
        iArr[222] = 22656;
        iArr[223] = 38977;
        iArr[224] = 34817;
        iArr[225] = 18624;
        iArr[226] = 18816;
        iArr[227] = 35137;
        iArr[228] = 19200;
        iArr[229] = 35777;
        iArr[230] = 35457;
        iArr[231] = 19008;
        iArr[232] = 19968;
        iArr[233] = 36545;
        iArr[234] = 36737;
        iArr[235] = 20288;
        iArr[236] = 36097;
        iArr[237] = 19904;
        iArr[238] = 19584;
        iArr[239] = 35905;
        iArr[240] = 17408;
        iArr[241] = 33985;
        iArr[242] = 34177;
        iArr[243] = 17728;
        iArr[244] = 34561;
        iArr[245] = 18368;
        iArr[246] = 18048;
        iArr[247] = 34369;
        iArr[248] = 33281;
        iArr[249] = 17088;
        iArr[Callback.DEFAULT_SWIPE_ANIMATION_DURATION] = 17280;
        iArr[251] = 33601;
        iArr[252] = 16640;
        iArr[253] = 33217;
        iArr[254] = 32897;
        iArr[255] = 16448;
        crc16Table = iArr;
    }

    private static int getLength(String data) {
        try {
            return data.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            Log.e("BlinkUp", Log.getStackTraceString(e));
            return 0;
        }
    }

    private void addEnrolmentPacketData(String token, String siteid) {
        int i;
        ByteBuffer tokenBuffer = ByteBuffer.wrap(new byte[8]);
        tokenBuffer.putLong(new BigInteger(token, 16).longValue());
        ByteBuffer siteidBuffer = ByteBuffer.wrap(new byte[8]);
        siteidBuffer.putLong(new BigInteger(siteid, 16).longValue());
        addByteToBitstream((byte) 5);
        addByteToBitstream((byte) 16);
        for (i = 7; i >= 0; i--) {
            addByteToBitstream(siteidBuffer.get(i));
        }
        for (i = 7; i >= 0; i--) {
            addByteToBitstream(tokenBuffer.get(i));
        }
    }

    private void startPacket(int length) {
        this.bitStream = new ArrayList();
        for (int i = 0; i < 8; i++) {
            addByteToBitstreamNoCRC((byte) 85);
        }
        if (this.triLevel) {
            addByteToBitstreamNoCRCPrependBlack((byte) 42);
        } else {
            addByteToBitstreamNoCRC((byte) 42);
        }
        this.crc = '\u0000';
        addByteToBitstream((byte) length);
    }

    private void finishPacket() {
        if (this.triLevel) {
            addByteToBitstreamNoCRCPrependBlack((byte) (this.crc >> 8));
            addByteToBitstreamNoCRCPrependBlack((byte) this.crc);
            return;
        }
        addByteToBitstreamNoCRC((byte) (this.crc >> 8));
        addByteToBitstreamNoCRC((byte) this.crc);
    }

    private void addByteToBitstreamNoCRC(byte c) {
        for (int j = 7; j >= 0; j--) {
            if (((c >> j) & 1) == 0) {
                this.bitStream.add(Integer.valueOf(0));
            } else {
                this.bitStream.add(Integer.valueOf(this.triLevel ? 1 : 2));
            }
        }
    }

    private void addByteToBitstreamNoCRCPrependBlack(byte c) {
        for (int j = 7; j >= 0; j--) {
            if (((c >> j) & 1) == 0) {
                this.bitStream.add(Integer.valueOf(0));
                this.bitStream.add(Integer.valueOf(1));
            } else {
                this.bitStream.add(Integer.valueOf(0));
                this.bitStream.add(Integer.valueOf(2));
            }
        }
    }

    private void addPacketData(String data, byte type) {
        addByteToBitstream(type);
        try {
            byte[] bytes = data.getBytes("UTF-8");
            addByteToBitstream((byte) bytes.length);
            for (byte b : bytes) {
                addByteToBitstream(b);
            }
        } catch (UnsupportedEncodingException e) {
            Log.e("BlinkUp", Log.getStackTraceString(e));
        }
    }

    public final int numBits() {
        return this.bitStream.size();
    }

    public final int bitAtIndex(int index) {
        return ((Integer) this.bitStream.get(index)).intValue();
    }

    public final void twoThirdSpeed() {
        ArrayList<Integer> slowStream = new ArrayList();
        for (int i = 0; i < this.bitStream.size(); i++) {
            slowStream.add((Integer) this.bitStream.get(i));
            if (i % 2 != 0) {
                slowStream.add((Integer) this.bitStream.get(i));
            }
        }
        this.bitStream = slowStream;
    }

    public static BlinkupPacket createFromIntent(Intent intent) {
        int i = 0;
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return null;
        }
        BlinkupPacket packet = new BlinkupPacket();
        String bitStream = extras.getString("bitStream");
        if (bitStream != null) {
            for (int i2 = 0; i2 < bitStream.length(); i2++) {
                packet.bitStream.add(Integer.valueOf(bitStream.substring(i2, i2 + 1)));
            }
            return packet;
        }
        if (extras.getBoolean("trilevel", false)) {
            packet.triLevel = true;
        }
        String mode = extras.getString("mode");
        String token;
        String siteid;
        if (mode.equals("wifi")) {
            String ssid = extras.getString("ssid");
            String pwd = extras.getString("pwd");
            token = extras.getString("token");
            siteid = extras.getString("siteid");
            if (ssid == null) {
                ssid = "";
            }
            if (pwd == null) {
                pwd = "";
            }
            int length = (getLength(ssid) + 2) + 18;
            if (pwd.length() > 0) {
                length += getLength(pwd) + 2;
            }
            packet.startPacket(length);
            packet.addPacketData(ssid, (byte) 1);
            if (pwd.length() > 0) {
                packet.addPacketData(pwd, (byte) 6);
            }
            packet.addEnrolmentPacketData(token, siteid);
            packet.finishPacket();
        } else if (mode.equals("wps")) {
            String pin = extras.getString("pin");
            token = extras.getString("token");
            siteid = extras.getString("siteid");
            if (pin == null) {
                pin = "";
            }
            packet.startPacket((getLength(pin) + 2) + 18);
            packet.addPacketData(pin, (byte) 3);
            packet.addEnrolmentPacketData(token, siteid);
            packet.finishPacket();
        } else if (mode.equals("fwUpgrade")) {
            packet.startPacket(2);
            packet.addByteToBitstream((byte) 4);
            packet.addByteToBitstream((byte) 0);
            packet.finishPacket();
        } else if (!mode.equals("clear")) {
            return null;
        } else {
            packet.startPacket(2);
            packet.addByteToBitstream((byte) 7);
            packet.addByteToBitstream((byte) 0);
            packet.finishPacket();
        }
        if (!extras.getBoolean("slow", false)) {
            return packet;
        }
        ArrayList arrayList = new ArrayList();
        while (i < packet.bitStream.size()) {
            arrayList.add((Integer) packet.bitStream.get(i));
            arrayList.add((Integer) packet.bitStream.get(i));
            i++;
        }
        packet.bitStream = arrayList;
        return packet;
    }

    public final String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.bitStream.size(); i++) {
            buf.append(this.bitStream.get(i));
        }
        return buf.toString();
    }

    private void addByteToBitstream(byte c) {
        this.crc = (char) ((this.crc << 8) ^ crc16Table[((this.crc >> 8) ^ c) & 255]);
        if (this.triLevel) {
            addByteToBitstreamNoCRCPrependBlack(c);
        } else {
            addByteToBitstreamNoCRC(c);
        }
    }
}
