package com.rachio.iro;

import com.google.android.gms.analytics.Tracker;
import com.rachio.iro.async.command.FetchAndCopyScheduleRuleCommand;
import com.rachio.iro.async.command.FetchAndCopyScheduleRuleCommand_MembersInjector;
import com.rachio.iro.async.command.FetchCalendarCommand;
import com.rachio.iro.async.command.FetchCalendarCommand_MembersInjector;
import com.rachio.iro.async.command.FetchDeviceCommand;
import com.rachio.iro.async.command.FetchDeviceCommand_MembersInjector;
import com.rachio.iro.async.command.FetchEventsCommand;
import com.rachio.iro.async.command.FetchEventsCommand_MembersInjector;
import com.rachio.iro.async.command.FetchIroPropertiesCommand;
import com.rachio.iro.async.command.FetchIroPropertiesCommand_MembersInjector;
import com.rachio.iro.async.command.FetchNozzlesCommand;
import com.rachio.iro.async.command.FetchNozzlesCommand_MembersInjector;
import com.rachio.iro.async.command.FetchUnbornDataCommand;
import com.rachio.iro.async.command.FetchUnbornDataCommand_MembersInjector;
import com.rachio.iro.async.command.FetchUserCommand;
import com.rachio.iro.async.command.FetchUserCommand_MembersInjector;
import com.rachio.iro.async.command.FetchWaterJournalCommand;
import com.rachio.iro.async.command.FetchWaterJournalCommand_MembersInjector;
import com.rachio.iro.async.command.FetchWaterUsageCommand;
import com.rachio.iro.async.command.FetchWaterUsageCommand_MembersInjector;
import com.rachio.iro.async.command.FetchWeatherForecastCommand;
import com.rachio.iro.async.command.FetchWeatherForecastCommand_MembersInjector;
import com.rachio.iro.async.command.FetchWeatherStationsCommand;
import com.rachio.iro.async.command.FetchWeatherStationsCommand_MembersInjector;
import com.rachio.iro.async.command.FetchZoneCommand;
import com.rachio.iro.async.command.FetchZoneCommand_MembersInjector;
import com.rachio.iro.async.command.FetchZonesCommand;
import com.rachio.iro.async.command.FetchZonesCommand_MembersInjector;
import com.rachio.iro.cloud.PushPull;
import com.rachio.iro.cloud.PushPull_MembersInjector;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.fcm.EventHandler;
import com.rachio.iro.fcm.EventHandler_MembersInjector;
import com.rachio.iro.gen2.MrvlProvService;
import com.rachio.iro.gen2.MrvlProvService_MembersInjector;
import com.rachio.iro.gen2.ProvActivity;
import com.rachio.iro.gen2.ProvActivity_MembersInjector;
import com.rachio.iro.model.db.Database;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.ui.activity.BaseActivity_MembersInjector;
import com.rachio.iro.ui.activity.DebugActivity;
import com.rachio.iro.ui.activity.DebugActivity_MembersInjector;
import com.rachio.iro.ui.activity.device.HistoryActivity;
import com.rachio.iro.ui.activity.device.HistoryActivity_MembersInjector;
import com.rachio.iro.ui.activity.device.RemoteControlActivity;
import com.rachio.iro.ui.activity.device.RemoteControlActivity_MembersInjector;
import com.rachio.iro.ui.activity.user.LoginProgressActivity;
import com.rachio.iro.ui.activity.user.LoginProgressActivity_MembersInjector;
import com.rachio.iro.ui.activity.user.MyNozzlesActivity;
import com.rachio.iro.ui.activity.user.MyNozzlesActivity_MembersInjector;
import com.rachio.iro.ui.activity.user.PasswordResetActivity;
import com.rachio.iro.ui.activity.user.PasswordResetActivity_MembersInjector;
import com.rachio.iro.ui.activity.user.RegistrationActivity;
import com.rachio.iro.ui.activity.user.RegistrationActivity_MembersInjector;
import com.rachio.iro.ui.activity.zone.NozzleConfigurationActivity;
import com.rachio.iro.ui.activity.zone.NozzleConfigurationActivity_MembersInjector;
import com.rachio.iro.ui.activity.zone.ZoneDetailsActivity;
import com.rachio.iro.ui.activity.zone.ZoneDetailsActivity_MembersInjector;
import com.rachio.iro.ui.fragment.BaseFragment;
import com.rachio.iro.ui.fragment.BaseFragment_MembersInjector;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask.Holder;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask_Holder_MembersInjector;
import com.rachio.iro.utils.UiThreadExecutor;
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class DaggerIroComponent implements IroComponent {
    static final /* synthetic */ boolean $assertionsDisabled = (!DaggerIroComponent.class.desiredAssertionStatus());
    private MembersInjector<BaseActivity> baseActivityMembersInjector;
    private MembersInjector<BaseFragment> baseFragmentMembersInjector;
    private MembersInjector<BootCompleteReceiver> bootCompleteReceiverMembersInjector;
    private MembersInjector<DebugActivity> debugActivityMembersInjector;
    private MembersInjector<EventHandler> eventHandlerMembersInjector;
    private MembersInjector<FetchAndCopyScheduleRuleCommand> fetchAndCopyScheduleRuleCommandMembersInjector;
    private MembersInjector<FetchCalendarCommand> fetchCalendarCommandMembersInjector;
    private MembersInjector<FetchDeviceCommand> fetchDeviceCommandMembersInjector;
    private MembersInjector<FetchEventsCommand> fetchEventsCommandMembersInjector;
    private MembersInjector<FetchIroPropertiesCommand> fetchIroPropertiesCommandMembersInjector;
    private MembersInjector<FetchNozzlesCommand> fetchNozzlesCommandMembersInjector;
    private MembersInjector<FetchUnbornDataCommand> fetchUnbornDataCommandMembersInjector;
    private MembersInjector<FetchUserCommand> fetchUserCommandMembersInjector;
    private MembersInjector<FetchWaterJournalCommand> fetchWaterJournalCommandMembersInjector;
    private MembersInjector<FetchWaterUsageCommand> fetchWaterUsageCommandMembersInjector;
    private MembersInjector<FetchWeatherForecastCommand> fetchWeatherForecastCommandMembersInjector;
    private MembersInjector<FetchWeatherStationsCommand> fetchWeatherStationsCommandMembersInjector;
    private MembersInjector<FetchZoneCommand> fetchZoneCommandMembersInjector;
    private MembersInjector<FetchZonesCommand> fetchZonesCommandMembersInjector;
    private MembersInjector<HistoryActivity> historyActivityMembersInjector;
    private MembersInjector<Holder> holderMembersInjector;
    private MembersInjector<IroApplication> iroApplicationMembersInjector;
    private MembersInjector<LoginProgressActivity> loginProgressActivityMembersInjector;
    private MembersInjector<MrvlProvService> mrvlProvServiceMembersInjector;
    private MembersInjector<MyNozzlesActivity> myNozzlesActivityMembersInjector;
    private MembersInjector<NozzleConfigurationActivity> nozzleConfigurationActivityMembersInjector;
    private MembersInjector<PasswordResetActivity> passwordResetActivityMembersInjector;
    private MembersInjector<ProvActivity> provActivityMembersInjector;
    private Provider<Database> provideDatabaseProvider;
    private Provider<PrefsWrapper> providePrefsWrapperProvider;
    private Provider<RestClient> provideRestClientProvider;
    private Provider<Tracker> provideTrackerProvider;
    private Provider<Executor> providesBackgroundThreadPoolProvider;
    private Provider<UiThreadExecutor> providesUiThreadExecutorProvider;
    private MembersInjector<PushPull> pushPullMembersInjector;
    private MembersInjector<RegistrationActivity> registrationActivityMembersInjector;
    private MembersInjector<RemoteControlActivity> remoteControlActivityMembersInjector;
    private MembersInjector<ZoneDetailsActivity> zoneDetailsActivityMembersInjector;

    public static final class Builder {
        private IroAppModule iroAppModule;

        private Builder() {
        }

        public final IroComponent build() {
            if (this.iroAppModule != null) {
                return new DaggerIroComponent();
            }
            throw new IllegalStateException(IroAppModule.class.getCanonicalName() + " must be set");
        }

        public final Builder iroAppModule(IroAppModule iroAppModule) {
            this.iroAppModule = (IroAppModule) Preconditions.checkNotNull(iroAppModule);
            return this;
        }
    }

    private DaggerIroComponent(Builder builder) {
        if ($assertionsDisabled || builder != null) {
            this.providePrefsWrapperProvider = DoubleCheck.provider(IroAppModule_ProvidePrefsWrapperFactory.create(builder.iroAppModule));
            this.provideDatabaseProvider = DoubleCheck.provider(IroAppModule_ProvideDatabaseFactory.create(builder.iroAppModule));
            this.provideRestClientProvider = DoubleCheck.provider(IroAppModule_ProvideRestClientFactory.create(builder.iroAppModule));
            this.iroApplicationMembersInjector = IroApplication_MembersInjector.create(this.providePrefsWrapperProvider, this.provideDatabaseProvider, this.provideRestClientProvider);
            this.eventHandlerMembersInjector = EventHandler_MembersInjector.create(this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.pushPullMembersInjector = PushPull_MembersInjector.create(this.provideRestClientProvider, this.provideDatabaseProvider);
            this.provideTrackerProvider = DoubleCheck.provider(IroAppModule_ProvideTrackerFactory.create(builder.iroAppModule));
            this.baseActivityMembersInjector = BaseActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider);
            this.remoteControlActivityMembersInjector = RemoteControlActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.loginProgressActivityMembersInjector = LoginProgressActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.registrationActivityMembersInjector = RegistrationActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.passwordResetActivityMembersInjector = PasswordResetActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.zoneDetailsActivityMembersInjector = ZoneDetailsActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider);
            this.debugActivityMembersInjector = DebugActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider);
            this.provActivityMembersInjector = ProvActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.baseFragmentMembersInjector = BaseFragment_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider);
            this.providesBackgroundThreadPoolProvider = DoubleCheck.provider(IroAppModule_ProvidesBackgroundThreadPoolFactory.create(builder.iroAppModule));
            this.providesUiThreadExecutorProvider = DoubleCheck.provider(IroAppModule_ProvidesUiThreadExecutorFactory.create(builder.iroAppModule));
            this.fetchWeatherForecastCommandMembersInjector = FetchWeatherForecastCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchDeviceCommandMembersInjector = FetchDeviceCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchWaterUsageCommandMembersInjector = FetchWaterUsageCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchWeatherStationsCommandMembersInjector = FetchWeatherStationsCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchCalendarCommandMembersInjector = FetchCalendarCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchZonesCommandMembersInjector = FetchZonesCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchEventsCommandMembersInjector = FetchEventsCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchZoneCommandMembersInjector = FetchZoneCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchNozzlesCommandMembersInjector = FetchNozzlesCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchUserCommandMembersInjector = FetchUserCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchWaterJournalCommandMembersInjector = FetchWaterJournalCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchIroPropertiesCommandMembersInjector = FetchIroPropertiesCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchAndCopyScheduleRuleCommandMembersInjector = FetchAndCopyScheduleRuleCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.fetchUnbornDataCommandMembersInjector = FetchUnbornDataCommand_MembersInjector.create(this.providesBackgroundThreadPoolProvider, this.providesUiThreadExecutorProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.historyActivityMembersInjector = HistoryActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider);
            this.myNozzlesActivityMembersInjector = MyNozzlesActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider, this.provideRestClientProvider);
            this.nozzleConfigurationActivityMembersInjector = NozzleConfigurationActivity_MembersInjector.create(this.provideTrackerProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider);
            this.bootCompleteReceiverMembersInjector = BootCompleteReceiver_MembersInjector.create(this.provideRestClientProvider, this.providePrefsWrapperProvider);
            this.mrvlProvServiceMembersInjector = MrvlProvService_MembersInjector.create(this.provideRestClientProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider);
            this.holderMembersInjector = RestClientProgressDialogAsyncTask_Holder_MembersInjector.create(this.provideRestClientProvider, this.provideDatabaseProvider, this.providePrefsWrapperProvider);
            return;
        }
        throw new AssertionError();
    }

    public static Builder builder() {
        return new Builder();
    }

    public final void inject(IroApplication application) {
        this.iroApplicationMembersInjector.injectMembers(application);
    }

    public final void inject(EventHandler eventHandler) {
        this.eventHandlerMembersInjector.injectMembers(eventHandler);
    }

    public final void inject(PushPull service) {
        this.pushPullMembersInjector.injectMembers(service);
    }

    public final void inject(BaseActivity activity) {
        this.baseActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(RemoteControlActivity activity) {
        this.remoteControlActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(LoginProgressActivity activity) {
        this.loginProgressActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(RegistrationActivity activity) {
        this.registrationActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(PasswordResetActivity activity) {
        this.passwordResetActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(DebugActivity activity) {
        this.debugActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(ProvActivity activity) {
        this.provActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(BaseFragment fragment) {
        this.baseFragmentMembersInjector.injectMembers(fragment);
    }

    public final void inject(FetchWeatherForecastCommand command) {
        this.fetchWeatherForecastCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchDeviceCommand command) {
        this.fetchDeviceCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchWaterUsageCommand command) {
        this.fetchWaterUsageCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchWeatherStationsCommand command) {
        this.fetchWeatherStationsCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchCalendarCommand command) {
        this.fetchCalendarCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchZonesCommand command) {
        this.fetchZonesCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchEventsCommand command) {
        this.fetchEventsCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchZoneCommand command) {
        this.fetchZoneCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchNozzlesCommand command) {
        this.fetchNozzlesCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchUserCommand command) {
        this.fetchUserCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchWaterJournalCommand command) {
        this.fetchWaterJournalCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchIroPropertiesCommand command) {
        this.fetchIroPropertiesCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchAndCopyScheduleRuleCommand command) {
        this.fetchAndCopyScheduleRuleCommandMembersInjector.injectMembers(command);
    }

    public final void inject(FetchUnbornDataCommand command) {
        this.fetchUnbornDataCommandMembersInjector.injectMembers(command);
    }

    public final void inject(HistoryActivity activity) {
        this.historyActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(MyNozzlesActivity activity) {
        this.myNozzlesActivityMembersInjector.injectMembers(activity);
    }

    public final void inject(NozzleConfigurationActivity fragment) {
        this.nozzleConfigurationActivityMembersInjector.injectMembers(fragment);
    }

    public final void inject(BootCompleteReceiver receiver) {
        this.bootCompleteReceiverMembersInjector.injectMembers(receiver);
    }

    public final void inject(MrvlProvService service) {
        this.mrvlProvServiceMembersInjector.injectMembers(service);
    }

    public final void inject(Holder holder) {
        this.holderMembersInjector.injectMembers(holder);
    }
}
