package android.support.graphics.drawable;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.VectorDrawable;
import android.os.Build.VERSION;
import android.support.graphics.drawable.PathParser.PathDataNode;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class VectorDrawableCompat extends VectorDrawableCommon {
    static final Mode DEFAULT_TINT_MODE = Mode.SRC_IN;
    private boolean mAllowCaching;
    private ConstantState mCachedConstantStateDelegate;
    private ColorFilter mColorFilter;
    private boolean mMutated;
    private PorterDuffColorFilter mTintFilter;
    private final Rect mTmpBounds;
    private final float[] mTmpFloats;
    private final Matrix mTmpMatrix;
    private VectorDrawableCompatState mVectorState;

    private static class VPath {
        int mChangingConfigurations;
        protected PathDataNode[] mNodes = null;
        String mPathName;

        public VPath(VPath copy) {
            this.mPathName = copy.mPathName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            this.mNodes = PathParser.deepCopyNodes(copy.mNodes);
        }

        public final void toPath(Path path) {
            path.reset();
            if (this.mNodes != null) {
                PathDataNode.nodesToPath(this.mNodes, path);
            }
        }

        public String getPathName() {
            return this.mPathName;
        }

        public boolean isClipPath() {
            return false;
        }

        public PathDataNode[] getPathData() {
            return this.mNodes;
        }

        public void setPathData(PathDataNode[] nodes) {
            Object obj;
            int i;
            PathDataNode[] pathDataNodeArr = this.mNodes;
            if (pathDataNodeArr == null || nodes == null) {
                obj = null;
            } else if (pathDataNodeArr.length != nodes.length) {
                obj = null;
            } else {
                i = 0;
                while (i < pathDataNodeArr.length) {
                    if (pathDataNodeArr[i].type != nodes[i].type || pathDataNodeArr[i].params.length != nodes[i].params.length) {
                        obj = null;
                        break;
                    }
                    i++;
                }
                obj = 1;
            }
            if (obj == null) {
                this.mNodes = PathParser.deepCopyNodes(nodes);
                return;
            }
            PathDataNode[] pathDataNodeArr2 = this.mNodes;
            for (i = 0; i < nodes.length; i++) {
                pathDataNodeArr2[i].type = nodes[i].type;
                for (int i2 = 0; i2 < nodes[i].params.length; i2++) {
                    pathDataNodeArr2[i].params[i2] = nodes[i].params[i2];
                }
            }
        }
    }

    private static class VClipPath extends VPath {
        public VClipPath(VClipPath copy) {
            super(copy);
        }

        public final void inflate(Resources r, AttributeSet attrs, Theme theme, XmlPullParser parser) {
            if (TypedArrayUtils.hasAttribute(parser, "pathData")) {
                TypedArray a = VectorDrawableCommon.obtainAttributes(r, theme, attrs, AndroidResources.styleable_VectorDrawableClipPath);
                String string = a.getString(0);
                if (string != null) {
                    this.mPathName = string;
                }
                string = a.getString(1);
                if (string != null) {
                    this.mNodes = PathParser.createNodesFromPathData(string);
                }
                a.recycle();
            }
        }

        public final boolean isClipPath() {
            return true;
        }
    }

    private static class VFullPath extends VPath {
        float mFillAlpha = 1.0f;
        int mFillColor = 0;
        int mFillRule;
        float mStrokeAlpha = 1.0f;
        int mStrokeColor = 0;
        Cap mStrokeLineCap = Cap.BUTT;
        Join mStrokeLineJoin = Join.MITER;
        float mStrokeMiterlimit = 4.0f;
        float mStrokeWidth = 0.0f;
        private int[] mThemeAttrs;
        float mTrimPathEnd = 1.0f;
        float mTrimPathOffset = 0.0f;
        float mTrimPathStart = 0.0f;

        public VFullPath(VFullPath copy) {
            super(copy);
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mStrokeColor = copy.mStrokeColor;
            this.mStrokeWidth = copy.mStrokeWidth;
            this.mStrokeAlpha = copy.mStrokeAlpha;
            this.mFillColor = copy.mFillColor;
            this.mFillRule = copy.mFillRule;
            this.mFillAlpha = copy.mFillAlpha;
            this.mTrimPathStart = copy.mTrimPathStart;
            this.mTrimPathEnd = copy.mTrimPathEnd;
            this.mTrimPathOffset = copy.mTrimPathOffset;
            this.mStrokeLineCap = copy.mStrokeLineCap;
            this.mStrokeLineJoin = copy.mStrokeLineJoin;
            this.mStrokeMiterlimit = copy.mStrokeMiterlimit;
        }

        public final void inflate(Resources r, AttributeSet attrs, Theme theme, XmlPullParser parser) {
            TypedArray a = VectorDrawableCommon.obtainAttributes(r, theme, attrs, AndroidResources.styleable_VectorDrawablePath);
            this.mThemeAttrs = null;
            if (TypedArrayUtils.hasAttribute(parser, "pathData")) {
                String string = a.getString(0);
                if (string != null) {
                    this.mPathName = string;
                }
                string = a.getString(2);
                if (string != null) {
                    this.mNodes = PathParser.createNodesFromPathData(string);
                }
                this.mFillColor = TypedArrayUtils.getNamedColor(a, parser, "fillColor", 1, this.mFillColor);
                this.mFillAlpha = TypedArrayUtils.getNamedFloat(a, parser, "fillAlpha", 12, this.mFillAlpha);
                int namedInt = TypedArrayUtils.getNamedInt(a, parser, "strokeLineCap", 8, -1);
                Cap cap = this.mStrokeLineCap;
                switch (namedInt) {
                    case 0:
                        cap = Cap.BUTT;
                        break;
                    case 1:
                        cap = Cap.ROUND;
                        break;
                    case 2:
                        cap = Cap.SQUARE;
                        break;
                }
                this.mStrokeLineCap = cap;
                namedInt = TypedArrayUtils.getNamedInt(a, parser, "strokeLineJoin", 9, -1);
                Join join = this.mStrokeLineJoin;
                switch (namedInt) {
                    case 0:
                        join = Join.MITER;
                        break;
                    case 1:
                        join = Join.ROUND;
                        break;
                    case 2:
                        join = Join.BEVEL;
                        break;
                }
                this.mStrokeLineJoin = join;
                this.mStrokeMiterlimit = TypedArrayUtils.getNamedFloat(a, parser, "strokeMiterLimit", 10, this.mStrokeMiterlimit);
                this.mStrokeColor = TypedArrayUtils.getNamedColor(a, parser, "strokeColor", 3, this.mStrokeColor);
                this.mStrokeAlpha = TypedArrayUtils.getNamedFloat(a, parser, "strokeAlpha", 11, this.mStrokeAlpha);
                this.mStrokeWidth = TypedArrayUtils.getNamedFloat(a, parser, "strokeWidth", 4, this.mStrokeWidth);
                this.mTrimPathEnd = TypedArrayUtils.getNamedFloat(a, parser, "trimPathEnd", 6, this.mTrimPathEnd);
                this.mTrimPathOffset = TypedArrayUtils.getNamedFloat(a, parser, "trimPathOffset", 7, this.mTrimPathOffset);
                this.mTrimPathStart = TypedArrayUtils.getNamedFloat(a, parser, "trimPathStart", 5, this.mTrimPathStart);
            }
            a.recycle();
        }

        final int getStrokeColor() {
            return this.mStrokeColor;
        }

        final void setStrokeColor(int strokeColor) {
            this.mStrokeColor = strokeColor;
        }

        final float getStrokeWidth() {
            return this.mStrokeWidth;
        }

        final void setStrokeWidth(float strokeWidth) {
            this.mStrokeWidth = strokeWidth;
        }

        final float getStrokeAlpha() {
            return this.mStrokeAlpha;
        }

        final void setStrokeAlpha(float strokeAlpha) {
            this.mStrokeAlpha = strokeAlpha;
        }

        final int getFillColor() {
            return this.mFillColor;
        }

        final void setFillColor(int fillColor) {
            this.mFillColor = fillColor;
        }

        final float getFillAlpha() {
            return this.mFillAlpha;
        }

        final void setFillAlpha(float fillAlpha) {
            this.mFillAlpha = fillAlpha;
        }

        final float getTrimPathStart() {
            return this.mTrimPathStart;
        }

        final void setTrimPathStart(float trimPathStart) {
            this.mTrimPathStart = trimPathStart;
        }

        final float getTrimPathEnd() {
            return this.mTrimPathEnd;
        }

        final void setTrimPathEnd(float trimPathEnd) {
            this.mTrimPathEnd = trimPathEnd;
        }

        final float getTrimPathOffset() {
            return this.mTrimPathOffset;
        }

        final void setTrimPathOffset(float trimPathOffset) {
            this.mTrimPathOffset = trimPathOffset;
        }
    }

    private static class VGroup {
        int mChangingConfigurations;
        final ArrayList<Object> mChildren = new ArrayList();
        private String mGroupName = null;
        private final Matrix mLocalMatrix = new Matrix();
        private float mPivotX = 0.0f;
        private float mPivotY = 0.0f;
        float mRotate = 0.0f;
        private float mScaleX = 1.0f;
        private float mScaleY = 1.0f;
        private final Matrix mStackedMatrix = new Matrix();
        private int[] mThemeAttrs;
        private float mTranslateX = 0.0f;
        private float mTranslateY = 0.0f;

        public VGroup(VGroup copy, ArrayMap<String, Object> targetsMap) {
            this.mRotate = copy.mRotate;
            this.mPivotX = copy.mPivotX;
            this.mPivotY = copy.mPivotY;
            this.mScaleX = copy.mScaleX;
            this.mScaleY = copy.mScaleY;
            this.mTranslateX = copy.mTranslateX;
            this.mTranslateY = copy.mTranslateY;
            this.mThemeAttrs = copy.mThemeAttrs;
            this.mGroupName = copy.mGroupName;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            if (this.mGroupName != null) {
                targetsMap.put(this.mGroupName, this);
            }
            this.mLocalMatrix.set(copy.mLocalMatrix);
            ArrayList<Object> children = copy.mChildren;
            for (int i = 0; i < children.size(); i++) {
                VGroup copyChild = children.get(i);
                if (copyChild instanceof VGroup) {
                    this.mChildren.add(new VGroup(copyChild, targetsMap));
                } else {
                    VPath newPath;
                    if (copyChild instanceof VFullPath) {
                        newPath = new VFullPath((VFullPath) copyChild);
                    } else if (copyChild instanceof VClipPath) {
                        newPath = new VClipPath((VClipPath) copyChild);
                    } else {
                        throw new IllegalStateException("Unknown object in the tree!");
                    }
                    this.mChildren.add(newPath);
                    if (newPath.mPathName != null) {
                        targetsMap.put(newPath.mPathName, newPath);
                    }
                }
            }
        }

        public final String getGroupName() {
            return this.mGroupName;
        }

        public final Matrix getLocalMatrix() {
            return this.mLocalMatrix;
        }

        public final void inflate(Resources res, AttributeSet attrs, Theme theme, XmlPullParser parser) {
            TypedArray a = VectorDrawableCommon.obtainAttributes(res, theme, attrs, AndroidResources.styleable_VectorDrawableGroup);
            this.mThemeAttrs = null;
            this.mRotate = TypedArrayUtils.getNamedFloat(a, parser, "rotation", 5, this.mRotate);
            this.mPivotX = a.getFloat(1, this.mPivotX);
            this.mPivotY = a.getFloat(2, this.mPivotY);
            this.mScaleX = TypedArrayUtils.getNamedFloat(a, parser, "scaleX", 3, this.mScaleX);
            this.mScaleY = TypedArrayUtils.getNamedFloat(a, parser, "scaleY", 4, this.mScaleY);
            this.mTranslateX = TypedArrayUtils.getNamedFloat(a, parser, "translateX", 6, this.mTranslateX);
            this.mTranslateY = TypedArrayUtils.getNamedFloat(a, parser, "translateY", 7, this.mTranslateY);
            String string = a.getString(0);
            if (string != null) {
                this.mGroupName = string;
            }
            updateLocalMatrix();
            a.recycle();
        }

        private void updateLocalMatrix() {
            this.mLocalMatrix.reset();
            this.mLocalMatrix.postTranslate(-this.mPivotX, -this.mPivotY);
            this.mLocalMatrix.postScale(this.mScaleX, this.mScaleY);
            this.mLocalMatrix.postRotate(this.mRotate, 0.0f, 0.0f);
            this.mLocalMatrix.postTranslate(this.mTranslateX + this.mPivotX, this.mTranslateY + this.mPivotY);
        }

        public final float getRotation() {
            return this.mRotate;
        }

        public final void setRotation(float rotation) {
            if (rotation != this.mRotate) {
                this.mRotate = rotation;
                updateLocalMatrix();
            }
        }

        public final float getPivotX() {
            return this.mPivotX;
        }

        public final void setPivotX(float pivotX) {
            if (pivotX != this.mPivotX) {
                this.mPivotX = pivotX;
                updateLocalMatrix();
            }
        }

        public final float getPivotY() {
            return this.mPivotY;
        }

        public final void setPivotY(float pivotY) {
            if (pivotY != this.mPivotY) {
                this.mPivotY = pivotY;
                updateLocalMatrix();
            }
        }

        public final float getScaleX() {
            return this.mScaleX;
        }

        public final void setScaleX(float scaleX) {
            if (scaleX != this.mScaleX) {
                this.mScaleX = scaleX;
                updateLocalMatrix();
            }
        }

        public final float getScaleY() {
            return this.mScaleY;
        }

        public final void setScaleY(float scaleY) {
            if (scaleY != this.mScaleY) {
                this.mScaleY = scaleY;
                updateLocalMatrix();
            }
        }

        public final float getTranslateX() {
            return this.mTranslateX;
        }

        public final void setTranslateX(float translateX) {
            if (translateX != this.mTranslateX) {
                this.mTranslateX = translateX;
                updateLocalMatrix();
            }
        }

        public final float getTranslateY() {
            return this.mTranslateY;
        }

        public final void setTranslateY(float translateY) {
            if (translateY != this.mTranslateY) {
                this.mTranslateY = translateY;
                updateLocalMatrix();
            }
        }
    }

    private static class VPathRenderer {
        private static final Matrix IDENTITY_MATRIX = new Matrix();
        float mBaseHeight;
        float mBaseWidth;
        private int mChangingConfigurations;
        private Paint mFillPaint;
        private final Matrix mFinalPathMatrix;
        private final Path mPath;
        private PathMeasure mPathMeasure;
        private final Path mRenderPath;
        int mRootAlpha;
        final VGroup mRootGroup;
        String mRootName;
        private Paint mStrokePaint;
        final ArrayMap<String, Object> mVGTargetsMap;
        float mViewportHeight;
        float mViewportWidth;

        public VPathRenderer() {
            this.mFinalPathMatrix = new Matrix();
            this.mBaseWidth = 0.0f;
            this.mBaseHeight = 0.0f;
            this.mViewportWidth = 0.0f;
            this.mViewportHeight = 0.0f;
            this.mRootAlpha = 255;
            this.mRootName = null;
            this.mVGTargetsMap = new ArrayMap();
            this.mRootGroup = new VGroup();
            this.mPath = new Path();
            this.mRenderPath = new Path();
        }

        public final void setRootAlpha(int alpha) {
            this.mRootAlpha = alpha;
        }

        public final int getRootAlpha() {
            return this.mRootAlpha;
        }

        public final void setAlpha(float alpha) {
            setRootAlpha((int) (255.0f * alpha));
        }

        public final float getAlpha() {
            return ((float) getRootAlpha()) / 255.0f;
        }

        public VPathRenderer(VPathRenderer copy) {
            this.mFinalPathMatrix = new Matrix();
            this.mBaseWidth = 0.0f;
            this.mBaseHeight = 0.0f;
            this.mViewportWidth = 0.0f;
            this.mViewportHeight = 0.0f;
            this.mRootAlpha = 255;
            this.mRootName = null;
            this.mVGTargetsMap = new ArrayMap();
            this.mRootGroup = new VGroup(copy.mRootGroup, this.mVGTargetsMap);
            this.mPath = new Path(copy.mPath);
            this.mRenderPath = new Path(copy.mRenderPath);
            this.mBaseWidth = copy.mBaseWidth;
            this.mBaseHeight = copy.mBaseHeight;
            this.mViewportWidth = copy.mViewportWidth;
            this.mViewportHeight = copy.mViewportHeight;
            this.mChangingConfigurations = copy.mChangingConfigurations;
            this.mRootAlpha = copy.mRootAlpha;
            this.mRootName = copy.mRootName;
            if (copy.mRootName != null) {
                this.mVGTargetsMap.put(copy.mRootName, this);
            }
        }

        private void drawGroupTree(VGroup currentGroup, Matrix currentMatrix, Canvas canvas, int w, int h, ColorFilter filter) {
            currentGroup.mStackedMatrix.set(currentMatrix);
            currentGroup.mStackedMatrix.preConcat(currentGroup.mLocalMatrix);
            canvas.save();
            for (int i = 0; i < currentGroup.mChildren.size(); i++) {
                VGroup child = currentGroup.mChildren.get(i);
                if (child instanceof VGroup) {
                    drawGroupTree(child, currentGroup.mStackedMatrix, canvas, w, h, filter);
                } else if (child instanceof VPath) {
                    VPath childPath = (VPath) child;
                    float f = ((float) w) / this.mViewportWidth;
                    float f2 = ((float) h) / this.mViewportHeight;
                    float min = Math.min(f, f2);
                    Matrix access$200 = currentGroup.mStackedMatrix;
                    this.mFinalPathMatrix.set(access$200);
                    this.mFinalPathMatrix.postScale(f, f2);
                    float[] fArr = new float[]{0.0f, 1.0f, 1.0f, 0.0f};
                    access$200.mapVectors(fArr);
                    f2 = (float) Math.hypot((double) fArr[0], (double) fArr[1]);
                    float hypot = (float) Math.hypot((double) fArr[2], (double) fArr[3]);
                    float f3 = (fArr[3] * fArr[0]) - (fArr[1] * fArr[2]);
                    f2 = Math.max(f2, hypot);
                    f = 0.0f;
                    if (f2 > 0.0f) {
                        f = Math.abs(f3) / f2;
                    }
                    if (f != 0.0f) {
                        childPath.toPath(this.mPath);
                        Path path = this.mPath;
                        this.mRenderPath.reset();
                        if (childPath.isClipPath()) {
                            this.mRenderPath.addPath(path, this.mFinalPathMatrix);
                            canvas.clipPath(this.mRenderPath);
                        } else {
                            Paint paint;
                            VFullPath vFullPath = (VFullPath) childPath;
                            if (!(vFullPath.mTrimPathStart == 0.0f && vFullPath.mTrimPathEnd == 1.0f)) {
                                hypot = (vFullPath.mTrimPathStart + vFullPath.mTrimPathOffset) % 1.0f;
                                f3 = (vFullPath.mTrimPathEnd + vFullPath.mTrimPathOffset) % 1.0f;
                                if (this.mPathMeasure == null) {
                                    this.mPathMeasure = new PathMeasure();
                                }
                                this.mPathMeasure.setPath(this.mPath, false);
                                float length = this.mPathMeasure.getLength();
                                hypot *= length;
                                f3 *= length;
                                path.reset();
                                if (hypot > f3) {
                                    this.mPathMeasure.getSegment(hypot, length, path, true);
                                    this.mPathMeasure.getSegment(0.0f, f3, path, true);
                                } else {
                                    this.mPathMeasure.getSegment(hypot, f3, path, true);
                                }
                                path.rLineTo(0.0f, 0.0f);
                            }
                            this.mRenderPath.addPath(path, this.mFinalPathMatrix);
                            if (vFullPath.mFillColor != 0) {
                                if (this.mFillPaint == null) {
                                    this.mFillPaint = new Paint();
                                    this.mFillPaint.setStyle(Style.FILL);
                                    this.mFillPaint.setAntiAlias(true);
                                }
                                paint = this.mFillPaint;
                                paint.setColor(VectorDrawableCompat.applyAlpha(vFullPath.mFillColor, vFullPath.mFillAlpha));
                                paint.setColorFilter(filter);
                                canvas.drawPath(this.mRenderPath, paint);
                            }
                            if (vFullPath.mStrokeColor != 0) {
                                if (this.mStrokePaint == null) {
                                    this.mStrokePaint = new Paint();
                                    this.mStrokePaint.setStyle(Style.STROKE);
                                    this.mStrokePaint.setAntiAlias(true);
                                }
                                paint = this.mStrokePaint;
                                if (vFullPath.mStrokeLineJoin != null) {
                                    paint.setStrokeJoin(vFullPath.mStrokeLineJoin);
                                }
                                if (vFullPath.mStrokeLineCap != null) {
                                    paint.setStrokeCap(vFullPath.mStrokeLineCap);
                                }
                                paint.setStrokeMiter(vFullPath.mStrokeMiterlimit);
                                paint.setColor(VectorDrawableCompat.applyAlpha(vFullPath.mStrokeColor, vFullPath.mStrokeAlpha));
                                paint.setColorFilter(filter);
                                paint.setStrokeWidth((f * min) * vFullPath.mStrokeWidth);
                                canvas.drawPath(this.mRenderPath, paint);
                            }
                        }
                    }
                }
            }
            canvas.restore();
        }

        public final void draw(Canvas canvas, int w, int h, ColorFilter filter) {
            drawGroupTree(this.mRootGroup, IDENTITY_MATRIX, canvas, w, h, null);
        }
    }

    private static class VectorDrawableCompatState extends ConstantState {
        boolean mAutoMirrored;
        boolean mCacheDirty;
        boolean mCachedAutoMirrored;
        Bitmap mCachedBitmap;
        int mCachedRootAlpha;
        ColorStateList mCachedTint;
        Mode mCachedTintMode;
        int mChangingConfigurations;
        Paint mTempPaint;
        ColorStateList mTint;
        Mode mTintMode;
        VPathRenderer mVPathRenderer;

        public VectorDrawableCompatState(VectorDrawableCompatState copy) {
            this.mTint = null;
            this.mTintMode = VectorDrawableCompat.DEFAULT_TINT_MODE;
            if (copy != null) {
                this.mChangingConfigurations = copy.mChangingConfigurations;
                this.mVPathRenderer = new VPathRenderer(copy.mVPathRenderer);
                if (copy.mVPathRenderer.mFillPaint != null) {
                    this.mVPathRenderer.mFillPaint = new Paint(copy.mVPathRenderer.mFillPaint);
                }
                if (copy.mVPathRenderer.mStrokePaint != null) {
                    this.mVPathRenderer.mStrokePaint = new Paint(copy.mVPathRenderer.mStrokePaint);
                }
                this.mTint = copy.mTint;
                this.mTintMode = copy.mTintMode;
                this.mAutoMirrored = copy.mAutoMirrored;
            }
        }

        public final void updateCachedBitmap(int width, int height) {
            this.mCachedBitmap.eraseColor(0);
            this.mVPathRenderer.draw(new Canvas(this.mCachedBitmap), width, height, null);
        }

        public VectorDrawableCompatState() {
            this.mTint = null;
            this.mTintMode = VectorDrawableCompat.DEFAULT_TINT_MODE;
            this.mVPathRenderer = new VPathRenderer();
        }

        public final Drawable newDrawable() {
            return new VectorDrawableCompat(this);
        }

        public final Drawable newDrawable(Resources res) {
            return new VectorDrawableCompat(this);
        }

        public final int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }
    }

    private static class VectorDrawableDelegateState extends ConstantState {
        private final ConstantState mDelegateState;

        public VectorDrawableDelegateState(ConstantState state) {
            this.mDelegateState = state;
        }

        public final Drawable newDrawable() {
            VectorDrawableCompat drawableCompat = new VectorDrawableCompat();
            drawableCompat.mDelegateDrawable = (VectorDrawable) this.mDelegateState.newDrawable();
            return drawableCompat;
        }

        public final Drawable newDrawable(Resources res) {
            VectorDrawableCompat drawableCompat = new VectorDrawableCompat();
            drawableCompat.mDelegateDrawable = (VectorDrawable) this.mDelegateState.newDrawable(res);
            return drawableCompat;
        }

        public final Drawable newDrawable(Resources res, Theme theme) {
            VectorDrawableCompat drawableCompat = new VectorDrawableCompat();
            drawableCompat.mDelegateDrawable = (VectorDrawable) this.mDelegateState.newDrawable(res, theme);
            return drawableCompat;
        }

        public final boolean canApplyTheme() {
            return this.mDelegateState.canApplyTheme();
        }

        public final int getChangingConfigurations() {
            return this.mDelegateState.getChangingConfigurations();
        }
    }

    public final /* bridge */ /* synthetic */ void applyTheme(Theme theme) {
        super.applyTheme(theme);
    }

    public final /* bridge */ /* synthetic */ void clearColorFilter() {
        super.clearColorFilter();
    }

    public final /* bridge */ /* synthetic */ ColorFilter getColorFilter() {
        return super.getColorFilter();
    }

    public final /* bridge */ /* synthetic */ Drawable getCurrent() {
        return super.getCurrent();
    }

    public final /* bridge */ /* synthetic */ int getMinimumHeight() {
        return super.getMinimumHeight();
    }

    public final /* bridge */ /* synthetic */ int getMinimumWidth() {
        return super.getMinimumWidth();
    }

    public final /* bridge */ /* synthetic */ boolean getPadding(Rect rect) {
        return super.getPadding(rect);
    }

    public final /* bridge */ /* synthetic */ int[] getState() {
        return super.getState();
    }

    public final /* bridge */ /* synthetic */ Region getTransparentRegion() {
        return super.getTransparentRegion();
    }

    public final /* bridge */ /* synthetic */ void jumpToCurrentState() {
        super.jumpToCurrentState();
    }

    public final /* bridge */ /* synthetic */ void setChangingConfigurations(int i) {
        super.setChangingConfigurations(i);
    }

    public final /* bridge */ /* synthetic */ void setColorFilter(int i, Mode mode) {
        super.setColorFilter(i, mode);
    }

    public final /* bridge */ /* synthetic */ void setFilterBitmap(boolean z) {
        super.setFilterBitmap(z);
    }

    public final /* bridge */ /* synthetic */ void setHotspot(float f, float f2) {
        super.setHotspot(f, f2);
    }

    public final /* bridge */ /* synthetic */ void setHotspotBounds(int i, int i2, int i3, int i4) {
        super.setHotspotBounds(i, i2, i3, i4);
    }

    public final /* bridge */ /* synthetic */ boolean setState(int[] iArr) {
        return super.setState(iArr);
    }

    VectorDrawableCompat() {
        this.mAllowCaching = true;
        this.mTmpFloats = new float[9];
        this.mTmpMatrix = new Matrix();
        this.mTmpBounds = new Rect();
        this.mVectorState = new VectorDrawableCompatState();
    }

    VectorDrawableCompat(VectorDrawableCompatState state) {
        this.mAllowCaching = true;
        this.mTmpFloats = new float[9];
        this.mTmpMatrix = new Matrix();
        this.mTmpBounds = new Rect();
        this.mVectorState = state;
        this.mTintFilter = updateTintFilter$5c32a288(state.mTint, state.mTintMode);
    }

    public final Drawable mutate() {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.mutate();
        } else if (!this.mMutated && super.mutate() == this) {
            this.mVectorState = new VectorDrawableCompatState(this.mVectorState);
            this.mMutated = true;
        }
        return this;
    }

    final Object getTargetByName(String name) {
        return this.mVectorState.mVPathRenderer.mVGTargetsMap.get(name);
    }

    public final ConstantState getConstantState() {
        if (this.mDelegateDrawable != null) {
            return new VectorDrawableDelegateState(this.mDelegateDrawable.getConstantState());
        }
        this.mVectorState.mChangingConfigurations = getChangingConfigurations();
        return this.mVectorState;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void draw(android.graphics.Canvas r14) {
        /*
        r13 = this;
        r9 = r13.mDelegateDrawable;
        if (r9 == 0) goto L_0x000a;
    L_0x0004:
        r9 = r13.mDelegateDrawable;
        r9.draw(r14);
    L_0x0009:
        return;
    L_0x000a:
        r9 = r13.mTmpBounds;
        r13.copyBounds(r9);
        r9 = r13.mTmpBounds;
        r9 = r9.width();
        if (r9 <= 0) goto L_0x0009;
    L_0x0017:
        r9 = r13.mTmpBounds;
        r9 = r9.height();
        if (r9 <= 0) goto L_0x0009;
    L_0x001f:
        r9 = r13.mColorFilter;
        if (r9 != 0) goto L_0x0111;
    L_0x0023:
        r4 = r13.mTintFilter;
    L_0x0025:
        r9 = r13.mTmpMatrix;
        r14.getMatrix(r9);
        r9 = r13.mTmpMatrix;
        r10 = r13.mTmpFloats;
        r9.getValues(r10);
        r9 = r13.mTmpFloats;
        r10 = 0;
        r9 = r9[r10];
        r0 = java.lang.Math.abs(r9);
        r9 = r13.mTmpFloats;
        r10 = 4;
        r9 = r9[r10];
        r1 = java.lang.Math.abs(r9);
        r9 = r13.mTmpFloats;
        r10 = 1;
        r9 = r9[r10];
        r2 = java.lang.Math.abs(r9);
        r9 = r13.mTmpFloats;
        r10 = 3;
        r9 = r9[r10];
        r3 = java.lang.Math.abs(r9);
        r9 = 0;
        r9 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r9 != 0) goto L_0x005f;
    L_0x005a:
        r9 = 0;
        r9 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1));
        if (r9 == 0) goto L_0x0063;
    L_0x005f:
        r0 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r1 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
    L_0x0063:
        r9 = r13.mTmpBounds;
        r9 = r9.width();
        r9 = (float) r9;
        r9 = r9 * r0;
        r8 = (int) r9;
        r9 = r13.mTmpBounds;
        r9 = r9.height();
        r9 = (float) r9;
        r9 = r9 * r1;
        r7 = (int) r9;
        r9 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r8 = java.lang.Math.min(r9, r8);
        r9 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
        r7 = java.lang.Math.min(r9, r7);
        if (r8 <= 0) goto L_0x0009;
    L_0x0083:
        if (r7 <= 0) goto L_0x0009;
    L_0x0085:
        r6 = r14.save();
        r9 = r13.mTmpBounds;
        r9 = r9.left;
        r9 = (float) r9;
        r10 = r13.mTmpBounds;
        r10 = r10.top;
        r10 = (float) r10;
        r14.translate(r9, r10);
        r9 = android.os.Build.VERSION.SDK_INT;
        r10 = 17;
        if (r9 < r10) goto L_0x0115;
    L_0x009c:
        r9 = r13.isAutoMirrored();
        if (r9 == 0) goto L_0x0115;
    L_0x00a2:
        r9 = r13.getLayoutDirection();
        r10 = 1;
        if (r9 != r10) goto L_0x0115;
    L_0x00a9:
        r5 = 1;
    L_0x00aa:
        if (r5 == 0) goto L_0x00be;
    L_0x00ac:
        r9 = r13.mTmpBounds;
        r9 = r9.width();
        r9 = (float) r9;
        r10 = 0;
        r14.translate(r9, r10);
        r9 = -1082130432; // 0xffffffffbf800000 float:-1.0 double:NaN;
        r10 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r14.scale(r9, r10);
    L_0x00be:
        r9 = r13.mTmpBounds;
        r10 = 0;
        r11 = 0;
        r9.offsetTo(r10, r11);
        r10 = r13.mVectorState;
        r9 = r10.mCachedBitmap;
        if (r9 == 0) goto L_0x00de;
    L_0x00cb:
        r9 = r10.mCachedBitmap;
        r9 = r9.getWidth();
        if (r8 != r9) goto L_0x0117;
    L_0x00d3:
        r9 = r10.mCachedBitmap;
        r9 = r9.getHeight();
        if (r7 != r9) goto L_0x0117;
    L_0x00db:
        r9 = 1;
    L_0x00dc:
        if (r9 != 0) goto L_0x00e9;
    L_0x00de:
        r9 = android.graphics.Bitmap.Config.ARGB_8888;
        r9 = android.graphics.Bitmap.createBitmap(r8, r7, r9);
        r10.mCachedBitmap = r9;
        r9 = 1;
        r10.mCacheDirty = r9;
    L_0x00e9:
        r9 = r13.mAllowCaching;
        if (r9 != 0) goto L_0x0119;
    L_0x00ed:
        r9 = r13.mVectorState;
        r9.updateCachedBitmap(r8, r7);
    L_0x00f2:
        r10 = r13.mVectorState;
        r11 = r13.mTmpBounds;
        r9 = r10.mVPathRenderer;
        r9 = r9.getRootAlpha();
        r12 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r9 >= r12) goto L_0x015f;
    L_0x0100:
        r9 = 1;
    L_0x0101:
        if (r9 != 0) goto L_0x0161;
    L_0x0103:
        if (r4 != 0) goto L_0x0161;
    L_0x0105:
        r9 = 0;
    L_0x0106:
        r10 = r10.mCachedBitmap;
        r12 = 0;
        r14.drawBitmap(r10, r12, r11, r9);
        r14.restoreToCount(r6);
        goto L_0x0009;
    L_0x0111:
        r4 = r13.mColorFilter;
        goto L_0x0025;
    L_0x0115:
        r5 = 0;
        goto L_0x00aa;
    L_0x0117:
        r9 = 0;
        goto L_0x00dc;
    L_0x0119:
        r9 = r13.mVectorState;
        r10 = r9.mCacheDirty;
        if (r10 != 0) goto L_0x015d;
    L_0x011f:
        r10 = r9.mCachedTint;
        r11 = r9.mTint;
        if (r10 != r11) goto L_0x015d;
    L_0x0125:
        r10 = r9.mCachedTintMode;
        r11 = r9.mTintMode;
        if (r10 != r11) goto L_0x015d;
    L_0x012b:
        r10 = r9.mCachedAutoMirrored;
        r11 = r9.mAutoMirrored;
        if (r10 != r11) goto L_0x015d;
    L_0x0131:
        r10 = r9.mCachedRootAlpha;
        r9 = r9.mVPathRenderer;
        r9 = r9.getRootAlpha();
        if (r10 != r9) goto L_0x015d;
    L_0x013b:
        r9 = 1;
    L_0x013c:
        if (r9 != 0) goto L_0x00f2;
    L_0x013e:
        r9 = r13.mVectorState;
        r9.updateCachedBitmap(r8, r7);
        r9 = r13.mVectorState;
        r10 = r9.mTint;
        r9.mCachedTint = r10;
        r10 = r9.mTintMode;
        r9.mCachedTintMode = r10;
        r10 = r9.mVPathRenderer;
        r10 = r10.getRootAlpha();
        r9.mCachedRootAlpha = r10;
        r10 = r9.mAutoMirrored;
        r9.mCachedAutoMirrored = r10;
        r10 = 0;
        r9.mCacheDirty = r10;
        goto L_0x00f2;
    L_0x015d:
        r9 = 0;
        goto L_0x013c;
    L_0x015f:
        r9 = 0;
        goto L_0x0101;
    L_0x0161:
        r9 = r10.mTempPaint;
        if (r9 != 0) goto L_0x0172;
    L_0x0165:
        r9 = new android.graphics.Paint;
        r9.<init>();
        r10.mTempPaint = r9;
        r9 = r10.mTempPaint;
        r12 = 1;
        r9.setFilterBitmap(r12);
    L_0x0172:
        r9 = r10.mTempPaint;
        r12 = r10.mVPathRenderer;
        r12 = r12.getRootAlpha();
        r9.setAlpha(r12);
        r9 = r10.mTempPaint;
        r9.setColorFilter(r4);
        r9 = r10.mTempPaint;
        goto L_0x0106;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.graphics.drawable.VectorDrawableCompat.draw(android.graphics.Canvas):void");
    }

    public final int getAlpha() {
        if (this.mDelegateDrawable != null) {
            return DrawableCompat.getAlpha(this.mDelegateDrawable);
        }
        return this.mVectorState.mVPathRenderer.getRootAlpha();
    }

    public final void setAlpha(int alpha) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.setAlpha(alpha);
        } else if (this.mVectorState.mVPathRenderer.getRootAlpha() != alpha) {
            this.mVectorState.mVPathRenderer.setRootAlpha(alpha);
            invalidateSelf();
        }
    }

    public final void setColorFilter(ColorFilter colorFilter) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.setColorFilter(colorFilter);
            return;
        }
        this.mColorFilter = colorFilter;
        invalidateSelf();
    }

    private PorterDuffColorFilter updateTintFilter$5c32a288(ColorStateList tint, Mode tintMode) {
        if (tint == null || tintMode == null) {
            return null;
        }
        return new PorterDuffColorFilter(tint.getColorForState(getState(), 0), tintMode);
    }

    @SuppressLint({"NewApi"})
    public final void setTint(int tint) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setTint(this.mDelegateDrawable, tint);
        } else {
            setTintList(ColorStateList.valueOf(tint));
        }
    }

    public final void setTintList(ColorStateList tint) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setTintList(this.mDelegateDrawable, tint);
            return;
        }
        VectorDrawableCompatState state = this.mVectorState;
        if (state.mTint != tint) {
            state.mTint = tint;
            this.mTintFilter = updateTintFilter$5c32a288(tint, state.mTintMode);
            invalidateSelf();
        }
    }

    public final void setTintMode(Mode tintMode) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setTintMode(this.mDelegateDrawable, tintMode);
            return;
        }
        VectorDrawableCompatState state = this.mVectorState;
        if (state.mTintMode != tintMode) {
            state.mTintMode = tintMode;
            this.mTintFilter = updateTintFilter$5c32a288(state.mTint, tintMode);
            invalidateSelf();
        }
    }

    public final boolean isStateful() {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.isStateful();
        }
        return super.isStateful() || !(this.mVectorState == null || this.mVectorState.mTint == null || !this.mVectorState.mTint.isStateful());
    }

    protected final boolean onStateChange(int[] stateSet) {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.setState(stateSet);
        }
        VectorDrawableCompatState state = this.mVectorState;
        if (state.mTint == null || state.mTintMode == null) {
            return false;
        }
        this.mTintFilter = updateTintFilter$5c32a288(state.mTint, state.mTintMode);
        invalidateSelf();
        return true;
    }

    public final int getOpacity() {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.getOpacity();
        }
        return -3;
    }

    public final int getIntrinsicWidth() {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.getIntrinsicWidth();
        }
        return (int) this.mVectorState.mVPathRenderer.mBaseWidth;
    }

    public final int getIntrinsicHeight() {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.getIntrinsicHeight();
        }
        return (int) this.mVectorState.mVPathRenderer.mBaseHeight;
    }

    public final boolean canApplyTheme() {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.canApplyTheme(this.mDelegateDrawable);
        }
        return false;
    }

    public final boolean isAutoMirrored() {
        if (this.mDelegateDrawable != null) {
            return DrawableCompat.isAutoMirrored(this.mDelegateDrawable);
        }
        return this.mVectorState.mAutoMirrored;
    }

    public final void setAutoMirrored(boolean mirrored) {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.setAutoMirrored(this.mDelegateDrawable, mirrored);
        } else {
            this.mVectorState.mAutoMirrored = mirrored;
        }
    }

    @SuppressLint({"NewApi"})
    public static VectorDrawableCompat create(Resources res, int resId, Theme theme) {
        if (VERSION.SDK_INT >= 24) {
            VectorDrawableCompat drawable = new VectorDrawableCompat();
            drawable.mDelegateDrawable = ResourcesCompat.getDrawable(res, resId, theme);
            drawable.mCachedConstantStateDelegate = new VectorDrawableDelegateState(drawable.mDelegateDrawable.getConstantState());
            return drawable;
        }
        try {
            int type;
            XmlPullParser parser = res.getXml(resId);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            do {
                type = parser.next();
                if (type == 2) {
                    break;
                }
            } while (type != 1);
            if (type == 2) {
                return createFromXmlInner(res, parser, attrs, theme);
            }
            throw new XmlPullParserException("No start tag found");
        } catch (XmlPullParserException e) {
            Log.e("VectorDrawableCompat", "parser error", e);
            return null;
        } catch (IOException e2) {
            Log.e("VectorDrawableCompat", "parser error", e2);
            return null;
        }
    }

    @SuppressLint({"NewApi"})
    public static VectorDrawableCompat createFromXmlInner(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        VectorDrawableCompat drawable = new VectorDrawableCompat();
        drawable.inflate(r, parser, attrs, theme);
        return drawable;
    }

    static int applyAlpha(int color, float alpha) {
        return (color & 16777215) | (((int) (((float) Color.alpha(color)) * alpha)) << 24);
    }

    @SuppressLint({"NewApi"})
    public final void inflate(Resources res, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.inflate(res, parser, attrs);
        } else {
            inflate(res, parser, attrs, null);
        }
    }

    public final void inflate(Resources res, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        if (this.mDelegateDrawable != null) {
            DrawableCompat.inflate(this.mDelegateDrawable, res, parser, attrs, theme);
            return;
        }
        VectorDrawableCompatState state = this.mVectorState;
        state.mVPathRenderer = new VPathRenderer();
        TypedArray a = VectorDrawableCommon.obtainAttributes(res, theme, attrs, AndroidResources.styleable_VectorDrawableTypeArray);
        VectorDrawableCompatState vectorDrawableCompatState = this.mVectorState;
        VPathRenderer vPathRenderer = vectorDrawableCompatState.mVPathRenderer;
        int namedInt = TypedArrayUtils.getNamedInt(a, parser, "tintMode", 6, -1);
        Mode mode = Mode.SRC_IN;
        switch (namedInt) {
            case 3:
                mode = Mode.SRC_OVER;
                break;
            case 5:
                mode = Mode.SRC_IN;
                break;
            case 9:
                mode = Mode.SRC_ATOP;
                break;
            case 14:
                mode = Mode.MULTIPLY;
                break;
            case 15:
                mode = Mode.SCREEN;
                break;
            case 16:
                if (VERSION.SDK_INT >= 11) {
                    mode = Mode.ADD;
                    break;
                }
                break;
        }
        vectorDrawableCompatState.mTintMode = mode;
        ColorStateList colorStateList = a.getColorStateList(1);
        if (colorStateList != null) {
            vectorDrawableCompatState.mTint = colorStateList;
        }
        boolean z = vectorDrawableCompatState.mAutoMirrored;
        if (TypedArrayUtils.hasAttribute(parser, "autoMirrored")) {
            z = a.getBoolean(5, z);
        }
        vectorDrawableCompatState.mAutoMirrored = z;
        vPathRenderer.mViewportWidth = TypedArrayUtils.getNamedFloat(a, parser, "viewportWidth", 7, vPathRenderer.mViewportWidth);
        vPathRenderer.mViewportHeight = TypedArrayUtils.getNamedFloat(a, parser, "viewportHeight", 8, vPathRenderer.mViewportHeight);
        if (vPathRenderer.mViewportWidth <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires viewportWidth > 0");
        } else if (vPathRenderer.mViewportHeight <= 0.0f) {
            throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires viewportHeight > 0");
        } else {
            vPathRenderer.mBaseWidth = a.getDimension(3, vPathRenderer.mBaseWidth);
            vPathRenderer.mBaseHeight = a.getDimension(2, vPathRenderer.mBaseHeight);
            if (vPathRenderer.mBaseWidth <= 0.0f) {
                throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires width > 0");
            } else if (vPathRenderer.mBaseHeight <= 0.0f) {
                throw new XmlPullParserException(a.getPositionDescription() + "<vector> tag requires height > 0");
            } else {
                vPathRenderer.setAlpha(TypedArrayUtils.getNamedFloat(a, parser, "alpha", 4, vPathRenderer.getAlpha()));
                String string = a.getString(0);
                if (string != null) {
                    vPathRenderer.mRootName = string;
                    vPathRenderer.mVGTargetsMap.put(string, vPathRenderer);
                }
                a.recycle();
                state.mChangingConfigurations = getChangingConfigurations();
                state.mCacheDirty = true;
                inflateInternal(res, parser, attrs, theme);
                this.mTintFilter = updateTintFilter$5c32a288(state.mTint, state.mTintMode);
            }
        }
    }

    private void inflateInternal(Resources res, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        VectorDrawableCompatState state = this.mVectorState;
        VPathRenderer pathRenderer = state.mVPathRenderer;
        boolean noPathTag = true;
        Stack<VGroup> groupStack = new Stack();
        groupStack.push(pathRenderer.mRootGroup);
        int eventType = parser.getEventType();
        int innerDepth = parser.getDepth() + 1;
        while (eventType != 1 && (parser.getDepth() >= innerDepth || eventType != 3)) {
            if (eventType == 2) {
                String tagName = parser.getName();
                VGroup currentGroup = (VGroup) groupStack.peek();
                if ("path".equals(tagName)) {
                    VFullPath path = new VFullPath();
                    path.inflate(res, attrs, theme, parser);
                    currentGroup.mChildren.add(path);
                    if (path.getPathName() != null) {
                        pathRenderer.mVGTargetsMap.put(path.getPathName(), path);
                    }
                    noPathTag = false;
                    state.mChangingConfigurations |= path.mChangingConfigurations;
                } else if ("clip-path".equals(tagName)) {
                    VClipPath path2 = new VClipPath();
                    path2.inflate(res, attrs, theme, parser);
                    currentGroup.mChildren.add(path2);
                    if (path2.getPathName() != null) {
                        pathRenderer.mVGTargetsMap.put(path2.getPathName(), path2);
                    }
                    state.mChangingConfigurations |= path2.mChangingConfigurations;
                } else if ("group".equals(tagName)) {
                    VGroup newChildGroup = new VGroup();
                    newChildGroup.inflate(res, attrs, theme, parser);
                    currentGroup.mChildren.add(newChildGroup);
                    groupStack.push(newChildGroup);
                    if (newChildGroup.getGroupName() != null) {
                        pathRenderer.mVGTargetsMap.put(newChildGroup.getGroupName(), newChildGroup);
                    }
                    state.mChangingConfigurations |= newChildGroup.mChangingConfigurations;
                }
            } else if (eventType == 3) {
                if ("group".equals(parser.getName())) {
                    groupStack.pop();
                }
            }
            eventType = parser.next();
        }
        if (noPathTag) {
            StringBuffer tag = new StringBuffer();
            if (tag.length() > 0) {
                tag.append(" or ");
            }
            tag.append("path");
            throw new XmlPullParserException("no " + tag + " defined");
        }
    }

    final void setAllowCaching(boolean allowCaching) {
        this.mAllowCaching = false;
    }

    protected final void onBoundsChange(Rect bounds) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.setBounds(bounds);
        }
    }

    public final int getChangingConfigurations() {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.getChangingConfigurations();
        }
        return super.getChangingConfigurations() | this.mVectorState.getChangingConfigurations();
    }

    public final void invalidateSelf() {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.invalidateSelf();
        } else {
            super.invalidateSelf();
        }
    }

    public final void scheduleSelf(Runnable what, long when) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.scheduleSelf(what, when);
        } else {
            super.scheduleSelf(what, when);
        }
    }

    public final boolean setVisible(boolean visible, boolean restart) {
        if (this.mDelegateDrawable != null) {
            return this.mDelegateDrawable.setVisible(visible, restart);
        }
        return super.setVisible(visible, restart);
    }

    public final void unscheduleSelf(Runnable what) {
        if (this.mDelegateDrawable != null) {
            this.mDelegateDrawable.unscheduleSelf(what);
        } else {
            super.unscheduleSelf(what);
        }
    }
}
