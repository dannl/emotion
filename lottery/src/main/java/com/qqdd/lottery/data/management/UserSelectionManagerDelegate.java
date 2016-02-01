package com.qqdd.lottery.data.management;

import android.os.AsyncTask;

import com.example.niub.utils.FileUtils;
import com.qqdd.lottery.R;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.UserSelection;

import java.io.File;
import java.util.List;

import la.niub.util.utils.AppContext;

public class UserSelectionManagerDelegate {

    private static final String CACHE_DIR = "selection";


    private static class SingletonHolder {
        private static UserSelectionManagerDelegate INSTANCE = new UserSelectionManagerDelegate();
    }

    public static UserSelectionManagerDelegate getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private UserSelectionManager mDataManager;
    private Task mTask;

    private UserSelectionManagerDelegate() {
        mDataManager = new UserSelectionManager(new File(FileUtils.getCacheDir(), CACHE_DIR));
    }


    public void loadNotRedeemed(final List<HistoryItem> history,
                                DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mDataManager.getNotRedeemed(history);
            }
        }, callback);

    }

    public void load(final List<HistoryItem> history, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mDataManager.getUserSelectionList(history);
            }
        }, callback);
    }

    public void loadMore(final List<HistoryItem> history, final long since, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mDataManager.loadMore(history, since);
            }
        }, callback);
    }

    public void addUserSelection(final UserSelection userSelection, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mDataManager.addUserSelection(userSelection);
            }
        }, callback);
    }
    public void addUserSelections(final List<UserSelection> userSelection, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mDataManager.addUserSelection(userSelection);
            }
        }, callback);
    }

    public void delete(final UserSelection userSelection, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mDataManager.delete(userSelection);
            }
        }, callback);
    }

    private void executeRunnable(Runnable runnable, DataLoadingCallback<UserSelectionOperationResult> callback) {
        if (mTask != null) {
            callback.onBusy();
            return;
        }
        mTask = new Task(runnable, callback);
        mTask.execute();
    }

    private class Task extends AsyncTask<Void,Void,UserSelectionOperationResult> {

        private final DataLoadingCallback<UserSelectionOperationResult> mCallback;
        private Runnable mRunnable;

        Task(Runnable runnable, DataLoadingCallback<UserSelectionOperationResult> callback){
            mRunnable = runnable;
            mCallback = callback;
        }

        @Override
        protected UserSelectionOperationResult doInBackground(Void... params) {
            return mRunnable.execute();
        }

        @Override
        protected void onPostExecute(UserSelectionOperationResult result) {
            if (mCallback != null) {
                if (result.getResultType() == UserSelectionOperationResult.ResultType.SUCCESS) {
                    mCallback.onLoaded(result);
                } else {
                    mCallback.onLoadFailed(AppContext.getInstance().getResources().getString(R.string.operation_failed));
                }
            }
            mTask = null;
        }
    }

    private interface Runnable {
        UserSelectionOperationResult execute();
    }


}
