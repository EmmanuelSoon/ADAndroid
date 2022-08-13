package com.team2.getfitwithhenry;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.team2.getfitwithhenry.model.HealthRecord;
import com.team2.getfitwithhenry.model.User;

import java.util.ArrayList;
import java.util.List;

public class CaloriesGraphFilterFragment extends Fragment {
    List<HealthRecord> healthRecordList = new ArrayList<>();
    private String[] graphFilter = {"Daily", "Weekly", "Monthly"};
    private String graphFilterItem = null;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItem;
    private LineChart weightLineChart;
    private List<String> getXAxisData;

    String getItem;
    public CaloriesGraphFilterFragment() {
        // Required empty public constructor
    }

    public CaloriesGraphFilterFragment(User user, List<HealthRecord> hrList, String getItem){
        this.healthRecordList = hrList;
        this.getItem = getItem;
        graphFilterItem = "Daily";

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calories_graph_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.weightLineChart = view.findViewById(R.id.caloriesLineChart);
        showDropdownList(view);
        showLineGraph(healthRecordList);
    }

    private void showDropdownList(View view) {
        autoCompleteTextView = view.findViewById(R.id.dropDownListforCalories);
        int testing = graphFilter.length;
        adapterItem = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.graph_list_item, graphFilter);
        // adapterItem = new ArrayAdapter<String>(this,graphFilter, R.layout.graph_list_item);
        autoCompleteTextView.setAdapter(adapterItem);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                graphFilterItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity().getApplicationContext(), "Item: " + graphFilterItem, Toast.LENGTH_SHORT).show();
                showLineGraph(healthRecordList);
                //  getFromServer(getData, "/user/getuserrecords", "daily");
            }
        });
    }

    private void showLineGraph(List<HealthRecord> healthRecordList) {
        if(healthRecordList==null){
            weightLineChart.setNoDataText("No Data to show! Please Update your information to show graph");
        }
        LineDataSet lineDataSet1 = new LineDataSet(dataValuesforChart(healthRecordList, graphFilterItem), "Calories tracking");
        lineDataSet1.setCubicIntensity(3f);
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        // lineDataSet1.setColor(Color.RED);
        lineDataSet1.setCircleColor(Color.YELLOW);
        lineDataSet1.setLineWidth(2f);
        lineDataSet1.setCircleSize(4f);

        lineDataSet1.setFillColor(ColorTemplate.getHoloBlue());
        lineDataSet1.setHighLightColor(Color.rgb(244, 117, 117));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);


        weightLineChart.setDrawGridBackground(true);
        weightLineChart.setDrawBorders(true);
        weightLineChart.setBorderColor(Color.LTGRAY);
        LineData data = new LineData(dataSets);
        weightLineChart.setData(data);
        weightLineChart.setExtraOffsets(0,0,20,50);
       // weightLineChart.setViewPortOffsets(10, 30, 10, 400);
        weightLineChart.setDragEnabled(true);
        weightLineChart.setTouchEnabled(true);

        //enable pinch zoom to avoid scalling x and y seperately
        weightLineChart.setPinchZoom(true);


        // styling Dataset Value
        lineDataSet1.setValueTextSize(10);
        lineDataSet1.setValueTextColor(Color.BLUE);
        // lineDataSet1.setAxisDependency(YAxis.AxisDependency.RIGHT);

//        List<String> xAxisLabel = getXAxisLabels(healthRecordList);
        XAxis xAxis = weightLineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(getXAxisData.size(), false); // yes, false. This is intentional
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisData));
//        xAxis.mAxisMaximum = 3;

        xAxis.setLabelRotationAngle(-60f);

        // changing yAxis label
        YAxis yAxisLeft = weightLineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setGranularityEnabled(true);
        //yAxisLeft.setEnabled(false);
        YAxis yAxisRight = weightLineChart.getAxisRight();
        yAxisRight.setGranularity(1f);
        yAxisRight.setGranularityEnabled(true);
        // yAxisRight.setEnabled(true);


        weightLineChart.setVisibleXRangeMaximum(7f);
        weightLineChart.invalidate();

    }

    private ArrayList<Entry> dataValuesforChart(List<HealthRecord> hrList,String filter) {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        getXAxisData = new ArrayList<String>();
        int count = 0;
        if (filter.equals("Daily")) {
            for (int i = hrList.size() - 1; i >= 0; i--) {
                HealthRecord testing1 = hrList.get(i);
                getXAxisData.add(hrList.get(i).getDate().toString());
                dataVals.add(new Entry(count, (float) hrList.get(i).getCalIntake()));
                count++;
            }
        }
        return dataVals;

    }


}