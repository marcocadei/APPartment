package com.unison.appartment.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.unison.appartment.R;
import com.unison.appartment.model.HomeUser;
import com.unison.appartment.mpandroidchart.ChartItem;
import com.unison.appartment.mpandroidchart.PieChartItem;
import com.unison.appartment.state.Appartment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment che rappresenta un insieme di grafici
 */
public class ChartsFragment extends Fragment {
    private final static double PIE_MIN_PERCENTAGE_SHOWN = 0.05;
    private final static double PIE_CUMULATED_PERCENTAGE_LIMIT = 0.9;

    ArrayList<Integer> colors;

    /**
     * Costruttore vuoto obbligatorio che viene usato nella creazione del fragment
     */
    public ChartsFragment() {
    }

    public static ChartsFragment newInstance(String param1, String param2) {
        ChartsFragment fragment = new ChartsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        ListView chartListView = view.findViewById(R.id.fragment_charts_list);

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
        if (total != cumulated) {
            entries.add(new PieEntry(total - cumulated, getString(R.string.general_pie_chart_others)));
        }
        chartItems.add(new PieChartItem(createPieData(entries), getContext(), getString(R.string.pie_chart_tasks_title)));

        // Torta con i premi completati
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
            if (homeUser.getCompletedTasks() / (total * 1.0) >= PIE_MIN_PERCENTAGE_SHOWN) {
                cumulated += homeUser.getClaimedRewards();
                entries.add(new PieEntry(homeUser.getClaimedRewards(), homeUser.getNickname()));
            }
            if (cumulated / (total * 1.0) >= PIE_CUMULATED_PERCENTAGE_LIMIT) {
                break;
            }
        }
        if (total != cumulated) {
            entries.add(new PieEntry(total - cumulated, getString(R.string.general_pie_chart_others)));
        }
        chartItems.add(new PieChartItem(createPieData(entries), getContext(), getString(R.string.pie_chart_rewards_title)));

        chartListView.setAdapter(new ChartAdapter(getContext(), 0, chartItems));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
