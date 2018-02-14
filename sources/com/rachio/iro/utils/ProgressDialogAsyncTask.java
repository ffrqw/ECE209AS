package com.rachio.iro.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.rachio.iro.R;
import java.lang.reflect.InvocationTargetException;

public abstract class ProgressDialogAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private final Context context;
    private Class customProgressDialogClass;
    private boolean dismiss;
    private final String message;
    private CustomProgressDialog pd;

    public interface CustomProgressDialog {
        void dismiss();

        void setCancelable(boolean z);

        void setMessage(CharSequence charSequence);

        void show();
    }

    private static final class InternalProgressDialog extends ProgressDialog implements CustomProgressDialog {
        public InternalProgressDialog(Context context) {
            super(context);
        }
    }

    public ProgressDialogAsyncTask(Context context) {
        this(context, null);
    }

    public ProgressDialogAsyncTask(Context context, String message) {
        this.customProgressDialogClass = InternalProgressDialog.class;
        this.dismiss = true;
        this.context = context;
        this.message = message;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        try {
            this.pd = (CustomProgressDialog) this.customProgressDialogClass.getConstructor(new Class[]{Context.class}).newInstance(new Object[]{this.context});
            this.pd.setCancelable(false);
            if (this.message != null) {
                this.pd.setMessage(this.message);
            } else {
                this.pd.setMessage(this.context.getText(R.string.please_wait));
            }
            this.pd.show();
        } catch (NoSuchMethodException nsme) {
            throw new RuntimeException(nsme);
        } catch (IllegalAccessException ias) {
            throw new RuntimeException(ias);
        } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite);
        } catch (InstantiationException ie) {
            throw new RuntimeException(ie);
        }
    }

    private void dismissProgressDialog() {
        if (this.dismiss) {
            try {
                this.pd.dismiss();
            } catch (Exception e) {
            }
        }
    }

    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        dismissProgressDialog();
    }

    public final void setProgressDialogClass(Class<? extends CustomProgressDialog> progressDialogClass) {
        this.customProgressDialogClass = progressDialogClass;
    }

    public CustomProgressDialog getCustomProgressDialog() {
        return this.pd;
    }

    public final void setOptions(boolean dismiss) {
        this.dismiss = false;
    }

    protected void onCancelled() {
        super.onCancelled();
        dismissProgressDialog();
    }
}
