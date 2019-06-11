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
import java.util.List;

/**
 * Fragment che rappresenta un insieme di grafici
 */
public class ChartsFragment extends Fragment {

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        ListView chartListView = view.findViewById(R.id.fragment_charts_list);

        ArrayList<ChartItem> chartItems = new ArrayList<>();
        // TODO riempire array di colori come in PieChartActivity (app esempio)
        // Torta con i task completati
        List<PieEntry> entries = new ArrayList<>();
        for(HomeUser homeUser : Appartment.getInstance().getHomeUsers().values()) {
            entries.add(new PieEntry(homeUser.getCompletedTasks(), homeUser.getNickname()));
        }
        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextColor(Color.BLACK);
        chartItems.add(new PieChartItem(pieData, getContext(), "Task\ncompletati"));

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
