package com.unison.appartment.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.unison.appartment.R;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.mpandroidchart.BarChartItem;
import com.unison.appartment.mpandroidchart.ChartItem;
import com.unison.appartment.mpandroidchart.PieChartItem;
import com.unison.appartment.state.Appartment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChartActivity extends ActivityWithNetworkConnectionDialog {

    private final static double PIE_MIN_PERCENTAGE_SHOWN = 0.05;
    private final static double PIE_CUMULATED_PERCENTAGE_LIMIT = 0.9;

    ArrayList<Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        /*
        Impostazione del comportamento della freccia presente sulla toolbar
        (alla pressione l'activity viene terminata).
         */
        Toolbar toolbar = findViewById(R.id.activity_chart_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ListView chartListView = findViewById(R.id.activity_chart_list);

        // Colori utilizzati per i grafici
        colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        ArrayList<ChartItem> chartItems = new ArrayList<>();

        // Torta con i task completati
        List<PieEntry> entries = new ArrayList<>();

        List<HomeUser> homeUsers = new ArrayList<>(Appartment.getInstance().getHomeUsers().values());
        Collections.sort(homeUsers, new Comparator<HomeUser>() {
            @Override
            public int compare(HomeUser o1, HomeUser o2) {
                return (int)Math.signum(Integer.compare(o2.getCompletedTasks(), o1.getCompletedTasks()));
            }
        });
        int total = 0;
        for(HomeUser homeUser : homeUsers) {
            total += homeUser.getCompletedTasks();
        }

        int cumulated = 0;
        for(HomeUser homeUser : homeUsers) {
            if (homeUser.getCompletedTasks() / (total * 1.0) >= PIE_MIN_PERCENTAGE_SHOWN) {
                cumulated += homeUser.getCompletedTasks();
                entries.add(new PieEntry(homeUser.getCompletedTasks(), homeUser.getNickname()));
            }
            if (cumulated / (total * 1.0) >= PIE_CUMULATED_PERCENTAGE_LIMIT) {
                break;
            }
        }
        if (entries.size() > 0) {
            if (total != cumulated) {
                entries.add(new PieEntry(total - cumulated, getString(R.string.pie_chart_others)));
            }
            chartItems.add(new PieChartItem(createPieData(entries), this, getString(R.string.pie_chart_tasks_title)));
        }

        // Torta con i premi ottenuti
        entries = new ArrayList<>();

        Collections.sort(homeUsers, new Comparator<HomeUser>() {
            @Override
            public int compare(HomeUser o1, HomeUser o2) {
                return (int)Math.signum(Integer.compare(o2.getClaimedRewards(), o1.getClaimedRewards()));
            }
        });
        total = 0;
        for(HomeUser homeUser : homeUsers) {
            total += homeUser.getClaimedRewards();
        }

        cumulated = 0;
        for(HomeUser homeUser : homeUsers) {
            if (homeUser.getClaimedRewards() / (total * 1.0) >= PIE_MIN_PERCENTAGE_SHOWN) {
                cumulated += homeUser.getClaimedRewards();
                entries.add(new PieEntry(homeUser.getClaimedRewards(), homeUser.getNickname()));
            }
            if (cumulated / (total * 1.0) >= PIE_CUMULATED_PERCENTAGE_LIMIT) {
                break;
            }
        }
        if (entries.size() > 0) {
            if (total != cumulated) {
                entries.add(new PieEntry(total - cumulated, getString(R.string.pie_chart_others)));
            }
            chartItems.add(new PieChartItem(createPieData(entries), this, getString(R.string.pie_chart_rewards_title)));
        }

        // Grafico a barre coi punti
        List<BarEntry> barEntries = new ArrayList<>();
        int i = 0;
        List<String> nicknames = new ArrayList<>();
        for(HomeUser homeUser : Appartment.getInstance().getHomeUsers().values()) {
            if (homeUser.getPoints() != 0) {
                nicknames.add(homeUser.getNickname());
                barEntries.add(new BarEntry(i++, (float)homeUser.getPoints()));
            }
        }
        if (barEntries.size() > 0) {
            BarDataSet barDataSet = new BarDataSet(barEntries, "");
            barDataSet.setColors(colors);
            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            chartItems.add(new BarChartItem(barData, this, getString(R.string.bar_chart_rewards_title), nicknames));
        }

        // Grafico a barre coi messaggi totali
        barEntries = new ArrayList<>();
        i = 0;
        nicknames = new ArrayList<>();
        for(HomeUser homeUser : Appartment.getInstance().getHomeUsers().values()) {
            int totalPosts = homeUser.getTextPosts() + homeUser.getAudioPosts() + homeUser.getImagePosts();
            if (totalPosts != 0f) {
                nicknames.add(homeUser.getNickname());
                barEntries.add(new BarEntry(i++, totalPosts));
            }
        }
        if (barEntries.size() > 0) {
            BarDataSet barDataSet = new BarDataSet(barEntries, "");
            barDataSet.setColors(colors);
            BarData barData = new BarData(barDataSet);
            barData.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return String.valueOf((int) value);
                }
            });
            chartItems.add(new BarChartItem(barData, this, getString(R.string.bar_chart_posts_title), nicknames));
        }

        chartListView.setAdapter(new ChartAdapter(this, 0, chartItems));

        if (chartItems.size() == 0) {
            // Se non ci sono grafici da visualizzare mostriamo una scritta all'utente
            View emptyList = findViewById(R.id.activity_chart_layout_empty_list);
            emptyList.setVisibility(View.VISIBLE);
        }
    }

    private PieData createPieData(List<PieEntry> entries) {
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setColors(colors);
        // Lunghezza delle stanghette
        pieDataSet.setValueLinePart1Length(0.8f);
        pieDataSet.setValueLinePart2Length(0.4f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextColor(Color.BLACK);

        return pieData;
    }

    private class ChartAdapter extends ArrayAdapter<ChartItem> {

        public ChartAdapter(@NonNull Context context, int resource, @NonNull List<ChartItem> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
    }
}
