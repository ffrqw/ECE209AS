package io.fabric.sdk.android.services.events;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.QueueFile;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueueFileEventStorage implements EventsStorage {
    private final Context context;
    private QueueFile queueFile = new QueueFile(this.workingFile);
    private File targetDirectory = new File(this.workingDirectory, this.targetDirectoryName);
    private final String targetDirectoryName;
    private final File workingDirectory;
    private final File workingFile;

    public QueueFileEventStorage(Context context, File workingDirectory, String workingFileName, String targetDirectoryName) throws IOException {
        this.context = context;
        this.workingDirectory = workingDirectory;
        this.targetDirectoryName = targetDirectoryName;
        this.workingFile = new File(this.workingDirectory, workingFileName);
        if (!this.targetDirectory.exists()) {
            this.targetDirectory.mkdirs();
        }
    }

    public final void add(byte[] data) throws IOException {
        this.queueFile.add(data);
    }

    public final int getWorkingFileUsedSizeInBytes() {
        return this.queueFile.usedBytes();
    }

    public final void rollOver(String targetName) throws IOException {
        Throwable th;
        Closeable closeable = null;
        this.queueFile.close();
        File file = this.workingFile;
        File file2 = new File(this.targetDirectory, targetName);
        Closeable fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
            try {
                closeable = getMoveOutputStream(file2);
                CommonUtils.copyStream(fileInputStream, closeable, new byte[1024]);
                CommonUtils.closeOrLog(fileInputStream, "Failed to close file input stream");
                CommonUtils.closeOrLog(closeable, "Failed to close output stream");
                file.delete();
                this.queueFile = new QueueFile(this.workingFile);
            } catch (Throwable th2) {
                th = th2;
                CommonUtils.closeOrLog(fileInputStream, "Failed to close file input stream");
                CommonUtils.closeOrLog(closeable, "Failed to close output stream");
                file.delete();
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            fileInputStream = null;
            CommonUtils.closeOrLog(fileInputStream, "Failed to close file input stream");
            CommonUtils.closeOrLog(closeable, "Failed to close output stream");
            file.delete();
            throw th;
        }
    }

    public OutputStream getMoveOutputStream(File targetFile) throws IOException {
        return new FileOutputStream(targetFile);
    }

    public final List<File> getBatchOfFilesToSend(int maxBatchSize) {
        List<File> batch = new ArrayList();
        for (File file : this.targetDirectory.listFiles()) {
            batch.add(file);
            if (batch.size() > 0) {
                break;
            }
        }
        return batch;
    }

    public final void deleteFilesInRollOverDirectory(List<File> files) {
        for (File file : files) {
            CommonUtils.logControlled(this.context, String.format("deleting sent analytics file %s", new Object[]{file.getName()}));
            file.delete();
        }
    }

    public final List<File> getAllFilesInRollOverDirectory() {
        return Arrays.asList(this.targetDirectory.listFiles());
    }

    public final void deleteWorkingFile() {
        try {
            this.queueFile.close();
        } catch (IOException e) {
        }
        this.workingFile.delete();
    }

    public final boolean isWorkingFileEmpty() {
        return this.queueFile.isEmpty();
    }

    public final boolean canWorkingFileStore(int newEventSizeInBytes, int maxByteSizePerFile) {
        return this.queueFile.hasSpaceFor(newEventSizeInBytes, maxByteSizePerFile);
    }
}
