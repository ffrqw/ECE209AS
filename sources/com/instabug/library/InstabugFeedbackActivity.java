package com.instabug.library;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.instabug.library.Feature.State;
import com.instabug.library.IBGCustomTextPlaceHolder.Key;
import com.instabug.library.OnSdkDismissedCallback.IssueState;
import com.instabug.library.d.b.b;
import com.instabug.library.d.c;
import com.instabug.library.d.e;
import com.instabug.library.model.IssueType;
import com.instabug.library.model.d;
import com.instabug.library.model.g;
import com.instabug.library.util.InstabugSDKLogger;
import com.instabug.library.util.j;
import com.instabug.library.util.l;
import com.rachio.iro.R;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;

public class InstabugFeedbackActivity extends FragmentActivity implements OnBackStackChangedListener, OnClickListener, _InstabugFeedbackActivity, b, com.instabug.library.d.c.a, e, com.instabug.library.e.a, f.b, com.instabug.library.l.a, com.instabug.library.p.a {

    private static class a extends AsyncTask<Object, Void, JSONObject> {
        WeakReference<InstabugFeedbackActivity> a;

        private a() {
        }

        protected final /* synthetic */ Object doInBackground(Object[] objArr) {
            return a(objArr);
        }

        protected final /* synthetic */ void onPostExecute(Object obj) {
            JSONObject jSONObject = (JSONObject) obj;
            if (!(jSONObject == null || !jSONObject.optBoolean("white_label", false) || this.a.get() == null)) {
                InstabugFeedbackActivity.a((InstabugFeedbackActivity) this.a.get());
            }
            super.onPostExecute(jSONObject);
        }

        private JSONObject a(Object... objArr) {
            try {
                this.a = new WeakReference((InstabugFeedbackActivity) objArr[1]);
                BufferedReader bufferedReader = new BufferedReader(new FileReader((File) objArr[0]));
                String readLine = bufferedReader.readLine();
                bufferedReader.close();
                return new JSONObject(readLine);
            } catch (Exception e) {
                return null;
            }
        }
    }

    static /* synthetic */ void a(InstabugFeedbackActivity instabugFeedbackActivity) {
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (intent.getIntExtra("com.instabug.library.process", 160)) {
            case 163:
                b(intent.getExtras().getString("com.instabug.library.conversation.issue.number"));
                return;
            case 164:
                a();
                return;
            case 166:
                finish();
                return;
            default:
                return;
        }
    }

    protected void onCreate(Bundle bundle) {
        int i;
        Context baseContext = getBaseContext();
        Locale locale = Instabug.getLocale();
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        baseContext.getApplicationContext().getResources().updateConfiguration(configuration, baseContext.getResources().getDisplayMetrics());
        super.onCreate(bundle);
        if (s.k() == IBGColorTheme.IBGColorThemeLight) {
            i = R.style.InstabugSdkTheme.Light;
        } else {
            i = R.style.InstabugSdkTheme.Dark;
        }
        setTheme(i);
        j.b(this);
        setContentView(R.layout.instabug_activity);
        v.a().a(new d(System.currentTimeMillis()));
        File file = new File(getExternalCacheDir() != null ? getExternalCacheDir() : getCacheDir(), "com.instabug.library.settings");
        new a().execute(new Object[]{file, this});
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        int intExtra = getIntent().getIntExtra("com.instabug.library.process", 160);
        if (bundle == null) {
            Uri uri = (Uri) getIntent().getParcelableExtra("com.instabug.library.file");
            if (uri != null) {
                v.a().a(this, uri, com.instabug.library.model.e.a.ATTACHMENT_FILE, q.a().i());
            }
            switch (intExtra) {
                case 160:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.instabug_anim_options_sheet_enter, R.anim.instabug_anim_options_sheet_exit).add(R.id.instabug_bottomsheet_container, new l(), "sheet").commit();
                    a(true, (int) R.id.instabug_bottomsheet_container);
                    break;
                case 161:
                    c();
                    break;
                case 162:
                    b();
                    break;
                case 163:
                    b(getIntent().getExtras().getString("com.instabug.library.conversation.issue.number"));
                    break;
                case 164:
                    a();
                    break;
                case 165:
                    v.a().a((d) getIntent().getSerializableExtra("com.instabug.library.hanging.issue"));
                    InstabugSDKLogger.d(this, "issue.getAttachments().size(): " + v.a().b().b().size());
                    v.a().a(false);
                    findViewById(R.id.instabug_fragment_bk_container).setVisibility(0);
                    if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.WHITE_LABELING) == State.DISABLED) {
                        findViewById(R.id.instabug_pbi_container).setVisibility(0);
                    }
                    if (getSupportFragmentManager().findFragmentByTag("feedback") == null) {
                        getSupportFragmentManager().beginTransaction().add(R.id.instabug_fragment_container, k.a(IssueType.BUG, v.a().b().g(), a(162)), "feedback").commit();
                        a(true, (int) R.id.instabug_fragment_container);
                    }
                    v.a();
                    v.a((Context) this);
                    break;
                case 166:
                    finish();
                    break;
            }
            ImageView imageView = (ImageView) findViewById(R.id.instabug_pbi_image);
            byte[] decode = Base64.decode("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".getBytes(), 0);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(decode, 0, decode.length));
            TextView textView = (TextView) findViewById(R.id.instabug_pbi_text);
            CharSequence charSequence = textView.getText().toString();
            if (!charSequence.contains("Instabug.com")) {
                charSequence = "Powered by Instabug.com";
            } else if (charSequence.contains("&") || charSequence.contains(",") || charSequence.contains("  ") || charSequence.contains("_") || charSequence.contains("-")) {
                charSequence = "Powered by Instabug.com";
            }
            textView.setText(charSequence);
            InstabugSDKLogger.d(this, ".onCreate(), Fire: SDK Invoking State Changed");
            Intent intent = new Intent();
            intent.setAction("SDK invoked");
            intent.putExtra("SDK invoking state", true);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        com.instabug.library.d.d.a().a((e) this);
        if (q.a().c() != null) {
            q.a().c().onSdkInvoked();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        InstabugSDKLogger.d(this, ".onDestroy(), Fire: SDK Invoking State Changed");
        Intent intent = new Intent();
        intent.setAction("SDK invoked");
        intent.putExtra("SDK invoking state", false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        com.instabug.library.d.d.a().b(this);
        if (!v.a().e() && v.a().d() == IssueState.IN_PROGRESS) {
            v.a().a(IssueState.CANCELLED);
        }
        if (q.a().d() != null) {
            q.a().d().onSdkDismissed(v.a().d(), v.a().b() == null ? null : v.a().b().a());
        }
    }

    public void onClick(View view) {
        List arrayList = new ArrayList(getSupportFragmentManager().getFragments());
        Collection arrayList2 = new ArrayList();
        arrayList2.add(null);
        arrayList.removeAll(arrayList2);
        InstabugSDKLogger.v(this, "Dark space clicked, fragments size is " + arrayList.size() + " fragments are " + arrayList);
        if (arrayList.size() > 0) {
            Fragment fragment = (Fragment) arrayList.get(arrayList.size() - 1);
            InstabugSDKLogger.v(this, "Checking current fragment type to see if should dismiss, currentFragment = " + fragment);
            if (fragment == null) {
                return;
            }
            if ((fragment instanceof p) || (fragment instanceof l)) {
                InstabugSDKLogger.v(this, "Success fragment dark space clicked, dismissing it");
                finish();
            }
        }
    }

    public final void a() {
        InstabugSDKLogger.v(this, "Starting conversations list");
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentById(R.id.instabug_fragment_container) instanceof c) {
            getSupportFragmentManager().popBackStack("conversations", 1);
        }
        beginTransaction.replace(R.id.instabug_fragment_container, c.f(), "conversations");
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            beginTransaction.addToBackStack("conversations");
        }
        beginTransaction.commit();
        a(true, (int) R.id.instabug_fragment_container);
        findViewById(R.id.instabug_bottomsheet_container).setVisibility(8);
        findViewById(R.id.instabug_fragment_bk_container).setVisibility(0);
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.WHITE_LABELING) == State.DISABLED) {
            findViewById(R.id.instabug_pbi_container).setVisibility(0);
        }
    }

    public final void b(String str) {
        b.a();
        b.a((Context) this);
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        a(false, (int) R.id.instabug_fragment_container);
        InstabugSDKLogger.v(this, "Current fragment " + getSupportFragmentManager().findFragmentById(R.id.instabug_fragment_container));
        if (getSupportFragmentManager().findFragmentById(R.id.instabug_fragment_container) instanceof com.instabug.library.d.b) {
            getSupportFragmentManager().popBackStack("conversation", 1);
        }
        beginTransaction.add(R.id.instabug_fragment_container, com.instabug.library.d.b.b(str), "conversation");
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            beginTransaction.addToBackStack("conversation");
        }
        beginTransaction.commit();
        findViewById(R.id.instabug_bottomsheet_container).setVisibility(8);
        findViewById(R.id.instabug_fragment_bk_container).setVisibility(0);
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.WHITE_LABELING) == State.DISABLED) {
            findViewById(R.id.instabug_pbi_container).setVisibility(0);
        }
    }

    public void onBackPressed() {
        findViewById(R.id.instabug_fragment_blackout).setVisibility(8);
        if (getSupportFragmentManager().getBackStackEntryCount() <= 0) {
            InstabugSDKLogger.d(this, "Reporting issue canceled. Deleting attachments");
            for (com.instabug.library.model.e eVar : v.a().b().b()) {
                v.a();
                v.a(eVar);
            }
        }
        a(false, (int) R.id.instabug_fragment_container);
        super.onBackPressed();
        a(true, (int) R.id.instabug_fragment_container);
        a(true, (int) R.id.instabug_bottomsheet_container);
    }

    private void a(boolean z, int i) {
        if (getSupportFragmentManager().findFragmentById(i) instanceof a) {
            ((a) getSupportFragmentManager().findFragmentById(i)).a(z);
        }
    }

    private String a(int i) {
        if (i == 161) {
            return l.a(Key.COMMENT_FIELD_HINT_FOR_FEEDBACK, getString(R.string.instabug_str_feedback_comment_hint));
        }
        return l.a(Key.COMMENT_FIELD_HINT_FOR_BUG_REPORT, getString(R.string.instabug_str_bug_comment_hint));
    }

    public final void onBackStackChanged() {
        if (getSupportFragmentManager().findFragmentByTag("sheet") != null && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            findViewById(R.id.instabug_bottomsheet_container).setVisibility(0);
            findViewById(R.id.instabug_fragment_bk_container).setVisibility(8);
        }
    }

    public final void b() {
        v.a().b().a(IssueType.BUG);
        Uri uri = (Uri) getIntent().getParcelableExtra("com.instabug.library.image");
        Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag("sheet");
        if (findFragmentByTag != null) {
            getSupportFragmentManager().beginTransaction().remove(findFragmentByTag).commit();
        }
        findViewById(R.id.instabug_fragment_bk_container).setVisibility(0);
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.WHITE_LABELING) == State.DISABLED) {
            findViewById(R.id.instabug_pbi_container).setVisibility(0);
        }
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        if (uri != null) {
            InstabugSDKLogger.d(this, "Starting bug reporter with screenshot: " + uri.getPath());
            v.a().b().a(uri, com.instabug.library.model.e.a.MAIN_SCREENSHOT);
            beginTransaction.add(R.id.instabug_fragment_container, e.a(uri), "annotation").commit();
        } else {
            beginTransaction.replace(R.id.instabug_fragment_container, k.a(IssueType.FEEDBACK, v.a().b().g(), a(161)), "feedback").commit();
        }
        a(true, (int) R.id.instabug_fragment_container);
    }

    public final void c() {
        v.a().b().a(IssueType.FEEDBACK);
        Uri uri = (Uri) getIntent().getParcelableExtra("com.instabug.library.image");
        if (uri != null) {
            InstabugSDKLogger.d(this, "Starting feedback sender with screenshot: " + uri.getPath());
            v.a().b().a(uri, com.instabug.library.model.e.a.MAIN_SCREENSHOT);
        }
        Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag("sheet");
        if (findFragmentByTag != null) {
            getSupportFragmentManager().beginTransaction().remove(findFragmentByTag).commit();
        }
        findViewById(R.id.instabug_fragment_bk_container).setVisibility(0);
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.WHITE_LABELING) == State.DISABLED) {
            findViewById(R.id.instabug_pbi_container).setVisibility(0);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.instabug_fragment_container, k.a(IssueType.FEEDBACK, v.a().b().g(), a(161)), "feedback").commit();
        a(true, (int) R.id.instabug_fragment_container);
    }

    public final void d() {
        b.a();
        b.a((Context) this);
        InstabugSDKLogger.v(this, "Starting conversations list");
        FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack("conversations", 1);
        beginTransaction.replace(R.id.instabug_fragment_container, c.f(), "conversations").addToBackStack("conversations").commit();
        a(true, (int) R.id.instabug_fragment_container);
        findViewById(R.id.instabug_bottomsheet_container).setVisibility(8);
        findViewById(R.id.instabug_fragment_bk_container).setVisibility(0);
        if (InstabugFeaturesManager.getInstance().getFeatureState(Feature.WHITE_LABELING) == State.DISABLED) {
            findViewById(R.id.instabug_pbi_container).setVisibility(0);
        }
    }

    public final void a(Uri uri, Bitmap bitmap) {
        try {
            bitmap.compress(CompressFormat.JPEG, 100, getContentResolver().openOutputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        a(false, (int) R.id.instabug_fragment_container);
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("annotation")).commit();
        if (getSupportFragmentManager().findFragmentByTag("feedback") == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.instabug_fragment_container, k.a(IssueType.BUG, v.a().b().g(), a(162)), "feedback").commit();
        }
        v.a();
        v.a((Context) this);
    }

    public final void e() {
        finish();
    }

    public final void c(String str) {
        a(false, (int) R.id.instabug_fragment_container);
        getSupportFragmentManager().beginTransaction().add(R.id.instabug_fragment_container, com.instabug.library.d.a.a(str), "attachment_viewer").addToBackStack("attachment_viewer").commit();
    }

    public final List<g> a(final List<g> list) {
        InstabugSDKLogger.d(this, list.size() + " New messages received to be notified while SDK is invoked");
        runOnUiThread(new Runnable(this) {
            final /* synthetic */ InstabugFeedbackActivity b;

            public final void run() {
                b.a().a(this.b, list);
            }
        });
        return null;
    }

    public final void a(String str) {
        onBackPressed();
        v.a().b(this, Uri.fromFile(new File(str)));
    }

    public final void d(String str) {
        b.a();
        b.a((Context) this);
        a(false, (int) R.id.instabug_fragment_container);
        getSupportFragmentManager().beginTransaction().add(R.id.instabug_fragment_container, com.instabug.library.d.b.b(str), "conversation").addToBackStack("conversation").commit();
    }
}
