package android.support.v7.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.Xml;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

final class AppCompatColorStateListInflater {
    public static ColorStateList createFromXml(Resources r, XmlPullParser parser, Theme theme) throws XmlPullParserException, IOException {
        AttributeSet attrs = Xml.asAttributeSet(parser);
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        String name = parser.getName();
        if (name.equals("selector")) {
            return inflate(r, parser, attrs, theme);
        }
        throw new XmlPullParserException(parser.getPositionDescription() + ": invalid color state list tag " + name);
    }

    private static ColorStateList inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Theme theme) throws XmlPullParserException, IOException {
        int innerDepth = parser.getDepth() + 1;
        int[][] stateSpecList = new int[20][];
        int[] colorList = new int[20];
        int listSize = 0;
        while (true) {
            int type = parser.next();
            if (type == 1) {
                break;
            }
            int depth = parser.getDepth();
            if (depth < innerDepth && type == 3) {
                break;
            } else if (type == 2 && depth <= innerDepth && parser.getName().equals("item")) {
                TypedArray a;
                int[] iArr = R.styleable.ColorStateListItem;
                if (theme == null) {
                    a = r.obtainAttributes(attrs, iArr);
                } else {
                    a = theme.obtainStyledAttributes(attrs, iArr, 0, 0);
                }
                int baseColor = a.getColor(R.styleable.ColorStateListItem_android_color, -65281);
                float alphaMod = 1.0f;
                if (a.hasValue(R.styleable.ColorStateListItem_android_alpha)) {
                    alphaMod = a.getFloat(R.styleable.ColorStateListItem_android_alpha, 1.0f);
                } else if (a.hasValue(R.styleable.ColorStateListItem_alpha)) {
                    alphaMod = a.getFloat(R.styleable.ColorStateListItem_alpha, 1.0f);
                }
                a.recycle();
                int numAttrs = attrs.getAttributeCount();
                int[] stateSpec = new int[numAttrs];
                int i = 0;
                int j = 0;
                while (i < numAttrs) {
                    int j2;
                    int stateResId = attrs.getAttributeNameResource(i);
                    if (stateResId == 16843173 || stateResId == 16843551 || stateResId == com.rachio.iro.R.attr.alpha) {
                        j2 = j;
                    } else {
                        j2 = j + 1;
                        if (!attrs.getAttributeBooleanValue(i, false)) {
                            stateResId = -stateResId;
                        }
                        stateSpec[j] = stateResId;
                    }
                    i++;
                    j = j2;
                }
                Object stateSpec2 = StateSet.trimStateSet(stateSpec, j);
                colorList = GrowingArrayUtils.append(colorList, listSize, ColorUtils.setAlphaComponent(baseColor, Math.round(((float) Color.alpha(baseColor)) * alphaMod)));
                stateSpecList = (int[][]) GrowingArrayUtils.append((Object[]) stateSpecList, listSize, stateSpec2);
                listSize++;
            }
        }
        int[] colors = new int[listSize];
        Object stateSpecs = new int[listSize][];
        System.arraycopy(colorList, 0, colors, 0, listSize);
        System.arraycopy(stateSpecList, 0, stateSpecs, 0, listSize);
        return new ColorStateList(stateSpecs, colors);
    }
}
