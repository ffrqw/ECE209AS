package com.nineoldandroids.animation;

import java.util.ArrayList;

public abstract class Animator implements Cloneable {
    ArrayList<Object> mListeners = null;

    public void start() {
    }

    public Animator clone() {
        try {
            Animator anim = (Animator) super.clone();
            if (this.mListeners != null) {
                ArrayList oldListeners = this.mListeners;
                anim.mListeners = new ArrayList();
                int numListeners = oldListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    anim.mListeners.add(oldListeners.get(i));
                }
            }
            return anim;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
