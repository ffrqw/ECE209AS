package com.squareup.picasso;

import android.graphics.Bitmap.Config;
import android.net.Uri;
import com.squareup.picasso.Picasso.Priority;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class Request {
    private static final long TOO_LONG_LOG = TimeUnit.SECONDS.toNanos(5);
    public final boolean centerCrop;
    public final int centerCropGravity;
    public final boolean centerInside;
    public final Config config;
    public final boolean hasRotationPivot;
    int id;
    int networkPolicy;
    public final boolean onlyScaleDown;
    public final int priority$159b5429;
    public final boolean purgeable;
    public final int resourceId;
    public final float rotationDegrees;
    public final float rotationPivotX;
    public final float rotationPivotY;
    public final String stableKey;
    long started;
    public final int targetHeight;
    public final int targetWidth;
    public final List<Transformation> transformations;
    public final Uri uri;

    public static final class Builder {
        private boolean centerInside;
        private Config config;
        private int priority$159b5429;
        private int resourceId;
        private int targetHeight;
        private int targetWidth;
        private Uri uri;

        Builder(Uri uri, int resourceId, Config bitmapConfig) {
            this.uri = uri;
            this.resourceId = resourceId;
            this.config = bitmapConfig;
        }

        final boolean hasImage() {
            return (this.uri == null && this.resourceId == 0) ? false : true;
        }

        final boolean hasSize() {
            return (this.targetWidth == 0 && this.targetHeight == 0) ? false : true;
        }

        final boolean hasPriority() {
            return this.priority$159b5429 != 0;
        }

        public final Builder resize(int targetWidth, int targetHeight) {
            if (targetWidth < 0) {
                throw new IllegalArgumentException("Width must be positive number or 0.");
            } else if (targetHeight < 0) {
                throw new IllegalArgumentException("Height must be positive number or 0.");
            } else if (targetHeight == 0 && targetWidth == 0) {
                throw new IllegalArgumentException("At least one dimension has to be positive number.");
            } else {
                this.targetWidth = targetWidth;
                this.targetHeight = targetHeight;
                return this;
            }
        }

        public final Builder centerInside() {
            this.centerInside = true;
            return this;
        }

        public final Builder priority$8880afd(int priority) {
            if (priority == 0) {
                throw new IllegalArgumentException("Priority invalid.");
            } else if (this.priority$159b5429 != 0) {
                throw new IllegalStateException("Priority already set.");
            } else {
                this.priority$159b5429 = priority;
                return this;
            }
        }

        public final Request build() {
            if (this.centerInside && this.targetWidth == 0 && this.targetHeight == 0) {
                throw new IllegalStateException("Center inside requires calling resize with positive width and height.");
            }
            if (this.priority$159b5429 == 0) {
                this.priority$159b5429 = Priority.NORMAL$159b5429;
            }
            return new Request(this.uri, this.resourceId, null, null, this.targetWidth, this.targetHeight, false, this.centerInside, 0, false, 0.0f, 0.0f, 0.0f, false, false, this.config, this.priority$159b5429);
        }
    }

    private Request(Uri uri, int resourceId, String stableKey, List<Transformation> transformations, int targetWidth, int targetHeight, boolean centerCrop, boolean centerInside, int centerCropGravity, boolean onlyScaleDown, float rotationDegrees, float rotationPivotX, float rotationPivotY, boolean hasRotationPivot, boolean purgeable, Config config, int priority) {
        this.uri = uri;
        this.resourceId = resourceId;
        this.stableKey = stableKey;
        if (transformations == null) {
            this.transformations = null;
        } else {
            this.transformations = Collections.unmodifiableList(transformations);
        }
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.centerCrop = centerCrop;
        this.centerInside = centerInside;
        this.centerCropGravity = centerCropGravity;
        this.onlyScaleDown = onlyScaleDown;
        this.rotationDegrees = rotationDegrees;
        this.rotationPivotX = rotationPivotX;
        this.rotationPivotY = rotationPivotY;
        this.hasRotationPivot = hasRotationPivot;
        this.purgeable = purgeable;
        this.config = config;
        this.priority$159b5429 = priority;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder("Request{");
        if (this.resourceId > 0) {
            builder.append(this.resourceId);
        } else {
            builder.append(this.uri);
        }
        if (!(this.transformations == null || this.transformations.isEmpty())) {
            for (Transformation transformation : this.transformations) {
                builder.append(' ').append(transformation.key());
            }
        }
        if (this.stableKey != null) {
            builder.append(" stableKey(").append(this.stableKey).append(')');
        }
        if (this.targetWidth > 0) {
            builder.append(" resize(").append(this.targetWidth).append(',').append(this.targetHeight).append(')');
        }
        if (this.centerCrop) {
            builder.append(" centerCrop");
        }
        if (this.centerInside) {
            builder.append(" centerInside");
        }
        if (this.rotationDegrees != 0.0f) {
            builder.append(" rotation(").append(this.rotationDegrees);
            if (this.hasRotationPivot) {
                builder.append(" @ ").append(this.rotationPivotX).append(',').append(this.rotationPivotY);
            }
            builder.append(')');
        }
        if (this.purgeable) {
            builder.append(" purgeable");
        }
        if (this.config != null) {
            builder.append(' ').append(this.config);
        }
        builder.append('}');
        return builder.toString();
    }

    final String logId() {
        long delta = System.nanoTime() - this.started;
        if (delta > TOO_LONG_LOG) {
            return plainId() + '+' + TimeUnit.NANOSECONDS.toSeconds(delta) + 's';
        }
        return plainId() + '+' + TimeUnit.NANOSECONDS.toMillis(delta) + "ms";
    }

    final String plainId() {
        return "[R" + this.id + ']';
    }

    public final boolean hasSize() {
        return (this.targetWidth == 0 && this.targetHeight == 0) ? false : true;
    }

    final boolean needsMatrixTransform() {
        return hasSize() || this.rotationDegrees != 0.0f;
    }

    final boolean hasCustomTransformations() {
        return this.transformations != null;
    }
}
