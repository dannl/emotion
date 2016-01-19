
package la.niub.util.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public class AsyncTaskUtils {

    public static enum Priority {
        /**
         * Task will be executed on a low priority thread one by one.
         */
        LOW(AsyncTask.LOW_PRIORITY_EXECUTOR),
        /**
         * Task will be executed on a small thread pool.
         */
        NORMAL(AsyncTask.DEFAULT_THREAD_POOL_EXECUTOR),
        /**
         * Task will be executed on a large thread pool.
         */
        HIGH(AsyncTask.THREAD_POOL_EXECUTOR);

        Executor mExecutor;
        Priority(Executor executor) {
            mExecutor = executor;
        }
    }

    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeAsyncTask(
            AsyncTask<Params, Progress, Result> asyncTask, Params... params) {
        return executeAsyncTask(asyncTask, Priority.NORMAL, params);
    }

    public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeAsyncTask(
            AsyncTask<Params, Progress, Result> asyncTask, Priority priority, Params... params) {

        try {
            return asyncTask.executeOnExecutor(priority.mExecutor, params);
        } catch (RejectedExecutionException e) {
            Log.e(null, e);
            return null;
        }

    }

    public static void executeRunnableAsync(final Runnable r, Priority priority) {
        executeAsyncTask(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                r.run();
                return null;
            }

        }, priority);
    }

}
