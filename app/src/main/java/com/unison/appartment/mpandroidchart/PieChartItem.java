package com.unison.appartment.mpandroidchart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.unison.appartment.R;

public class PieChartItem extends ChartItem {

//    private final Typeface mTf;
    private final SpannableString mCenterText;

    public PieChartItem(ChartData<?> cd, Context c, String centerText) {
        super(cd);

//        mTf = Typeface.createFromAsset(c.getAssets(), "OpenSans-Regular.ttf");
        mCenterText = generateCenterText(centerText);
    }

    @Override
    public int getItemType() {
        return TYPE_PIECHART;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, Context c) {

        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = LayoutInflater.from(c).inflate(
                    R.layout.list_item_piechart, null);
            holder.chart = convertView.findViewById(R.id.chart);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // apply styling
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setHoleRadius(52f);
        holder.chart.setTransparentCircleRadius(57f);
        holder.chart.setCenterText(mCenterText);
        holder.chart.setCenterTextSize(9f);
        holder.chart.setUsePercentValues(true);
        holder.chart.setExtraOffsets(30, 10, 30, 10);
        holder.chart.getLegend().setEnabled(false);
        holder.chart.setDrawHoleEnabled(true);
        holder.chart.setHoleColor(Color.TRANSPARENT);

        mChartData.setValueFormatter(new PercentFormatter());
        mChartData.setValueTextSize(11f);
        // set data
        holder.chart.setData((PieData) mChartData);

        holder.chart.setEntryLabelColor(Color.BLACK);
        holder.chart.animateY(1000);
        holder.chart.invalidate();

        return convertView;
    }

    private SpannableString generateCenterText(String centerText) {
        SpannableString s = new SpannableString(centerText);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s.length(), 0);
        return s;
    }

    private static class ViewHolder {
        PieChart chart;
    }
}