package com.qqdd.lottery.chart;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.data.Lottery;
import com.qqdd.lottery.test.TestRoundRates;
import com.qqdd.lottery.utils.NumUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import la.niub.util.utils.StorageHelper;
import la.niub.util.utils.UIUtil;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private static final int RANGE_DIVIDER = 100;
    private LineChart mChart;

    private static final int[] COLORS = new int[]{
            0xffff0000,
            0xff00ff00,
            0xff0000ff,
            0xff000000,
            0xffffff00,
            0xff00ffff,
            0xffff00ff,
            0xff0ff000,
            0xff000ff0,
            0xfff0000f
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mChart = (LineChart) findViewById(R.id.chart);

        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        //        // enable value highlighting
        //        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);


    }

    private void afterSetData() {
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        //        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        //        leftAxis.setAxisMaxValue(200f);
        leftAxis.setDrawGridLines(true);
    }


    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals1, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        //        set1.setVisible(false);
        //        set1.setCircleHoleColor(Color.WHITE);

        ArrayList<Entry> yVals2 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = range;
            float val = (float) (Math.random() * mult) + 450;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals2.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set2 = new LineDataSet(yVals2, "DataSet 2");
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.WHITE);
        set2.setLineWidth(2f);
        set2.setCircleSize(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set2);
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
        mChart.invalidate();

        afterSetData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_go_home_100000) {
            loadGoHome100000();
            return true;
        } else if (id == R.id.action_go_home_1000000) {
            loadGoHome1000000();
            return true;
        } else if (id == R.id.action_rate) {
            showFilePicker();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilePicker() {
        final File root = getFile();
        final String[] names = root.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return !TextUtils.isEmpty(filename) && filename.endsWith("_rates");
            }
        });
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                this);
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = names[which];
                loadRate(name);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public File getFile() {
        return new File(StorageHelper.getExternalStorageDirectory(), "Ltt");
    }

    private void loadRate(final String name) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<String> xV = new ArrayList<>();
        final File f = new File(getFile(), name);
        final List<TestRoundRates> rates = TestRoundRates.load(f);
        ArrayList<Entry> yV = new ArrayList<>();
        float max = 0;
        int larger = 0;
        float total = 0;
        for (int j = 0; j < rates.size(); j++) {
            final TestRoundRates testRoundRates = rates.get(j);
            xV.add(testRoundRates.getRecord()
                    .getDateDisplay());
            yV.add(new Entry(testRoundRates.getRate(), j));
            if (max < testRoundRates.getRate()) {
                max  = testRoundRates.getRate();
            }
            if (testRoundRates.getRate() > 0.0666f) {
                larger ++;total += testRoundRates.getRate();
            }
        }
        Log.e("TEST", " larger: " + larger + " avr: " + (total / larger));
        LineDataSet dataSet = new LineDataSet(yV, name);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        int color = COLORS[0];
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(1f);
        dataSet.setCircleSize(2f);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(color);
        dataSet.setDrawCircleHole(false);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));

        dataSets.add(dataSet);

        ArrayList<Entry> yVRandom = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++) {
            yVRandom.add(new Entry(0.0666f, i));
        }

        dataSet = new LineDataSet(yVRandom, "随机");
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        color = COLORS[1];
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(1f);
        dataSet.setCircleSize(1f);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(color);
        dataSet.setDrawCircleHole(false);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));

        dataSets.add(dataSet);

        LineData data = new LineData(xV, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        mChart.setData(data);

        mChart.invalidate();

        afterSetData();

    }

    private void loadGoHome1000000() {
        loadGoHome(1000000);
    }

    private void loadGoHome100000() {
        loadGoHome(100000);
    }

    private void loadGoHome(final int count) {
        TimeToGoHome goHome = TimeToGoHome.load(getFile(), Lottery.Type.DLT, count);
        if (goHome.size() == 0) {
            UIUtil.showToastSafe(this, "没数据啊！");
            return;
        }
        float[] rate = calculateTimeToHomeRate(goHome);
        ArrayList<String> xV = new ArrayList<>();
        ArrayList<Entry> yV = new ArrayList<>();
        for (int i = 0; i < rate.length; i++) {
            xV.add(String.valueOf(i * (goHome.getTestCount() / RANGE_DIVIDER)));
            yV.add(new Entry(rate[i], i));
        }
        LineDataSet dataSet = new LineDataSet(yV, String.valueOf(count));
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        final int color = COLORS[new Random().nextInt(COLORS.length)];
        dataSet.setColor(color);
        dataSet.setCircleColor(color);
        dataSet.setLineWidth(1f);
        dataSet.setCircleSize(2f);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(color);
        dataSet.setDrawCircleHole(false);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        LineData data = new LineData(xV, dataSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        mChart.setData(data);

        mChart.invalidate();

        afterSetData();

    }


    private float[] calculateTimeToHomeRate(TimeToGoHome timeToGoHome) {
        if (timeToGoHome == null) {
            return NumUtils.newEmptyFloatArray(RANGE_DIVIDER);
        }
        final int testCount = timeToGoHome.getTestCount();
        final int range = testCount / RANGE_DIVIDER;
        final int[] occ = NumUtils.newEmptyIntArray(RANGE_DIVIDER);
        for (int i = 0; i < timeToGoHome.size(); i++) {
            final int index = timeToGoHome.get(i) / range;
            occ[index]++;
        }
        return NumUtils.calculateProbability(occ);
    }


    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
