package com.rachio.iro.ui.activity;

public interface ActivityThatSaves {

    public interface DisableSave {
    }

    boolean hasChanges();

    boolean isBusy();

    boolean isValid();

    void save();
}
