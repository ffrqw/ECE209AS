package com.rachio.iro.binder;

public class CardInfo {
    public Object data;
    public int desiredPosition;
    public ModelViewType type;

    public CardInfo(ModelViewType type) {
        this(type, null);
    }

    public CardInfo(ModelViewType type, Object data) {
        this(type, data, -1);
    }

    public CardInfo(ModelViewType type, Object data, int desiredPosition) {
        this.data = data;
        this.type = type;
        this.desiredPosition = desiredPosition;
    }
}
