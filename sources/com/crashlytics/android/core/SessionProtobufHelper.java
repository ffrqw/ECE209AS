package com.crashlytics.android.core;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Build.VERSION;
import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.common.IdManager.DeviceIdentifierType;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

final class SessionProtobufHelper {
    private static final ByteString SIGNAL_DEFAULT_BYTE_STRING = ByteString.copyFromUtf8("0");
    private static final ByteString UNITY_PLATFORM_BYTE_STRING = ByteString.copyFromUtf8("Unity");

    public static void writeBeginSession(CodedOutputStream cos, String sessionId, String generator, long startedAtSeconds) throws Exception {
        cos.writeBytes(1, ByteString.copyFromUtf8(generator));
        cos.writeBytes(2, ByteString.copyFromUtf8(sessionId));
        cos.writeUInt64(3, startedAtSeconds);
    }

    public static void writeSessionApp(CodedOutputStream cos, String packageName, String apiKey, String versionCode, String versionName, String installUuid, int deliveryMechanism, String unityVersion) throws Exception {
        ByteString packageNameBytes = ByteString.copyFromUtf8(packageName);
        ByteString apiKeyBytes = ByteString.copyFromUtf8(apiKey);
        ByteString versionCodeBytes = ByteString.copyFromUtf8(versionCode);
        ByteString versionNameBytes = ByteString.copyFromUtf8(versionName);
        ByteString installIdBytes = ByteString.copyFromUtf8(installUuid);
        ByteString unityVersionBytes = unityVersion != null ? ByteString.copyFromUtf8(unityVersion) : null;
        cos.writeTag(7, 2);
        int computeBytesSize = ((CodedOutputStream.computeBytesSize(1, packageNameBytes) + 0) + CodedOutputStream.computeBytesSize(2, versionCodeBytes)) + CodedOutputStream.computeBytesSize(3, versionNameBytes);
        int sessionAppOrgSize = getSessionAppOrgSize(apiKeyBytes);
        computeBytesSize = (computeBytesSize + (sessionAppOrgSize + (CodedOutputStream.computeTagSize(5) + CodedOutputStream.computeRawVarint32Size(sessionAppOrgSize)))) + CodedOutputStream.computeBytesSize(6, installIdBytes);
        if (unityVersionBytes != null) {
            computeBytesSize = (computeBytesSize + CodedOutputStream.computeBytesSize(8, UNITY_PLATFORM_BYTE_STRING)) + CodedOutputStream.computeBytesSize(9, unityVersionBytes);
        }
        cos.writeRawVarint32(computeBytesSize + CodedOutputStream.computeEnumSize(10, deliveryMechanism));
        cos.writeBytes(1, packageNameBytes);
        cos.writeBytes(2, versionCodeBytes);
        cos.writeBytes(3, versionNameBytes);
        cos.writeTag(5, 2);
        cos.writeRawVarint32(getSessionAppOrgSize(apiKeyBytes));
        cos.writeBytes(1, apiKeyBytes);
        cos.writeBytes(6, installIdBytes);
        if (unityVersionBytes != null) {
            cos.writeBytes(8, UNITY_PLATFORM_BYTE_STRING);
            cos.writeBytes(9, unityVersionBytes);
        }
        cos.writeEnum(10, deliveryMechanism);
    }

    public static void writeSessionOS(CodedOutputStream cos, boolean isRooted) throws Exception {
        ByteString releaseBytes = ByteString.copyFromUtf8(VERSION.RELEASE);
        ByteString codeNameBytes = ByteString.copyFromUtf8(VERSION.CODENAME);
        cos.writeTag(8, 2);
        cos.writeRawVarint32((((CodedOutputStream.computeEnumSize(1, 3) + 0) + CodedOutputStream.computeBytesSize(2, releaseBytes)) + CodedOutputStream.computeBytesSize(3, codeNameBytes)) + CodedOutputStream.computeBoolSize(4, isRooted));
        cos.writeEnum(1, 3);
        cos.writeBytes(2, releaseBytes);
        cos.writeBytes(3, codeNameBytes);
        cos.writeBool(4, isRooted);
    }

    public static void writeSessionDevice(CodedOutputStream cos, String clsDeviceId, int arch, String model, int availableProcessors, long totalRam, long diskSpace, boolean isEmulator, Map<DeviceIdentifierType, String> ids, int state, String manufacturer, String modelClass) throws Exception {
        int i;
        int i2;
        ByteString clsDeviceIDBytes = ByteString.copyFromUtf8(clsDeviceId);
        ByteString modelBytes = stringToByteString(model);
        ByteString modelClassBytes = stringToByteString(modelClass);
        ByteString manufacturerBytes = stringToByteString(manufacturer);
        cos.writeTag(9, 2);
        int computeEnumSize = CodedOutputStream.computeEnumSize(3, arch) + (CodedOutputStream.computeBytesSize(1, clsDeviceIDBytes) + 0);
        if (modelBytes == null) {
            i = 0;
        } else {
            i = CodedOutputStream.computeBytesSize(4, modelBytes);
        }
        i = ((((i + computeEnumSize) + CodedOutputStream.computeUInt32Size(5, availableProcessors)) + CodedOutputStream.computeUInt64Size(6, totalRam)) + CodedOutputStream.computeUInt64Size(7, diskSpace)) + CodedOutputStream.computeBoolSize(10, isEmulator);
        if (ids != null) {
            i2 = i;
            for (Entry entry : ids.entrySet()) {
                i = getDeviceIdentifierSize((DeviceIdentifierType) entry.getKey(), (String) entry.getValue());
                i2 = (i + (CodedOutputStream.computeTagSize(11) + CodedOutputStream.computeRawVarint32Size(i))) + i2;
            }
        } else {
            i2 = i;
        }
        computeEnumSize = (i2 + CodedOutputStream.computeUInt32Size(12, state)) + (manufacturerBytes == null ? 0 : CodedOutputStream.computeBytesSize(13, manufacturerBytes));
        if (modelClassBytes == null) {
            i = 0;
        } else {
            i = CodedOutputStream.computeBytesSize(14, modelClassBytes);
        }
        cos.writeRawVarint32(i + computeEnumSize);
        cos.writeBytes(1, clsDeviceIDBytes);
        cos.writeEnum(3, arch);
        cos.writeBytes(4, modelBytes);
        cos.writeUInt32(5, availableProcessors);
        cos.writeUInt64(6, totalRam);
        cos.writeUInt64(7, diskSpace);
        cos.writeBool(10, isEmulator);
        for (Entry<DeviceIdentifierType, String> id : ids.entrySet()) {
            cos.writeTag(11, 2);
            cos.writeRawVarint32(getDeviceIdentifierSize((DeviceIdentifierType) id.getKey(), (String) id.getValue()));
            cos.writeEnum(1, ((DeviceIdentifierType) id.getKey()).protobufIndex);
            cos.writeBytes(2, ByteString.copyFromUtf8((String) id.getValue()));
        }
        cos.writeUInt32(12, state);
        if (manufacturerBytes != null) {
            cos.writeBytes(13, manufacturerBytes);
        }
        if (modelClassBytes != null) {
            cos.writeBytes(14, modelClassBytes);
        }
    }

    public static void writeSessionUser(CodedOutputStream cos, String id, String name, String email) throws Exception {
        if (id == null) {
            id = "";
        }
        ByteString idBytes = ByteString.copyFromUtf8(id);
        ByteString nameBytes = stringToByteString(name);
        ByteString emailBytes = stringToByteString(email);
        int size = CodedOutputStream.computeBytesSize(1, idBytes) + 0;
        if (name != null) {
            size += CodedOutputStream.computeBytesSize(2, nameBytes);
        }
        if (email != null) {
            size += CodedOutputStream.computeBytesSize(3, emailBytes);
        }
        cos.writeTag(6, 2);
        cos.writeRawVarint32(size);
        cos.writeBytes(1, idBytes);
        if (name != null) {
            cos.writeBytes(2, nameBytes);
        }
        if (email != null) {
            cos.writeBytes(3, emailBytes);
        }
    }

    public static void writeSessionEvent(CodedOutputStream cos, long eventTime, String eventType, TrimmedThrowableData exception, Thread exceptionThread, StackTraceElement[] exceptionStack, Thread[] otherThreads, List<StackTraceElement[]> otherStacks, Map<String, String> customAttributes, LogFileManager logFileManager, RunningAppProcessInfo runningAppProcessInfo, int orientation, String packageName, String buildId, Float batteryLevel, int batteryVelocity, boolean proximityEnabled, long usedRamInBytes, long diskUsedInBytes) throws Exception {
        ByteString optionalBuildIdBytes;
        ByteString packageNameBytes = ByteString.copyFromUtf8(packageName);
        if (buildId == null) {
            optionalBuildIdBytes = null;
        } else {
            optionalBuildIdBytes = ByteString.copyFromUtf8(buildId.replace("-", ""));
        }
        ByteString logByteString = logFileManager.getByteStringForLog();
        if (logByteString == null) {
            Fabric.getLogger().d("CrashlyticsCore", "No log data to include with this event.");
        }
        logFileManager.clearLog();
        cos.writeTag(10, 2);
        int computeUInt64Size = (CodedOutputStream.computeUInt64Size(1, eventTime) + 0) + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(eventType));
        int eventAppSize = getEventAppSize(exception, exceptionThread, exceptionStack, otherThreads, otherStacks, 8, packageNameBytes, optionalBuildIdBytes, customAttributes, runningAppProcessInfo, orientation);
        eventAppSize = (eventAppSize + (CodedOutputStream.computeTagSize(3) + CodedOutputStream.computeRawVarint32Size(eventAppSize))) + computeUInt64Size;
        int eventDeviceSize = getEventDeviceSize(batteryLevel, batteryVelocity, proximityEnabled, orientation, usedRamInBytes, diskUsedInBytes);
        eventAppSize += eventDeviceSize + (CodedOutputStream.computeTagSize(5) + CodedOutputStream.computeRawVarint32Size(eventDeviceSize));
        if (logByteString != null) {
            eventDeviceSize = CodedOutputStream.computeBytesSize(1, logByteString);
            eventAppSize += eventDeviceSize + (CodedOutputStream.computeTagSize(6) + CodedOutputStream.computeRawVarint32Size(eventDeviceSize));
        }
        cos.writeRawVarint32(eventAppSize);
        cos.writeUInt64(1, eventTime);
        cos.writeBytes(2, ByteString.copyFromUtf8(eventType));
        cos.writeTag(3, 2);
        cos.writeRawVarint32(getEventAppSize(exception, exceptionThread, exceptionStack, otherThreads, otherStacks, 8, packageNameBytes, optionalBuildIdBytes, customAttributes, runningAppProcessInfo, orientation));
        cos.writeTag(1, 2);
        cos.writeRawVarint32(getEventAppExecutionSize(exception, exceptionThread, exceptionStack, otherThreads, otherStacks, 8, packageNameBytes, optionalBuildIdBytes));
        writeThread(cos, exceptionThread, exceptionStack, 4, true);
        int length = otherThreads.length;
        for (eventDeviceSize = 0; eventDeviceSize < length; eventDeviceSize++) {
            writeThread(cos, otherThreads[eventDeviceSize], (StackTraceElement[]) otherStacks.get(eventDeviceSize), 0, false);
        }
        writeSessionEventAppExecutionException(cos, exception, 1, 8, 2);
        cos.writeTag(3, 2);
        cos.writeRawVarint32(getEventAppExecutionSignalSize());
        cos.writeBytes(1, SIGNAL_DEFAULT_BYTE_STRING);
        cos.writeBytes(2, SIGNAL_DEFAULT_BYTE_STRING);
        cos.writeUInt64(3, 0);
        cos.writeTag(4, 2);
        cos.writeRawVarint32(getBinaryImageSize(packageNameBytes, optionalBuildIdBytes));
        cos.writeUInt64(1, 0);
        cos.writeUInt64(2, 0);
        cos.writeBytes(3, packageNameBytes);
        if (optionalBuildIdBytes != null) {
            cos.writeBytes(4, optionalBuildIdBytes);
        }
        if (!(customAttributes == null || customAttributes.isEmpty())) {
            for (Entry entry : customAttributes.entrySet()) {
                cos.writeTag(2, 2);
                cos.writeRawVarint32(getEventAppCustomAttributeSize((String) entry.getKey(), (String) entry.getValue()));
                cos.writeBytes(1, ByteString.copyFromUtf8((String) entry.getKey()));
                String str = (String) entry.getValue();
                if (str == null) {
                    str = "";
                }
                cos.writeBytes(2, ByteString.copyFromUtf8(str));
            }
        }
        if (runningAppProcessInfo != null) {
            cos.writeBool(3, runningAppProcessInfo.importance != 100);
        }
        cos.writeUInt32(4, orientation);
        cos.writeTag(5, 2);
        cos.writeRawVarint32(getEventDeviceSize(batteryLevel, batteryVelocity, proximityEnabled, orientation, usedRamInBytes, diskUsedInBytes));
        if (batteryLevel != null) {
            cos.writeFloat(1, batteryLevel.floatValue());
        }
        cos.writeSInt32(2, batteryVelocity);
        cos.writeBool(3, proximityEnabled);
        cos.writeUInt32(4, orientation);
        cos.writeUInt64(5, usedRamInBytes);
        cos.writeUInt64(6, diskUsedInBytes);
        if (logByteString != null) {
            cos.writeTag(6, 2);
            cos.writeRawVarint32(CodedOutputStream.computeBytesSize(1, logByteString));
            cos.writeBytes(1, logByteString);
        }
    }

    private static void writeSessionEventAppExecutionException(CodedOutputStream cos, TrimmedThrowableData exception, int chainDepth, int maxChainedExceptionsDepth, int field) throws Exception {
        cos.writeTag(field, 2);
        cos.writeRawVarint32(getEventAppExecutionExceptionSize(exception, 1, maxChainedExceptionsDepth));
        cos.writeBytes(1, ByteString.copyFromUtf8(exception.className));
        String message = exception.localizedMessage;
        if (message != null) {
            cos.writeBytes(3, ByteString.copyFromUtf8(message));
        }
        for (StackTraceElement element : exception.stacktrace) {
            writeFrame(cos, 4, element, true);
        }
        TrimmedThrowableData cause = exception.cause;
        if (cause == null) {
            return;
        }
        if (chainDepth < maxChainedExceptionsDepth) {
            writeSessionEventAppExecutionException(cos, cause, chainDepth + 1, maxChainedExceptionsDepth, 6);
            return;
        }
        int overflowCount = 0;
        while (cause != null) {
            cause = cause.cause;
            overflowCount++;
        }
        cos.writeUInt32(7, overflowCount);
    }

    private static void writeThread(CodedOutputStream cos, Thread thread, StackTraceElement[] stackTraceElements, int importance, boolean isCrashedThread) throws Exception {
        cos.writeTag(1, 2);
        cos.writeRawVarint32(getThreadSize(thread, stackTraceElements, importance, isCrashedThread));
        cos.writeBytes(1, ByteString.copyFromUtf8(thread.getName()));
        cos.writeUInt32(2, importance);
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            writeFrame(cos, 3, stackTraceElement, isCrashedThread);
        }
    }

    private static void writeFrame(CodedOutputStream cos, int fieldIndex, StackTraceElement element, boolean isCrashedThread) throws Exception {
        int i = 4;
        cos.writeTag(fieldIndex, 2);
        cos.writeRawVarint32(getFrameSize(element, isCrashedThread));
        if (element.isNativeMethod()) {
            cos.writeUInt64(1, (long) Math.max(element.getLineNumber(), 0));
        } else {
            cos.writeUInt64(1, 0);
        }
        cos.writeBytes(2, ByteString.copyFromUtf8(element.getClassName() + "." + element.getMethodName()));
        if (element.getFileName() != null) {
            cos.writeBytes(3, ByteString.copyFromUtf8(element.getFileName()));
        }
        if (!element.isNativeMethod() && element.getLineNumber() > 0) {
            cos.writeUInt64(4, (long) element.getLineNumber());
        }
        if (!isCrashedThread) {
            i = 0;
        }
        cos.writeUInt32(5, i);
    }

    private static int getSessionAppOrgSize(ByteString apiKey) {
        return CodedOutputStream.computeBytesSize(1, apiKey) + 0;
    }

    private static int getDeviceIdentifierSize(DeviceIdentifierType type, String value) {
        return CodedOutputStream.computeEnumSize(1, type.protobufIndex) + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(value));
    }

    private static int getBinaryImageSize(ByteString packageNameBytes, ByteString optionalBuildIdBytes) {
        int size = ((CodedOutputStream.computeUInt64Size(1, 0) + 0) + CodedOutputStream.computeUInt64Size(2, 0)) + CodedOutputStream.computeBytesSize(3, packageNameBytes);
        if (optionalBuildIdBytes != null) {
            return size + CodedOutputStream.computeBytesSize(4, optionalBuildIdBytes);
        }
        return size;
    }

    private static int getEventAppSize(TrimmedThrowableData exception, Thread exceptionThread, StackTraceElement[] exceptionStack, Thread[] otherThreads, List<StackTraceElement[]> otherStacks, int maxChainedExceptionsDepth, ByteString packageNameBytes, ByteString optionalBuildIdBytes, Map<String, String> customAttributes, RunningAppProcessInfo runningAppProcessInfo, int orientation) {
        int executionSize = getEventAppExecutionSize(exception, exceptionThread, exceptionStack, otherThreads, otherStacks, maxChainedExceptionsDepth, packageNameBytes, optionalBuildIdBytes);
        int size = ((CodedOutputStream.computeTagSize(1) + CodedOutputStream.computeRawVarint32Size(executionSize)) + executionSize) + 0;
        if (customAttributes != null) {
            for (Entry<String, String> entry : customAttributes.entrySet()) {
                int entrySize = getEventAppCustomAttributeSize((String) entry.getKey(), (String) entry.getValue());
                size += (CodedOutputStream.computeTagSize(2) + CodedOutputStream.computeRawVarint32Size(entrySize)) + entrySize;
            }
        }
        if (runningAppProcessInfo != null) {
            size += CodedOutputStream.computeBoolSize(3, runningAppProcessInfo.importance != 100);
        }
        return size + CodedOutputStream.computeUInt32Size(4, orientation);
    }

    private static int getEventAppExecutionSize(TrimmedThrowableData exception, Thread exceptionThread, StackTraceElement[] exceptionStack, Thread[] otherThreads, List<StackTraceElement[]> otherStacks, int maxChainedExceptionDepth, ByteString packageNameBytes, ByteString optionalBuildIdBytes) {
        int threadSize = getThreadSize(exceptionThread, exceptionStack, 4, true);
        int size = ((CodedOutputStream.computeTagSize(1) + CodedOutputStream.computeRawVarint32Size(threadSize)) + threadSize) + 0;
        int len = otherThreads.length;
        for (int i = 0; i < len; i++) {
            threadSize = getThreadSize(otherThreads[i], (StackTraceElement[]) otherStacks.get(i), 0, false);
            size += (CodedOutputStream.computeTagSize(1) + CodedOutputStream.computeRawVarint32Size(threadSize)) + threadSize;
        }
        int exceptionSize = getEventAppExecutionExceptionSize(exception, 1, maxChainedExceptionDepth);
        size += (CodedOutputStream.computeTagSize(2) + CodedOutputStream.computeRawVarint32Size(exceptionSize)) + exceptionSize;
        int signalSize = getEventAppExecutionSignalSize();
        size += (CodedOutputStream.computeTagSize(3) + CodedOutputStream.computeRawVarint32Size(signalSize)) + signalSize;
        int binaryImageSize = getBinaryImageSize(packageNameBytes, optionalBuildIdBytes);
        return size + ((CodedOutputStream.computeTagSize(3) + CodedOutputStream.computeRawVarint32Size(binaryImageSize)) + binaryImageSize);
    }

    private static int getEventAppCustomAttributeSize(String key, String value) {
        int size = CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(key));
        if (value == null) {
            value = "";
        }
        return size + CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(value));
    }

    private static int getEventDeviceSize(Float batteryLevel, int batteryVelocity, boolean proximityEnabled, int orientation, long heapAllocatedSize, long diskUsed) {
        int size = 0;
        if (batteryLevel != null) {
            size = CodedOutputStream.computeFloatSize(1, batteryLevel.floatValue()) + 0;
        }
        return ((((size + CodedOutputStream.computeSInt32Size(2, batteryVelocity)) + CodedOutputStream.computeBoolSize(3, proximityEnabled)) + CodedOutputStream.computeUInt32Size(4, orientation)) + CodedOutputStream.computeUInt64Size(5, heapAllocatedSize)) + CodedOutputStream.computeUInt64Size(6, diskUsed);
    }

    private static int getEventAppExecutionExceptionSize(TrimmedThrowableData ex, int chainDepth, int maxChainedExceptionsDepth) {
        int size = CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(ex.className)) + 0;
        String message = ex.localizedMessage;
        if (message != null) {
            size += CodedOutputStream.computeBytesSize(3, ByteString.copyFromUtf8(message));
        }
        for (StackTraceElement element : ex.stacktrace) {
            int frameSize = getFrameSize(element, true);
            size += (CodedOutputStream.computeTagSize(4) + CodedOutputStream.computeRawVarint32Size(frameSize)) + frameSize;
        }
        TrimmedThrowableData cause = ex.cause;
        if (cause == null) {
            return size;
        }
        if (chainDepth < maxChainedExceptionsDepth) {
            int exceptionSize = getEventAppExecutionExceptionSize(cause, chainDepth + 1, maxChainedExceptionsDepth);
            return size + ((CodedOutputStream.computeTagSize(6) + CodedOutputStream.computeRawVarint32Size(exceptionSize)) + exceptionSize);
        }
        int overflowCount = 0;
        while (cause != null) {
            cause = cause.cause;
            overflowCount++;
        }
        return size + CodedOutputStream.computeUInt32Size(7, overflowCount);
    }

    private static int getEventAppExecutionSignalSize() {
        return ((CodedOutputStream.computeBytesSize(1, SIGNAL_DEFAULT_BYTE_STRING) + 0) + CodedOutputStream.computeBytesSize(2, SIGNAL_DEFAULT_BYTE_STRING)) + CodedOutputStream.computeUInt64Size(3, 0);
    }

    private static int getFrameSize(StackTraceElement element, boolean isCrashedThread) {
        int size;
        int i = 2;
        if (element.isNativeMethod()) {
            size = CodedOutputStream.computeUInt64Size(1, (long) Math.max(element.getLineNumber(), 0)) + 0;
        } else {
            size = CodedOutputStream.computeUInt64Size(1, 0) + 0;
        }
        size += CodedOutputStream.computeBytesSize(2, ByteString.copyFromUtf8(element.getClassName() + "." + element.getMethodName()));
        if (element.getFileName() != null) {
            size += CodedOutputStream.computeBytesSize(3, ByteString.copyFromUtf8(element.getFileName()));
        }
        if (!element.isNativeMethod() && element.getLineNumber() > 0) {
            size += CodedOutputStream.computeUInt64Size(4, (long) element.getLineNumber());
        }
        if (!isCrashedThread) {
            i = 0;
        }
        return size + CodedOutputStream.computeUInt32Size(5, i);
    }

    private static int getThreadSize(Thread thread, StackTraceElement[] stackTraceElements, int importance, boolean isCrashedThread) {
        int size = CodedOutputStream.computeBytesSize(1, ByteString.copyFromUtf8(thread.getName())) + CodedOutputStream.computeUInt32Size(2, importance);
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            int frameSize = getFrameSize(stackTraceElement, isCrashedThread);
            size += (CodedOutputStream.computeTagSize(3) + CodedOutputStream.computeRawVarint32Size(frameSize)) + frameSize;
        }
        return size;
    }

    private static ByteString stringToByteString(String s) {
        return s == null ? null : ByteString.copyFromUtf8(s);
    }
}
