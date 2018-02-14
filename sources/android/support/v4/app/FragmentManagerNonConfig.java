package android.support.v4.app;

import java.util.List;

public final class FragmentManagerNonConfig {
    private final List<FragmentManagerNonConfig> mChildNonConfigs;
    private final List<Fragment> mFragments;

    FragmentManagerNonConfig(List<Fragment> fragments, List<FragmentManagerNonConfig> childNonConfigs) {
        this.mFragments = fragments;
        this.mChildNonConfigs = childNonConfigs;
    }

    final List<Fragment> getFragments() {
        return this.mFragments;
    }

    final List<FragmentManagerNonConfig> getChildNonConfigs() {
        return this.mChildNonConfigs;
    }
}
