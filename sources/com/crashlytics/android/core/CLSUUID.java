package com.crashlytics.android.core;

import android.os.Process;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.IdManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

final class CLSUUID {
    private static String _clsId;
    private static final AtomicLong _sequenceNumber = new AtomicLong(0);

    public CLSUUID(IdManager idManager) {
        bytes = new byte[10];
        long time = new Date().getTime();
        long j = time / 1000;
        time %= 1000;
        ByteBuffer allocate = ByteBuffer.allocate(4);
        allocate.putInt((int) j);
        allocate.order(ByteOrder.BIG_ENDIAN);
        allocate.position(0);
        byte[] array = allocate.array();
        bytes[0] = array[0];
        bytes[1] = array[1];
        bytes[2] = array[2];
        bytes[3] = array[3];
        array = convertLongToTwoByteBuffer(time);
        bytes[4] = array[0];
        bytes[5] = array[1];
        array = convertLongToTwoByteBuffer(_sequenceNumber.incrementAndGet());
        bytes[6] = array[0];
        bytes[7] = array[1];
        array = convertLongToTwoByteBuffer((long) Integer.valueOf(Process.myPid()).shortValue());
        bytes[8] = array[0];
        bytes[9] = array[1];
        String idSha = CommonUtils.sha1(idManager.getAppInstallIdentifier());
        String timeSeqPid = CommonUtils.hexify(bytes);
        _clsId = String.format(Locale.US, "%s-%s-%s-%s", new Object[]{timeSeqPid.substring(0, 12), timeSeqPid.substring(12, 16), timeSeqPid.subSequence(16, 20), idSha.substring(0, 12)}).toUpperCase(Locale.US);
    }

    private static byte[] convertLongToTwoByteBuffer(long value) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort((short) ((int) value));
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.position(0);
        return buf.array();
    }

    public final String toString() {
        return _clsId;
    }
}
