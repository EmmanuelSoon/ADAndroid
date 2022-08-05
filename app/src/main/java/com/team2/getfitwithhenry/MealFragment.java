package com.team2.getfitwithhenry;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.team2.getfitwithhenry.adapter.MealListAdapter;
import com.team2.getfitwithhenry.model.DietRecord;
import com.team2.getfitwithhenry.model.HealthRecord;

import java.util.List;

public class MealFragment extends Fragment implements AdapterView.OnItemClickListener  {

    private ListView mMealView;
    private List<DietRecord> dietRecordList;

    public MealFragment() {
        // Required empty public constructor
    }

    public void setDietRecordList(List<DietRecord> dietRecordList){
        this.dietRecordList = dietRecordList;
        updateDietRecords();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal, container, false);
    }

    private void updateDietRecords(){
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // update the listViews
                    MealListAdapter mlAdaptor = new MealListAdapter(getContext(), dietRecordList);
                    View view = getView();
                    ListView listView = view.findViewById(R.id.mealView);
                    if(listView != null){
                        listView.setAdapter(mlAdaptor);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        MealFragment mealFragment = (MealFragment) fm.findFragmentById(R.id.fragment_meal);
                        listView.setOnItemClickListener(mealFragment);
                    }
                    TextView empty = view.findViewById(R.id.empty);
                    if(empty != null){
                        if (dietRecordList.size() != 0){
                            empty.setText("");
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> av, View v, int pos, long id){
        TextView textView = v.findViewById(R.id.titleView);
        String str = textView.getText().toString();
        Toast.makeText(this.getContext(), str, Toast.LENGTH_LONG).show();
        //TODO meal details
    }
}