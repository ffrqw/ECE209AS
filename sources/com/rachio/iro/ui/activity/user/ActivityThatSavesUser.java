package com.rachio.iro.ui.activity.user;

import com.rachio.iro.model.apionly.ErrorResponse;
import com.rachio.iro.model.user.User;
import com.rachio.iro.ui.activity.ActivityThatSaves;
import com.rachio.iro.ui.activity.BaseActivity;
import com.rachio.iro.utils.RestClientProgressDialogAsyncTask;

public abstract class ActivityThatSavesUser extends BaseActivity implements ActivityThatSaves {
    protected final void saveUser(User user, boolean finishOnSuccess) {
        new RestClientProgressDialogAsyncTask<User, Void, User>(this, true) {
            protected /* bridge */ /* synthetic */ Object doInBackground(Object[] objArr) {
                User[] userArr = (User[]) objArr;
                ActivityThatSavesUser.this.database.lock();
                User user = (User) this.holder.restClient.putObject(User.class, userArr[0], this.errorHandler);
                if (!(user == null || user.hasError())) {
                    ActivityThatSavesUser.this.database.save(user, true);
                }
                ActivityThatSavesUser.this.database.unlock();
                return user;
            }

            public final void onFailure(ErrorResponse errorResponse) {
            }

            public final /* bridge */ /* synthetic */ void onSuccess(ErrorResponse errorResponse) {
                if (true) {
                    ActivityThatSavesUser.this.finish();
                }
            }
        }.execute(new User[]{user});
    }
}
