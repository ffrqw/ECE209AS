package com.instabug.library.migration;

import android.content.Context;
import com.instabug.library.s;
import com.instabug.library.util.InstabugSDKLogger;
import java.util.ArrayList;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public final class b {
    private static final AbstractMigration[] a = new AbstractMigration[]{new a()};

    public static void a(Context context) {
        ArrayList arrayList = new ArrayList();
        AbstractMigration[] abstractMigrationArr = a;
        for (int i = 0; i <= 0; i++) {
            AbstractMigration abstractMigration = abstractMigrationArr[0];
            abstractMigration.initialize(context);
            boolean z = abstractMigration.shouldMigrate() && abstractMigration.getMigrationVersion() > s.h() && abstractMigration.getMigrationVersion() <= 1;
            InstabugSDKLogger.d(b.class, "Checking if should apply this migration: " + abstractMigration + ", result is " + z + " last migration version is " + s.h() + " target migration version 1");
            if (z) {
                arrayList.add(abstractMigration.migrate());
            }
        }
        Observable[] a = a(arrayList);
        if (a == null || a.length == 0) {
            InstabugSDKLogger.d(b.class, "No migrations to run");
        } else {
            Observable.merge(a).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<String>() {
                public final /* synthetic */ void onNext(Object obj) {
                    InstabugSDKLogger.d(b.class, "Migration " + ((String) obj) + " done");
                }

                public final void onStart() {
                    super.onStart();
                }

                public final void onCompleted() {
                    InstabugSDKLogger.d(b.class, "All Migrations completed, setting lastMigrationVersion to 1");
                    s.a(1);
                }

                public final void onError(Throwable th) {
                    InstabugSDKLogger.d(b.class, "Migration failed" + th.getMessage());
                }
            });
        }
    }

    private static Observable[] a(ArrayList<Observable<String>> arrayList) {
        Observable[] observableArr = new Observable[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            observableArr[i] = (Observable) arrayList.get(i);
        }
        return observableArr;
    }
}
