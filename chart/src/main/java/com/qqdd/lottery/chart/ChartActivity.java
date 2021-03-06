package com.qqdd.lottery.chart;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.qqdd.lottery.calculate.data.Rate;
import com.qqdd.lottery.calculate.data.RateList;
import com.qqdd.lottery.calculate.data.TimeToGoHome;
import com.qqdd.lottery.data.KeyValuePair;
import com.qqdd.lottery.utils.NumUtils;
import com.qqdd.lottery.utils.SimpleIOUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import la.niub.util.utils.StorageHelper;

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
        if (id == R.id.action_rate) {
            final File rateRoot = new File(getFile(), "rates");
            showFilePicker(rateRoot, null, new FileLoader() {

                @Override
                public void load(String file) {
                    loadRate(rateRoot, file);
                }
            });
            return true;
        } else if (id ==R.id.action_kv) {
            showFilePicker(getFile(), new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(KeyValuePair.TAIL);
                }
            }, new FileLoader() {
                @Override
                public void load(String file) {
                    try {
                        loadKV(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private interface FileLoader {

        void load(String file);
    }

    private void showFilePicker(File root, final FilenameFilter filter, final FileLoader fileLoader) {
        final String[] names = root.list(filter);
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(
                this);
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = names[which];
                fileLoader.load(name);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public File getFile() {
        return new File(StorageHelper.getExternalStorageDirectory(), "Ltt");
    }

    private void loadRate(File root, final String name) {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<String> xV = new ArrayList<>();
        final File f = new File(root, name);
        final RateList rates = RateList.loadFrom(f);
        final LineData lineData = mChart.getLineData();
        int xVCount = 0;
        if (lineData != null) {
             xVCount = lineData
                    .getXValCount();
        }
        ArrayList<Entry> yV = new ArrayList<>();
        float max = 0;
        int larger = 0;
        float total = 0;
        for (int j = 0; j < rates.size(); j++) {
            final Rate rate = rates.get(j);
            xV.add(rate.getRecord()
                    .getDateDisplay());
            yV.add(new Entry(rate.getRate(), j));
            if (max < rate.getRate()) {
                max  = rate.getRate();
            }
            if (rate.getRate() > 0.0666f) {
                larger ++;total += rate.getRate();
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

        if (xVCount == rates.size()) {
            LineData data = mChart.getLineData();
            dataSet.setColor(COLORS[data.getDataSetCount() % COLORS.length ]);
            dataSet.setCircleColor(COLORS[data.getDataSetCount() % COLORS.length ]);
            data.addDataSet(dataSet);
            mChart.setData(data);
        } else {
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
        }

        mChart.invalidate();

        afterSetData();

    }
    private void loadKV(final String name) throws IOException, JSONException {

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<String> xV = new ArrayList<>();
        final File f = new File(getFile(), name);
        final List<KeyValuePair> rates = KeyValuePair.parseArray(
                new JSONArray(SimpleIOUtils.loadContent(new FileInputStream(f))));
        ArrayList<Entry> yV = new ArrayList<>();
        int larger = 0;
        float total = 0;
        for (int j = 0; j < rates.size(); j++) {
            final KeyValuePair testRoundRates = rates.get(j);
            xV.add(testRoundRates.getKey());
            yV.add(new Entry(testRoundRates.getValue(), j));
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

        dataSets.add(formatAverageLine(rates, 5, COLORS[1]));
        dataSets.add(formatTotalAverageLine(rates, COLORS[2]));

        ArrayList<Entry> yVRandom = new ArrayList<>();
        for (int i = 0; i < rates.size(); i++) {
            yVRandom.add(new Entry(0.0666f, i));
        }

        LineData data = new LineData(xV, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        mChart.setData(data);

        mChart.invalidate();

        afterSetData();

    }

    private LineDataSet formatTotalAverageLine(final List<KeyValuePair> kv, final int color) {
        ArrayList<Entry> yV = new ArrayList<>();
        for (int i = 0; i < kv.size(); i++) {
            float total = 0;
            for (int j = 0; j < i + 1; j++) {
                total += kv.get(j).getValue();
            }
            yV.add(new Entry(total / (i + 1), i));
        }
        LineDataSet dataSet = new LineDataSet(yV, "av_total");
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setColor(color);
        dataSet.setLineWidth(1f);
        dataSet.setCircleSize(2f);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setDrawCubic(true);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        return dataSet;
    }

    private LineDataSet formatAverageLine(final List<KeyValuePair> kv, final int av, final int color) {
        if (kv == null || kv.size() < av) {
            return null;
        }
        ArrayList<String> xV = new ArrayList<>();
        ArrayList<Entry> yV = new ArrayList<>();
        for (int i = 0; i < av; i++) {
            yV.add(new Entry(0, i));
        }
        for (int i = av; i < kv.size(); i++) {
            float total = 0;
            for (int j = i - av; j < i; j++) {
                total += kv.get(j).getValue();
            }
            yV.add(new Entry(total / av, i));
        }
        LineDataSet dataSet = new LineDataSet(yV, "av_" + av);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setColor(color);
        dataSet.setLineWidth(1f);
        dataSet.setCircleSize(2f);
        dataSet.setFillAlpha(65);
        dataSet.setFillColor(color);
        dataSet.setDrawCircles(false);
        dataSet.setDrawCubic(true);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        return dataSet;
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
