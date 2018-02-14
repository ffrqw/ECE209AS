package android.support.v4.app;

public abstract class LoaderManager {

    public interface LoaderCallbacks<D> {
    }

    public boolean hasRunningLoaders() {
        return false;
    }
}
