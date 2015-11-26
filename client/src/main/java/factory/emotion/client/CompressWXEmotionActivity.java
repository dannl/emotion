package factory.emotion.client;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.niub.utils.UIUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import factory.emotion.gif.GifCompressor;
import factory.emotion.gifreader.FileInputStreamWrapper;
import factory.emotion.gifreader.GifImageView;

public class CompressWXEmotionActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0x1;
    private GifImageView mSrc;
    private GifImageView mDest;
    private Uri mSrcFile;
    private EditText mSampleRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_wxemotion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSrc = (GifImageView) findViewById(R.id.source);
        mDest = (GifImageView) findViewById(R.id.dest);
        mSampleRate = (EditText) findViewById(R.id.sample_rate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compress, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_file) {
            final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/gif");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
                        FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT)
                        .show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void play(View v) {
        if (v instanceof  GifImageView) {
            ((GifImageView)v).startAnimation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("Compress", "File Uri: " + uri.toString());
                    // Get the path
                    try {
                        mSrcFile = uri;
                        mSrc.setBytes(new FileInputStreamWrapper(
                                (FileInputStream) getContentResolver().openInputStream(uri)));
                        mSrc.startAnimation();
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "failed to open file!", Toast.LENGTH_SHORT)
                                .show();
                    } catch (ClassCastException e) {
                        Toast.makeText(this,
                                "failed to open file, can not cast to file input stream!",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private GifCompressor mGifCompressor;

    public void process(View view) {
        if (mSrcFile == null) {
            Toast.makeText(this, "pls open a gif file first!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (mGifCompressor != null) {
            Toast.makeText(this, "processing! pls wait!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        mSrc.stopAnimation();
        try {
            final String text = mSampleRate.getText().toString();
            int sampleRate = 0;
            if (TextUtils.isEmpty(text)) {
                sampleRate = 0;
            } else {
                sampleRate = Integer.valueOf(text);
            }
            mGifCompressor = new GifCompressor(this, new FileInputStreamWrapper(
                    (FileInputStream) getContentResolver().openInputStream(mSrcFile)), sampleRate
                    , 500 * 1024);
            mGifCompressor.compress(new GifCompressor.CompressCallback() {
                @Override
                public void onStart() {
                    UIUtils.toast(CompressWXEmotionActivity.this, "started compress!!");
                }

                @Override
                public void onSuccess(String fileResult) {
                    UIUtils.toast(CompressWXEmotionActivity.this, "succeeded! " + fileResult);
                    try {
                        mDest.setBytes(new FileInputStreamWrapper(new FileInputStream(fileResult)));
                        mDest.startAnimation();
                    } catch (FileNotFoundException ignored) {
                        UIUtils.toast(CompressWXEmotionActivity.this,
                                "failed to open " + fileResult);
                    }
                    mGifCompressor = null;
                }

                @Override
                public void onErr(String err) {
                    mGifCompressor = null;
                    UIUtils.toast(CompressWXEmotionActivity.this, "failed! " + err);
                }
            });
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "failed to open file!", Toast.LENGTH_SHORT)
                    .show();
        } catch (ClassCastException e) {
            Toast.makeText(this,
                    "failed to open file, can not cast to file input stream!",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
