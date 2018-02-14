package com.rachio.iro.gen2;

import com.rachio.iro.gen2.MrvlProvService.FailureReason;

public class ProvFailureException extends Exception {
    public ProvFailureException(FailureReason reason) {
        super(reason.toString());
    }
}
