package android.support.v4.content.res;

import android.content.res.Resources;
import android.os.Build.VERSION;

public final class ConfigurationHelper {
    private static final ConfigurationHelperImpl IMPL;

    private interface ConfigurationHelperImpl {
        int getScreenHeightDp(Resources resources);

        int getScreenWidthDp(Resources resources);

        int getSmallestScreenWidthDp(Resources resources);
    }

    private static class GingerbreadImpl implements ConfigurationHelperImpl {
        GingerbreadImpl() {
        }

        public int getScreenHeightDp(Resources resources) {
            return ConfigurationHelperGingerbread.getScreenHeightDp(resources);
        }

        public int getScreenWidthDp(Resources resources) {
            return ConfigurationHelperGingerbread.getScreenWidthDp(resources);
        }

        public int getSmallestScreenWidthDp(Resources resources) {
            return Math.min(ConfigurationHelperGingerbread.getScreenWidthDp(resources), ConfigurationHelperGingerbread.getScreenHeightDp(resources));
        }
    }

    private static class HoneycombMr2Impl extends GingerbreadImpl {
        HoneycombMr2Impl() {
        }

        public final int getScreenHeightDp(Resources resources) {
            return resources.getConfiguration().screenHeightDp;
        }

        public final int getScreenWidthDp(Resources resources) {
            return resources.getConfiguration().screenWidthDp;
        }

        public final int getSmallestScreenWidthDp(Resources resources) {
            return resources.getConfiguration().smallestScreenWidthDp;
        }
    }

    private static class JellybeanMr1Impl extends HoneycombMr2Impl {
        JellybeanMr1Impl() {
        }
    }

    static {
        int sdk = VERSION.SDK_INT;
        if (sdk >= 17) {
            IMPL = new JellybeanMr1Impl();
        } else if (sdk >= 13) {
            IMPL = new HoneycombMr2Impl();
        } else {
            IMPL = new GingerbreadImpl();
        }
    }

    public static int getScreenHeightDp(Resources resources) {
        return IMPL.getScreenHeightDp(resources);
    }

    public static int getScreenWidthDp(Resources resources) {
        return IMPL.getScreenWidthDp(resources);
    }

    public static int getSmallestScreenWidthDp(Resources resources) {
        return IMPL.getSmallestScreenWidthDp(resources);
    }
}
