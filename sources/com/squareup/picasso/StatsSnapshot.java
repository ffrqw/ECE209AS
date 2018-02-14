package com.squareup.picasso;

public final class StatsSnapshot {
    public final long averageDownloadSize;
    public final long averageOriginalBitmapSize;
    public final long averageTransformedBitmapSize;
    public final long cacheHits;
    public final long cacheMisses;
    public final int downloadCount;
    public final int maxSize;
    public final int originalBitmapCount;
    public final int size;
    public final long timeStamp;
    public final long totalDownloadSize;
    public final long totalOriginalBitmapSize;
    public final long totalTransformedBitmapSize;
    public final int transformedBitmapCount;

    public StatsSnapshot(int maxSize, int size, long cacheHits, long cacheMisses, long totalDownloadSize, long totalOriginalBitmapSize, long totalTransformedBitmapSize, long averageDownloadSize, long averageOriginalBitmapSize, long averageTransformedBitmapSize, int downloadCount, int originalBitmapCount, int transformedBitmapCount, long timeStamp) {
        this.maxSize = maxSize;
        this.size = size;
        this.cacheHits = cacheHits;
        this.cacheMisses = cacheMisses;
        this.totalDownloadSize = totalDownloadSize;
        this.totalOriginalBitmapSize = totalOriginalBitmapSize;
        this.totalTransformedBitmapSize = totalTransformedBitmapSize;
        this.averageDownloadSize = averageDownloadSize;
        this.averageOriginalBitmapSize = averageOriginalBitmapSize;
        this.averageTransformedBitmapSize = averageTransformedBitmapSize;
        this.downloadCount = downloadCount;
        this.originalBitmapCount = originalBitmapCount;
        this.transformedBitmapCount = transformedBitmapCount;
        this.timeStamp = timeStamp;
    }

    public final String toString() {
        return "StatsSnapshot{maxSize=" + this.maxSize + ", size=" + this.size + ", cacheHits=" + this.cacheHits + ", cacheMisses=" + this.cacheMisses + ", downloadCount=" + this.downloadCount + ", totalDownloadSize=" + this.totalDownloadSize + ", averageDownloadSize=" + this.averageDownloadSize + ", totalOriginalBitmapSize=" + this.totalOriginalBitmapSize + ", totalTransformedBitmapSize=" + this.totalTransformedBitmapSize + ", averageOriginalBitmapSize=" + this.averageOriginalBitmapSize + ", averageTransformedBitmapSize=" + this.averageTransformedBitmapSize + ", originalBitmapCount=" + this.originalBitmapCount + ", transformedBitmapCount=" + this.transformedBitmapCount + ", timeStamp=" + this.timeStamp + '}';
    }
}
