package com.rachio.iro.ui.fragment.dashboard;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rachio.iro.R;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand.FetchDeviceListener;
import com.rachio.iro.model.device.Device;
import com.rachio.iro.model.mapping.JsonMapper;
import com.rachio.iro.model.reporting.AnnualSummary;
import com.rachio.iro.model.reporting.WaterUseResponse;
import com.rachio.iro.model.reporting.WeatherIntelligence;
import com.rachio.iro.model.reporting.WeatherIntelligence.Result;
import com.rachio.iro.model.user.User;
import com.rachio.iro.reporting.ReportingRestClient;
import com.rachio.iro.reporting.ReportingUtils;
import com.rachio.iro.ui.activity.reporting.ReportDetailActivity;
import com.rachio.iro.ui.fragment.NestableChartFragment;
import com.rachio.iro.ui.view.reports.BasePicker.Listener;
import com.rachio.iro.ui.view.reports.MonthPicker;
import com.rachio.iro.ui.view.reports.YearPicker;
import com.rachio.iro.utils.StringUtils;
import com.rachio.iro.utils.UnitUtils;
import com.shinobicontrols.charts.Series;
import com.shinobicontrols.charts.ShinobiChart;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportsDashboardFragment extends BaseDeviceDashboardFragment implements FetchDeviceListener {
    private static final String communityWaterAnalysis;
    private static final String userWaterUseAnalysis;
    private static final String weatherIntelligenceAnalysis;
    private TextView commCubic;
    private TextView commGallons;
    private TextView commPool;
    private RunnableAsyncTask communityStatsLoader;
    private YearPicker communityUseYear;
    private NestableChartFragment dailyUseChart;
    private View dailyUseHolder;
    private MonthPicker dailyUseMonth;
    private ProgressBar dailyUsePbar;
    private long deviceLastRan = 0;
    private RunnableAsyncTask monthLoader;
    private NestableChartFragment monthlyUseChart;
    private View monthlyUseHolder;
    private YearPicker monthlyUseYear;
    private TextView rainDelays;
    private User user;
    private TextView userSaved;
    private RunnableAsyncTask userStatsLoader;
    private TextView userUsed;
    private RunnableAsyncTask yearLoader;
    private YearPicker yearToDateYear;
    private ProgressBar yearlyUsePbar;

    private abstract class RunnableAsyncTask extends AsyncTask<Void, Runnable, Void> {
        boolean loadFailed;

        private RunnableAsyncTask() {
            this.loadFailed = false;
        }

        protected /* bridge */ /* synthetic */ void onProgressUpdate(Object[] objArr) {
            Runnable[] runnableArr = (Runnable[]) objArr;
            if (!(isCancelled() || !ReportsDashboardFragment.this.isVisible() || runnableArr[0] == null)) {
                runnableArr[0].run();
            }
            if (runnableArr[0] == null) {
                this.loadFailed = true;
            }
        }
    }

    static /* synthetic */ Runnable access$3000(ReportsDashboardFragment x0) {
        int i = 0;
        Date start = x0.communityUseYear.getStart();
        WeatherIntelligence communityWaterWeatherIntelligenceForTimeFrame = ReportingRestClient.getCommunityWaterWeatherIntelligenceForTimeFrame(x0.database, start, x0.communityUseYear.getEnd(), communityWaterAnalysis);
        if (communityWaterWeatherIntelligenceForTimeFrame == null) {
            return null;
        }
        int i2;
        int year = start.getYear() + 1900;
        if (year == 2014 || year == 2015) {
            i2 = 1;
        } else {
            i2 = 0;
        }
        double d = ((Result) communityWaterWeatherIntelligenceForTimeFrame.result.get(0)).value.totalWaterSaved;
        if (i2 != 0) {
            i = 5000000;
        }
        final Double valueOf = Double.valueOf(((double) i) + d);
        return new Runnable() {
            public void run() {
                double cubicFeet = ((double) valueOf.floatValue()) * 0.133681d;
                double swimmingPools = ((double) valueOf.floatValue()) / 660000.0d;
                double waterSaved = UnitUtils.convertGallonsToUserUnits(ReportsDashboardFragment.this.user, valueOf.doubleValue());
                double waterSavedVolume = UnitUtils.convertCubicFeetToUserUnits(ReportsDashboardFragment.this.user, cubicFeet);
                String gallonsLabel = String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(Math.round(waterSaved)));
                String cubicFeetLabel = String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(Math.round(waterSavedVolume)));
                String swimmingPoolLabel = String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(Math.round(swimmingPools)));
                ReportsDashboardFragment.this.commGallons.setText(gallonsLabel);
                ReportsDashboardFragment.this.commCubic.setText(cubicFeetLabel);
                ReportsDashboardFragment.this.commPool.setText(swimmingPoolLabel);
            }
        };
    }

    public static ReportsDashboardFragment newInstance(String deviceId) {
        ReportsDashboardFragment fragment = new ReportsDashboardFragment();
        fragment.setArguments(BaseDeviceDashboardFragment.createArgs(deviceId));
        return fragment;
    }

    private synchronized void loadMonth() {
        if (this.monthLoader != null) {
            this.monthLoader.cancel(true);
        }
        this.monthLoader = new RunnableAsyncTask() {
            protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                super.onPostExecute((Void) obj);
                ReportsDashboardFragment.this.monthLoader = null;
            }

            protected void onPreExecute() {
                ReportsDashboardFragment.this.dailyUseHolder.setVisibility(4);
                ReportsDashboardFragment.this.dailyUsePbar.setVisibility(0);
            }

            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                publishProgress(new Runnable[]{ReportsDashboardFragment.access$400(ReportsDashboardFragment.this, ReportsDashboardFragment.this.mDeviceId, ReportsDashboardFragment.this.deviceLastRan)});
                return null;
            }
        };
        this.monthLoader.execute(new Void[0]);
    }

    private synchronized void loadYear() {
        if (this.yearLoader != null) {
            this.yearLoader.cancel(true);
        }
        this.yearLoader = new RunnableAsyncTask() {
            protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                super.onPostExecute((Void) obj);
                ReportsDashboardFragment.this.yearLoader = null;
            }

            protected void onPreExecute() {
                ReportsDashboardFragment.this.monthlyUseHolder.setVisibility(4);
                ReportsDashboardFragment.this.yearlyUsePbar.setVisibility(0);
            }

            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                publishProgress(new Runnable[]{ReportsDashboardFragment.access$800(ReportsDashboardFragment.this, ReportsDashboardFragment.this.mDeviceId, ReportsDashboardFragment.this.deviceLastRan)});
                return null;
            }
        };
        this.yearLoader.execute(new Void[0]);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_dashboardreports, container, false);
        this.dailyUseChart = new NestableChartFragment();
        this.monthlyUseChart = new NestableChartFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add((int) R.id.daily_headerHolder, this.dailyUseChart);
        ft.add((int) R.id.reports_yearly_use_holder, this.monthlyUseChart);
        ft.commit();
        this.user = User.getLoggedInUser(this.database, this.prefsWrapper);
        String waterUnits = UnitUtils.getNameOfWaterUnits(this.user);
        String waterVolumeUnits = UnitUtils.getNameOfWaterVolumeUnits(this.user);
        TextView userSavedLabel = (TextView) content.findViewById(R.id.reports_user_saved_label);
        TextView communitySavedLabel = (TextView) content.findViewById(R.id.reports_community_saved_label);
        TextView communitySavedVolumeLabel = (TextView) content.findViewById(R.id.reports_community_savedvolume_label);
        ((TextView) content.findViewById(R.id.reports_user_used_label)).setText(String.format("Water Used (%s)", new Object[]{waterUnits}));
        userSavedLabel.setText(String.format("Water Saved (%s)", new Object[]{waterUnits}));
        communitySavedLabel.setText(waterUnits);
        communitySavedVolumeLabel.setText(waterVolumeUnits);
        this.yearToDateYear = (YearPicker) content.findViewById(R.id.reports_user_year_to_date_year);
        this.userUsed = (TextView) content.findViewById(R.id.reports_user_used);
        this.userSaved = (TextView) content.findViewById(R.id.reports_user_saved);
        this.rainDelays = (TextView) content.findViewById(R.id.reports_user_raindelays);
        this.communityUseYear = (YearPicker) content.findViewById(R.id.reports_community_year);
        this.commGallons = (TextView) content.findViewById(R.id.reports_community_saved);
        this.commCubic = (TextView) content.findViewById(R.id.reports_community_saved_volume);
        this.commPool = (TextView) content.findViewById(R.id.reports_community_saved_swimmingpools);
        this.monthlyUseYear = (YearPicker) content.findViewById(R.id.reports_montly_use_year);
        this.dailyUsePbar = (ProgressBar) content.findViewById(R.id.pBar_dailyUse);
        this.yearlyUsePbar = (ProgressBar) content.findViewById(R.id.reports_yearly_use_progressbar);
        this.dailyUseHolder = content.findViewById(R.id.daily_headerHolder);
        this.monthlyUseHolder = content.findViewById(R.id.reports_yearly_use_holder);
        FrameLayout dailyUseCover = (FrameLayout) content.findViewById(R.id.reports_daily_use_cover);
        FrameLayout yearlyUseCover = (FrameLayout) content.findViewById(R.id.reports_yearly_use_cover);
        this.dailyUseMonth = (MonthPicker) content.findViewById(R.id.reports_daily_use_month);
        this.yearToDateYear.setListener(new Listener() {
            public final void onMonthChanged$ed1f2c9() {
                if (ReportsDashboardFragment.this.userStatsLoader != null) {
                    ReportsDashboardFragment.this.userStatsLoader.cancel(true);
                }
                ReportsDashboardFragment.this.loadUserStats();
            }
        });
        this.communityUseYear.setListener(new Listener() {
            public final void onMonthChanged$ed1f2c9() {
                if (ReportsDashboardFragment.this.communityStatsLoader != null) {
                    ReportsDashboardFragment.this.communityStatsLoader.cancel(true);
                }
                ReportsDashboardFragment.this.loadCommunityStats();
            }
        });
        this.dailyUseMonth.setListener(new Listener() {
            public final void onMonthChanged$ed1f2c9() {
                if (ReportsDashboardFragment.this.monthLoader != null) {
                    ReportsDashboardFragment.this.monthLoader.cancel(true);
                }
                ReportsDashboardFragment.this.loadMonth();
            }
        });
        this.monthlyUseYear.setListener(new Listener() {
            public final void onMonthChanged$ed1f2c9() {
                if (ReportsDashboardFragment.this.yearLoader != null) {
                    ReportsDashboardFragment.this.yearLoader.cancel(true);
                }
                ReportsDashboardFragment.this.loadYear();
            }
        });
        dailyUseCover.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ReportsDashboardFragment.access$1700(ReportsDashboardFragment.this, "daily_use", ReportsDashboardFragment.this.dailyUseMonth.getStart());
            }
        });
        yearlyUseCover.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ReportsDashboardFragment.access$1700(ReportsDashboardFragment.this, "monthly_use", ReportsDashboardFragment.this.monthlyUseYear.getStart());
            }
        });
        return content;
    }

    public void onResume() {
        super.onResume();
        loadDevice();
    }

    private void loadDevice() {
        if (this.fetchDeviceCommand == null) {
            showProgress((int) R.string.progress_text_loading_device_information);
            this.fetchDeviceCommand = new FetchDeviceCommand(this, this.mDeviceId);
            this.fetchDeviceCommand.execute();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.userStatsLoader != null) {
            this.userStatsLoader.cancel(true);
        }
        if (this.communityStatsLoader != null) {
            this.communityStatsLoader.cancel(true);
        }
        if (this.monthLoader != null) {
            this.monthLoader.cancel(true);
        }
        if (this.yearLoader != null) {
            this.yearLoader.cancel(true);
        }
    }

    static {
        Map<String, Map<String, String>> analysisMap = new HashMap();
        Map<String, String> totalSchedulesRun = new HashMap();
        Map<String, String> totalWaterUsed = new HashMap();
        Map<String, String> totalWateringTime = new HashMap();
        totalSchedulesRun.put("target_property", "event.scheduleId");
        totalSchedulesRun.put("analysis_type", "count_unique");
        totalWaterUsed.put("target_property", "event.estimatedGallons");
        totalWaterUsed.put("analysis_type", "sum");
        totalWateringTime.put("target_property", "event.duration");
        totalWateringTime.put("analysis_type", "sum");
        analysisMap.put("totalSchedulesRun", totalSchedulesRun);
        analysisMap.put("totalWaterUsed", totalWaterUsed);
        analysisMap.put("totalWateringTime", totalWateringTime);
        userWaterUseAnalysis = JsonMapper.toJson(analysisMap);
        Map<String, Map<String, String>> analysisMap2 = new HashMap();
        Map<String, String> totalWaterSaved = new HashMap();
        Map<String, String> totalRainDelayTime = new HashMap();
        Map<String, String> totalRainDelays = new HashMap();
        totalWaterSaved.put("target_property", "event.estimatedGallons");
        totalWaterSaved.put("analysis_type", "sum");
        totalRainDelayTime.put("target_property", "event.duration");
        totalRainDelayTime.put("analysis_type", "sum");
        totalRainDelays.put("analysis_type", "count");
        analysisMap2.put("totalWaterSaved", totalWaterSaved);
        analysisMap2.put("totalRainDelayTime", totalRainDelayTime);
        analysisMap2.put("totalRainDelays", totalRainDelays);
        weatherIntelligenceAnalysis = JsonMapper.toJson(analysisMap2);
        analysisMap2 = new HashMap();
        totalWaterSaved = new HashMap();
        totalWaterSaved.put("target_property", "event.estimatedGallons");
        totalWaterSaved.put("analysis_type", "sum");
        analysisMap2.put("totalWaterSaved", totalWaterSaved);
        communityWaterAnalysis = JsonMapper.toJson(analysisMap2);
    }

    public final void onDeviceLoaded(Device device) {
        this.fetchDeviceCommand = null;
        if (device != null) {
            Date deviceLastRunDate = device.getLastRunDate();
            this.deviceLastRan = deviceLastRunDate != null ? deviceLastRunDate.getTime() : 0;
            Calendar rachioFounded = Calendar.getInstance();
            rachioFounded.set(2015, 0, 0);
            this.yearToDateYear.setLowerLimit(device.createDate);
            this.communityUseYear.setLowerLimit(rachioFounded.getTime());
            this.dailyUseMonth.setLowerLimit(device.createDate);
            this.monthlyUseYear.setLowerLimit(device.createDate);
            loadUserStats();
            loadCommunityStats();
            loadMonth();
            loadYear();
        }
    }

    private void loadUserStats() {
        this.userStatsLoader = new RunnableAsyncTask() {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                if (!isCancelled()) {
                    publishProgress(new Runnable[]{ReportsDashboardFragment.access$2800(ReportsDashboardFragment.this)});
                }
                if (!isCancelled()) {
                    publishProgress(new Runnable[]{ReportsDashboardFragment.access$2900(ReportsDashboardFragment.this)});
                }
                return null;
            }

            protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                super.onPostExecute((Void) obj);
                ReportsDashboardFragment.this.userStatsLoader = null;
            }
        };
        this.userStatsLoader.execute(new Void[]{null});
    }

    private void loadCommunityStats() {
        this.communityStatsLoader = new RunnableAsyncTask() {
            protected /* bridge */ /* synthetic */ void onPostExecute(Object obj) {
                super.onPostExecute((Void) obj);
                ReportsDashboardFragment.this.communityStatsLoader = null;
            }

            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                if (!isCancelled()) {
                    publishProgress(new Runnable[]{ReportsDashboardFragment.access$3000(ReportsDashboardFragment.this)});
                }
                return null;
            }
        };
        this.communityStatsLoader.execute(new Void[]{null});
    }

    public final String getSection() {
        return getString(R.string.navigation_section_reports);
    }

    public final void onDeviceDataChanged(String deviceId) {
        if (StringUtils.equals(this.mDeviceId, deviceId)) {
            loadDevice();
        }
    }

    public final void onSelectedDeviceChanged(String newDeviceId) {
        super.onSelectedDeviceChanged(newDeviceId);
        this.dailyUseHolder.setVisibility(4);
        this.monthlyUseHolder.setVisibility(4);
    }

    static /* synthetic */ Runnable access$400(ReportsDashboardFragment x0, String x1, long x2) {
        final Date start = x0.dailyUseMonth.getStart();
        final Date end = x0.dailyUseMonth.getEnd();
        final List dailyUseData = ReportingUtils.getDailyUseData(x0.database, x1, x2, start, end);
        return dailyUseData != null ? new Runnable() {
            public void run() {
                ShinobiChart shinobiChart = ReportingUtils.getDailyWaterUseOrTimeChart$3c6cbfe2(ReportsDashboardFragment.this.getActivity(), ReportsDashboardFragment.this.user, ReportsDashboardFragment.this.dailyUseChart, start, end, true, false);
                List<Series<?>> series = new ArrayList();
                for (Series<?> s : shinobiChart.getSeries()) {
                    series.add(s);
                }
                for (Series<?> s2 : series) {
                    shinobiChart.removeSeries(s2);
                }
                shinobiChart.addSeries(ReportingUtils.getSeriesForDailyWatering(ReportsDashboardFragment.this.getActivity(), ReportsDashboardFragment.this.user, (WaterUseResponse) dailyUseData.get(0), R.color.rachio_blue, true));
                shinobiChart.addSeries(ReportingUtils.getSeriesForDailyWatering(ReportsDashboardFragment.this.getActivity(), ReportsDashboardFragment.this.user, (WaterUseResponse) dailyUseData.get(1), R.color.rachio_green, true));
                ReportsDashboardFragment.this.dailyUseHolder.setVisibility(0);
                ReportsDashboardFragment.this.dailyUsePbar.setVisibility(8);
            }
        } : null;
    }

    static /* synthetic */ Runnable access$800(ReportsDashboardFragment x0, String x1, long x2) {
        final Date start = x0.monthlyUseYear.getStart();
        final ArrayList monthlyUseData = ReportingUtils.getMonthlyUseData(x0.database, x1, x2, start, x0.monthlyUseYear.getEnd());
        return monthlyUseData != null ? new Runnable() {
            public void run() {
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(start);
                ShinobiChart shinobiChart = ReportingUtils.getMonthlyWaterUseOrTimeChart(ReportsDashboardFragment.this.getActivity(), ReportsDashboardFragment.this.monthlyUseChart, UnitUtils.getNameOfWaterUnits(ReportsDashboardFragment.this.user), true, false, startCal.get(1));
                List<Series<?>> series = new ArrayList();
                for (Series<?> s : shinobiChart.getSeries()) {
                    series.add(s);
                }
                for (Series<?> s2 : series) {
                    shinobiChart.removeSeries(s2);
                }
                shinobiChart.addSeries(ReportingUtils.getSeriesForMonthlyWatering(ReportsDashboardFragment.this.getActivity(), ReportsDashboardFragment.this.user, (WaterUseResponse) monthlyUseData.get(0), R.color.rachio_blue, true));
                shinobiChart.addSeries(ReportingUtils.getSeriesForMonthlyWatering(ReportsDashboardFragment.this.getActivity(), ReportsDashboardFragment.this.user, (WaterUseResponse) monthlyUseData.get(1), R.color.rachio_green, true));
                shinobiChart.redrawChart();
                ReportsDashboardFragment.this.yearlyUsePbar.setVisibility(8);
                ReportsDashboardFragment.this.monthlyUseHolder.setVisibility(0);
            }
        } : null;
    }

    static /* synthetic */ void access$1700(ReportsDashboardFragment x0, String x1, Date x2) {
        Intent intent = new Intent(x0.getActivity(), ReportDetailActivity.class);
        intent.putExtra("type", x1);
        intent.putExtra("DEVICEID", x0.mDeviceId);
        if (x2 != null) {
            intent.putExtra("month", x2.getMonth());
            intent.putExtra("year", x2.getYear() + 1900);
        }
        x0.startActivity(intent);
    }

    static /* synthetic */ Runnable access$2800(ReportsDashboardFragment x0) {
        AnnualSummary annualWaterUseSummaryStatsForTimeFrame = ReportingRestClient.getAnnualWaterUseSummaryStatsForTimeFrame(x0.database, x0.yearToDateYear.getStart(), x0.yearToDateYear.getEnd(), x0.mDeviceId, 0, userWaterUseAnalysis);
        if (annualWaterUseSummaryStatsForTimeFrame == null) {
            return null;
        }
        final double convertGallonsToUserUnits = UnitUtils.convertGallonsToUserUnits(x0.user, ((AnnualSummary.Result) annualWaterUseSummaryStatsForTimeFrame.result.get(0)).value.totalWaterUsed);
        return new Runnable() {
            public void run() {
                ReportsDashboardFragment.this.userUsed.setText(String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(Math.round(convertGallonsToUserUnits))));
            }
        };
    }

    static /* synthetic */ Runnable access$2900(ReportsDashboardFragment x0) {
        final WeatherIntelligence annualWaterWeatherIntelligenceForCurrentYear = ReportingRestClient.getAnnualWaterWeatherIntelligenceForCurrentYear(x0.database, x0.mDeviceId, 0, weatherIntelligenceAnalysis);
        return annualWaterWeatherIntelligenceForCurrentYear != null ? new Runnable() {
            public void run() {
                ReportsDashboardFragment.this.userSaved.setText(String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(Math.round(UnitUtils.convertGallonsToUserUnits(ReportsDashboardFragment.this.user, ((Result) annualWaterWeatherIntelligenceForCurrentYear.result.get(0)).value.totalWaterSaved)))));
                ReportsDashboardFragment.this.rainDelays.setText(String.valueOf(((Result) annualWaterWeatherIntelligenceForCurrentYear.result.get(0)).value.totalRainDelays));
            }
        } : null;
    }
}
