package android.support.graphics.drawable;

import android.graphics.Path;
import android.util.Log;
import com.shinobicontrols.charts.R;
import java.util.ArrayList;

final class PathParser {

    private static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    public static class PathDataNode {
        float[] params;
        char type;

        PathDataNode(char type, float[] params) {
            this.type = type;
            this.params = params;
        }

        PathDataNode(PathDataNode n) {
            this.type = n.type;
            this.params = PathParser.copyOfRange(n.params, 0, n.params.length);
        }

        public static void nodesToPath(PathDataNode[] node, Path path) {
            float[] current = new float[6];
            char previousCommand = 'm';
            for (int i = 0; i < node.length; i++) {
                int i2;
                char c = node[i].type;
                float[] fArr = node[i].params;
                float f = current[0];
                float f2 = current[1];
                float f3 = current[2];
                float f4 = current[3];
                float f5 = current[4];
                float f6 = current[5];
                switch (c) {
                    case R.styleable.ChartTheme_sc_pieDonutLabelColor /*65*/:
                    case 'a':
                        i2 = 7;
                        break;
                    case R.styleable.ChartTheme_sc_financialFallingColor /*67*/:
                    case 'c':
                        i2 = 6;
                        break;
                    case R.styleable.ChartTheme_sc_annotationBackgroundColor /*72*/:
                    case 'V':
                    case 'h':
                    case 'v':
                        i2 = 1;
                        break;
                    case 'L':
                    case 'M':
                    case 'T':
                    case 'l':
                    case 'm':
                    case 't':
                        i2 = 2;
                        break;
                    case 'Q':
                    case 'S':
                    case 'q':
                    case 's':
                        i2 = 4;
                        break;
                    case 'Z':
                    case 'z':
                        path.close();
                        path.moveTo(f5, f6);
                        f4 = f6;
                        f3 = f5;
                        f2 = f6;
                        f = f5;
                        i2 = 2;
                        break;
                    default:
                        i2 = 2;
                        break;
                }
                int i3 = 0;
                float f7 = f6;
                float f8 = f5;
                float f9 = f2;
                float f10 = f;
                while (i3 < fArr.length) {
                    float f11;
                    float f12;
                    boolean z;
                    boolean z2;
                    switch (c) {
                        case R.styleable.ChartTheme_sc_pieDonutLabelColor /*65*/:
                            f5 = fArr[i3 + 5];
                            f2 = fArr[i3 + 6];
                            f = fArr[i3];
                            f11 = fArr[i3 + 1];
                            f12 = fArr[i3 + 2];
                            z = fArr[i3 + 3] != 0.0f;
                            if (fArr[i3 + 4] != 0.0f) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            drawArc(path, f10, f9, f5, f2, f, f11, f12, z, z2);
                            f3 = fArr[i3 + 5];
                            f4 = fArr[i3 + 6];
                            f6 = f7;
                            f5 = f8;
                            f2 = f3;
                            f = f4;
                            f11 = f3;
                            f3 = f4;
                            break;
                        case R.styleable.ChartTheme_sc_financialFallingColor /*67*/:
                            path.cubicTo(fArr[i3], fArr[i3 + 1], fArr[i3 + 2], fArr[i3 + 3], fArr[i3 + 4], fArr[i3 + 5]);
                            f5 = fArr[i3 + 4];
                            f6 = fArr[i3 + 5];
                            f2 = fArr[i3 + 2];
                            f = f6;
                            f11 = f5;
                            f6 = f7;
                            f5 = f8;
                            f3 = fArr[i3 + 3];
                            break;
                        case R.styleable.ChartTheme_sc_annotationBackgroundColor /*72*/:
                            path.lineTo(fArr[i3], f9);
                            f6 = f7;
                            f2 = f3;
                            f = f9;
                            f11 = fArr[i3];
                            f3 = f4;
                            f5 = f8;
                            break;
                        case 'L':
                            path.lineTo(fArr[i3], fArr[i3 + 1]);
                            f5 = fArr[i3];
                            f2 = f3;
                            f = fArr[i3 + 1];
                            f11 = f5;
                            f6 = f7;
                            f5 = f8;
                            f3 = f4;
                            break;
                        case 'M':
                            f5 = fArr[i3];
                            f6 = fArr[i3 + 1];
                            if (i3 <= 0) {
                                path.moveTo(fArr[i3], fArr[i3 + 1]);
                                f2 = f3;
                                f = f6;
                                f11 = f5;
                                f3 = f4;
                                break;
                            }
                            path.lineTo(fArr[i3], fArr[i3 + 1]);
                            f2 = f3;
                            f = f6;
                            f11 = f5;
                            f6 = f7;
                            f5 = f8;
                            f3 = f4;
                            break;
                        case 'Q':
                            path.quadTo(fArr[i3], fArr[i3 + 1], fArr[i3 + 2], fArr[i3 + 3]);
                            f3 = fArr[i3];
                            f4 = fArr[i3 + 1];
                            f5 = fArr[i3 + 2];
                            f2 = f3;
                            f = fArr[i3 + 3];
                            f11 = f5;
                            f6 = f7;
                            f5 = f8;
                            f3 = f4;
                            break;
                        case 'S':
                            if (previousCommand == 'c' || previousCommand == 's' || previousCommand == 'C' || previousCommand == 'S') {
                                f6 = (2.0f * f10) - f3;
                                f3 = (2.0f * f9) - f4;
                            } else {
                                f3 = f9;
                                f6 = f10;
                            }
                            path.cubicTo(f6, f3, fArr[i3], fArr[i3 + 1], fArr[i3 + 2], fArr[i3 + 3]);
                            f3 = fArr[i3];
                            f4 = fArr[i3 + 1];
                            f5 = fArr[i3 + 2];
                            f2 = f3;
                            f = fArr[i3 + 3];
                            f11 = f5;
                            f6 = f7;
                            f5 = f8;
                            f3 = f4;
                            break;
                        case 'T':
                            if (previousCommand == 'q' || previousCommand == 't' || previousCommand == 'Q' || previousCommand == 'T') {
                                f10 = (2.0f * f10) - f3;
                                f9 = (2.0f * f9) - f4;
                            }
                            path.quadTo(f10, f9, fArr[i3], fArr[i3 + 1]);
                            f5 = fArr[i3];
                            f3 = f9;
                            f2 = f10;
                            f = fArr[i3 + 1];
                            f11 = f5;
                            f6 = f7;
                            f5 = f8;
                            break;
                        case 'V':
                            path.lineTo(f10, fArr[i3]);
                            f5 = f8;
                            f2 = f3;
                            f = fArr[i3];
                            f11 = f10;
                            f3 = f4;
                            f6 = f7;
                            break;
                        case 'a':
                            f5 = fArr[i3 + 5] + f10;
                            f2 = fArr[i3 + 6] + f9;
                            f = fArr[i3];
                            f11 = fArr[i3 + 1];
                            f12 = fArr[i3 + 2];
                            z = fArr[i3 + 3] != 0.0f;
                            if (fArr[i3 + 4] != 0.0f) {
                                z2 = true;
                            } else {
                                z2 = false;
                            }
                            drawArc(path, f10, f9, f5, f2, f, f11, f12, z, z2);
                            f3 = f10 + fArr[i3 + 5];
                            f4 = fArr[i3 + 6] + f9;
                            f6 = f7;
                            f5 = f8;
                            f2 = f3;
                            f = f4;
                            f11 = f3;
                            f3 = f4;
                            break;
                        case 'c':
                            path.rCubicTo(fArr[i3], fArr[i3 + 1], fArr[i3 + 2], fArr[i3 + 3], fArr[i3 + 4], fArr[i3 + 5]);
                            f2 = f10 + fArr[i3 + 2];
                            f = fArr[i3 + 5] + f9;
                            f11 = f10 + fArr[i3 + 4];
                            f6 = f7;
                            f5 = f8;
                            f3 = fArr[i3 + 3] + f9;
                            break;
                        case 'h':
                            path.rLineTo(fArr[i3], 0.0f);
                            f6 = f7;
                            f2 = f3;
                            f = f9;
                            f11 = f10 + fArr[i3];
                            f3 = f4;
                            f5 = f8;
                            break;
                        case 'l':
                            path.rLineTo(fArr[i3], fArr[i3 + 1]);
                            f2 = f3;
                            f = fArr[i3 + 1] + f9;
                            f11 = f10 + fArr[i3];
                            f6 = f7;
                            f5 = f8;
                            f3 = f4;
                            break;
                        case 'm':
                            f5 = f10 + fArr[i3];
                            f6 = fArr[i3 + 1] + f9;
                            if (i3 <= 0) {
                                path.rMoveTo(fArr[i3], fArr[i3 + 1]);
                                f2 = f3;
                                f = f6;
                                f11 = f5;
                                f3 = f4;
                                break;
                            }
                            path.rLineTo(fArr[i3], fArr[i3 + 1]);
                            f2 = f3;
                            f = f6;
                            f11 = f5;
                            f6 = f7;
                            f5 = f8;
                            f3 = f4;
                            break;
                        case 'q':
                            path.rQuadTo(fArr[i3], fArr[i3 + 1], fArr[i3 + 2], fArr[i3 + 3]);
                            f2 = f10 + fArr[i3];
                            f = fArr[i3 + 3] + f9;
                            f11 = f10 + fArr[i3 + 2];
                            f6 = f7;
                            f5 = f8;
                            f3 = fArr[i3 + 1] + f9;
                            break;
                        case 's':
                            if (previousCommand == 'c' || previousCommand == 's' || previousCommand == 'C' || previousCommand == 'S') {
                                f6 = f10 - f3;
                                f3 = f9 - f4;
                            } else {
                                f3 = 0.0f;
                                f6 = 0.0f;
                            }
                            path.rCubicTo(f6, f3, fArr[i3], fArr[i3 + 1], fArr[i3 + 2], fArr[i3 + 3]);
                            f2 = f10 + fArr[i3];
                            f = fArr[i3 + 3] + f9;
                            f11 = f10 + fArr[i3 + 2];
                            f6 = f7;
                            f5 = f8;
                            f3 = fArr[i3 + 1] + f9;
                            break;
                        case 't':
                            if (previousCommand == 'q' || previousCommand == 't' || previousCommand == 'Q' || previousCommand == 'T') {
                                f6 = f10 - f3;
                                f4 = f9 - f4;
                            } else {
                                f4 = 0.0f;
                                f6 = 0.0f;
                            }
                            path.rQuadTo(f6, f4, fArr[i3], fArr[i3 + 1]);
                            f2 = f10 + f6;
                            f = fArr[i3 + 1] + f9;
                            f11 = f10 + fArr[i3];
                            f6 = f7;
                            f5 = f8;
                            f3 = f4 + f9;
                            break;
                        case 'v':
                            path.rLineTo(0.0f, fArr[i3]);
                            f5 = f8;
                            f2 = f3;
                            f = fArr[i3] + f9;
                            f11 = f10;
                            f3 = f4;
                            f6 = f7;
                            break;
                        default:
                            f6 = f7;
                            f5 = f8;
                            f2 = f3;
                            f = f9;
                            f11 = f10;
                            f3 = f4;
                            break;
                    }
                    i3 += i2;
                    f7 = f6;
                    f8 = f5;
                    f9 = f;
                    f10 = f11;
                    previousCommand = c;
                    f4 = f3;
                    f3 = f2;
                }
                current[0] = f10;
                current[1] = f9;
                current[2] = f3;
                current[3] = f4;
                current[4] = f8;
                current[5] = f7;
                previousCommand = node[i].type;
            }
        }

        private static void drawArc(Path p, float x0, float y0, float x1, float y1, float a, float b, float theta, boolean isMoreThanHalf, boolean isPositiveArc) {
            double thetaD;
            double cosTheta;
            double sinTheta;
            double x0p;
            double y0p;
            double x1p;
            double y1p;
            double dx;
            double dy;
            double disc;
            double cx;
            double cy;
            while (true) {
                thetaD = Math.toRadians((double) theta);
                cosTheta = Math.cos(thetaD);
                sinTheta = Math.sin(thetaD);
                x0p = ((((double) x0) * cosTheta) + (((double) y0) * sinTheta)) / ((double) a);
                y0p = ((((double) (-x0)) * sinTheta) + (((double) y0) * cosTheta)) / ((double) b);
                x1p = ((((double) x1) * cosTheta) + (((double) y1) * sinTheta)) / ((double) a);
                y1p = ((((double) (-x1)) * sinTheta) + (((double) y1) * cosTheta)) / ((double) b);
                dx = x0p - x1p;
                dy = y0p - y1p;
                double xm = (x0p + x1p) / 2.0d;
                double ym = (y0p + y1p) / 2.0d;
                double dsq = (dx * dx) + (dy * dy);
                if (dsq != 0.0d) {
                    disc = (1.0d / dsq) - 0.25d;
                    if (disc >= 0.0d) {
                        break;
                    }
                    Log.w("PathParser", "Points are too far apart " + dsq);
                    float adjust = (float) (Math.sqrt(dsq) / 1.99999d);
                    a *= adjust;
                    b *= adjust;
                } else {
                    Log.w("PathParser", " Points are coincident");
                    return;
                }
            }
            double s = Math.sqrt(disc);
            double sdx = s * dx;
            double sdy = s * dy;
            if (isMoreThanHalf == isPositiveArc) {
                cx = xm - sdy;
                cy = ym + sdx;
            } else {
                cx = xm + sdy;
                cy = ym - sdx;
            }
            double eta0 = Math.atan2(y0p - cy, x0p - cx);
            double sweep = Math.atan2(y1p - cy, x1p - cx) - eta0;
            if (isPositiveArc != (sweep >= 0.0d)) {
                if (sweep > 0.0d) {
                    sweep -= 6.283185307179586d;
                } else {
                    sweep += 6.283185307179586d;
                }
            }
            cx *= (double) a;
            cy *= (double) b;
            arcToBezier(p, (cx * cosTheta) - (cy * sinTheta), (cx * sinTheta) + (cy * cosTheta), (double) a, (double) b, (double) x0, (double) y0, thetaD, eta0, sweep);
        }

        private static void arcToBezier(Path p, double cx, double cy, double a, double b, double e1x, double e1y, double theta, double start, double sweep) {
            int numSegments = (int) Math.ceil(Math.abs((4.0d * sweep) / 3.141592653589793d));
            double eta1 = start;
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            double cosEta1 = Math.cos(eta1);
            double sinEta1 = Math.sin(eta1);
            double ep1x = (((-a) * cosTheta) * sinEta1) - ((b * sinTheta) * cosEta1);
            double ep1y = (((-a) * sinTheta) * sinEta1) + ((b * cosTheta) * cosEta1);
            double anglePerSegment = sweep / ((double) numSegments);
            for (int i = 0; i < numSegments; i++) {
                double eta2 = eta1 + anglePerSegment;
                double sinEta2 = Math.sin(eta2);
                double cosEta2 = Math.cos(eta2);
                double e2x = (((a * cosTheta) * cosEta2) + cx) - ((b * sinTheta) * sinEta2);
                double e2y = (((a * sinTheta) * cosEta2) + cy) + ((b * cosTheta) * sinEta2);
                double ep2x = (((-a) * cosTheta) * sinEta2) - ((b * sinTheta) * cosEta2);
                double ep2y = (((-a) * sinTheta) * sinEta2) + ((b * cosTheta) * cosEta2);
                double tanDiff2 = Math.tan((eta2 - eta1) / 2.0d);
                double alpha = (Math.sin(eta2 - eta1) * (Math.sqrt(4.0d + ((3.0d * tanDiff2) * tanDiff2)) - 1.0d)) / 3.0d;
                double q1x = e1x + (alpha * ep1x);
                double q1y = e1y + (alpha * ep1y);
                double q2x = e2x - (alpha * ep2x);
                double q2y = e2y - (alpha * ep2y);
                p.rLineTo(0.0f, 0.0f);
                p.cubicTo((float) q1x, (float) q1y, (float) q2x, (float) q2y, (float) e2x, (float) e2y);
                eta1 = eta2;
                e1x = e2x;
                e1y = e2y;
                ep1x = ep2x;
                ep1y = ep2y;
            }
        }
    }

    static float[] copyOfRange(float[] original, int start, int end) {
        if (end < 0) {
            throw new IllegalArgumentException();
        }
        int originalLength = original.length;
        if (originalLength < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        float[] result = new float[end];
        System.arraycopy(original, 0, result, 0, Math.min(end, originalLength));
        return result;
    }

    public static PathDataNode[] createNodesFromPathData(String pathData) {
        if (pathData == null) {
            return null;
        }
        int start = 0;
        int end = 1;
        ArrayList<PathDataNode> list = new ArrayList();
        while (end < pathData.length()) {
            end = nextStart(pathData, end);
            String s = pathData.substring(start, end).trim();
            if (s.length() > 0) {
                addNode(list, s.charAt(0), getFloats(s));
            }
            start = end;
            end++;
        }
        if (end - start == 1 && start < pathData.length()) {
            addNode(list, pathData.charAt(start), new float[0]);
        }
        return (PathDataNode[]) list.toArray(new PathDataNode[list.size()]);
    }

    public static PathDataNode[] deepCopyNodes(PathDataNode[] source) {
        if (source == null) {
            return null;
        }
        PathDataNode[] copy = new PathDataNode[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = new PathDataNode(source[i]);
        }
        return copy;
    }

    private static int nextStart(String s, int end) {
        while (end < s.length()) {
            char c = s.charAt(end);
            if (((c - 65) * (c - 90) <= 0 || (c - 97) * (c - 122) <= 0) && c != 'e' && c != 'E') {
                break;
            }
            end++;
        }
        return end;
    }

    private static void addNode(ArrayList<PathDataNode> list, char cmd, float[] val) {
        list.add(new PathDataNode(cmd, val));
    }

    private static float[] getFloats(String s) {
        if (((s.charAt(0) == 'z' ? 1 : 0) | (s.charAt(0) == 'Z' ? 1 : 0)) != 0) {
            return new float[0];
        }
        try {
            float[] results = new float[s.length()];
            int startPosition = 1;
            ExtractFloatResult result = new ExtractFloatResult();
            int totalLength = s.length();
            int count = 0;
            while (startPosition < totalLength) {
                int endPosition;
                int count2;
                Object obj = null;
                result.mEndWithNegOrDot = false;
                Object obj2 = null;
                Object obj3 = null;
                int i = startPosition;
                while (i < s.length()) {
                    Object obj4 = null;
                    switch (s.charAt(i)) {
                        case ' ':
                        case R.styleable.ChartTheme_sc_seriesAreaGradientTransparentColor3 /*44*/:
                            obj = 1;
                            break;
                        case R.styleable.ChartTheme_sc_seriesLineColor4 /*45*/:
                            if (i != startPosition && r11 == null) {
                                obj = 1;
                                result.mEndWithNegOrDot = true;
                                break;
                            }
                        case R.styleable.ChartTheme_sc_seriesAreaColor4 /*46*/:
                            if (obj2 != null) {
                                obj = 1;
                                result.mEndWithNegOrDot = true;
                                break;
                            }
                            obj2 = 1;
                            break;
                        case R.styleable.ChartTheme_sc_bandSeriesLowColor /*69*/:
                        case 'e':
                            obj4 = 1;
                            break;
                    }
                    if (obj == null) {
                        i++;
                        obj3 = obj4;
                    } else {
                        result.mEndPosition = i;
                        endPosition = result.mEndPosition;
                        if (startPosition >= endPosition) {
                            count2 = count + 1;
                            results[count] = Float.parseFloat(s.substring(startPosition, endPosition));
                        } else {
                            count2 = count;
                        }
                        if (result.mEndWithNegOrDot) {
                            startPosition = endPosition + 1;
                            count = count2;
                        } else {
                            startPosition = endPosition;
                            count = count2;
                        }
                    }
                }
                result.mEndPosition = i;
                endPosition = result.mEndPosition;
                if (startPosition >= endPosition) {
                    count2 = count;
                } else {
                    count2 = count + 1;
                    results[count] = Float.parseFloat(s.substring(startPosition, endPosition));
                }
                if (result.mEndWithNegOrDot) {
                    startPosition = endPosition + 1;
                    count = count2;
                } else {
                    startPosition = endPosition;
                    count = count2;
                }
            }
            return copyOfRange(results, 0, count);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error in parsing \"" + s + "\"", e);
        }
    }
}
