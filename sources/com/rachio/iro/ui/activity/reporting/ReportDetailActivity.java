package com.rachio.iro.ui.activity.reporting;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.reporting.WaterUseResponse;
import com.rachio.iro.model.user.User;
import com.rachio.iro.reporting.ReportingUtils;
import com.rachio.iro.ui.fragment.NestableChartFragment;
import com.rachio.iro.ui.view.reports.BasePicker.Listener;
import com.rachio.iro.ui.view.reports.MonthPicker;
import com.rachio.iro.ui.view.reports.YearPicker;
import com.rachio.iro.utils.UnitUtils;
import com.shinobicontrols.charts.Series;
import com.shinobicontrols.charts.ShinobiChart;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportDetailActivity extends BaseReportsActivity implements FetchDeviceListener {
    private FrameLayout chartHolder;
    private Device device;
    private String deviceId;
    private FetchDeviceCommand fetchDeviceCommand;
    private AsyncTask<Void, Void, Runnable> loader;
    private MonthPicker monthPicker;
    private ProgressBar progressBar;
    private String type;
    private YearPicker yearPicker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_report_detail);
        wireupToolbarActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.deviceId = getDeviceIdFromExtras();
        Intent intent = getIntent();
        this.type = intent.getStringExtra("type");
        if (this.type == null) {
            throw new IllegalStateException();
        }
        this.monthPicker = (MonthPicker) findViewById(R.id.reports_detail_month);
        this.yearPicker = (YearPicker) findViewById(R.id.reports_detail_year);
        this.chartHolder = (FrameLayout) findViewById(R.id.reports_detail_chart_holder);
        this.progressBar = (ProgressBar) findViewById(R.id.reports_detail_progressbar);
        if (intent.hasExtra("month") && intent.hasExtra("year")) {
            this.monthPicker.setMonthAndYear(intent.getIntExtra("month", -1), intent.getIntExtra("year", -1));
            this.yearPicker.setMonthAndYear(intent.getIntExtra("month", -1), intent.getIntExtra("year", -1));
        }
        if (this.type.equals("daily_use")) {
            this.yearPicker.setVisibility(8);
            getSupportActionBar().setSubtitle("Daily Water Use");
        } else if (this.type.equals("monthly_use")) {
            this.monthPicker.setVisibility(8);
            getSupportActionBar().setSubtitle("Monthly Water Use");
        } else {
            throw new IllegalStateException();
        }
        this.monthPicker.setListener(new Listener() {
            public final void onMonthChanged$ed1f2c9() {
                ReportDetailActivity.this.load();
            }
        });
        this.yearPicker.setListener(new Listener() {
            public final void onMonthChanged$ed1f2c9() {
                ReportDetailActivity.this.load();
            }
        });
    }

    private void load() {
        if (this.device != null) {
            if (this.loader != null) {
                this.loader.cancel(true);
            }
            this.loader = new AsyncTask<Void, Void, Runnable>() {
                protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                    final User localUser = ReportDetailActivity.this.device.getLocalUser();
                    Date lastRunDate = ReportDetailActivity.this.device.getLastRunDate();
                    long time = lastRunDate != null ? lastRunDate.getTime() : 0;
                    final ArrayList dailyUseData;
                    if (ReportDetailActivity.this.type.equals("daily_use")) {
                        dailyUseData = ReportingUtils.getDailyUseData(ReportDetailActivity.this.database, ReportDetailActivity.this.device.id, time, ReportDetailActivity.this.monthPicker.getStart(), ReportDetailActivity.this.monthPicker.getEnd());
                        if (dailyUseData != null) {
                            return new Runnable() {
                                public void run() {
                                    ShinobiChart shinobiChart = ReportingUtils.getDailyWaterUseOrTimeChart$3c6cbfe2(ReportDetailActivity.this, localUser, (NestableChartFragment) ReportDetailActivity.this.getSupportFragmentManager().findFragmentById(R.id.reports_detail_chart), ReportDetailActivity.this.monthPicker.getStart(), ReportDetailActivity.this.monthPicker.getEnd(), true, true);
                                    List<Series<?>> series = new ArrayList();
                                    for (Series<?> s : shinobiChart.getSeries()) {
                                        series.add(s);
                                    }
                                    for (Series<?> s2 : series) {
                                        shinobiChart.removeSeries(s2);
                                    }
                                    shinobiChart.addSeries(ReportingUtils.getSeriesForDailyWatering(ReportDetailActivity.this, localUser, (WaterUseResponse) dailyUseData.get(0), R.color.rachio_blue, true));
                                    shinobiChart.addSeries(ReportingUtils.getSeriesForDailyWatering(ReportDetailActivity.this, localUser, (WaterUseResponse) dailyUseData.get(1), R.color.rachio_green, true));
                                }
                            };
                        }
                    } else if (ReportDetailActivity.this.type.equals("monthly_use")) {
                        final Date start = ReportDetailActivity.this.yearPicker.getStart();
                        dailyUseData = ReportingUtils.getMonthlyUseData(ReportDetailActivity.this.database, ReportDetailActivity.this.deviceId, 0, start, ReportDetailActivity.this.yearPicker.getEnd());
                        if (dailyUseData != null) {
                            return new Runnable() {
                                public void run() {
                                    Calendar startCal = Calendar.getInstance();
                                    startCal.setTime(start);
                                    ShinobiChart shinobiChart = ReportingUtils.getMonthlyWaterUseOrTimeChart(ReportDetailActivity.this, (NestableChartFragment) ReportDetailActivity.this.getSupportFragmentManager().findFragmentById(R.id.reports_detail_chart), UnitUtils.getNameOfWaterUnits(localUser), true, true, startCal.get(1));
                                    List<Series<?>> series = new ArrayList();
                                    for (Series<?> s : shinobiChart.getSeries()) {
                                        series.add(s);
                                    }
                                    for (Series<?> s2 : series) {
                                        shinobiChart.removeSeries(s2);
                                    }
                                    shinobiChart.addSeries(ReportingUtils.getSeriesForMonthlyWatering(ReportDetailActivity.this, localUser, (WaterUseResponse) dailyUseData.get(0), R.color.rachio_blue, true));
                                    shinobiChart.addSeries(ReportingUtils.getSeriesForMonthlyWatering(ReportDetailActivity.this, localUser, (WaterUseResponse) dailyUseData.get(1), R.color.rachio_green, true));
                                }
                            };
                        }
                    } else {
                        throw new IllegalStateException();
                    }
                    return null;
                }

                protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                    Runnable runnable = (Runnable) obj;
                    if (!isCancelled()) {
                        if (runnable != null) {
                            runnable.run();
                            ReportDetailActivity.this.chartHolder.setVisibility(0);
                        } else {
                            ReportDetailActivity.this.toastGenericError();
                        }
                        ReportDetailActivity.this.progressBar.setVisibility(8);
                        ReportDetailActivity.this.loader = null;
                    }
                }

                protected void onPreExecute() {
                    super.onPreExecute();
                    ReportDetailActivity.this.chartHolder.setVisibility(8);
                    ReportDetailActivity.this.progressBar.setVisibility(0);
                }
            };
            this.loader.execute(new Void[0]);
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.fetchDeviceCommand == null) {
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.deviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.loader != null) {
            this.loader.cancel(true);
        }
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        this.device = device;
        this.monthPicker.setLowerLimit(device.createDate);
        this.yearPicker.setLowerLimit(device.createDate);
        load();
    }
}
