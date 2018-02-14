package com.rachio.iro.reporting;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import com.rachio.iro.R;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.model.reporting.WaterUseResponse;
import com.rachio.iro.model.reporting.WaterUseResponse.Result;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.fragment.NestableChartFragment;
import com.rachio.iro.utils.CalendarUtil;
import com.rachio.iro.utils.UnitUtils;
import com.shinobicontrols.charts.AxisStyle;
import com.shinobicontrols.charts.AxisTitleStyle;
import com.shinobicontrols.charts.ColumnSeries;
import com.shinobicontrols.charts.ColumnSeriesStyle;
import com.shinobicontrols.charts.DataAdapter;
import com.shinobicontrols.charts.DataPoint;
import com.shinobicontrols.charts.DateFrequency;
import com.shinobicontrols.charts.DateFrequency.Denomination;
import com.shinobicontrols.charts.DateRange;
import com.shinobicontrols.charts.DateTimeAxis;
import com.shinobicontrols.charts.NumberAxis;
import com.shinobicontrols.charts.SeriesStyle.FillStyle;
import com.shinobicontrols.charts.ShinobiChart;
import com.shinobicontrols.charts.SimpleDataAdapter;
import com.shinobicontrols.charts.TickMark.ClippingMode;
import com.shinobicontrols.charts.TickMark.Orientation;
import com.shinobicontrols.charts.TickStyle;
import com.shinobicontrols.charts.Title;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReportingUtils {
    private static final String TAG = ReportingUtils.class.getSimpleName();
    private static final SimpleDateFormat dayNameAndDateformat = new SimpleDateFormat("E\nd", Locale.US);
    private static final SimpleDateFormat dayformat = new SimpleDateFormat("d", Locale.US);
    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("MMMMM", Locale.US);
    private static final SimpleDateFormat resultFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    private static ColumnSeries createBaseColumnSeries(Context context, DataAdapter<Date, Double> dataAdapter, String title, int colorRes) {
        ColumnSeries series = new ColumnSeries();
        series.setTitle(title);
        ((ColumnSeriesStyle) series.getStyle()).setLineColor(context.getResources().getColor(colorRes));
        ((ColumnSeriesStyle) series.getStyle()).setAreaColor(context.getResources().getColor(colorRes));
        ((ColumnSeriesStyle) series.getStyle()).setFillStyle(FillStyle.FLAT);
        series.setDataAdapter(dataAdapter);
        return series;
    }

    private static final Date parseResultDate(String date) {
        Date parse;
        try {
            parse = ReportingRestClient.resultFormatWithTZ.parse(date);
        } catch (ParseException e) {
            try {
                parse = resultFormat.parse(date);
            } catch (ParseException ee) {
                throw new RuntimeException(ee);
            }
        }
        return parse;
    }

    public static ColumnSeries getSeriesForDailyWatering(Context context, User user, WaterUseResponse waterUseResponse, int color, boolean waterUse) {
        SimpleDataAdapter<Date, Double> dataAdapter = new SimpleDataAdapter();
        for (Result result : waterUseResponse.result) {
            double value = UnitUtils.convertGallonsToUserUnits(user, result.value);
            Date start = parseResultDate(result.timeframe.start);
            Calendar c = Calendar.getInstance();
            c.setTime(start);
            c.add(14, TimeZone.getDefault().getRawOffset());
            dataAdapter.add(new DataPoint(c.getTime(), Double.valueOf(value)));
        }
        return createBaseColumnSeries(context, dataAdapter, "", color);
    }

    public static ColumnSeries getSeriesForMonthlyWatering(Context context, User user, WaterUseResponse waterUseResponse, int color, boolean waterUse) {
        SimpleDataAdapter<Date, Double> dataAdapter = new SimpleDataAdapter();
        for (Result result : waterUseResponse.result) {
            Date start = parseResultDate(result.timeframe.start);
            Calendar c = Calendar.getInstance();
            c.setTime(start);
            c.add(14, TimeZone.getDefault().getRawOffset());
            dataAdapter.add(new DataPoint(c.getTime(), Double.valueOf(UnitUtils.convertGallonsToUserUnits(user, result.value))));
        }
        return createBaseColumnSeries(context, dataAdapter, "", color);
    }

    private static DateTimeAxis createBaseXAxis(Context context, DateRange dateRange) {
        DateTimeAxis xAxis = new DateTimeAxis(dateRange);
        xAxis.getStyle().setLineWidth(1.0f);
        xAxis.getStyle().setLineColor(context.getResources().getColor(R.color.rachio_grey));
        xAxis.getStyle().setInterSeriesSetPadding(0.0f);
        xAxis.setTickMarkClippingModeHigh(ClippingMode.TICKS_AND_LABELS_PERSIST);
        AxisStyle style = xAxis.getStyle();
        TickStyle tickStyle = new TickStyle();
        tickStyle.setMajorTicksShown(false);
        tickStyle.setLabelTextSize(9.0f);
        tickStyle.setTickGap(0.0f);
        tickStyle.setLabelOrientation(Orientation.HORIZONTAL);
        tickStyle.setMinorTicksShown(true);
        tickStyle.setLineColor(16777215);
        style.setTickStyle(tickStyle);
        AxisTitleStyle axisTitleStyle = new AxisTitleStyle();
        axisTitleStyle.setTypeface(Typeface.create("HelveticaNeue-Light", 0));
        xAxis.getStyle().setTitleStyle(axisTitleStyle);
        return xAxis;
    }

    private static NumberAxis createBaseYAxis(String title) {
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTitle(title);
        AxisTitleStyle axt = new AxisTitleStyle();
        axt.setTextSize(9.0f);
        axt.setOrientation(Title.Orientation.VERTICAL);
        axt.setMargin(0.0f);
        yAxis.getStyle().setTitleStyle(axt);
        TickStyle yStyle = new TickStyle();
        yStyle.setMajorTicksShown(false);
        yStyle.setLabelTextSize(8.0f);
        yAxis.getStyle().setTickStyle(yStyle);
        yAxis.getStyle().setLineWidth(1.0f);
        return yAxis;
    }

    private static ShinobiChart getBaseChart(Context context, NestableChartFragment fragment) {
        ShinobiChart shinobiChart = fragment.getShinobiChart();
        shinobiChart.getStyle().setPlotAreaBackgroundColor(context.getResources().getColor(17170443));
        shinobiChart.getStyle().setCanvasBackgroundColor(context.getResources().getColor(17170443));
        shinobiChart.getStyle().setBackgroundColor(context.getResources().getColor(17170443));
        return shinobiChart;
    }

    private static long zeroLastRanIfDataShouldntHaveChanged(long lastRan, Date endDate) {
        long endTime = endDate.getTime();
        if (endDate.getTime() >= lastRan) {
            return lastRan;
        }
        Log.d(TAG, "end date " + endTime + " is before lastran " + lastRan);
        return 0;
    }

    public static ArrayList<WaterUseResponse> getDailyUseData(Database database, String deviceId, long lastRan, Date start, Date end) {
        ArrayList<WaterUseResponse> waterUseResponses = new ArrayList();
        lastRan = zeroLastRanIfDataShouldntHaveChanged(lastRan, end);
        WaterUseResponse result = ReportingRestClient.getDailyWaterUseDataForDateRangeSchedule(database, ReportingRestClient.createTimeFrame(start, end), deviceId, lastRan);
        if (result == null) {
            return null;
        }
        waterUseResponses.add(result);
        WaterUseResponse result2 = ReportingRestClient.getDailyWaterUseDataForDateRangeIntelligence(database, ReportingRestClient.createTimeFrame(start, end), deviceId, lastRan);
        if (result2 == null) {
            return null;
        }
        waterUseResponses.add(result2);
        return waterUseResponses;
    }

    public static ShinobiChart getDailyWaterUseOrTimeChart$3c6cbfe2(Context context, User user, NestableChartFragment fragment, Date startDate, Date endDate, boolean waterUse, boolean pannable) {
        ShinobiChart shinobiChart = getBaseChart(context, fragment);
        DateTimeAxis xAxis = createBaseXAxis(context, new DateRange(startDate, endDate));
        xAxis.setLabelFormat(pannable ? dayNameAndDateformat : dayformat);
        if (pannable) {
            int startDay = 0;
            int endDay = 7;
            Date now = new Date();
            if (startDate.getMonth() == now.getMonth()) {
                endDay = now.getDate();
                startDay = Math.max(endDay - 7, 0);
            }
            Calendar zoomedStart = Calendar.getInstance();
            zoomedStart.set(2, startDate.getMonth());
            zoomedStart.set(5, startDay);
            CalendarUtil.setToStartOfDay(zoomedStart);
            Calendar zoomedEnd = Calendar.getInstance();
            zoomedEnd.set(2, startDate.getMonth());
            zoomedEnd.set(5, endDay);
            CalendarUtil.setToEndOfDay(zoomedStart);
            xAxis.setDefaultRange(new DateRange(zoomedStart.getTime(), zoomedEnd.getTime()));
        } else {
            List<Date> daysOfMonth = new ArrayList();
            Calendar xDays = Calendar.getInstance();
            xDays.set(2, startDate.getMonth());
            for (int i = 7; i < 35; i += 7) {
                xDays.set(5, i);
                daysOfMonth.add(xDays.getTime());
            }
            xAxis.setMajorTickMarkValues(daysOfMonth);
        }
        xAxis.enableGesturePanning(pannable);
        xAxis.enableGestureZooming(true);
        shinobiChart.setXAxis(xAxis);
        NumberAxis yAxis = createBaseYAxis(UnitUtils.getNameOfWaterUnits(user));
        yAxis.setRangePaddingHigh(Double.valueOf(100.0d));
        shinobiChart.setYAxis(yAxis);
        shinobiChart.getLegend().setVisibility(8);
        return shinobiChart;
    }

    public static ArrayList<WaterUseResponse> getMonthlyUseData(Database database, String deviceId, long lastRan, Date start, Date end) {
        lastRan = zeroLastRanIfDataShouldntHaveChanged(lastRan, end);
        ArrayList<WaterUseResponse> waterUseResponses = new ArrayList();
        WaterUseResponse result = ReportingRestClient.getMonthlyWaterUseDataForCurrentYearSchedule(database, ReportingRestClient.createTimeFrame(start, end), deviceId, lastRan);
        if (result == null) {
            return null;
        }
        waterUseResponses.add(result);
        WaterUseResponse result2 = ReportingRestClient.getMonthlyWaterUseDataForCurrentYearIntelligence(database, ReportingRestClient.createTimeFrame(start, end), deviceId, lastRan);
        if (result2 == null) {
            return null;
        }
        waterUseResponses.add(result2);
        return waterUseResponses;
    }

    public static ShinobiChart getMonthlyWaterUseOrTimeChart(Context context, NestableChartFragment fragment, String yAxisTitle, boolean waterUse, boolean pannable, int year) {
        ShinobiChart shinobiChart = getBaseChart(context, fragment);
        Calendar january = Calendar.getInstance();
        Calendar december = Calendar.getInstance();
        Calendar zoomedStart = Calendar.getInstance();
        Calendar zoomedEnd = Calendar.getInstance();
        zoomedStart.add(2, -2);
        zoomedStart.set(1, year);
        CalendarUtil.setToStartOfMonth(zoomedStart);
        zoomedEnd.set(1, year);
        zoomedEnd.add(2, 2);
        CalendarUtil.setToEndOfMonth(zoomedEnd);
        january.set(year, 0, 1, 0, 0);
        december.set(year, 11, 31, 23, 59);
        DateTimeAxis xAxis = createBaseXAxis(context, new DateRange(january.getTime(), december.getTime()));
        xAxis.setLabelFormat(monthFormat);
        xAxis.setMajorTickFrequency(new DateFrequency(1, Denomination.MONTHS));
        xAxis.setMinorTickFrequency(null);
        xAxis.enableGesturePanning(pannable);
        if (pannable) {
            xAxis.setDefaultRange(new DateRange(zoomedStart.getTime(), zoomedEnd.getTime()));
        }
        shinobiChart.setXAxis(xAxis);
        NumberAxis yAxis = createBaseYAxis(yAxisTitle);
        yAxis.setRangePaddingHigh(Double.valueOf(100.0d));
        shinobiChart.setYAxis(yAxis);
        return shinobiChart;
    }
}
