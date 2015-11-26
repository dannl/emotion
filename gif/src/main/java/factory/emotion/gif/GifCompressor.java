package factory.emotion.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import factory.emotion.gifmaker.AnimatedGifMaker;
import factory.emotion.gifreader.GifDecoder;
import factory.emotion.gifreader.InputWrapper;

/**
 * Created by danliu on 11/24/15.
 */
public class GifCompressor {

    public static class CompressResult {

        public static final int RESULT_OK = 0;
        public static final int RESULT_ERR = -1;

        private String mResult;
        private int mResultCode;

        public void setResult(String result) {
            mResult = result;
        }

        public void setResultCode(int err) {
            mResultCode = err;
        }

        public int getResultCode() {
            return mResultCode;
        }

        public String getResult() {
            return mResult;
        }

    }

    public interface CompressCallback {

        void onStart();

        void onSuccess(String fileResult);

        void onErr(String err);

    }

    /**
     * the minimum size of the result per frame.
     */
    private static final long MIN_DEST_FRAME_BITMAP_SIZE = 30 * 30 * 12;

    private InputWrapper mInput;
    private int mSampleRate;
    private int mMaxFileSize;
    private GifDecoder mGifDecoder;
    private boolean mStarted;
    private Context mContext;

    public GifCompressor(@NotNull Context context, @NotNull final InputWrapper input,
                         final int sampleRate, final int maxSize) {
        mContext = context;
        mInput = input;
        mSampleRate = sampleRate;
        mMaxFileSize = maxSize;
        mGifDecoder = new GifDecoder();
    }

    public boolean isStarted() {
        return mStarted;
    }

    public void compress(@NotNull final CompressCallback callback) {
        if (mStarted) {
            return;
        }
        final AsyncTask<Void, Void, CompressResult> task = new AsyncTask<Void, Void, CompressResult>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                callback.onStart();
            }

            @Override
            protected CompressResult doInBackground(Void... params) {
                return compressImpl();
            }

            @Override
            protected void onPostExecute(CompressResult compressResult) {
                if (compressResult.getResultCode() == CompressResult.RESULT_ERR) {
                    callback.onErr(compressResult.getResult());
                } else {
                    callback.onSuccess(compressResult.getResult());
                }
            }
        };
        task.execute();
    }

    private CompressResult compressImpl() {
        final CompressResult compressResult = new CompressResult();
        final int status = mGifDecoder.read(mInput);
        if (status != GifDecoder.STATUS_OK) {
            compressResult.setResultCode(CompressResult.RESULT_ERR);
            compressResult.setResult("gif decode err!");
            return compressResult;
        }
        mGifDecoder.advance();
        final int frameCount = mGifDecoder.getFrameCount();
        final int srcWidth = mGifDecoder.getWidth();
        final int srcHeight = mGifDecoder.getHeight();

        if (frameCount <= 0 || srcWidth == 0 || srcHeight == 0) {
            compressResult.setResultCode(CompressResult.RESULT_ERR);
            compressResult.setResult("gif read err!");
        }
        int sampleRate = mSampleRate;
        //当sampleRate小于0或者大于等于总帧数的时候，强制不做截取。
        if (sampleRate < 1 || sampleRate >= frameCount) {
            sampleRate = 1;
        }

        //calculate bitmap compress
        long maxSize = mMaxFileSize;
        int destFrameCount = frameCount / sampleRate;
        //consider the header size...
        long destFrameBitmapSize = maxSize / destFrameCount;

        if (destFrameBitmapSize < MIN_DEST_FRAME_BITMAP_SIZE) {
            compressResult.setResult(
                    "the gif is too large to compress, the compress result will display invisible. " +
                            "pls increase the sample rate or max file size!");
            compressResult.setResultCode(CompressResult.RESULT_ERR);
            return compressResult;
        }

        float d = ((float) srcWidth) / srcHeight;

        int destHeight = (int) Math.floor(Math.sqrt(destFrameBitmapSize / 12 / (1 + d)));
        int destWidth = (int) (destHeight * d);

        //init gif maker.
        AnimatedGifMaker gifMaker = new AnimatedGifMaker();
        final File resultDir = mContext.getExternalFilesDir(Constants.COMPRESS_RESULT_DIR);
        final File resultFile = new File(resultDir, System.currentTimeMillis() + ".gif");
        final String filePath = resultFile.getAbsolutePath();

        compressResult.setResult(filePath);

        gifMaker.start(filePath);
        gifMaker.setRepeat(0);
        gifMaker.setTransparent(new Color());

        //write gif frames
        for (int i = 0; i < frameCount; i++) {
            Bitmap bmp = mGifDecoder.getNextFrame();
            int delay = mGifDecoder.getFrameDelay();
            mGifDecoder.advance();
            if (i % sampleRate != 0) {
                //ignore this frame!
                recycleBitmapIfNeeded(bmp);
                continue;
            }
            gifMaker.setDelay(delay);
            Log.e("TEST", "compress frame " + i);
            //create result bitmap with sizes
//            Bitmap frameBmp = Bitmap.createScaledBitmap(bmp, destWidth, destHeight, false);
            Bitmap frameBmp = bmp;
            //FIXME it's dirty to draw the bitmap again...
            Bitmap drawIt = Bitmap.createBitmap(frameBmp.getWidth(), frameBmp.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(drawIt);
            canvas.drawBitmap(frameBmp, 0, 0, null);
            //TODO delay? dispose method? etc.
            gifMaker.addFrame(drawIt);
            recycleBitmapIfNeeded(bmp);
            recycleBitmapIfNeeded(frameBmp);
            recycleBitmapIfNeeded(drawIt);
        }

        gifMaker.finish();

        compressResult.setResultCode(CompressResult.RESULT_OK);
        return compressResult;
    }

    private void recycleBitmapIfNeeded(Bitmap bmp) {
        if (bmp == null || bmp.isRecycled()) {
            return;
        }
        bmp.recycle();
    }


}
