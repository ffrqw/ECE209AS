package com.rachio.iro;

import com.rachio.iro.async.command.FetchAndCopyScheduleRuleCommand;
import com.rachio.iro.async.command.FetchCalendarCommand;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchEventsCommand;
import com.rachio.iro.async.command.FetchIroPropertiesCommand;
import com.rachio.iro.async.command.FetchNozzlesCommand;
import com.rachio.iro.async.command.FetchUnbornDataCommand;
import com.rachio.iro.async.command.FetchUserCommand;
import com.rachio.iro.async.command.FetchWaterJournalCommand;
import com.rachio.iro.async.command.FetchWaterUsageCommand;
import com.rachio.iro.async.command.FetchWeatherForecastCommand;
import com.rachio.iro.async.command.FetchWeatherStationsCommand;
import com.rachio.iro.async.command.FetchZoneCommand;
import com.rachio.iro.async.command.FetchZonesCommand;
import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.fcm.EventHandler;
import com.rachio.iro.gen2.MrvlProvService;
import com.rachio.iro.gen2.ProvActivity;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.DebugActivity;
import com.rachio.iro.ui.activity.device.HistoryActivity;
import com.rachio.iro.ui.activity.device.RemoteControlActivity;
import com.rachio.iro.ui.activity.user.LoginProgressActivity;
import com.rachio.iro.ui.activity.user.MyNozzlesActivity;
import com.rachio.iro.ui.activity.user.PasswordResetActivity;
import com.rachio.iro.ui.activity.user.RegistrationActivity;
import com.rachio.iro.ui.activity.zone.NozzleConfigurationActivity;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.Holder;

public interface IroGraph {
    void inject(BootCompleteReceiver bootCompleteReceiver);

    void inject(IroApplication iroApplication);

    void inject(FetchAndCopyScheduleRuleCommand fetchAndCopyScheduleRuleCommand);

    void inject(FetchCalendarCommand fetchCalendarCommand);

    void inject(FetchDeviceCommand fetchDeviceCommand);

    void inject(FetchEventsCommand fetchEventsCommand);

    void inject(FetchIroPropertiesCommand fetchIroPropertiesCommand);

    void inject(FetchNozzlesCommand fetchNozzlesCommand);

    void inject(FetchUnbornDataCommand fetchUnbornDataCommand);

    void inject(FetchUserCommand fetchUserCommand);

    void inject(FetchWaterJournalCommand fetchWaterJournalCommand);

    void inject(FetchWaterUsageCommand fetchWaterUsageCommand);

    void inject(FetchWeatherForecastCommand fetchWeatherForecastCommand);

    void inject(FetchWeatherStationsCommand fetchWeatherStationsCommand);

    void inject(FetchZoneCommand fetchZoneCommand);

    void inject(FetchZonesCommand fetchZonesCommand);

    void inject(PushPull pushPull);

    void inject(EventHandler eventHandler);

    void inject(MrvlProvService mrvlProvService);

    void inject(ProvActivity provActivity);

    void inject(BaseActivity baseActivity);

    void inject(DebugActivity debugActivity);

    void inject(HistoryActivity historyActivity);

    void inject(RemoteControlActivity remoteControlActivity);

    void inject(LoginProgressActivity loginProgressActivity);

    void inject(MyNozzlesActivity myNozzlesActivity);

    void inject(PasswordResetActivity passwordResetActivity);

    void inject(RegistrationActivity registrationActivity);

    void inject(NozzleConfigurationActivity nozzleConfigurationActivity);

    void inject(BaseFragment baseFragment);

    void inject(Holder holder);
}
