package com.rachio.iro;

import com.rachio.iro.cloud.RestClient;
import dagger.MembersInjector;
import javax.inject.Provider;

public final class BootCompleteReceiver_MembersInjector implements MembersInjector<BootCompleteReceiver> {
    static final /* synthetic */ boolean $assertionsDisabled = (!BootCompleteReceiver_MembersInjector.class.desiredAssertionStatus());
    private final Provider<PrefsWrapper> prefsWrapperProvider;
    private final Provider<RestClient> restClientProvider;

    public final /* bridge */ /* synthetic */ void injectMembers(Object obj) {
        BootCompleteReceiver bootCompleteReceiver = (BootCompleteReceiver) obj;
        if (bootCompleteReceiver == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        bootCompleteReceiver.restClient = (RestClient) this.restClientProvider.get();
        bootCompleteReceiver.prefsWrapper = (PrefsWrapper) this.prefsWrapperProvider.get();
    }

    private BootCompleteReceiver_MembersInjector(Provider<RestClient> restClientProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        if ($assertionsDisabled || restClientProvider != null) {
            this.restClientProvider = restClientProvider;
            if ($assertionsDisabled || prefsWrapperProvider != null) {
                this.prefsWrapperProvider = prefsWrapperProvider;
                return;
            }
            throw new AssertionError();
        }
        throw new AssertionError();
    }

    public static MembersInjector<BootCompleteReceiver> create(Provider<RestClient> restClientProvider, Provider<PrefsWrapper> prefsWrapperProvider) {
        return new BootCompleteReceiver_MembersInjector(restClientProvider, prefsWrapperProvider);
    }
}
