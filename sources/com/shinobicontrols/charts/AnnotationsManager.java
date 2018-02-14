package com.shinobicontrols.charts;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.shinobicontrols.charts.Annotation.Position;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationsManager {
    private final af J;
    private final List<Annotation> K;
    private final Map<Annotation, bh> L = new HashMap();
    private final a M = new a();

    private class a implements a {
        final /* synthetic */ AnnotationsManager N;

        private a(AnnotationsManager annotationsManager) {
            this.N = annotationsManager;
        }

        public void a(Annotation annotation) {
            this.N.a(annotation);
        }
    }

    AnnotationsManager(af chart) {
        this.J = chart;
        this.K = new ArrayList();
    }

    public List<Annotation> getAnnotations() {
        return Collections.unmodifiableList(this.K);
    }

    public void removeAnnotation(Annotation annotation) {
        c(annotation);
    }

    public void removeAllAnnotations() {
        a(new ArrayList(this.K));
    }

    public void removeAllAnnotations(Axis<?, ?> axis) {
        List arrayList = new ArrayList();
        for (Annotation annotation : this.K) {
            if (annotation.getXAxis() == axis || annotation.getYAxis() == axis) {
                arrayList.add(annotation);
            }
        }
        a(arrayList);
    }

    private void c(Annotation annotation) {
        this.J.em.b(annotation.getView(), annotation.getPosition());
        this.K.remove(annotation);
        d(annotation);
    }

    private void a(List<Annotation> list) {
        for (Annotation c : list) {
            c(c);
        }
    }

    private void d(Annotation annotation) {
        bh bhVar = (bh) this.L.get(annotation);
        if (bhVar != null) {
            bhVar.cP();
            this.L.remove(annotation);
        }
    }

    public Annotation addTextAnnotation(String text, Object xValue, Object yValue, Axis<?, ?> xAxis, Axis<?, ?> yAxis) {
        this.J.br();
        View textView = new TextView(this.J.getContext());
        textView.setLayoutParams(new LayoutParams(-2, -2));
        int c = ca.c(this.J.getResources().getDisplayMetrics().density, 6.0f);
        textView.setPadding(c, c, c, c);
        textView.setText(text);
        Annotation annotation = new Annotation(textView, xAxis, yAxis, e.G);
        annotation.setXValue(xValue);
        annotation.setYValue(yValue);
        e(annotation);
        return annotation;
    }

    public Annotation addViewAnnotation(View view, Object xValue, Object yValue, Axis<?, ?> xAxis, Axis<?, ?> yAxis) {
        this.J.br();
        if (view == null) {
            throw new IllegalArgumentException("Annotation cannot have a null View.");
        }
        if (view.getLayoutParams() == null) {
            view.setLayoutParams(new LayoutParams(-2, -2));
        }
        Annotation annotation = new Annotation(view, xAxis, yAxis, e.I);
        annotation.setXValue(xValue);
        annotation.setYValue(yValue);
        e(annotation);
        return annotation;
    }

    public Annotation addHorizontalLineAnnotation(Object yValue, float thickness, Axis<?, ?> xAxis, Axis<?, ?> yAxis) {
        this.J.br();
        int c = ca.c(this.J.getResources().getDisplayMetrics().density, thickness);
        View view = new View(this.J.getContext());
        view.setLayoutParams(new LayoutParams(-1, c));
        Annotation annotation = new Annotation(view, xAxis, yAxis, e.H);
        annotation.setYValue(yValue);
        e(annotation);
        return annotation;
    }

    public Annotation addVerticalLineAnnotation(Object xValue, float thickness, Axis<?, ?> xAxis, Axis<?, ?> yAxis) {
        this.J.br();
        int c = ca.c(this.J.getResources().getDisplayMetrics().density, thickness);
        View view = new View(this.J.getContext());
        view.setLayoutParams(new LayoutParams(c, -1));
        Annotation annotation = new Annotation(view, xAxis, yAxis, e.H);
        annotation.setXValue(xValue);
        e(annotation);
        return annotation;
    }

    public Annotation addHorizontalBandAnnotation(Range<?> yRange, Axis<?, ?> xAxis, Axis<?, ?> yAxis) {
        this.J.br();
        View view = new View(this.J.getContext());
        view.setLayoutParams(new LayoutParams(-1, 0));
        Annotation annotation = new Annotation(view, xAxis, yAxis, e.H);
        annotation.setYRange(yRange);
        e(annotation);
        return annotation;
    }

    public Annotation addVerticalBandAnnotation(Range<?> xRange, Axis<?, ?> xAxis, Axis<?, ?> yAxis) {
        this.J.br();
        View view = new View(this.J.getContext());
        view.setLayoutParams(new LayoutParams(0, -1));
        Annotation annotation = new Annotation(view, xAxis, yAxis, e.H);
        annotation.setXRange(xRange);
        e(annotation);
        return annotation;
    }

    public Annotation addBoxAnnotation(Range<?> xRange, Range<?> yRange, Axis<?, ?> xAxis, Axis<?, ?> yAxis) {
        this.J.br();
        View view = new View(this.J.getContext());
        view.setLayoutParams(new LayoutParams(0, 0));
        Annotation annotation = new Annotation(view, xAxis, yAxis, e.H);
        annotation.setXRange(xRange);
        annotation.setYRange(yRange);
        e(annotation);
        return annotation;
    }

    private AnnotationStyle g() {
        AnnotationStyle annotationStyle = new AnnotationStyle();
        annotationStyle.a(this.J.bs().eQ());
        return annotationStyle;
    }

    private void e(Annotation annotation) {
        annotation.setStyle(g());
        annotation.e();
        f(annotation);
        this.K.add(annotation);
        this.J.em.a(annotation.getView(), annotation.getPosition());
    }

    private void f(Annotation annotation) {
        this.L.put(annotation, annotation.a(this.M));
    }

    void a(int i, int i2, Position position) {
        int size = this.K.size();
        for (int i3 = 0; i3 < size; i3++) {
            Annotation annotation = (Annotation) this.K.get(i3);
            if (annotation.getPosition() == position) {
                LayoutParams layoutParams = annotation.getView().getLayoutParams();
                annotation.measure(ViewGroup.getChildMeasureSpec(i, 0, a(layoutParams, annotation)), ViewGroup.getChildMeasureSpec(i2, 0, b(layoutParams, annotation)));
            }
        }
    }

    private int a(LayoutParams layoutParams, Annotation annotation) {
        return layoutParams.width == 0 ? g(annotation) : layoutParams.width;
    }

    private int b(LayoutParams layoutParams, Annotation annotation) {
        return layoutParams.height == 0 ? h(annotation) : layoutParams.height;
    }

    private int g(Annotation annotation) {
        if (annotation.getXRange() == null) {
            return 0;
        }
        double translatePoint = annotation.getXAxis().translatePoint(annotation.getXRange().getMinimum());
        double translatePoint2 = annotation.getXAxis().translatePoint(annotation.getXRange().getMaximum());
        return (int) Math.round(annotation.getXAxis().ao.a(translatePoint2, this.J.em.fb, this.J.em.fc) - annotation.getXAxis().ao.a(translatePoint, this.J.em.fb, this.J.em.fc));
    }

    private int h(Annotation annotation) {
        if (annotation.getYRange() == null) {
            return 0;
        }
        return (int) Math.round(annotation.getYAxis().ao.a(annotation.getYAxis().translatePoint(annotation.getYRange().getMinimum()), this.J.em.fb, this.J.em.fc) - annotation.getYAxis().ao.a(annotation.getYAxis().translatePoint(annotation.getYRange().getMaximum()), this.J.em.fb, this.J.em.fc));
    }

    void a(int i, int i2, int i3, int i4, Position position) {
        int size = this.K.size();
        for (int i5 = 0; i5 < size; i5++) {
            Annotation annotation = (Annotation) this.K.get(i5);
            if (annotation.getPosition() == position) {
                i(annotation);
                annotation.layout(i, i2, i3, i4);
            }
        }
    }

    private void i(Annotation annotation) {
        if (annotation.getXAxis().J != this.J || annotation.getYAxis().J != this.J) {
            throw new IllegalStateException(annotation.getView().getContext().getString(R.string.AnnotationMustBeOnSameChart));
        }
    }

    void forceLayout() {
        if (this.K.size() > 0) {
            this.J.em.bD();
            this.J.em.bE();
        }
    }

    private void a(Annotation annotation) {
        this.J.em.b(annotation.getView(), annotation.getPosition() == Position.IN_FRONT_OF_DATA ? Position.BEHIND_DATA : Position.IN_FRONT_OF_DATA);
        this.J.em.a(annotation.getView(), annotation.getPosition());
    }

    void e() {
        int size = this.K.size();
        for (int i = 0; i < size; i++) {
            ((Annotation) this.K.get(i)).e();
        }
    }
}
