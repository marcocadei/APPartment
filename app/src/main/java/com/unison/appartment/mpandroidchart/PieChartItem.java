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
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.unison.appartment.R;

public class PieChartItem extends ChartItem {

    private final String title;

    public PieChartItem(ChartData<?> cd, Context c, String title) {
        super(cd);
        this.title = title;
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
            holder.title = convertView.findViewById(R.id.chart_title);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Titolo del grafico
        holder.title.setText(title);
        // apply styling
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setUsePercentValues(true);
        holder.chart.setExtraOffsets(30, 10, 30, 20);
        holder.chart.getLegend().setEnabled(false);
        holder.chart.setDrawHoleEnabled(false);

        mChartData.setValueFormatter(new PercentFormatter());
        mChartData.setValueTextSize(11f);
        // set data
        holder.chart.setData((PieData) mChartData);

        holder.chart.setEntryLabelColor(Color.BLACK);
        holder.chart.animateY(1000);
        holder.chart.invalidate();

        return convertView;
    }


    private static class ViewHolder {
        PieChart chart;
        TextView title;
    }
}