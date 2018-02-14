package io.fabric.sdk.android.services.events;

import android.content.Context;
import io.fabric.sdk.android.services.common.CommonUtils;
import io.fabric.sdk.android.services.common.CurrentTimeProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class EventsFilesManager<T> {
    protected final Context context;
    protected final CurrentTimeProvider currentTimeProvider;
    private final int defaultMaxFilesToKeep;
    protected final EventsStorage eventStorage;
    protected volatile long lastRollOverTime;
    protected final List<EventsStorageListener> rollOverListeners = new CopyOnWriteArrayList();
    protected final EventTransform<T> transform;

    static class FileWithTimestamp {
        final File file;
        final long timestamp;

        public FileWithTimestamp(File file, long timestamp) {
            this.file = file;
            this.timestamp = timestamp;
        }
    }

    protected abstract String generateUniqueRollOverFileName();

    public EventsFilesManager(Context context, EventTransform<T> transform, CurrentTimeProvider currentTimeProvider, EventsStorage eventStorage, int defaultMaxFilesToKeep) throws IOException {
        this.context = context.getApplicationContext();
        this.transform = transform;
        this.eventStorage = eventStorage;
        this.currentTimeProvider = currentTimeProvider;
        this.lastRollOverTime = this.currentTimeProvider.getCurrentTimeMillis();
        this.defaultMaxFilesToKeep = 100;
    }

    public final void writeEvent(T event) throws IOException {
        byte[] eventBytes = this.transform.toBytes(event);
        if (!this.eventStorage.canWorkingFileStore(eventBytes.length, getMaxByteSizePerFile())) {
            CommonUtils.logControlled$3aaf2084(this.context, 4, String.format(Locale.US, "session analytics events file is %d bytes, new event is %d bytes, this is over flush limit of %d, rolling it over", new Object[]{Integer.valueOf(this.eventStorage.getWorkingFileUsedSizeInBytes()), Integer.valueOf(eventBytes.length), Integer.valueOf(getMaxByteSizePerFile())}));
            rollFileOver();
        }
        this.eventStorage.add(eventBytes);
    }

    public final void registerRollOverListener(EventsStorageListener listener) {
        if (listener != null) {
            this.rollOverListeners.add(listener);
        }
    }

    public final boolean rollFileOver() throws IOException {
        boolean fileRolledOver = false;
        if (!this.eventStorage.isWorkingFileEmpty()) {
            this.eventStorage.rollOver(generateUniqueRollOverFileName());
            CommonUtils.logControlled$3aaf2084(this.context, 4, String.format(Locale.US, "generated new file %s", new Object[]{targetFileName}));
            this.lastRollOverTime = this.currentTimeProvider.getCurrentTimeMillis();
            fileRolledOver = true;
        }
        for (EventsStorageListener onRollOver$552c4e01 : this.rollOverListeners) {
            try {
                onRollOver$552c4e01.onRollOver$552c4e01();
            } catch (Exception e) {
                CommonUtils.logControlledError$43da9ce8(this.context, "One of the roll over listeners threw an exception");
            }
        }
        return fileRolledOver;
    }

    protected int getMaxFilesToKeep() {
        return this.defaultMaxFilesToKeep;
    }

    protected int getMaxByteSizePerFile() {
        return 8000;
    }

    public final List<File> getBatchOfFilesToSend() {
        return this.eventStorage.getBatchOfFilesToSend(1);
    }

    public final void deleteSentFiles(List<File> files) {
        this.eventStorage.deleteFilesInRollOverDirectory(files);
    }

    public final void deleteAllEventsFiles() {
        this.eventStorage.deleteFilesInRollOverDirectory(this.eventStorage.getAllFilesInRollOverDirectory());
        this.eventStorage.deleteWorkingFile();
    }

    public final void deleteOldestInRollOverIfOverMax() {
        List<File> allFiles = this.eventStorage.getAllFilesInRollOverDirectory();
        int maxFiles = getMaxFilesToKeep();
        if (allFiles.size() > maxFiles) {
            int numberOfFilesToDelete = allFiles.size() - maxFiles;
            CommonUtils.logControlled(this.context, String.format(Locale.US, "Found %d files in  roll over directory, this is greater than %d, deleting %d oldest files", new Object[]{Integer.valueOf(allFiles.size()), Integer.valueOf(maxFiles), Integer.valueOf(numberOfFilesToDelete)}));
            TreeSet<FileWithTimestamp> sortedFiles = new TreeSet(new Comparator<FileWithTimestamp>() {
                public final /* bridge */ /* synthetic */ int compare(Object obj, Object obj2) {
                    return (int) (((FileWithTimestamp) obj).timestamp - ((FileWithTimestamp) obj2).timestamp);
                }
            });
            for (File file : allFiles) {
                sortedFiles.add(new FileWithTimestamp(file, parseCreationTimestampFromFileName(file.getName())));
            }
            ArrayList<File> toDelete = new ArrayList();
            Iterator it = sortedFiles.iterator();
            while (it.hasNext()) {
                toDelete.add(((FileWithTimestamp) it.next()).file);
                if (toDelete.size() == numberOfFilesToDelete) {
                    break;
                }
            }
            this.eventStorage.deleteFilesInRollOverDirectory(toDelete);
        }
    }

    private static long parseCreationTimestampFromFileName(String fileName) {
        long j = 0;
        String[] fileNameParts = fileName.split("_");
        if (fileNameParts.length == 3) {
            try {
                j = Long.valueOf(fileNameParts[2]).longValue();
            } catch (NumberFormatException e) {
            }
        }
        return j;
    }
}
