package factory.emotion.client.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.io.IOException;

import factory.emotion.client.R;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class TestGifLibActivity extends AppCompatActivity {

    private GifImageView mSrc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_gif_lib);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSrc = (GifImageView) findViewById(R.id.source);
        final GifDrawable fromPath;
        try {
            fromPath = new GifDrawable("/sdcard/sample.gif");
            mSrc.setImageDrawable(fromPath);
            fromPath.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
