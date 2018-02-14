package com.rachio.iro.async.command;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import com.rachio.iro.IroApplication;
import com.rachio.iro.IroGraph;
import com.rachio.iro.PrefsWrapper;
import com.rachio.iro.cloud.RestClient;
import com.rachio.iro.model.db.Database;

public abstract class BaseCommand<T> extends SynchronousCommand<T> {
    Database database;
    PrefsWrapper prefsWrapper;
    RestClient restClient;

    protected abstract void handleResult(T t);

    protected abstract T loadResult();

    public static IroGraph component(Object listener) {
        IroApplication app = IroApplication.get(toContext(listener));
        if (app != null) {
            return app.component();
        }
        return null;
    }

    public static Context toContext(Object listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener can't be null");
        } else if (listener instanceof Context) {
            return (Context) listener;
        } else {
            if (listener instanceof Fragment) {
                return ((Fragment) listener).getActivity();
            }
            if (listener instanceof View) {
                return ((View) listener).getContext();
            }
            throw new IllegalArgumentException("Could not figure out how to get a context from the given listener: " + listener.getClass());
        }
    }
}
