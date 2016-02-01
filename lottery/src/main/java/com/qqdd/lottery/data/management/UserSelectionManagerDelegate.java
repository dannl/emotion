package com.qqdd.lottery.data.management;

import android.os.AsyncTask;

import com.example.niub.utils.FileUtils;
import com.qqdd.lottery.R;
import com.qqdd.lottery.data.HistoryItem;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.data.UserSelection;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import la.niub.util.utils.AppContext;

public class UserSelectionManagerDelegate {


    private static class SingletonHolder {
        private static UserSelectionManagerDelegate INSTANCE = new UserSelectionManagerDelegate();
    }

    public static UserSelectionManagerDelegate getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private HashMap<Lottery.Type, UserSelectionManager> mManagers;
    private HashMap<Lottery.Type, Task> mTaskCache;

    private UserSelectionManagerDelegate() {
        mManagers = new HashMap<>();
        mManagers.put(Lottery.Type.DLT, new UserSelectionManager(new File(FileUtils.getCacheDir(),
                UserSelectionManager.getCacheFolderWithType(Lottery.Type.DLT))));
        mManagers.put(Lottery.Type.SSQ, new UserSelectionManager(new File(FileUtils.getCacheDir(),
                UserSelectionManager.getCacheFolderWithType(Lottery.Type.SSQ))));
        mTaskCache = new HashMap<>();
    }


    public void loadNotRedeemed(final Lottery.Type type, final List<HistoryItem> history,
                                DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(type, new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mManagers.get(type).getNotRedeemed(history);
            }
        }, callback);

    }

    public void load(final Lottery.Type type, final List<HistoryItem> history, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(type, new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mManagers.get(type).getUserSelectionList(history);
            }
        }, callback);
    }

    public void loadMore(final Lottery.Type type, final List<HistoryItem> history, final long since, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(type, new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mManagers.get(type).loadMore(history, since);
            }
        }, callback);
    }

    public void addUserSelection(final Lottery.Type type,final UserSelection userSelection, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(type, new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mManagers.get(type).addUserSelection(userSelection);
            }
        }, callback);
    }
    public void addUserSelections(final Lottery.Type type,final List<UserSelection> userSelection, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(type, new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mManagers.get(type).addUserSelection(userSelection);
            }
        }, callback);
    }

    public void delete(final Lottery.Type type, final UserSelection userSelection, final DataLoadingCallback<UserSelectionOperationResult> callback) {
        executeRunnable(type, new Runnable() {
            @Override
            public UserSelectionOperationResult execute() {
                return mManagers.get(type).delete(userSelection);
            }
        }, callback);
    }

    private void executeRunnable(Lottery.Type type, Runnable runnable, DataLoadingCallback<UserSelectionOperationResult> callback) {
        if (mTaskCache.get(type) != null) {
            callback.onBusy();
            return;
        }
        final Task task = new Task(type, runnable, callback);
        mTaskCache.put(type, task);
        task.execute();
    }

    private class Task extends AsyncTask<Void,Void,UserSelectionOperationResult> {

        private final DataLoadingCallback<UserSelectionOperationResult> mCallback;
        private Runnable mRunnable;
        private Lottery.Type mType;

        Task(Lottery.Type type, Runnable runnable, DataLoadingCallback<UserSelectionOperationResult> callback){
            mRunnable = runnable;
            mCallback = callback;
            mType = type;
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
            mTaskCache.remove(mType);
        }
    }

    private interface Runnable {
        UserSelectionOperationResult execute();
    }


}
