package com.rachio.iro.async.command;

import com.rachio.iro.model.user.User;

public class FetchUserCommand extends BaseCommand<User> {
    private final Listener listener;
    private final String userId;

    public interface Listener {
        void onUserLoaded(User user);
    }

    protected final /* bridge */ /* synthetic */ void handleResult(Object obj) {
        this.listener.onUserLoaded((User) obj);
    }

    public FetchUserCommand(Listener listener, String userId) {
        if (listener == null || userId == null) {
            throw new IllegalArgumentException();
        }
        this.listener = listener;
        this.userId = userId;
        BaseCommand.component(listener).inject(this);
    }

    protected final /* bridge */ /* synthetic */ Object loadResult() {
        return (User) this.database.find(User.class, this.userId);
    }
}
