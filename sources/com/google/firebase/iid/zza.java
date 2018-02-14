package com.google.firebase.iid;

import android.support.v7.widget.RecyclerView.ItemAnimator;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public final class zza {
    public static KeyPair zzvJ() {
        try {
            KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA");
            instance.initialize(ItemAnimator.FLAG_MOVED);
            return instance.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
    }
}
