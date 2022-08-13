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
import com.team2.getfitwithhenry.model.WeekMonthData;

import java.util.ArrayList;
import java.util.List;

public class WeightGraphFilterFragment extends Fragment {

    List<HealthRecord> healthRecordList = new ArrayList<>();
    private String[] graphFilter;
    private String graphFilterItem = null;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItem;
    private List<String> getXAxisData;
    private LineChart LineChart;
    private List<WeekMonthData> weekList;
    private List<WeekMonthData> monthList;
    private String[] monthLabel;

    String getItem;
    public WeightGraphFilterFragment() {
        // Required empty public constructor
    }

    public WeightGraphFilterFragment(User user, List<HealthRecord> hrList, List<WeekMonthData> weekList,List<WeekMonthData> monthList,String[] monthLabelFilter, String[] graphFilter, String getItem){
       this.healthRecordList = hrList;
       this.getItem = getItem;
       graphFilterItem = "Last 7 Days";
       this.weekList = weekList;
       this.monthList = monthList;
       this.monthLabel = monthLabelFilter;
       this.graphFilter = graphFilter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight_graph_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LineChart = view.findViewById(R.id.weightLineChart);
        showDropdownList(view);
        showLineGraph(healthRecordList);
    }

    private void showDropdownList(View view) {
        autoCompleteTextView = view.findViewById(R.id.dropDownListforWeight);
        adapterItem = new ArrayAdapter<String>(getActivity().getApplicationContext(),R.layout.graph_list_item, graphFilter);
        autoCompleteTextView.setAdapter(adapterItem);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                graphFilterItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity().getApplicationContext(), "Item: " + graphFilterItem, Toast.LENGTH_SHORT).show();
                showLineGraph(healthRecordList);
            }
        });
    }

    private void showLineGraph(List<HealthRecord> healthRecordList) {
        if(healthRecordList==null){
            LineChart.setNoDataText("No Data to show! Please Update your information to show graph");
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


        LineChart.setDrawGridBackground(true);
        LineChart.setDrawBorders(true);
        LineChart.setBorderColor(Color.LTGRAY);
        LineData data = new LineData(dataSets);
        LineChart.setData(data);
        LineChart.setExtraOffsets(0,0,20,50);
        // weightLineChart.setViewPortOffsets(10, 30, 10, 400);
        LineChart.setDragEnabled(true);
        LineChart.setTouchEnabled(true);

        //enable pinch zoom to avoid scalling x and y seperately
        LineChart.setPinchZoom(true);


        // styling Dataset Value
        lineDataSet1.setValueTextSize(10);
        lineDataSet1.setValueTextColor(Color.BLUE);

        XAxis xAxis = LineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(getXAxisData.size(), false); // yes, false. This is intentional
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisData));

        xAxis.setLabelRotationAngle(-60f);

        // changing yAxis label
        YAxis yAxisLeft = LineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);
        yAxisLeft.setGranularityEnabled(true);

        YAxis yAxisRight = LineChart.getAxisRight();
        yAxisRight.setGranularity(1f);
        yAxisRight.setGranularityEnabled(true);

       // LineChart.setVisibleXRangeMaximum(7f);
        LineChart.invalidate();

    }

    private ArrayList<Entry> dataValuesforChart(List<HealthRecord> hrList, String filter) {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        int count = 0;
        if (filter.equals("Last 7 Days")) {
            getXAxisData = new ArrayList<String>();
            for (int i = hrList.size() - 1; i >= 0; i--) {
                HealthRecord testing1 = hrList.get(i);
                getXAxisData.add(hrList.get(i).getDate().toString());
                dataVals.add(new Entry(count, (float) hrList.get(i).getUserWeight()));
                count++;
            }
        }
        if (filter.equals("Last 7 Weeks/Year")) {
            getXAxisData = new ArrayList<String>();
            for (int i = weekList.size() - 1; i >= 0; i--) {
                //HealthRecord testing1 = hrList.get(i);
                getXAxisData.add("week "+weekList.get(i).getWeekMonthRepr().toString());
                float user_weight = weekList.get(i).getUser_weight().floatValue();
                dataVals.add(new Entry(count,user_weight));
                count++;
            }
        }

        if (filter.equals("Last 7 Months/Year")) {
            getXAxisData = new ArrayList<String>();
                //HealthRecord testing1 = hrList.get(i);
                for (int i = monthList.size() - 1; i >= 0; i--) {
                    //HealthRecord testing1 = hrList.get(i);
                    switch (monthList.get(i).getWeekMonthRepr()){
                        case ("1"):
                            getXAxisData.add(monthLabel[0]);
                            break;
                        case("2"):
                            getXAxisData.add(monthLabel[1]);
                            break;
                        case ("3"):
                            getXAxisData.add(monthLabel[2]);
                            break;
                        case("4"):
                            getXAxisData.add(monthLabel[3]);
                            break;
                        case("5"):
                            getXAxisData.add(monthLabel[3]);
                            break;
                        case ("6"):
                            getXAxisData.add(monthLabel[4]);
                            break;
                        case("7"):
                            getXAxisData.add(monthLabel[5]);
                            break;
                        case ("8"):
                            getXAxisData.add(monthLabel[6]);
                            break;
                        case("9"):
                            getXAxisData.add(monthLabel[7]);
                            break;
                        case ("10"):
                            getXAxisData.add(monthLabel[8]);
                            break;
                        case("11"):
                            getXAxisData.add(monthLabel[9]);
                            break;
                        case ("12"):
                            getXAxisData.add(monthLabel[10]);
                            break;
                        default:
                            getXAxisData.add("");
                            break;
                    }
                float user_weight = monthList.get(i).getUser_weight().floatValue();
                dataVals.add(new Entry(count,user_weight));
                count++;
            }
        }

        return dataVals;

    }


}