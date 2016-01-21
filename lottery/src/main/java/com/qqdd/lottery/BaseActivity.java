package com.qqdd.lottery;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import la.niub.util.utils.UIUtil;

/**
 * Created by danliu on 1/21/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    protected void showProgress(final int stringId) {
        showProgress(getString(stringId));
    }

    protected void showProgress(final String msg) {
        showProgress(msg, true);
    }

    protected void showProgress(final String msg, boolean cancelable) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCancelable(cancelable);
        UIUtil.showDialogSafe(mProgressDialog);
    }

    protected void dismissProgress() {
        UIUtil.dismissDialogSafe(mProgressDialog);
    }
}
