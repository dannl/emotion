package com.qqdd.lottery;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import la.niub.util.utils.UIUtil;

/**
 * Created by danliu on 1/21/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public final void showProgress(final int stringId) {
        showProgress(getString(stringId));
    }

    public final void showProgress(final String msg) {
        showProgress(msg, true);
    }

    public final void showProgress(final String msg, boolean cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(cancelable);
        UIUtil.showDialogSafe(mProgressDialog);
    }

    public final void dismissProgress() {
        UIUtil.dismissDialogSafe(mProgressDialog);
    }

    public final void showSnackBar(final String msg) {
        final View content = findViewById(android.R.id.content);
        if (content != null) {
            Snackbar.make(content, msg, Snackbar.LENGTH_SHORT).show();
        }
    }
}
