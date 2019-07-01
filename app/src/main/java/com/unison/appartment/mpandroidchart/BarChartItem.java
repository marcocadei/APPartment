package com.unison.appartment.mpandroidchart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.unison.appartment.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BarChartItem extends ChartItem {

    private String title;
    private List<String> labels;

    private final static int DP_PER_BAR = 60;
    private final static int DP_OFFSET = 60;

    public BarChartItem(ChartData<?> cd, Context c, String title, List<String> labels) {
        super(cd);
        this.title = title;
        this.labels = labels;
    }

    @Override
    public int getItemType() {
        return TYPE_BARCHART;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_barchart, null);
            holder.chart = convertView.findViewById(R.id.chart);
            holder.title = convertView.findViewById(R.id.chart_title);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Titolo del grafico
        holder.title.setText(title);
        // apply styling
        holder.chart.getDescription().setEnabled(false);
        holder.chart.getLegend().setEnabled(false);
        holder.chart.setDrawGridBackground(false);
        holder.chart.setDrawBarShadow(false);

        holder.chart.setDrawValueAboveBar(true);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        holder.chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        holder.chart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // chart.setDrawBarShadow(true);

        holder.chart.setDrawGridBackground(false);

        XAxis xl = holder.chart.getXAxis();
        xl.setPosition(XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(new IndexAxisValueFormatter(labels));
        xl.setLabelCount(labels.size());

        YAxis yl = holder.chart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yr = holder.chart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f);

        // Modifica dell'altezza in base al numero di righe visualizzate
        final float scale = c.getResources().getDisplayMetrics().density;
        holder.chart.getLayoutParams().height = (int) ((DP_PER_BAR * mChartData.getEntryCount() + DP_OFFSET) * scale + 0.5f);

        // set data
        holder.chart.setData((BarData) mChartData);
        holder.chart.getData().setValueTextSize(10f);
        holder.chart.setFitBars(true);
        holder.chart.setExtraOffsets(0, 10, 30, 20);
        holder.chart.animateY(1000);
        holder.chart.invalidate();


        return convertView;
    }

    private static class ViewHolder {
        BarChart chart;
        TextView title;
    }
}
